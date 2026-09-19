package com.gymtracker

import com.gymtracker.data.auth.TokenStore

actual fun createPlatformTokenStore(): TokenStore {
    return TokenStore()
}
