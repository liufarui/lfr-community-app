package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val category: String = "general",
    val status: String = "active",
    val priority: String = "P2",
    val participants: List<String> = emptyList(),
    val votes: TopicVotes = TopicVotes(),
    @SerialName("comments_count") val commentsCount: Int = 0,
    val deadline: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("closed_at") val closedAt: String? = null,
    @SerialName("closed_reason") val closedReason: String? = null,
)

@Serializable
data class TopicVotes(
    val up: List<String> = emptyList(),
    val down: List<String> = emptyList(),
)

@Serializable
data class Comment(
    val id: String = "",
    @SerialName("topic_id") val topicId: String = "",
    val author: String = "",
    val content: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class CreateTopicRequest(
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val deadline: String? = null,
)

@Serializable
data class UpdateTopicRequest(
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val priority: String? = null,
)

@Serializable
data class CreateTopicResponse(val ok: Boolean, val topic: Topic)

@Serializable
data class UpdateTopicResponse(val ok: Boolean, val topic: Topic)

@Serializable
data class VoteResponse(val ok: Boolean, val votes: TopicVotes)

@Serializable
data class AddCommentResponse(val ok: Boolean, val comment: Comment)

@Serializable
data class CloseTopicResponse(val ok: Boolean, val topic: Topic)
