package com.gymtracker.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

// Animation durations following UX best practices (200-500ms)
object AnimationDurations {
    const val FAST = 200
    const val NORMAL = 300
    const val SLOW = 500
    const val PAGE_TRANSITION = 400
}

// Easing curves for natural motion
object AnimationEasing {
    val Standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val Decelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val Accelerate = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val Bounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
}

// Fade in animation
fun fadeInTransition(durationMillis: Int = AnimationDurations.NORMAL) = fadeIn(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = AnimationEasing.Standard
    )
)

// Fade out animation
fun fadeOutTransition(durationMillis: Int = AnimationDurations.NORMAL) = fadeOut(
    animationSpec = tween(
        durationMillis = durationMillis,
        easing = AnimationEasing.Standard
    )
)

// Slide in from right (for forward navigation)
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(
        durationMillis = AnimationDurations.PAGE_TRANSITION,
        easing = AnimationEasing.Standard
    )
)

// Slide out to left (for forward navigation)
fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth / 3 },
    animationSpec = tween(
        durationMillis = AnimationDurations.PAGE_TRANSITION,
        easing = AnimationEasing.Standard
    )
)

// Slide in from left (for back navigation)
fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth / 3 },
    animationSpec = tween(
        durationMillis = AnimationDurations.PAGE_TRANSITION,
        easing = AnimationEasing.Standard
    )
)

// Slide out to right (for back navigation)
fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(
        durationMillis = AnimationDurations.PAGE_TRANSITION,
        easing = AnimationEasing.Standard
    )
)

// Scale + fade for dialogs and modals
fun scaleInTransition() = scaleIn(
    initialScale = 0.9f,
    animationSpec = tween(
        durationMillis = AnimationDurations.SLOW,
        easing = AnimationEasing.Bounce
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = AnimationDurations.NORMAL,
        easing = AnimationEasing.Standard
    )
)

fun scaleOutTransition() = scaleOut(
    targetScale = 0.9f,
    animationSpec = tween(
        durationMillis = AnimationDurations.FAST,
        easing = AnimationEasing.Standard
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = AnimationDurations.FAST,
        easing = AnimationEasing.Standard
    )
)

// Animated modifier for card press effect
@Composable
fun Modifier.animatedPress(
    isPressed: Boolean,
    onPressScale: Float = 0.98f
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) onPressScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// Animated visibility for list items
@Composable
fun AnimatedListItem(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(AnimationDurations.NORMAL)
        ) + slideInVertically(
            initialOffsetY = { it / 20 },
            animationSpec = tween(
                durationMillis = AnimationDurations.NORMAL,
                easing = AnimationEasing.Decelerate
            )
        ),
        modifier = modifier
    ) {
        content()
    }
}

// Pulse animation for important elements
@Composable
fun Modifier.pulseAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = AnimationEasing.Standard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// Shimmer loading effect
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = AnimationEasing.Standard),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    return this.alpha(alpha)
}
