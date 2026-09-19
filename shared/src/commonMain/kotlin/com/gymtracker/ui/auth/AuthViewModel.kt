package com.gymtracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.data.auth.AuthRepository
import com.gymtracker.data.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        _registerUiState.value = _registerUiState.value.copy(isLoading = true, error = null)
                    }
                    is AuthState.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                        _registerUiState.value = _registerUiState.value.copy(isLoading = false, error = null)
                    }
                    is AuthState.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = state.message)
                        _registerUiState.value = _registerUiState.value.copy(isLoading = false, error = state.message)
                    }
                    is AuthState.Idle -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _registerUiState.value = _registerUiState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(name, email, password)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        _registerUiState.value = _registerUiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
