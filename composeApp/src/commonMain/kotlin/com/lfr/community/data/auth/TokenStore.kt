package com.lfr.community.data.auth

interface TokenStore {
    suspend fun load(): String?
    suspend fun save(token: String)
    suspend fun clear()
}
