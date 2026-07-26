package com.gymtracker.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.*
import platform.darwin.dispatch_get_main_queue

/**
 * Implementação iOS do ConnectivityMonitor.
 * Usa NWPathMonitor para monitorar mudanças de rede.
 */
actual class ConnectivityMonitor {
    
    private val _isOnline = MutableStateFlow(false)
    actual val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val monitor: nw_path_monitor_t = nw_path_monitor_create()
    
    init {
        // Verifica estado inicial
        _isOnline.value = checkCurrentPath()
        
        // Configura monitor
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            _isOnline.value = (status == nw_path_status_satisfied)
        }
        
        // Inicia monitor na main queue
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }
    
    private fun checkCurrentPath(): Boolean {
        val path = nw_path_monitor_copy_current_path(monitor)
        return if (path != null) {
            nw_path_get_status(path) == nw_path_status_satisfied
        } else {
            false
        }
    }
    
    actual suspend fun checkConnectivity(): Boolean {
        return checkCurrentPath()
    }
    
    fun destroy() {
        nw_path_monitor_cancel(monitor)
    }
}

/**
 * Factory para criar ConnectivityMonitor no iOS
 */
actual fun createConnectivityMonitor(): ConnectivityMonitor = ConnectivityMonitor()
