package com.gymtracker

import com.gymtracker.data.auth.AuthRepository
import com.gymtracker.data.auth.TokenStore
import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.repository.DietRepository
import com.gymtracker.data.repository.GoalRepository
import com.gymtracker.data.repository.HybridWorkoutRepository
import com.gymtracker.data.repository.NotificationManager
import com.gymtracker.data.repository.StreakRepository
import com.gymtracker.data.repository.FinanceRepository
import com.gymtracker.data.repository.StudentRepository
import com.gymtracker.data.repository.VideoRepository
import com.gymtracker.data.repository.WorkoutRepository
import com.gymtracker.data.sync.AppConfig
import com.gymtracker.data.sync.ConnectivityMonitor
import com.gymtracker.data.sync.KtorApiClient
import com.gymtracker.data.sync.SyncManager
import com.gymtracker.data.sync.createConnectivityMonitor

object AppDependencies {
    val workoutRepo = WorkoutRepository()
    val goalRepo = GoalRepository()
    val dietRepo = DietRepository()
    val streakRepo = StreakRepository()
    val notificationManager = NotificationManager()
    val studentRepo = StudentRepository()
    val videoRepo = VideoRepository()
    val financeRepo = FinanceRepository()

    lateinit var connectivityMonitor: ConnectivityMonitor
        private set
    lateinit var syncManager: SyncManager
        private set
    lateinit var hybridWorkoutRepo: HybridWorkoutRepository
        private set
    lateinit var apiClient: KtorApiClient
        private set
    lateinit var tokenStore: TokenStore
        private set
    lateinit var authRepository: AuthRepository
        private set

    suspend fun init(factory: DatabaseDriverFactory) {
        DatabaseProvider.init(factory)

        tokenStore = createPlatformTokenStore()

        apiClient = KtorApiClient(AppConfig.DEFAULT_API_URL)

        authRepository = AuthRepository(tokenStore, apiClient)

        apiClient = apiClient.withTokenProvider { authRepository.getStoredToken() }

        connectivityMonitor = createConnectivityMonitor()

        syncManager = SyncManager(connectivityMonitor)
        syncManager.setApiClient(apiClient)

        hybridWorkoutRepo = HybridWorkoutRepository(syncManager, connectivityMonitor)

        AppConfig.configure(AppConfig.DEFAULT_API_URL)
    }

    fun updateBackendUrl(url: String) {
        AppConfig.configure(url)
        val currentToken = authRepository.getStoredToken()
        apiClient.close()
        apiClient = KtorApiClient(url) { currentToken }
        syncManager.setApiClient(apiClient)
    }

    fun logout() {
        authRepository.logout()
    }

    fun cleanup() {
        apiClient.close()
        syncManager.destroy()
    }
}

expect fun createPlatformTokenStore(): TokenStore
