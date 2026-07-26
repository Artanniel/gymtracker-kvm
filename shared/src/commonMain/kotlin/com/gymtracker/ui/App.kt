package com.gymtracker.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.AppDependencies
import com.gymtracker.ui.animations.*
import com.gymtracker.ui.ads.StrategicAdPlacement
import com.gymtracker.ui.ads.AdPosition
import com.gymtracker.ui.diet.DietScreen
import com.gymtracker.ui.history.HistoryScreen
import com.gymtracker.ui.home.HomeScreen
import com.gymtracker.ui.progress.ProgressScreen
import com.gymtracker.ui.progress.ProgressViewModel
import com.gymtracker.ui.settings.SettingsScreen
import com.gymtracker.ui.sync.SyncStatusScreen
import com.gymtracker.ui.workout.WorkoutSessionScreen

import com.gymtracker.ui.auth.LoginScreen
import com.gymtracker.ui.auth.RegisterScreen
import com.gymtracker.ui.onboarding.OnboardingScreen
import com.gymtracker.ui.onboarding.UserProfile
import com.gymtracker.ui.notifications.NotificationsScreen
import com.gymtracker.ui.splash.SplashScreen
import com.gymtracker.ui.theme.FitTrackTheme

enum class Screen { HOME, HISTORY, DIET, PROGRESS }
enum class AppState { SPLASH, LOGIN, REGISTER, ONBOARDING, AUTHENTICATED }

private val screens = Screen.entries

@Composable
fun App() {
    var appState by remember { mutableStateOf(AppState.SPLASH) }
    var currentTab by remember { mutableStateOf(Screen.HOME) }
    var activeWorkoutId by remember { mutableStateOf<String?>(null) }
    var activeExerciseIdForProgress by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showSync by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf(UserProfile()) }
    
    // Sync state
    val pendingCount by AppDependencies.syncManager.pendingCount.collectAsState()

    FitTrackTheme {
        AnimatedContent(
            targetState = appState,
            transitionSpec = {
                when {
                    targetState == AppState.LOGIN && initialState == AppState.SPLASH -> {
                        fadeIn(tween(AnimationDurations.SLOW)) togetherWith fadeOut(tween(AnimationDurations.FAST))
                    }
                    targetState == AppState.ONBOARDING && initialState == AppState.LOGIN -> {
                        slideInFromRight() + fadeIn() togetherWith slideOutToLeft() + fadeOut()
                    }
                    targetState == AppState.AUTHENTICATED && initialState == AppState.ONBOARDING -> {
                        slideInFromRight() + fadeIn() togetherWith slideOutToLeft() + fadeOut()
                    }
                    targetState == AppState.LOGIN && initialState == AppState.AUTHENTICATED -> {
                        slideInFromLeft() + fadeIn() togetherWith slideOutToRight() + fadeOut()
                    }
                    else -> {
                        fadeIn(tween(AnimationDurations.NORMAL)) togetherWith fadeOut(tween(AnimationDurations.NORMAL))
                    }
                }
            },
            label = "appStateTransition"
        ) { state ->
            when (state) {
                AppState.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = { appState = AppState.LOGIN }
                    )
                }
                AppState.LOGIN -> {
                    LoginScreen(
                        onLoginSuccess = { appState = AppState.ONBOARDING },
                        onNavigateToRegister = { appState = AppState.REGISTER }
                    )
                }
                AppState.REGISTER -> {
                    RegisterScreen(
                        onRegisterSuccess = { appState = AppState.ONBOARDING },
                        onNavigateBack = { appState = AppState.LOGIN }
                    )
                }
                AppState.ONBOARDING -> {
                    OnboardingScreen(
                        onComplete = { profile ->
                            userProfile = profile
                            appState = AppState.AUTHENTICATED
                        }
                    )
                }
                AppState.AUTHENTICATED -> {
                    if (showSync) {
                        SyncStatusScreen(
                            syncManager = AppDependencies.syncManager,
                            connectivityMonitor = AppDependencies.connectivityMonitor,
                            onBack = { showSync = false }
                        )
                    } else if (showNotifications) {
                        NotificationsScreen(onBack = { showNotifications = false })
                    } else if (showSettings) {
                        SettingsScreen(onBack = { showSettings = false })
                    } else if (activeExerciseIdForProgress != null) {
                        val (exerciseId, exerciseName) = activeExerciseIdForProgress!!
                        val progressVm: ProgressViewModel = viewModel { ProgressViewModel() }
                        LaunchedEffect(exerciseId, exerciseName) {
                            progressVm.loadExercises()
                            progressVm.selectExercise(exerciseId, exerciseName)
                        }
                        val selected by progressVm.selectedProgress.collectAsState()
                        if (selected != null && selected != com.gymtracker.data.model.ExerciseProgress.Empty) {
                            com.gymtracker.ui.progress.ProgressDetailScreen(
                                progress = selected!!,
                                onBack = {
                                    progressVm.clearSelection()
                                    activeExerciseIdForProgress = null
                                }
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (activeWorkoutId != null) {
                        WorkoutSessionScreen(
                            workoutId = activeWorkoutId!!,
                            onFinished = { activeWorkoutId = null },
                            onViewProgress = { id, name -> activeExerciseIdForProgress = id to name }
                        )
                    } else {
                        val pagerState = rememberPagerState(pageCount = { screens.size })
                        val tabIcons = listOf(
                            Icons.Filled.FitnessCenter,
                            Icons.Filled.History,
                            Icons.Filled.Restaurant,
                            Icons.AutoMirrored.Filled.TrendingUp
                        )
                        val tabLabels = listOf("Treinos", "Histórico", "Dieta", "Progresso")

                        LaunchedEffect(currentTab) {
                            pagerState.animateScrollToPage(currentTab.ordinal)
                        }
                        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                            if (!pagerState.isScrollInProgress) {
                                currentTab = screens[pagerState.currentPage]
                            }
                        }

                        Scaffold(
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ) {
                                    screens.forEachIndexed { index, screen ->
                                        NavigationBarItem(
                                            selected = currentTab == screen,
                                            onClick = { currentTab = screen },
                                            icon = { Icon(tabIcons[index], contentDescription = tabLabels[index]) },
                                            label = { Text(tabLabels[index]) }
                                        )
                                    }
                                }
                            }
                        ) { padding ->
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize().padding(padding)
                            ) { page ->
                                AnimatedContent(
                                    targetState = page,
                                    transitionSpec = {
                                        fadeIn(tween(AnimationDurations.FAST)) togetherWith
                                                fadeOut(tween(AnimationDurations.FAST))
                                    },
                                    label = "pageTransition"
                                ) { targetPage ->
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background
                                    ) {
                                        when (screens[targetPage]) {
                                            Screen.HOME -> HomeScreen(
                                                onStartWorkout = { activeWorkoutId = it },
                                                onSettings = { showSettings = true },
                                                onNotifications = { showNotifications = true },
                                                onSync = { showSync = true },
                                                pendingSyncCount = pendingCount
                                            )
                                            Screen.HISTORY -> HistoryScreen(
                                                onViewProgress = { id, name -> activeExerciseIdForProgress = id to name }
                                            )
                                            Screen.DIET -> DietScreen()
                                            Screen.PROGRESS -> ProgressScreen()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun gymColorScheme() = lightColorScheme(
    primary   = androidx.compose.ui.graphics.Color(0xFF1E9E3E),
    secondary = androidx.compose.ui.graphics.Color(0xFF5C8DD6),
    error     = androidx.compose.ui.graphics.Color(0xFFE8362D),
    background = androidx.compose.ui.graphics.Color(0xFFF5F7F5)
)
