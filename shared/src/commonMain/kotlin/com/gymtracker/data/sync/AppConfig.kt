package com.gymtracker.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Configuração do backend para sincronização.
 * 
 * Suporta:
 * 1. URL dinâmica (Railway deploy)
 * 2. Configuração local (desenvolvimento)
 * 3. Fallback para localhost
 */
object AppConfig {
    
    private val _apiUrl = MutableStateFlow(DEFAULT_API_URL)
    val apiUrl: StateFlow<String> = _apiUrl.asStateFlow()
    
    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()
    
    /**
     * Configura URL do backend
     */
    fun configure(url: String) {
        val cleanUrl = url.trimEnd('/')
        _apiUrl.value = cleanUrl
        _isConfigured.value = true
    }
    
    /**
     * Configura URL do backend com ambiente
     */
    fun configure(environment: Environment) {
        val url = when (environment) {
            Environment.DEVELOPMENT -> "http://localhost:8082"
            Environment.STAGING -> "https://gymtracker-staging.railway.app"
            Environment.PRODUCTION -> "https://gymtracker.railway.app"
        }
        configure(url)
    }
    
    /**
     * Retorna URL da API para endpoint específico
     */
    fun getApiEndpoint(endpoint: String): String {
        return "${_apiUrl.value}/api/$endpoint"
    }
    
    /**
     * Retorna URL base da API
     */
    fun getBaseUrl(): String = _apiUrl.value
    
    /**
     * Verifica se backend está configurado
     */
    fun isBackendConfigured(): Boolean = _isConfigured.value
    
    /**
     * Reseta configuração
     */
    fun reset() {
        _apiUrl.value = DEFAULT_API_URL
        _isConfigured.value = false
    }
    
    const val DEFAULT_API_URL = "http://localhost:8082"
    
    enum class Environment {
        DEVELOPMENT,
        STAGING,
        PRODUCTION
    }
}

/**
 * Configurações de sincronização
 */
object SyncConfig {
    
    /**
     * Intervalo de sync automático (em millis)
     */
    const val AUTO_SYNC_INTERVAL = 30_000L // 30 segundos
    
    /**
     * Máximo de tentativas antes de marcar como falha
     */
    const val MAX_RETRY_ATTEMPTS = 3
    
    /**
     * Timeout para requests HTTP (em millis)
     */
    const val REQUEST_TIMEOUT = 10_000L // 10 segundos
    
    /**
     * Limpar operações sincronizadas após X dias
     */
    const val CLEANUP_DAYS = 7
    
    /**
     * Habilitar sync automático
     */
    const val AUTO_SYNC_ENABLED = true
    
    /**
     * Habilitar pull automático ao reconectar
     */
    const val AUTO_PULL_ON_RECONNECT = true
}

/**
 * Configurações de autenticação
 */
object AuthConfig {
    
    /**
     * Chave para armazenar token JWT localmente
     */
    const val TOKEN_KEY = "gymtracker_jwt_token"
    
    /**
     * Chave para armazenar refresh token
     */
    const val REFRESH_TOKEN_KEY = "gymtracker_refresh_token"
    
    /**
     * Chave para armazenar dados do usuário
     */
    const val USER_DATA_KEY = "gymtracker_user_data"
    
    /**
     * Tempo máximo de validade do token (em millis)
     * 7 dias
     */
    const val TOKEN_MAX_AGE = 7 * 24 * 60 * 60 * 1000L
    
    /**
     * Tempo antes do token expirar para fazer refresh (em millis)
     * 1 dia
     */
    const val REFRESH_BEFORE_EXPIRY = 24 * 60 * 60 * 1000L
}
