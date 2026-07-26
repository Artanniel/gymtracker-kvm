package com.gymtracker.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    var iconScale by remember { mutableFloatStateOf(0f) }
    var iconRotation by remember { mutableFloatStateOf(0f) }
    var titleAlpha by remember { mutableFloatStateOf(0f) }
    var titleSlideY by remember { mutableFloatStateOf(50f) }
    var subtitleAlpha by remember { mutableFloatStateOf(0f) }
    var loadingAlpha by remember { mutableFloatStateOf(0f) }
    var backgroundAlpha by remember { mutableFloatStateOf(0f) }

    // Infinite pulse animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Launch animations sequence
    LaunchedEffect(Unit) {
        // Phase 1: Background fade in
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f))
        ) { value, _ -> backgroundAlpha = value }

        delay(100)

        // Phase 2: Icon appears with bounce
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ -> iconScale = value }

        // Phase 2b: Icon rotation
        animate(
            initialValue = -180f,
            targetValue = 0f,
            animationSpec = tween(600, easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f))
        ) { value, _ -> iconRotation = value }

        delay(200)

        // Phase 3: Title slides up and fades in
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(500, easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f))
        ) { value, _ -> titleAlpha = value }

        animate(
            initialValue = 50f,
            targetValue = 0f,
            animationSpec = tween(500, easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f))
        ) { value, _ -> titleSlideY = value }

        delay(100)

        // Phase 4: Subtitle fades in
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(400, easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f))
        ) { value, _ -> subtitleAlpha = value }

        delay(100)

        // Phase 5: Loading indicator appears
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { value, _ -> loadingAlpha = value }

        // Wait and transition
        delay(1500)

        // Phase 6: Fade out everything
        animate(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = tween(400, easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f))
        ) { value, _ ->
            backgroundAlpha = value
        }

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(backgroundAlpha)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FitTrackBackground,
                        Color(0xFF0A120A),
                        FitTrackBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles in background
        Box(
            modifier = Modifier
                .size(300.dp)
                .alpha(glowAlpha * 0.3f)
                .background(
                    color = FitTrackPrimary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .alpha(glowAlpha * 0.2f)
                .background(
                    color = FitTrackPrimary.copy(alpha = 0.15f),
                    shape = CircleShape
                )
                .align(Alignment.Center)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Icon with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = iconScale * pulseScale
                        scaleY = iconScale * pulseScale
                        rotationZ = iconRotation
                    }
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(glowAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    FitTrackPrimary.copy(alpha = 0.4f),
                                    FitTrackPrimary.copy(alpha = 0.0f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Icon background
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = FitTrackPrimary.copy(alpha = 0.2f),
                    shadowElevation = 16.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Icon
                        Icon(
                            imageVector = Icons.Filled.FitnessCenter,
                            contentDescription = "FitTrack Logo",
                            tint = FitTrackPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = "FitTrack",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = FitTrackTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha
                        translationY = titleSlideY
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Seu parceiro de treinos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = FitTrackTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Loading indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(loadingAlpha)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = FitTrackPrimary,
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preparando seus treinos...",
                    fontSize = 14.sp,
                    color = FitTrackTextSecondary.copy(alpha = 0.7f)
                )
            }
        }

        // Version info at bottom
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = FitTrackTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(loadingAlpha)
        )
    }
}
