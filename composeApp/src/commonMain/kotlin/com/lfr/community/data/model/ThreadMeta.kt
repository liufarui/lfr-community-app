package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadMeta(
    val id: String = "",
    val title: String = "",
    val room: String = "",
    val status: String = "discussing",
    @SerialName("rounds_count") val roundsCount: Int = 0,
    @SerialName("last_active") val lastActive: String = "",
    val participants: List<String> = emptyList(),
    @SerialName("vision_id") val visionId: String? = null,
    @SerialName("red_team") val redTeam: String? = null,
    @SerialName("decision_id") val decisionId: String? = null,
)
