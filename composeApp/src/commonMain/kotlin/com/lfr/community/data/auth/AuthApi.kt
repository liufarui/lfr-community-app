package com.lfr.community.data.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
) {
    suspend fun login(username: String, password: String): LoginResponse {
        val response = client.post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(username, password))
        }.body<LoginResponse>()

        tokenStore.save(response.token)
        return response
    }

    suspend fun logout() {
        tokenStore.clear()
    }

    suspend fun isLoggedIn(): Boolean = tokenStore.load() != null

    /**
     * 预留桩，后端未实现。
     * 当前策略：token 过期后跳登录页重新 login。
     * 后续后端实现后，改为调用 /api/refresh-token。
     */
    suspend fun refreshToken(): LoginResponse {
        throw NotImplementedError("refreshToken 后端暂未实现，需重新 login")
    }
}
