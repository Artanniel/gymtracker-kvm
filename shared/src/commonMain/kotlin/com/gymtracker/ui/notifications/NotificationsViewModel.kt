package com.gymtracker.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.AppNotification
import com.gymtracker.data.model.NotificationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val notificationManager = AppDependencies.notificationManager

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _settings = MutableStateFlow(NotificationSettings())
    val settings: StateFlow<NotificationSettings> = _settings

    fun load() {
        viewModelScope.launch {
            _notifications.value = notificationManager.getNotifications()
            _unreadCount.value = notificationManager.getUnreadCount()
            _settings.value = notificationManager.getSettings()
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationManager.markAsRead(id)
            _notifications.value = notificationManager.getNotifications()
            _unreadCount.value = notificationManager.getUnreadCount()
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationManager.markAllAsRead()
            _notifications.value = notificationManager.getNotifications()
            _unreadCount.value = notificationManager.getUnreadCount()
        }
    }

    fun updateSettings(newSettings: NotificationSettings) {
        viewModelScope.launch {
            notificationManager.updateSettings(newSettings)
            _settings.value = newSettings
        }
    }
}
