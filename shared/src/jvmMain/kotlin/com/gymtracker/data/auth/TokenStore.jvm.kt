package com.gymtracker.data.auth

import java.io.File

actual class TokenStore(private val dir: File) {
    private fun file(name: String) = File(dir, "gymtracker_$name")

    actual fun getToken(): String? = file("token").let { if (it.exists()) it.readText() else null }
    actual fun setToken(token: String) { file("token").writeText(token) }
    actual fun getRefreshToken(): String? = file("refresh").let { if (it.exists()) it.readText() else null }
    actual fun setRefreshToken(token: String) { file("refresh").writeText(token) }
    actual fun getUserData(): String? = file("user").let { if (it.exists()) it.readText() else null }
    actual fun setUserData(data: String) { file("user").writeText(data) }
    actual fun clearAll() { dir.listFiles()?.filter { it.name.startsWith("gymtracker_") }?.forEach { it.delete() } }
}
