package com.gymtracker.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.browser.window

/**
 * Implementação Web (WasmJS) do ConnectivityMonitor.
 * Usa navigator.onLine e eventos do browser.
 */
actual class ConnectivityMonitor {
    
    private val _isOnline = MutableStateFlow(false)
    actual val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    init {
        // Verifica estado inicial
        _isOnline.value = checkOnlineStatus()
        
        // Escuta eventos de conectividade do browser
        window.addEventListener("online", { event ->
            _isOnline.value = true
        })
        
        window.addEventListener("offline", { event ->
            _isOnline.value = false
        })
    }
    
    private fun checkOnlineStatus(): Boolean {
        return window.navigator.onLine
    }
    
    actual suspend fun checkConnectivity(): Boolean {
        return checkOnlineStatus()
    }
    
    fun destroy() {
        // Remove listeners (browser cleanup)
        // Nota: em WasmJS, o cleanup é automático quando a página descarrega
    }
}

/**
 * Factory para criar ConnectivityMonitor no Web (WasmJS)
 */
actual fun createConnectivityMonitor(): ConnectivityMonitor = ConnectivityMonitor()
