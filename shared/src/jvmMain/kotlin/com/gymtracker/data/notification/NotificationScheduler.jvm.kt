package com.gymtracker.data.notification

actual class NotificationScheduler actual constructor() {
    actual fun scheduleWorkoutReminder(hour: Int, minute: Int) {
        println("[JVM] Workout reminder scheduled for $hour:$minute")
    }

    actual fun scheduleWaterReminder(intervalHours: Int) {
        println("[JVM] Water reminder scheduled every $intervalHours hours")
    }

    actual fun cancelWorkoutReminders() {
        println("[JVM] Workout reminders cancelled")
    }

    actual fun cancelWaterReminders() {
        println("[JVM] Water reminders cancelled")
    }

    actual fun requestPermission() {
        println("[JVM] Notification permission requested")
    }
}
