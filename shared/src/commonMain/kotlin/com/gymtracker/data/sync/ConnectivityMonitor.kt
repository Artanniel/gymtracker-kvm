package com.gymtracker.data.sync

import kotlinx.coroutines.flow.StateFlow

/**
 * Monitor de conectividade de rede multiplatform.
 * Detecta se o dispositivo está online para sincronização de dados.
 */
expect class ConnectivityMonitor {
    /**
     * Estado observável de conectividade.
     * true = online, false = offline
     */
    val isOnline: StateFlow<Boolean>
    
    /**
     * Força uma verificação imediata de conectividade.
     * @return true se online, false se offline
     */
    suspend fun checkConnectivity(): Boolean
}

/**
 * Factory para criar ConnectivityMonitor na plataforma correta
 */
expect fun createConnectivityMonitor(): ConnectivityMonitor

/**
 * Estados de sincronização
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Error(val message: String) : SyncState()
    data class Progress(val current: Int, val total: Int) : SyncState()
}

/**
 * Ações de sincronização
 */
enum class SyncAction {
    CREATE,
    UPDATE,
    DELETE
}

/**
 * Entidade pendente de sincronização
 */
data class PendingSync(
    val id: String,
    val entityType: String,
    val entityId: String,
    val action: SyncAction,
    val payload: String,
    val createdAt: Long,
    val retryCount: Int = 0,
    val lastError: String? = null,
    val synced: Boolean = false
)
