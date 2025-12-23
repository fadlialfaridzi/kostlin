package com.example.kostlin.data.local.session

import java.util.concurrent.atomic.AtomicReference

object AuthTokenStore {
    private val accessTokenRef = AtomicReference<String?>(null)
    private val refreshTokenRef = AtomicReference<String?>(null)

    fun setTokens(accessToken: String?, refreshToken: String?) {
        accessTokenRef.set(accessToken)
        refreshTokenRef.set(refreshToken)
    }

    fun clear() {
        accessTokenRef.set(null)
        refreshTokenRef.set(null)
    }

    fun getAccessToken(): String? = accessTokenRef.get()
    fun getRefreshToken(): String? = refreshTokenRef.get()
}
