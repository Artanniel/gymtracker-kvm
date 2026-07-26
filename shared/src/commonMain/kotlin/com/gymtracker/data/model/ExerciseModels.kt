package com.gymtracker.data.model

data class ExerciseDetail(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val muscleGroups: List<MuscleGroup>,
    val equipment: List<Equipment>,
    val difficulty: Difficulty,
    val instructions: String,
    val tips: String,
    val videoUrl: String? = null
)

enum class ExerciseCategory(val displayName: String) {
    STRENGTH("Força"),
    CARDIO("Cardio"),
    HIIT("HIIT"),
    FLEXIBILITY("Flexibilidade"),
    PLYOMETRICS("Pliometria"),
    BODYWEIGHT("Peso Corporal")
}

enum class MuscleGroup(val displayName: String, val region: MuscleRegion) {
    // Upper Body
    CHEST("Peito", MuscleRegion.UPPER),
    SHOULDERS("Ombros", MuscleRegion.UPPER),
    BICEPS("Bíceps", MuscleRegion.UPPER),
    TRICEPS("Tríceps", MuscleRegion.UPPER),
    BACK("Costas", MuscleRegion.UPPER),
    FOREARMS("Antebraços", MuscleRegion.UPPER),

    // Core
    ABS("Abdômen", MuscleRegion.CORE),
    OBLIQUES("Oblíquos", MuscleRegion.CORE),
    LOWER_BACK("Lombar", MuscleRegion.CORE),

    // Lower Body
    QUADS("Quadríceps", MuscleRegion.LOWER),
    HAMSTRINGS("Posterior", MuscleRegion.LOWER),
    GLUTES("Glúteos", MuscleRegion.LOWER),
    CALVES("Panturrilhas", MuscleRegion.LOWER),
    HIP_FLEXORS("Flexores do Quadril", MuscleRegion.LOWER)
}

enum class MuscleRegion(val displayName: String) {
    UPPER("Tren Superior"),
    CORE("Core"),
    LOWER("Tren Inferior"),
    FULL_BODY("Corpo Inteiro")
}

enum class Equipment(val displayName: String) {
    NONE("Sem equipamento"),
    DUMBBELLS("Halteres"),
    BARBELL("Barra"),
    KETTLEBELL("Kettlebell"),
    PULL_UP_BAR("Barra de Pull-up"),
    RESISTANCE_BAND("Elástico"),
    MACHINE("Máquina"),
    CABLE("Polia"),
    SWISS_BALL("Suíço"),
    BENCH("Banco")
}

enum class Difficulty(val displayName: String, val level: Int) {
    BEGINNER("Iniciante", 1),
    INTERMEDIATE("Intermediário", 2),
    ADVANCED("Avançado", 3),
    ELITE("Elite", 4)
}

data class WorkoutTemplate(
    val id: String,
    val name: String,
    val description: String,
    val category: WorkoutCategory,
    val difficulty: Difficulty,
    val duration: Int, // minutes
    val exercises: List<WorkoutExercise>,
    val targetMuscles: List<MuscleGroup>,
    val equipment: List<Equipment>,
    val caloriesBurned: Int,
    val tags: List<String>
)

enum class WorkoutCategory(val displayName: String) {
    STRENGTH("Força"),
    CARDIO("Cardio"),
    HIIT("HIIT"),
    FULL_BODY("Corpo Inteiro"),
    UPPER_BODY("Tren Superior"),
    LOWER_BODY("Tren Inferior"),
    CORE("Core"),
    FLEXIBILITY("Flexibilidade"),
    METABOLIC("Metabólico")
}

data class WorkoutExercise(
    val exercise: ExerciseDetail,
    val sets: Int,
    val reps: String, // "8-12" or "30s" for time-based
    val restSeconds: Int,
    val tempo: String? = null,
    val notes: String? = null
)

data class WorkoutGenerationRequest(
    val fitnessLevel: Difficulty,
    val availableEquipment: List<Equipment>,
    val targetMuscles: List<MuscleGroup>,
    val workoutCategory: WorkoutCategory,
    val durationMinutes: Int,
    val focusArea: MuscleRegion
)
