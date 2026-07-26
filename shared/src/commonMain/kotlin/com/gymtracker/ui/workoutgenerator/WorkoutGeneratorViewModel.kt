package com.gymtracker.ui.workoutgenerator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.data.model.*
import com.gymtracker.data.repository.WorkoutGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutGeneratorViewModel : ViewModel() {

    private val generator = WorkoutGenerator()

    private val _generatedWorkout = MutableStateFlow<WorkoutTemplate?>(null)
    val generatedWorkout: StateFlow<WorkoutTemplate?> = _generatedWorkout

    private val _weeklyPlan = MutableStateFlow<List<WorkoutTemplate>>(emptyList())
    val weeklyPlan: StateFlow<List<WorkoutTemplate>> = _weeklyPlan

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedEquipment = MutableStateFlow<List<Equipment>>(listOf(Equipment.NONE))
    val selectedEquipment: StateFlow<List<Equipment>> = _selectedEquipment

    private val _fitnessLevel = MutableStateFlow(Difficulty.BEGINNER)
    val fitnessLevel: StateFlow<Difficulty> = _fitnessLevel

    private val _focusArea = MutableStateFlow(MuscleRegion.FULL_BODY)
    val focusArea: StateFlow<MuscleRegion> = _focusArea

    private val _duration = MutableStateFlow(30)
    val duration: StateFlow<Int> = _duration

    fun updateEquipment(equipment: List<Equipment>) {
        _selectedEquipment.value = equipment
    }

    fun updateFitnessLevel(level: Difficulty) {
        _fitnessLevel.value = level
    }

    fun updateFocusArea(area: MuscleRegion) {
        _focusArea.value = area
    }

    fun updateDuration(minutes: Int) {
        _duration.value = minutes
    }

    fun generateWorkout() {
        viewModelScope.launch {
            _isLoading.value = true

            val request = WorkoutGenerationRequest(
                fitnessLevel = _fitnessLevel.value,
                availableEquipment = _selectedEquipment.value,
                targetMuscles = getMuscleGroupsForRegion(_focusArea.value),
                workoutCategory = getCategoryForRegion(_focusArea.value),
                durationMinutes = _duration.value,
                focusArea = _focusArea.value
            )

            _generatedWorkout.value = generator.generateWorkout(request)
            _isLoading.value = false
        }
    }

    fun generateWeeklyPlan(workoutDaysPerWeek: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            _weeklyPlan.value = generator.generateWeeklyPlan(
                fitnessLevel = _fitnessLevel.value,
                availableEquipment = _selectedEquipment.value,
                workoutDaysPerWeek = workoutDaysPerWeek
            )

            _isLoading.value = false
        }
    }

    private fun getMuscleGroupsForRegion(region: MuscleRegion): List<MuscleGroup> {
        return when (region) {
            MuscleRegion.UPPER -> listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.SHOULDERS, MuscleGroup.BICEPS, MuscleGroup.TRICEPS)
            MuscleRegion.LOWER -> listOf(MuscleGroup.QUADS, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CALVES)
            MuscleRegion.CORE -> listOf(MuscleGroup.ABS, MuscleGroup.OBLIQUES, MuscleGroup.LOWER_BACK)
            MuscleRegion.FULL_BODY -> MuscleGroup.entries.toList()
        }
    }

    private fun getCategoryForRegion(region: MuscleRegion): WorkoutCategory {
        return when (region) {
            MuscleRegion.UPPER -> WorkoutCategory.UPPER_BODY
            MuscleRegion.LOWER -> WorkoutCategory.LOWER_BODY
            MuscleRegion.CORE -> WorkoutCategory.CORE
            MuscleRegion.FULL_BODY -> WorkoutCategory.FULL_BODY
        }
    }
}
