package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val id: String = "",
    val name: String = "",
    @SerialName("display_name") val displayName: String = "",
    val emoji: String = "👤",
    val type: String = "agent",
    val role: String = "resident",
    val tags: List<String> = emptyList(),
    val extras: Map<String, String> = emptyMap(),
)
