package com.gymtracker.data.sync

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.db.Pending_sync
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Gerenciador de sincronização offline-first.
 * 
 * Estratégia:
 * 1. App salva sempre no banco local (rápido, offline)
 * 2. Operação é enfileirada em pending_sync
 * 3. Quando online, processa fila e envia ao backend
 * 4. Backend é fonte de verdade (remote wins em conflitos)
 */
class SyncManager(
    private val connectivity: ConnectivityMonitor
) {
    private val db get() = DatabaseProvider.get()
    private val syncQ get() = db.syncTableQueries
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var syncJob: Job? = null
    
    // Callback para enviar dados ao backend (será injetado)
    private var apiClient: ApiClient? = null
    
    init {
        // Atualiza contagem inicial
        scope.launch {
            updatePendingCount()
        }
        
        // Observa conectividade e sincroniza quando online
        scope.launch {
            connectivity.isOnline.collect { online ->
                if (online) {
                    processPendingSync()
                }
            }
        }
    }
    
    /**
     * Configura o cliente API para envio de dados
     */
    fun setApiClient(client: ApiClient) {
        apiClient = client
    }
    
    /**
     * Enfileira uma operação para sincronização posterior
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun queueForSync(
        entityType: String,
        entityId: String,
        action: SyncAction,
        payload: String // JSON string do payload
    ): String {
        val id = Uuid.random().toString()
        
        syncQ.insert(
            id = id,
            entity_type = entityType,
            entity_id = entityId,
            action = action.name,
            payload = payload,
            created_at = Clock.System.now().toEpochMilliseconds(),
            retry_count = 0,
            last_error = null,
            synced = 0
        )
        
        updatePendingCount()
        
        // Tenta sincronizar imediatamente se online
        if (connectivity.isOnline.value) {
            processPendingSync()
        }
        
        return id
    }
    
    /**
     * Processa todas as operações pendentes
     */
    suspend fun processPendingSync() {
        if (_syncState.value is SyncState.Syncing) return
        
        _syncState.value = SyncState.Syncing
        
        try {
            val pending = syncQ.getAllPending().awaitAsList()
            
            if (pending.isEmpty()) {
                _syncState.value = SyncState.Idle
                return
            }
            
            var processed = 0
            val total = pending.size
            
            for (item in pending) {
                try {
                    // Envia ao backend
                    sendToBackend(item)
                    
                    // Marca como sincronizada
                    syncQ.markSynced(item.id)
                    processed++
                    
                    _syncState.value = SyncState.Progress(processed, total)
                } catch (e: Exception) {
                    // Incrementa retry
                    syncQ.incrementRetry(item.id, e.message ?: "Unknown error")
                    
                    // Se excedeu retry, marca como falha permanente
                    if (item.retry_count >= MAX_RETRIES) {
                        // Notificar usuário sobre falha?
                        println("Sync failed permanently for ${item.entity_type}:${item.entity_id}")
                    }
                }
            }
            
            updatePendingCount()
            _syncState.value = SyncState.Idle
            
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
        }
    }
    
    /**
     * Envia operação ao backend
     */
    private suspend fun sendToBackend(item: Pending_sync) {
        val client = apiClient ?: throw IllegalStateException("ApiClient not configured")
        
        when (item.action) {
            SyncAction.CREATE.name -> {
                client.post(item.entity_type, item.payload)
            }
            SyncAction.UPDATE.name -> {
                client.put(item.entity_type, item.entity_id, item.payload)
            }
            SyncAction.DELETE.name -> {
                client.delete(item.entity_type, item.entity_id)
            }
        }
    }
    
    /**
     * Puxa dados do servidor (pull)
     */
    suspend fun pullFromServer() {
        if (!connectivity.isOnline.value) return
        
        try {
            val client = apiClient ?: return
            
            // Puxa dados atualizados
            val remoteData = client.getAll("workout_sessions")
            
            // TODO: Merge com dados locais (remote wins)
            // localDB.workoutSessionQueries.syncAll(remoteData)
            
        } catch (e: Exception) {
            println("Pull from server failed: ${e.message}")
        }
    }
    
    /**
     * Limpa operações sincronizadas antigas
     */
    suspend fun cleanupSynced() {
        val sevenDaysAgo = Clock.System.now().toEpochMilliseconds() - (7 * 24 * 60 * 60 * 1000)
        syncQ.deleteOldPending(sevenDaysAgo)
        syncQ.deleteSynced()
    }
    
    /**
     * Retorna operações com falha
     */
    suspend fun getFailedSyncs(): List<Pending_sync> {
        return syncQ.getFailedSyncs().awaitAsList()
    }
    
    private suspend fun updatePendingCount() {
        val count = syncQ.getPendingCount().awaitAsOne()
        _pendingCount.value = count.toInt()
    }
    
    fun destroy() {
        syncJob?.cancel()
        scope.cancel()
    }
    
    companion object {
        const val MAX_RETRIES = 3
    }
}

/**
 * Interface do cliente API (será implementada quando backend existir)
 */
interface ApiClient {
    suspend fun post(entityType: String, payload: String)
    suspend fun put(entityType: String, entityId: String, payload: String)
    suspend fun delete(entityType: String, entityId: String)
    suspend fun getAll(entityType: String): String
}
