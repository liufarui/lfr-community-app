package com.lfr.community.data.api

import com.lfr.community.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CommunityApi(baseUrl: String = "http://127.0.0.1:3001/api") {
    var apiBase: String = baseUrl
        private set

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    fun updateBaseUrl(url: String) {
        apiBase = url.trimEnd('/')
    }

    suspend fun fetchMembers(): List<Member> =
        client.get("$apiBase/members-parsed").body()

    suspend fun fetchMessages(limit: Int = 50): List<Message> {
        val dateDirs: List<String> = client.get("$apiBase/data/messages").body()
        val sorted = dateDirs.filter { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }.sortedDescending()
        val messages = mutableListOf<Message>()
        for (dateDir in sorted) {
            if (messages.size >= limit) break
            try {
                val files: List<String> = client.get("$apiBase/data/messages/$dateDir").body()
                val jsonlFiles = files.filter { it.endsWith(".jsonl") }.sortedDescending()
                for (f in jsonlFiles) {
                    val batch: List<Message> = client.get("$apiBase/data/messages/$dateDir/$f").body()
                    messages.addAll(batch)
                    if (messages.size >= limit) break
                }
            } catch (_: Exception) {}
        }
        return messages.sortedByDescending { it.time }.take(limit)
    }

    suspend fun fetchGroupChats(): List<GroupChat> =
        client.get("$apiBase/group-chats").body()

    suspend fun createGroupChat(name: String, emoji: String, members: List<String>): GroupChat =
        client.post("$apiBase/group-chats/create") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "emoji" to emoji, "members" to members))
        }.body()

    suspend fun updateGroupChat(chatId: String, name: String?, emoji: String?): GroupChat =
        client.put("$apiBase/group-chats/$chatId") {
            contentType(ContentType.Application.Json)
            val body = mutableMapOf<String, String>()
            name?.let { body["name"] = it }
            emoji?.let { body["emoji"] = it }
            setBody(body)
        }.body()

    suspend fun addGroupChatMembers(chatId: String, members: List<String>): GroupChat =
        client.post("$apiBase/group-chats/$chatId/members") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("members" to members))
        }.body()

    suspend fun removeGroupChatMember(chatId: String, memberId: String): GroupChat =
        client.delete("$apiBase/group-chats/$chatId/members/$memberId").body()

    suspend fun fetchGroupChatMessages(chatId: String): List<Message> =
        client.get("$apiBase/group-chats/$chatId/messages").body()

    suspend fun postGroupChatMessage(chatId: String, content: String): HttpResponse =
        client.post("$apiBase/group-chats/$chatId/post") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }
}
