package com.gymtracker.data.model

data class Workout(
    val id: String,
    val name: String,
    val shortName: String,
    val exercises: List<Exercise>,
    val isFlexible: Boolean = false,
    val minRestDays: Int = 0,
    val idealRestDays: Int = 0,
    val tip: String? = null
)

data class Exercise(
    val id: String,
    val workoutId: String,
    val name: String,
    val sets: List<SetDefinition>
)

data class SetDefinition(
    val setNumber: Int,
    val type: SetType,
    val repsTarget: String
)

enum class SetType { WARMUP, WORKING, HARD }

enum class ProgressStatus { NONE, UP, SAME, DOWN }

enum class GoalType(
    val id: String,
    val displayName: String,
    val unit: String,
    val icon: String,
    val defaultTarget: Int,
    val step: Int
) {
    STEPS("steps", "Passos Diários", "passos", "🚶", 10_000, 500),
    SWIMMING("swimming", "Natação", "metros", "🏊", 1_000, 100),
    WATER("water", "Água", "ml", "💧", 2_500, 250);

    companion object {
        fun fromId(id: String) = entries.find { it.id == id }
    }
}
