package com.lfr.community.data.auth

import platform.Foundation.NSUserDefaults

class IosTokenStore : TokenStore {
    private val key = "jwt_token"

    override suspend fun load(): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    override suspend fun save(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, key)
    }

    override suspend fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
    }
}
