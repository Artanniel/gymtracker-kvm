package com.gymtracker.data.notification

expect class NotificationScheduler() {
    fun scheduleWorkoutReminder(hour: Int, minute: Int)
    fun scheduleWaterReminder(intervalHours: Int)
    fun cancelWorkoutReminders()
    fun cancelWaterReminders()
    fun requestPermission()
}

enum class NotificationType {
    WORKOUT_REMINDER,
    WATER_REMINDER,
    STREAK_WARNING,
    ACHIEVEMENT,
    WEEKLY_SUMMARY
}
