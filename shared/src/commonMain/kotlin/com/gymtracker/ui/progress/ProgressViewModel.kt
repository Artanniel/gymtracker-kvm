package com.gymtracker.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.ExerciseProgress
import com.gymtracker.data.model.WorkoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val repo = AppDependencies.workoutRepo

    private val _exercises = MutableStateFlow<List<ExerciseEntry>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntry>> = _exercises

    private val _selectedProgress = MutableStateFlow<ExerciseProgress?>(null)
    val selectedProgress: StateFlow<ExerciseProgress?> = _selectedProgress

    fun loadExercises() {
        _exercises.value = WorkoutData.allWorkouts.flatMap { workout ->
            workout.exercises.map { ExerciseEntry(it.id, it.name, workout.name) }
        }
    }

    fun selectExercise(exerciseId: String, exerciseName: String) {
        viewModelScope.launch {
            _selectedProgress.value = repo.getExerciseProgress(exerciseId, exerciseName)
        }
    }

    fun clearSelection() {
        _selectedProgress.value = null
    }
}

data class ExerciseEntry(
    val id: String,
    val name: String,
    val workoutName: String
)