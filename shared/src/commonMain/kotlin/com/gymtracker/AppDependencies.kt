package com.gymtracker

import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.repository.DietRepository
import com.gymtracker.data.repository.GoalRepository
import com.gymtracker.data.repository.NotificationManager
import com.gymtracker.data.repository.StreakRepository
import com.gymtracker.data.repository.WorkoutRepository
import com.gymtracker.data.sync.AppConfig
import com.gymtracker.data.sync.ConnectivityMonitor
import com.gymtracker.data.sync.KtorApiClient
import com.gymtracker.data.sync.SyncManager
import com.gymtracker.data.sync.createConnectivityMonitor
import com.gymtracker.data.repository.HybridWorkoutRepository

object AppDependencies {
    val workoutRepo = WorkoutRepository()
    val goalRepo    = GoalRepository()
    val dietRepo    = DietRepository()
    val streakRepo  = StreakRepository()
    val notificationManager = NotificationManager()
    
    lateinit var connectivityMonitor: ConnectivityMonitor
        private set
    lateinit var syncManager: SyncManager
        private set
    lateinit var hybridWorkoutRepo: HybridWorkoutRepository
        private set
    lateinit var apiClient: KtorApiClient
        private set

    suspend fun init(factory: DatabaseDriverFactory) {
        DatabaseProvider.init(factory)
        
        // Configurar connectivity monitor (expect/actual por plataforma)
        connectivityMonitor = createConnectivityMonitor()
        
        // Configurar API client (Ktor HTTP Client)
        apiClient = KtorApiClient(AppConfig.DEFAULT_API_URL)
        
        // Configurar sync manager com monitor de conectividade e API client
        syncManager = SyncManager(connectivityMonitor)
        syncManager.setApiClient(apiClient)
        
        // Configurar repository híbrido (local + sync)
        hybridWorkoutRepo = HybridWorkoutRepository(syncManager, connectivityMonitor)
        
        // Configurar URL padrão do backend (pode ser alterado em Settings)
        AppConfig.configure(AppConfig.DEFAULT_API_URL)
    }
    
    /**
     * Atualiza URL do backend e reconecta
     */
    fun updateBackendUrl(url: String) {
        AppConfig.configure(url)
        apiClient.close()
        apiClient = KtorApiClient(url)
        syncManager.setApiClient(apiClient)
    }
    
    /**
     * Limpa recursos
     */
    fun cleanup() {
        apiClient.close()
        syncManager.destroy()
    }
}
