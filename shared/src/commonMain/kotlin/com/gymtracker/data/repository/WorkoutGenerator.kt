package com.gymtracker.data.repository

import com.gymtracker.data.model.*
import kotlinx.datetime.Clock

class WorkoutGenerator {

    private val exerciseDatabase = ExerciseDatabase

    fun generateWorkout(request: WorkoutGenerationRequest): WorkoutTemplate {
        val selectedExercises = selectExercises(request)

        return WorkoutTemplate(
            id = "generated_${Clock.System.now().toEpochMilliseconds()}",
            name = generateWorkoutName(request),
            description = generateDescription(request),
            category = request.workoutCategory,
            difficulty = request.fitnessLevel,
            duration = request.durationMinutes,
            exercises = selectedExercises,
            targetMuscles = request.targetMuscles,
            equipment = request.availableEquipment,
            caloriesBurned = estimateCalories(request, selectedExercises),
            tags = generateTags(request)
        )
    }

    private fun selectExercises(request: WorkoutGenerationRequest): List<WorkoutExercise> {
        val availableExercises = exerciseDatabase.exercises.filter { exercise ->
            val matchesEquipment = exercise.equipment.any { it in request.availableEquipment } ||
                    exercise.equipment.contains(Equipment.NONE)
            val matchesDifficulty = exercise.difficulty.level <= request.fitnessLevel.level + 1
            val matchesMuscle = exercise.muscleGroups.any { it in request.targetMuscles } ||
                    request.focusArea == MuscleRegion.FULL_BODY

            matchesEquipment && matchesDifficulty && matchesMuscle
        }

        val selected = mutableListOf<WorkoutExercise>()
        val usedExerciseIds = mutableSetOf<String>()

        // Ensure at least one exercise per target muscle group
        request.targetMuscles.forEach { muscleGroup ->
            val exercisesForMuscle = availableExercises.filter {
                it.muscleGroups.contains(muscleGroup) && it.id !in usedExerciseIds
            }

            if (exercisesForMuscle.isNotEmpty()) {
                val exercise = exercisesForMuscle.random()
                selected.add(createWorkoutExercise(exercise, request.fitnessLevel))
                usedExerciseIds.add(exercise.id)
            }
        }

        // Add more exercises based on duration
        val targetExerciseCount = when {
            request.durationMinutes <= 15 -> 4
            request.durationMinutes <= 30 -> 6
            request.durationMinutes <= 45 -> 8
            else -> 10
        }

        val remainingExercises = availableExercises.filter { it.id !in usedExerciseIds }

        while (selected.size < targetExerciseCount && remainingExercises.isNotEmpty()) {
            val exercise = remainingExercises.random()
            if (exercise.id !in usedExerciseIds) {
                selected.add(createWorkoutExercise(exercise, request.fitnessLevel))
                usedExerciseIds.add(exercise.id)
            }
        }

        return selected.shuffled()
    }

    private fun createWorkoutExercise(exercise: ExerciseDetail, difficulty: Difficulty): WorkoutExercise {
        val (sets, reps) = when (difficulty) {
            Difficulty.BEGINNER -> Pair(3, "10-12")
            Difficulty.INTERMEDIATE -> Pair(4, "8-12")
            Difficulty.ADVANCED -> Pair(4, "6-10")
            Difficulty.ELITE -> Pair(5, "4-8")
        }

        val restSeconds = when (exercise.category) {
            ExerciseCategory.STRENGTH -> 90
            ExerciseCategory.HIIT -> 30
            ExerciseCategory.CARDIO -> 15
            ExerciseCategory.PLYOMETRICS -> 60
            else -> 45
        }

        return WorkoutExercise(
            exercise = exercise,
            sets = sets,
            reps = reps,
            restSeconds = restSeconds,
            tempo = if (exercise.category == ExerciseCategory.STRENGTH) "2-1-2" else null
        )
    }

