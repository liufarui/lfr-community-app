package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupChat(
    val id: String = "",
    val name: String = "",
    val emoji: String = "💬",
    @SerialName("avatar_image") val avatarImage: String? = null,
    val members: List<String> = emptyList(),
    val creator: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("last_message") val lastMessage: Message? = null,
    @SerialName("message_count") val messageCount: Int = 0,
)
