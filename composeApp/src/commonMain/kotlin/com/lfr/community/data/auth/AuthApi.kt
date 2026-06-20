package com.lfr.community.data.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
) {
    suspend fun login(username: String, password: String): Result<LoginResponse> = runCatching {
        val response = client.post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }.body<LoginResponse>()

        tokenStore.save(response.token)
        response
    }

    suspend fun logout() {
        tokenStore.clear()
    }

    suspend fun isLoggedIn(): Boolean = tokenStore.load() != null

    suspend fun refreshToken(): LoginResponse {
        val response = client.post("/api/refresh-token").body<LoginResponse>()
        tokenStore.save(response.token)
        return response
    }
}
