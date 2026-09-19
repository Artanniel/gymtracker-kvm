package com.gymtracker

import com.gymtracker.data.auth.TokenStore
import java.io.File

actual fun createPlatformTokenStore(): TokenStore {
    val dir = File(System.getProperty("user.home"), ".gymtracker")
    if (!dir.exists()) dir.mkdirs()
    return TokenStore(dir)
}
