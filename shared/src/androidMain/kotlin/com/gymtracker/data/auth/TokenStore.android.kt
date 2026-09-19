package com.gymtracker.data.auth

import android.content.Context
import android.content.SharedPreferences

actual class TokenStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "gymtracker_prefs", Context.MODE_PRIVATE
    )

    actual fun getToken(): String? = prefs.getString("jwt_token", null)
    actual fun setToken(token: String) { prefs.edit().putString("jwt_token", token).apply() }
    actual fun getRefreshToken(): String? = prefs.getString("refresh_token", null)
    actual fun setRefreshToken(token: String) { prefs.edit().putString("refresh_token", token).apply() }
    actual fun getUserData(): String? = prefs.getString("user_data", null)
    actual fun setUserData(data: String) { prefs.edit().putString("user_data", data).apply() }
    actual fun clearAll() { prefs.edit().clear().apply() }
}
