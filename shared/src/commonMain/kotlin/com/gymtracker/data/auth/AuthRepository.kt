package com.gymtracker.data.auth

import com.gymtracker.data.sync.KtorApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AuthTokens(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("expires_in") val expiresIn: Int = 0,
    @SerialName("token_type") val tokenType: String = "",
    @SerialName("id_token") val idToken: String = ""
)

@Serializable
data class UserData(
    val id: String = "",
    val email: String = "",
    val name: String = ""
)

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val tokens: AuthTokens, val user: UserData) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(
    private val tokenStore: TokenStore,
    private val apiClient: KtorApiClient
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun hasStoredToken(): Boolean {
        return !tokenStore.getToken().isNullOrBlank()
    }

    fun getStoredToken(): String? = tokenStore.getToken()

    fun getStoredRefreshToken(): String? = tokenStore.getRefreshToken()

    fun getStoredUser(): UserData? {
        val data = tokenStore.getUserData() ?: return null
        return try {
            json.decodeFromString<UserData>(data)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(email: String, password: String): AuthState {
        _authState.value = AuthState.Loading

        return try {
            val response = apiClient.login(email, password)
            val tokens = json.decodeFromString<AuthTokens>(response)

            tokenStore.setToken(tokens.accessToken)
            if (tokens.refreshToken.isNotBlank()) {
                tokenStore.setRefreshToken(tokens.refreshToken)
            }

            val user = UserData(id = email, email = email, name = email.substringBefore("@"))
            tokenStore.setUserData(json.encodeToString(UserData.serializer(), user))

            val state = AuthState.Success(tokens, user)
            _authState.value = state
            _isLoggedIn.value = true
            state
        } catch (e: Exception) {
            val state = AuthState.Error(e.message ?: "Login failed")
            _authState.value = state
            state
        }
    }

    suspend fun register(name: String, email: String, password: String): AuthState {
        _authState.value = AuthState.Loading

        return try {
            apiClient.register(name, email, password)
            login(email, password)
        } catch (e: Exception) {
            val state = AuthState.Error(e.message ?: "Registration failed")
            _authState.value = state
            state
        }
    }

    suspend fun refresh(): Boolean {
        val refreshToken = tokenStore.getRefreshToken() ?: return false

        return try {
            val response = apiClient.refreshToken(refreshToken)
            val tokens = json.decodeFromString<AuthTokens>(response)

            tokenStore.setToken(tokens.accessToken)
            if (tokens.refreshToken.isNotBlank()) {
                tokenStore.setRefreshToken(tokens.refreshToken)
            }

            _isLoggedIn.value = true
            true
        } catch (e: Exception) {
            logout()
            false
        }
    }

    fun logout() {
        tokenStore.clearAll()
        _authState.value = AuthState.Idle
        _isLoggedIn.value = false
    }

    fun restoreSession(): Boolean {
        val token = tokenStore.getToken()
        if (token.isNullOrBlank()) return false

        val user = getStoredUser() ?: return false
        val tokens = AuthTokens(accessToken = token, refreshToken = tokenStore.getRefreshToken() ?: "")

        _authState.value = AuthState.Success(tokens, user)
        _isLoggedIn.value = true
        return true
    }
}
