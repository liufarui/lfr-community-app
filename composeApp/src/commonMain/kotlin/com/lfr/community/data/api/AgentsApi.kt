package com.lfr.community.data.api

import com.lfr.community.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AgentsApi(private val client: HttpClient) {

    suspend fun list(): List<Agent> {
        return client.get("/api/agents-data").body<AgentsResponse>().agents
    }

    suspend fun update(id: String, req: UpdateAgentRequest): Agent {
        return client.put("/api/agents/$id") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body<UpdateAgentResponse>().agent
    }

    suspend fun updatePrompt(id: String, prompt: String) {
        client.put("/api/agent-prompt/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("prompt" to prompt))
        }
    }
}
