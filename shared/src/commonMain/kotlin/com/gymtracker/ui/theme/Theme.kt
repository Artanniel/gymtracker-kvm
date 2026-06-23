package com.gymtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FitTrackColorScheme = darkColorScheme(
    primary = FitTrackPrimary,
    onPrimary = FitTrackBackground,
    background = FitTrackBackground,
    onBackground = FitTrackTextPrimary,
    surface = FitTrackSurface,
    onSurface = FitTrackTextPrimary,
    surfaceVariant = FitTrackSurface,
    onSurfaceVariant = FitTrackTextSecondary,
    error = FitTrackError,
    onError = FitTrackTextPrimary
)

@Composable
fun FitTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FitTrackColorScheme,
        content = content
    )
}
