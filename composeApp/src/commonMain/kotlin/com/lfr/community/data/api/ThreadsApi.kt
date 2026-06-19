package com.lfr.community.data.api

import com.lfr.community.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class ThreadsApi(private val client: HttpClient) {

    suspend fun getByRoom(roomId: String): List<ThreadMeta> {
        return client.get("/api/threads-by-room/$roomId").body()
    }

    suspend fun getMessages(threadId: String): List<Message> {
        return client.get("/api/thread/$threadId/messages").body()
    }

    suspend fun create(title: String, room: String, firstMessage: String? = null): ThreadMeta {
        return client.post("/api/thread/create") {
            contentType(ContentType.Application.Json)
            setBody(CreateThreadRequest(title, room, firstMessage))
        }.body<CreateThreadResponse>().thread
    }

    suspend fun post(threadId: String, content: String): Message {
        return client.post("/api/thread/$threadId/post") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body<PostMessageResponse>().message
    }
}

@Serializable
data class CreateThreadRequest(
    val title: String,
    val room: String,
    val firstMessage: String? = null,
)

@Serializable
data class CreateThreadResponse(val ok: Boolean, val thread: ThreadMeta)
