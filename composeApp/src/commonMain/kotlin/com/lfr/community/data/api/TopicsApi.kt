package com.lfr.community.data.api

import com.lfr.community.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class TopicsApi(private val client: HttpClient) {

    suspend fun list(status: String? = null, category: String? = null): List<Topic> {
        return client.get("/api/topics") {
            status?.let { parameter("status", it) }
            category?.let { parameter("category", it) }
        }.body()
    }

    suspend fun get(id: String): Topic {
        return client.get("/api/topics/$id").body()
    }

    suspend fun create(req: CreateTopicRequest): Topic {
        return client.post("/api/topics") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body<CreateTopicResponse>().topic
    }

    suspend fun update(id: String, req: UpdateTopicRequest): Topic {
        return client.put("/api/topics/$id") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body<UpdateTopicResponse>().topic
    }

    suspend fun vote(id: String, vote: VoteType): TopicVotes {
        return client.post("/api/topics/$id/vote") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("vote" to vote.name.lowercase()))
        }.body<VoteResponse>().votes
    }

    suspend fun listComments(id: String): List<Comment> {
        return client.get("/api/topics/$id/comments").body()
    }

    suspend fun addComment(id: String, content: String): Comment {
        return client.post("/api/topics/$id/comments") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body<AddCommentResponse>().comment
    }

    suspend fun close(id: String, reason: String? = null): Topic {
        return client.post("/api/topics/$id/close") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("reason" to reason))
        }.body<CloseTopicResponse>().topic
    }
}

enum class VoteType { UP, DOWN }
