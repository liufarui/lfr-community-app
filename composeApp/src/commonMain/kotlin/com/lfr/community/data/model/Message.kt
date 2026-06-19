package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val author: String = "",
    val time: String = "",
    val type: String = "动态",
    val room: String = "",
    val content: String = "",
    val mentions: List<String> = emptyList(),
    val reactions: Map<String, Int> = emptyMap(),
    @SerialName("thread_id") val threadId: String? = null,
)
