package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Agent(
    val id: String = "",
    val name: String = "",
    val fullname: String = "",
    val gender: String = "",
    val role: String = "",
    val category: String = "",
    val description: String = "",
    val emoji: String = "",
    val color: String = "",
    val active: Boolean = true,
    val personality: String = "",
    val model: String = "claude-code",
)

@Serializable
data class AgentsResponse(
    val version: String = "",
    val count: Int = 0,
    val agents: List<Agent> = emptyList(),
)

@Serializable
data class UpdateAgentRequest(
    val name: String? = null,
    val description: String? = null,
    val tags: List<String>? = null,
    val extras: Map<String, String>? = null,
    @SerialName("preferred_model") val preferredModel: String? = null,
)

@Serializable
data class UpdateAgentResponse(val ok: Boolean, val agent: Agent)
