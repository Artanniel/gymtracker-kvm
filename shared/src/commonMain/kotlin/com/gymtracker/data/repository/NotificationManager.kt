package com.gymtracker.data.repository

import com.gymtracker.data.model.*
import kotlinx.datetime.Clock

class NotificationManager {

    private val notifications = mutableListOf<AppNotification>()
    private var settings = NotificationSettings()

    fun getSettings(): NotificationSettings = settings

    fun updateSettings(newSettings: NotificationSettings) {
        settings = newSettings
    }

    fun getNotifications(): List<AppNotification> = notifications.sortedByDescending { it.timestamp }

    fun getUnreadCount(): Int = notifications.count { !it.read }

    fun markAsRead(id: String) {
        val index = notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            val notification = notifications[index]
            notifications[index] = notification.copy(read = true)
        }
    }

    fun markAllAsRead() {
        val updated = notifications.map { it.copy(read = true) }
        notifications.clear()
        notifications.addAll(updated)
    }

    fun addNotification(notification: AppNotification) {
        notifications.add(0, notification)
        if (notifications.size > 50) {
            notifications.removeAt(notifications.lastIndex)
        }
    }

    fun createWorkoutReminder(workoutName: String) {
        addNotification(
            AppNotification(
                id = "workout_${Clock.System.now().toEpochMilliseconds()}",
                type = NotificationType.WORKOUT_REMINDER,
                title = settings.reminderMessage,
                message = "Hora de treinar: $workoutName",
                timestamp = Clock.System.now().toEpochMilliseconds(),
                actionUrl = "workout"
            )
        )
    }

    fun createStreakWarning(daysRemaining: Int) {
        addNotification(
            AppNotification(
                id = "streak_${Clock.System.now().toEpochMilliseconds()}",
                type = NotificationType.STREAK_WARNING,
                title = "Sua sequência está em risco!",
                message = "Faltam $daysRemaining dias para perder sua sequência. Treine hoje!",
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    fun createAchievementUnlocked(badge: Badge) {
        addNotification(
            AppNotification(
                id = "achievement_${badge.id}_${Clock.System.now().toEpochMilliseconds()}",
                type = NotificationType.ACHIEVEMENT_UNLOCKED,
                title = "Conquista desbloqueada!",
                message = "${badge.icon} ${badge.displayName} - ${badge.description}",
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    fun createWeeklySummary(workoutsCompleted: Int, streakDays: Int) {
        val message = when {
            workoutsCompleted == 0 -> "Você não treinou esta semana. Vamos começar?"
            workoutsCompleted < 3 -> "Você treinou $workoutsCompleted vezes esta semana. Continue!"
            else -> "Parabéns! Você treinou $workoutsCompleted vezes esta semana!"
        }

        addNotification(
            AppNotification(
                id = "summary_${Clock.System.now().toEpochMilliseconds()}",
                type = NotificationType.WEEKLY_SUMMARY,
                title = "Resumo Semanal",
                message = message,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    fun createRestDayReminder() {
        addNotification(
            AppNotification(
                id = "rest_${Clock.System.now().toEpochMilliseconds()}",
                type = NotificationType.REST_DAY_REMINDER,
                title = "Dia de descanso",
                message = "Lembre-se: o descanso é essencial para o crescimento muscular!",
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }
}
