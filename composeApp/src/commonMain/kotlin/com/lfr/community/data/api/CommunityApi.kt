package com.lfr.community.data.api

import com.lfr.community.data.auth.*
import com.lfr.community.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CommunityApi(
    baseUrl: String = "https://liufarui.top/community-api",
    private val tokenStore: TokenStore = InMemoryTokenStore(),
) {
    var apiBase: String = baseUrl
        private set

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenStore.load()?.let { BearerTokens(it, "") }
                }
                refreshTokens {
                    try {
                        val response = auth.refreshToken()
                        BearerTokens(response.token, "")
                    } catch (e: Exception) {
                        println("[CommunityApi] warn: refreshTokens failed: ${e.message}")
                        throw e
                    }
                }
                sendWithoutRequest { true }
            }
        }

        defaultRequest {
            url(apiBase)
        }
    }

    val auth = AuthApi(client, tokenStore)
    val topics = TopicsApi(client)
    val agents = AgentsApi(client)
    val threads = ThreadsApi(client)

    suspend fun fetchMembers(): List<Member> =
        client.get("/api/members-parsed").body()

    suspend fun fetchMessages(limit: Int = 50, offset: Int = 0): List<Message> {
        val dateDirs: List<String> = client.get("/api/data/messages").body()
        val sorted = dateDirs.filter { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }.sortedDescending()
        val messages = mutableListOf<Message>()
        for (dateDir in sorted) {
            if (messages.size >= offset + limit) break
            try {
                val files: List<String> = client.get("/api/data/messages/$dateDir").body()
                val jsonlFiles = files.filter { it.endsWith(".jsonl") }.sortedDescending()
                for (f in jsonlFiles) {
                    val batch: List<Message> = client.get("/api/data/messages/$dateDir/$f").body()
                    messages.addAll(batch)
                    if (messages.size >= offset + limit) break
                }
            } catch (e: Exception) {
                println("[CommunityApi] error: fetchMessages failed on $dateDir: ${e.message}")
            }
        }
        return messages.sortedByDescending { it.time }.drop(offset).take(limit)
    }

    suspend fun fetchGroupChats(): List<GroupChat> =
        client.get("/api/group-chats").body()

    suspend fun createGroupChat(name: String, emoji: String, members: List<String>): GroupChat =
        client.post("/api/group-chats/create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "emoji" to emoji, "members" to members))
        }.body()

    suspend fun updateGroupChat(chatId: String, name: String?, emoji: String?): GroupChat =
        client.put("/api/group-chats/$chatId") {
            contentType(ContentType.Application.Json)
            val body = mutableMapOf<String, String>()
            name?.let { body["name"] = it }
            emoji?.let { body["emoji"] = it }
            setBody(body)
        }.body()

    suspend fun addGroupChatMembers(chatId: String, members: List<String>): GroupChat =
        client.post("/api/group-chats/$chatId/members") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("members" to members))
        }.body()

    suspend fun removeGroupChatMember(chatId: String, memberId: String): GroupChat =
        client.delete("/api/group-chats/$chatId/members/$memberId").body()

    suspend fun fetchGroupChatMessages(chatId: String): List<Message> =
        client.get("/api/group-chats/$chatId/messages").body()

    suspend fun postGroupChatMessage(chatId: String, content: String): Message =
        client.post("/api/group-chats/$chatId/post") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body<PostMessageResponse>().message
}

@Serializable
data class PostMessageResponse(val ok: Boolean, val message: Message)
