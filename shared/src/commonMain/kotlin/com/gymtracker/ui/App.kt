package com.gymtracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.gymtracker.ui.diet.DietScreen
import com.gymtracker.ui.history.HistoryScreen
import com.gymtracker.ui.home.HomeScreen
import com.gymtracker.ui.workout.WorkoutSessionScreen

import com.gymtracker.ui.auth.LoginScreen
import com.gymtracker.ui.auth.RegisterScreen
import com.gymtracker.ui.theme.FitTrackTheme

enum class Screen { HOME, HISTORY, DIET }
enum class AppState { LOGIN, REGISTER, AUTHENTICATED }

@Composable
fun App() {
    var appState by remember { mutableStateOf(AppState.LOGIN) }
    var currentTab by remember { mutableStateOf(Screen.HOME) }
    var activeWorkoutId by remember { mutableStateOf<String?>(null) }

    FitTrackTheme {
        when (appState) {
            AppState.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { appState = AppState.AUTHENTICATED },
                    onNavigateToRegister = { appState = AppState.REGISTER }
                )
            }
            AppState.REGISTER -> {
                RegisterScreen(
                    onRegisterSuccess = { appState = AppState.AUTHENTICATED },
                    onNavigateBack = { appState = AppState.LOGIN }
                )
            }
            AppState.AUTHENTICATED -> {
                if (activeWorkoutId != null) {
                    WorkoutSessionScreen(
                        workoutId = activeWorkoutId!!,
                        onFinished = { activeWorkoutId = null }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == Screen.HOME,
                                    onClick = { currentTab = Screen.HOME },
                                    icon = { Icon(Icons.Default.FitnessCenter, "Treinos") },
                                    label = { Text("Treinos") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == Screen.HISTORY,
                                    onClick = { currentTab = Screen.HISTORY },
                                    icon = { Icon(Icons.Default.History, "Histórico") },
                                    label = { Text("Histórico") }
                                )
                                NavigationBarItem(
                                    selected = currentTab == Screen.DIET,
                                    onClick = { currentTab = Screen.DIET },
                                    icon = { Icon(Icons.Default.Restaurant, "Dieta") },
                                    label = { Text("Dieta") }
                                )
                            }
                        }
                    ) { padding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            when (currentTab) {
                                Screen.HOME    -> HomeScreen(onStartWorkout = { activeWorkoutId = it })
                                Screen.HISTORY -> HistoryScreen()
                                Screen.DIET    -> DietScreen()
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
