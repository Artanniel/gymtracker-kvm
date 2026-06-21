package com.gymtracker.ui.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DietViewModel : ViewModel() {
    private val repo = AppDependencies.dietRepo

    private val _checklist = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val checklist: StateFlow<Map<String, Boolean>> = _checklist

    fun load() {
        viewModelScope.launch { _checklist.value = repo.getTodayChecklist() }
    }

    fun toggle(itemId: String, value: Boolean) {
        viewModelScope.launch {
            repo.toggle(itemId, value)
            _checklist.value = _checklist.value + (itemId to value)
        }
    }
}
