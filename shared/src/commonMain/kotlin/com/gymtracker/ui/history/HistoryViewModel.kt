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

    fun load() {
        viewModelScope.launch {
            repo.getAllSessionsFlow().collect { _sessions.value = it }
        }
    }
}
