package com.lfr.community.data.auth

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

class IosTokenStore : TokenStore {
    private val service = "com.lfr.community.jwt"
    private val account = "token"

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override suspend fun load(): String? = memScoped {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service as CFTypeRef?,
            kSecAttrAccount to account as CFTypeRef?,
            kSecReturnData to true as CFTypeRef?,
            kSecMatchLimit to kSecMatchLimitOne,
        ).mapKeys { it.key as CFTypeRef? }
            .mapValues { it.value as CFTypeRef? }
            .toMap() as CFDictionaryRef?

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)
        if (status == errSecSuccess) {
            val data = result.value as? NSData
            data?.let { NSString.create(it, NSUTF8StringEncoding) as? String }
        } else null
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override suspend fun save(token: String) {
        clear()
        memScoped {
            val data = (token as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
            val attrs = mutableMapOf<Any?, Any?>(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service as CFTypeRef?,
                kSecAttrAccount to account as CFTypeRef?,
                kSecValueData to data as CFTypeRef?,
                kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly as CFTypeRef?,
            ).mapKeys { it.key as CFTypeRef? }
                .mapValues { it.value as CFTypeRef? }
                .toMap() as CFDictionaryRef?
            SecItemAdd(attrs, null)
        }
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override suspend fun clear() = memScoped {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service as CFTypeRef?,
            kSecAttrAccount to account as CFTypeRef?,
        ).mapKeys { it.key as CFTypeRef? }
            .mapValues { it.value as CFTypeRef? }
            .toMap() as CFDictionaryRef?
        SecItemDelete(query)
        Unit
    }
}
