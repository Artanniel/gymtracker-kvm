package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.model.*
import app.cash.sqldelight.async.coroutines.awaitAsList
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class StreakRepository {

    private val db get() = DatabaseProvider.get()
    private val sessionQ get() = db.workoutSessionQueries

    suspend fun getStreakData(): StreakData {
        val sessions = sessionQ.getAll().awaitAsList()
        if (sessions.isEmpty()) return StreakData()

        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date
        val sessionsDates = sessions
            .map { session ->
                Instant.fromEpochMilliseconds(session.date).toLocalDateTime(timeZone).date
            }
            .distinct()
            .sortedDescending()

        val totalWorkouts = sessions.size

        // Calculate current streak
        var currentStreak = 0
        var checkDate = today
        var streakActive = false

        for (date in sessionsDates) {
            if (date == checkDate) {
                currentStreak++
                streakActive = true
                checkDate = checkDate.minus(1, DateTimeUnit.DAY)
            } else if (date == checkDate.minus(1, DateTimeUnit.DAY)) {
                // Allow gap of one day (rest day)
                currentStreak++
                checkDate = date.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }

        // Check if streak is still active (today or yesterday)
        val lastWorkoutDate = sessionsDates.first()
        val daysSinceLastWorkout = lastWorkoutDate.daysUntil(today)
        streakActive = daysSinceLastWorkout <= 1

        // Calculate longest streak
        val longestStreak = calculateLongestStreak(sessionsDates)

        // Calculate workouts this week
        val weekStart = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        val workoutsThisWeek = sessionsDates.count { it >= weekStart }

        return StreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastWorkoutDate = sessions.first().date,
            totalWorkouts = totalWorkouts,
            workoutsThisWeek = workoutsThisWeek,
            isStreakActive = streakActive
        )
    }

    private fun calculateLongestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        var longest = 1
        var current = 1

        for (i in 1 until dates.size) {
            val diff = dates[i - 1].daysUntil(dates[i])
            if (diff <= 1) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }

        return longest
    }

    suspend fun getUnlockedBadges(): List<UserBadge> {
        val streakData = getStreakData()
        val now = Clock.System.now().toEpochMilliseconds()
        val unlocked = mutableListOf<UserBadge>()

        // Check streak badges
        Badge.entries.filter { it.id.startsWith("streak_") }.forEach { badge ->
            if (streakData.currentStreak >= badge.requirement || streakData.longestStreak >= badge.requirement) {
                unlocked.add(UserBadge(badge, now, streakData.currentStreak))
            }
        }

        // Check total workout badges
        Badge.entries.filter { it.id.startsWith("workouts_") }.forEach { badge ->
            if (streakData.totalWorkouts >= badge.requirement) {
                unlocked.add(UserBadge(badge, now, streakData.totalWorkouts))
            }
        }

        // Check weekly badges
        Badge.entries.filter { it.id.startsWith("week_") }.forEach { badge ->
            if (streakData.workoutsThisWeek >= badge.requirement) {
                unlocked.add(UserBadge(badge, now, streakData.workoutsThisWeek))
            }
        }

        // Check first workout badge
        if (streakData.totalWorkouts >= 1) {
            unlocked.add(UserBadge(Badge.FIRST_WORKOUT, now, 1))
        }

        return unlocked
    }

    suspend fun getBadgeProgress(): Map<Badge, Int> {
        val streakData = getStreakData()
        val progress = mutableMapOf<Badge, Int>()

        Badge.entries.forEach { badge ->
            val current = when {
                badge.id.startsWith("streak_") -> streakData.currentStreak
                badge.id.startsWith("workouts_") -> streakData.totalWorkouts
                badge.id.startsWith("week_") -> streakData.workoutsThisWeek
                badge.id == "first_workout" -> if (streakData.totalWorkouts >= 1) 1 else 0
                else -> 0
            }
            progress[badge] = if (current > badge.requirement) badge.requirement else current
        }

        return progress
    }
}
