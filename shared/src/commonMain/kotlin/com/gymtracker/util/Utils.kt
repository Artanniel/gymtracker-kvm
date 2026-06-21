package com.gymtracker.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

fun formatTimer(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

fun todayKey(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${now.year}-${now.monthNumber.toString().padStart(2, '0')}-${now.dayOfMonth.toString().padStart(2, '0')}"
}

fun daysSince(epochMs: Long): Long {
    val nowMs = Clock.System.now().toEpochMilliseconds()
    return abs(nowMs - epochMs) / (1000L * 60 * 60 * 24)
}

fun formatDaysAgo(epochMs: Long?): String {
    if (epochMs == null) return "Nunca treinado"
    return when (val d = daysSince(epochMs)) {
        0L -> "Hoje"
        1L -> "Ontem"
        else -> "Há $d dias"
    }
}

sealed class AvailabilityStatus {
    data class Available(val ideal: Boolean) : AvailabilityStatus()
    data class Waiting(val daysLeft: Long) : AvailabilityStatus()
    object NeverDone : AvailabilityStatus()
}

fun checkAvailability(lastMs: Long?, minDays: Int, idealDays: Int): AvailabilityStatus {
    if (lastMs == null) return AvailabilityStatus.NeverDone
    val d = daysSince(lastMs)
    return when {
        d >= idealDays -> AvailabilityStatus.Available(true)
        d >= minDays   -> AvailabilityStatus.Available(false)
        else           -> AvailabilityStatus.Waiting(minDays - d)
    }
}
