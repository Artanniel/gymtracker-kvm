package com.gymtracker.data.sync

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetAddress
import java.net.URL

/**
 * Implementação Desktop (JVM) do ConnectivityMonitor.
 * Usa ping periódico e DNS lookup para verificar conectividade.
 */
actual class ConnectivityMonitor {
    
    private val _isOnline = MutableStateFlow(false)
    actual val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var checkJob: Job? = null
    
    // Endpoints para verificar conectividade
    private val healthEndpoints = listOf(
        "https://www.google.com",
        "https://www.cloudflare.com",
        "https://1.1.1.1"
    )
    
    init {
        // Verifica estado inicial
        _isOnline.value = checkCurrentState()
        
        // Inicia polling periódico (a cada 30 segundos)
        startPeriodicCheck()
    }
    
    private fun startPeriodicCheck() {
        checkJob = scope.launch {
            while (isActive) {
                delay(30_000) // 30 segundos
                _isOnline.value = checkCurrentState()
            }
        }
    }
    
    private fun checkCurrentState(): Boolean {
        return try {
            // Tenta resolver DNS (mais rápido que HTTP)
            val inetAddress = InetAddress.getByName("1.1.1.1")
            inetAddress.isReachable(3000) // 3 segundos timeout
        } catch (e: Exception) {
            // Fallback: tenta conectar em um endpoint
            try {
                val url = URL(healthEndpoints.first())
                val connection = url.openConnection()
                connection.connectTimeout = 3000
                connection.connect()
                connection.inputStream.close()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    actual suspend fun checkConnectivity(): Boolean {
        return withContext(Dispatchers.IO) {
            checkCurrentState()
        }
    }
    
    fun destroy() {
        checkJob?.cancel()
        scope.cancel()
    }
}

/**
 * Factory para criar ConnectivityMonitor no Desktop (JVM)
 */
actual fun createConnectivityMonitor(): ConnectivityMonitor = ConnectivityMonitor()
