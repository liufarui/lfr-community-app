package com.lfr.community.data.auth

class InMemoryTokenStore : TokenStore {
    private var token: String? = null

    override suspend fun load(): String? = token

    override suspend fun save(token: String) {
        this.token = token
    }

    override suspend fun clear() {
        token = null
    }
}
