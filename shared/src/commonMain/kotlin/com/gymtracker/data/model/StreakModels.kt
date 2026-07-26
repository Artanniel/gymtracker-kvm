package com.gymtracker.data.model

data class StreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastWorkoutDate: Long? = null,
    val totalWorkouts: Int = 0,
    val workoutsThisWeek: Int = 0,
    val isStreakActive: Boolean = false
)

enum class Badge(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val requirement: Int,
    val tier: BadgeTier
) {
    // Streak badges
    STREAK_3("streak_3", "Iniciante", "3 dias seguidos treinando", "🔥", 3, BadgeTier.BRONZE),
    STREAK_7("streak_7", "Dedicado", "7 dias seguidos treinando", "🔥", 7, BadgeTier.SILVER),
    STREAK_14("streak_14", "Comprometido", "14 dias seguidos treinando", "🔥", 14, BadgeTier.GOLD),
    STREAK_30("streak_30", "Lendário", "30 dias seguidos treinando", "🔥", 30, BadgeTier.DIAMOND),

    // Total workout badges
    WORKOUTS_5("workouts_5", "Primeiro Passo", "Complete 5 treinos", "💪", 5, BadgeTier.BRONZE),
    WORKOUTS_10("workouts_10", "Focado", "Complete 10 treinos", "💪", 10, BadgeTier.SILVER),
    WORKOUTS_25("workouts_25", "Atleta", "Complete 25 treinos", "💪", 25, BadgeTier.GOLD),
    WORKOUTS_50("workouts_50", "Mestre", "Complete 50 treinos", "💪", 50, BadgeTier.DIAMOND),
    WORKOUTS_100("workouts_100", "Lenda", "Complete 100 treinos", "💪", 100, BadgeTier.DIAMOND),

    // Weekly badges
    WEEK_3("week_3", "Semana Perfeita", "Treine 3x numa semana", "📅", 3, BadgeTier.BRONZE),
    WEEK_5("week_5", "Semana Intensa", "Treine 5x numa semana", "📅", 5, BadgeTier.SILVER),

    // Special badges
    FIRST_WORKOUT("first_workout", "Começou Agora", "Complete seu primeiro treino", "🎯", 1, BadgeTier.BRONZE),
    MORNING_WORKOUT("morning_workout", "Madrugador", "Treine antes das 8h", "🌅", 1, BadgeTier.BRONZE),
    WEEKEND_WARRIOR("weekend_warrior", "Guerreiro", "Treine no fim de semana", "⚔️", 1, BadgeTier.BRONZE)
}

enum class BadgeTier(val displayName: String, val color: Long) {
    BRONZE("Bronze", 0xFFCD7F32),
    SILVER("Prata", 0xFFC0C0C0),
    GOLD("Ouro", 0xFFFFD700),
    DIAMOND("Diamante", 0xFFB9F2FF)
}

data class UserBadge(
    val badge: Badge,
    val unlockedAt: Long,
    val progress: Int = 0
)
