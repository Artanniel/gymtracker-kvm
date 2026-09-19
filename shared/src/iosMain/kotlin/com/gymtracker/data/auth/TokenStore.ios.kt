package com.gymtracker.data.auth

import platform.Foundation.NSUserDefaults
import platform.Foundation.standardUserDefaults

actual class TokenStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getToken(): String? = defaults.stringForKey("jwt_token")
    actual fun setToken(token: String) { defaults.setObject(token, forKey = "jwt_token") }
    actual fun getRefreshToken(): String? = defaults.stringForKey("refresh_token")
    actual fun setRefreshToken(token: String) { defaults.setObject(token, forKey = "refresh_token") }
    actual fun getUserData(): String? = defaults.stringForKey("user_data")
    actual fun setUserData(data: String) { defaults.setObject(data, forKey = "user_data") }
    actual fun clearAll() {
        defaults.removeObjectForKey("jwt_token")
        defaults.removeObjectForKey("refresh_token")
        defaults.removeObjectForKey("user_data")
    }
}
