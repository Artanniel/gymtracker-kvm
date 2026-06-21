package com.gymtracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.GoalType
import com.gymtracker.data.model.WorkoutData
import com.gymtracker.data.repository.GoalState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val workoutRepo = AppDependencies.workoutRepo
    private val goalRepo    = AppDependencies.goalRepo

    private val _lastDates = MutableStateFlow<Map<String, Long?>>(emptyMap())
    val lastDates: StateFlow<Map<String, Long?>> = _lastDates

    private val _goals = MutableStateFlow<List<GoalState>>(emptyList())
    val goals: StateFlow<List<GoalState>> = _goals

    fun load() {
        viewModelScope.launch {
            val map = mutableMapOf<String, Long?>()
            WorkoutData.allWorkouts.forEach { w ->
                map[w.id] = workoutRepo.getLastSession(w.id)?.date
            }
            _lastDates.value = map
            _goals.value = GoalType.entries.map { goalRepo.getTodayState(it) }
        }
    }

    fun saveGoal(type: GoalType, target: Int, actual: Int) {
        viewModelScope.launch {
            goalRepo.saveTodayValue(type, target, actual)
            _goals.value = GoalType.entries.map { goalRepo.getTodayState(it) }
        }
    }

    fun updateTarget(type: GoalType, newTarget: Int) {
        viewModelScope.launch {
            goalRepo.updateTarget(type, newTarget)
            _goals.value = GoalType.entries.map { goalRepo.getTodayState(it) }
        }
    }
}
