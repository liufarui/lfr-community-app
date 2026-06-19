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
    val reactions: Map<String, List<String>> = emptyMap(),
    @SerialName("thread_id") val threadId: String? = null,
) {
    val messageType: MessageType
        get() = MessageType.fromValue(type)
}

enum class MessageType(val value: String) {
    DYNAMIC("动态"),
    PROPOSAL("提议"),
    REPLY("回复");

    companion object {
        fun fromValue(value: String): MessageType =
            entries.find { it.value == value } ?: DYNAMIC
    }
}
