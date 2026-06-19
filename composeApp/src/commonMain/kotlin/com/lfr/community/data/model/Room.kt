package com.lfr.community.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val access: String = "tag_based",
    @SerialName("required_tags") val requiredTags: List<String> = emptyList(),
)

@Serializable
data class RoomsConfig(
    val rooms: List<Room> = emptyList(),
)
