package com.gymtracker.data.model

data class NotificationSettings(
    val workoutReminders: Boolean = true,
    val streakReminders: Boolean = true,
    val achievementAlerts: Boolean = true,
    val reminderTime: String = "18:00",
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5), // Mon-Fri
    val reminderMessage: String = "Hora de treinar!"
)

enum class NotificationType(val channel: String, val defaultTitle: String) {
    WORKOUT_REMINDER("workouts", "Lembrete de Treino"),
    STREAK_WARNING("streaks", "Sequência em Risco!"),
    ACHIEVEMENT_UNLOCKED("achievements", "Conquista Desbloqueada!"),
    WEEKLY_SUMMARY("summaries", "Resumo Semanal"),
    REST_DAY_REMINDER("rest", "Dia de Descanso")
}

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long,
    val read: Boolean = false,
    val actionUrl: String? = null
)
