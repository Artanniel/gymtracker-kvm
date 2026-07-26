package com.gymtracker.ui.onboarding

data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val weight: Double = 70.0,
    val height: Int = 170,
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    val goals: List<FitnessGoal> = emptyList(),
    val workoutDaysPerWeek: Int = 3,
    val preferredWorkoutTime: WorkoutTime = WorkoutTime.ANY,
    val hasCompletedOnboarding: Boolean = false
)

enum class FitnessLevel(val displayName: String, val description: String) {
    BEGINNER("Iniciante", "0-6 meses de treino"),
    INTERMEDIATE("Intermediário", "6-24 meses de treino"),
    ADVANCED("Avançado", "Mais de 2 anos de treino")
}

enum class FitnessGoal(val displayName: String, val icon: String) {
    LOSE_WEIGHT("Perder peso", "⚖️"),
    GAIN_MUSCLE("Ganhar massa muscular", "💪"),
    IMPROVE_ENDURANCE("Melhorar resistência", "🏃"),
    INCREASE_STRENGTH("Aumentar força", "🏋️"),
    STAY_HEALTHY("Manter saúde", "❤️"),
    IMPROVE_FLEXIBILITY("Melhorar flexibilidade", "🧘")
}

enum class WorkoutTime(val displayName: String, val icon: String) {
    MORNING("Manhã", "🌅"),
    AFTERNOON("Tarde", "☀️"),
    EVENING("Noite", "🌙"),
    ANY("Qualquer", "⏰")
}
