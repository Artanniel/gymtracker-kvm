package com.gymtracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.db.Workout_sessions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repo = AppDependencies.workoutRepo

    private val _sessions = MutableStateFlow<List<Workout_sessions>>(emptyList())
    val sessions: StateFlow<List<Workout_sessions>> = _sessions

    private val _sessionExercises = MutableStateFlow<Map<Long, List<Pair<String, String>>>>(emptyMap())
    val sessionExercises: StateFlow<Map<Long, List<Pair<String, String>>>> = _sessionExercises

    fun load() {
        viewModelScope.launch {
            repo.getAllSessionsFlow().collect { sessionList ->
                _sessions.value = sessionList
                val exerciseMap = mutableMapOf<Long, List<Pair<String, String>>>()
                sessionList.forEach { session ->
                    val exercises = repo.getExercisesBySession(session.id)
                    exerciseMap[session.id] = exercises
                }
                _sessionExercises.value = exerciseMap
            }
        }
    }
}
