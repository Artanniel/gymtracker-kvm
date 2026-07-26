package com.gymtracker.data.model

data class ExerciseProgress(
    val exerciseId: String,
    val exerciseName: String,
    val dataPoints: List<DataPoint>,
    val suggestion: SuggestedLoad?
) {
    data class DataPoint(
        val weightKg: Double,
        val repsActual: Int,
        val sessionDate: Long
    )

    companion object {
        val Empty = ExerciseProgress("", "", emptyList(), null)
    }
}

data class SuggestedLoad(
    val suggestedWeightKg: Double,
    val suggestedReps: Int,
    val strategy: String,
    val confidence: Confidence
) {
    enum class Confidence { LOW, MEDIUM, HIGH }
}