    private fun generateWorkoutName(request: WorkoutGenerationRequest): String {
        val muscleName = when (request.focusArea) {
            MuscleRegion.UPPER -> "Tren Superior"
            MuscleRegion.LOWER -> "Tren Inferior"
            MuscleRegion.CORE -> "Core"
            MuscleRegion.FULL_BODY -> "Corpo Inteiro"
        }

        val categoryName = when (request.workoutCategory) {
            WorkoutCategory.STRENGTH -> "Força"
            WorkoutCategory.CARDIO -> "Cardio"
            WorkoutCategory.HIIT -> "HIIT"
            WorkoutCategory.FULL_BODY -> "Completo"
            WorkoutCategory.UPPER_BODY -> "Superior"
            WorkoutCategory.LOWER_BODY -> "Inferior"
            WorkoutCategory.CORE -> "Core"
            WorkoutCategory.FLEXIBILITY -> "Flexibilidade"
            WorkoutCategory.METABOLIC -> "Metabólico"
        }

        return "$categoryName - $muscleName"
    }

    private fun generateDescription(request: WorkoutGenerationRequest): String {
        val difficultyText = when (request.fitnessLevel) {
            Difficulty.BEGINNER -> "para iniciantes"
            Difficulty.INTERMEDIATE -> "para intermediários"
            Difficulty.ADVANCED -> "para avançados"
            Difficulty.ELITE -> "para elite"
        }

        return "Treino personalizado $difficultyText, com foco em ${request.focusArea.displayName.lowercase()}. " +
                "Duração estimada: ${request.durationMinutes} minutos."
    }

    private fun estimateCalories(request: WorkoutGenerationRequest, exercises: List<WorkoutExercise>): Int {
        val baseCaloriesPerMinute = when (request.workoutCategory) {
            WorkoutCategory.STRENGTH -> 8
            WorkoutCategory.CARDIO -> 12
            WorkoutCategory.HIIT -> 14
            else -> 9
        }

        val difficultyMultiplier = when (request.fitnessLevel) {
            Difficulty.BEGINNER -> 0.8
            Difficulty.INTERMEDIATE -> 1.0
            Difficulty.ADVANCED -> 1.2
            Difficulty.ELITE -> 1.4
        }

        return (request.durationMinutes * baseCaloriesPerMinute * difficultyMultiplier).toInt()
    }

    private fun generateTags(request: WorkoutGenerationRequest): List<String> {
        val tags = mutableListOf<String>()

        tags.add(request.fitnessLevel.displayName.lowercase())
        tags.add(request.workoutCategory.displayName.lowercase())

        if (request.availableEquipment.contains(Equipment.NONE)) {
            tags.add("sem equipamento")
        }

        if (request.durationMinutes <= 20) {
            tags.add("rápido")
        }

        return tags
    }

    fun generateWeeklyPlan(
        fitnessLevel: Difficulty,
        availableEquipment: List<Equipment>,
        workoutDaysPerWeek: Int
    ): List<WorkoutTemplate> {
        val workouts = mutableListOf<WorkoutTemplate>()
        val muscleGroups = listOf(
            MuscleRegion.UPPER,
            MuscleRegion.LOWER,
            MuscleRegion.CORE,
            MuscleRegion.FULL_BODY
        )

        repeat(workoutDaysPerWeek) { day ->
            val focusArea = muscleGroups[day % muscleGroups.size]
            val category = when (focusArea) {
                MuscleRegion.UPPER -> WorkoutCategory.UPPER_BODY
                MuscleRegion.LOWER -> WorkoutCategory.LOWER_BODY
                MuscleRegion.CORE -> WorkoutCategory.CORE
                MuscleRegion.FULL_BODY -> WorkoutCategory.FULL_BODY
            }

            val request = WorkoutGenerationRequest(
                fitnessLevel = fitnessLevel,
                availableEquipment = availableEquipment,
                targetMuscles = when (focusArea) {
                    MuscleRegion.UPPER -> listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS)
                    MuscleRegion.LOWER -> listOf(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES)
                    MuscleRegion.CORE -> listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES)
                    MuscleRegion.FULL_BODY -> MuscleGroup.entries.toList()
                },
                workoutCategory = category,
                durationMinutes = 30 + (day % 3) * 10, // 30, 40, 50 minutes
                focusArea = focusArea
            )

            workouts.add(generateWorkout(request))
        }

        return workouts
    }
}
