package com.gymtracker.data.auth

actual class TokenStore {
    private val storage = mutableMapOf<String, String>()

    actual fun getToken(): String? = storage["jwt_token"]
    actual fun setToken(token: String) { storage["jwt_token"] = token }
    actual fun getRefreshToken(): String? = storage["refresh_token"]
    actual fun setRefreshToken(token: String) { storage["refresh_token"] = token }
    actual fun getUserData(): String? = storage["user_data"]
    actual fun setUserData(data: String) { storage["user_data"] = data }
    actual fun clearAll() { storage.clear() }
}
