package com.lfr.community.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val role: String,
    @kotlinx.serialization.SerialName("expires_in") val expiresIn: String,
)
