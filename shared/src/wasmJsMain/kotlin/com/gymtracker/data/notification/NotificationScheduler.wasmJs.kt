package com.gymtracker.data.notification

actual class NotificationScheduler actual constructor() {
    actual fun scheduleWorkoutReminder(hour: Int, minute: Int) {
        println("[Wasm] Workout reminder scheduled for $hour:$minute")
    }

    actual fun scheduleWaterReminder(intervalHours: Int) {
        println("[Wasm] Water reminder scheduled every $intervalHours hours")
    }

    actual fun cancelWorkoutReminders() {
        println("[Wasm] Workout reminders cancelled")
    }

    actual fun cancelWaterReminders() {
        println("[Wasm] Water reminders cancelled")
    }

    actual fun requestPermission() {
        println("[Wasm] Notification permission requested")
    }
}
