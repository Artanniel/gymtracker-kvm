package com.gymtracker.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.db.Students
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentsViewModel : ViewModel() {

    private val repo = AppDependencies.studentRepo

    private val _students = MutableStateFlow<List<Students>>(emptyList())
    val students: StateFlow<List<Students>> = _students.asStateFlow()

    private val _selectedStudent = MutableStateFlow<Students?>(null)
    val selectedStudent: StateFlow<Students?> = _selectedStudent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getAllStudentsFlow().collect { list ->
                _students.value = list
                _isLoading.value = false
            }
        }
    }

    fun loadStudent(id: Long) {
        viewModelScope.launch {
            _selectedStudent.value = repo.getStudentById(id)
        }
    }

    fun createStudent(name: String, email: String?, phone: String?, birthDate: Long?, notes: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.createStudent(name, email, phone, birthDate, notes)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar aluno"
            }
            _isLoading.value = false
        }
    }

    fun updateStudent(id: Long, name: String, email: String?, phone: String?, birthDate: Long?, notes: String, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.updateStudent(id, name, email, phone, birthDate, notes, isActive)
                _selectedStudent.value = repo.getStudentById(id)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar aluno"
            }
            _isLoading.value = false
        }
    }

    fun deleteStudent(id: Long) {
        viewModelScope.launch {
            try {
                repo.deleteStudent(id)
                _selectedStudent.value = null
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao excluir aluno"
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                repo.getAllStudentsFlow().collect { _students.value = it }
            } else {
                _students.value = repo.searchStudents(query)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
