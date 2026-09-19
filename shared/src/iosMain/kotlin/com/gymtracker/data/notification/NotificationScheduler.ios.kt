package com.gymtracker.data.notification

actual class NotificationScheduler actual constructor() {
    actual fun scheduleWorkoutReminder(hour: Int, minute: Int) {
        println("[iOS] Workout reminder scheduled for $hour:$minute")
    }

    actual fun scheduleWaterReminder(intervalHours: Int) {
        println("[iOS] Water reminder scheduled every $intervalHours hours")
    }

    actual fun cancelWorkoutReminders() {
        println("[iOS] Workout reminders cancelled")
    }

    actual fun cancelWaterReminders() {
        println("[iOS] Water reminders cancelled")
    }

    actual fun requestPermission() {
        println("[iOS] Notification permission requested")
    }
}
