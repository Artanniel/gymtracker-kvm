package com.gymtracker.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Implementação Android do ConnectivityMonitor.
 * Usa ConnectivityManager para monitorar mudanças de rede.
 */
actual class ConnectivityMonitor(private val context: Context) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _isOnline = MutableStateFlow(false)
    actual val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnline.value = true
        }
        
        override fun onLost(network: Network) {
            _isOnline.value = false
        }
        
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            _isOnline.value = hasInternet
        }
    }
    
    init {
        // Verifica estado inicial
        _isOnline.value = checkCurrentNetworkState()
        
        // Registra callback para mudanças
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
    
    private fun checkCurrentNetworkState(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    actual suspend fun checkConnectivity(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val network = connectivityManager.activeNetwork
            val capabilities = if (network != null) {
                connectivityManager.getNetworkCapabilities(network)
            } else {
                null
            }
            val isAvailable = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            continuation.resume(isAvailable)
        }
    }
    
    fun destroy() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Callback pode não estar registrado
        }
    }
}

/**
 * Factory para criar ConnectivityMonitor no Android
 */
actual fun createConnectivityMonitor(): ConnectivityMonitor {
    // Será chamado de um contexto Android (Activity/Service)
    throw NotImplementedError("Use ConnectivityMonitor(context) para Android")
}
