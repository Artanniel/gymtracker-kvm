package com.gymtracker.data.notification

import com.gymtracker.data.auth.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PushNotificationManager(
    private val tokenStore: TokenStore
) {
    companion object {
        const val PUSH_TOKEN_KEY = "push_token"
        const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
        const val WORKOUT_REMINDER_KEY = "workout_reminder_time"
        const val WATER_REMINDER_KEY = "water_reminder_enabled"
    }

    private val _pushToken = MutableStateFlow<String?>(null)
    val pushToken: StateFlow<String?> = _pushToken.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init {
        _pushToken.value = tokenStore.getToken() // reuse token store temporarily
        // In real impl, use separate keys
    }

    fun setPushToken(token: String) {
        tokenStore.setToken(token)
        _pushToken.value = token
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun getPushToken(): String? = _pushToken.value
}
