package com.gymtracker.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Bem-vindo ao FitTrack!",
        subtitle = "Seu companheiro de treino pessoal com IA",
        icon = Icons.Filled.FitnessCenter,
        gradient = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32))
    ),
    OnboardingPage(
        title = "Seus Treinos",
        subtitle = "Acompanhe seus treinos com detalhes e.progressão",
        icon = Icons.Filled.TrendingUp,
        gradient = listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
    ),
    OnboardingPage(
        title = "Sequências",
        subtitle = "Mantenha sua sequência de treinos ativa!",
        icon = Icons.Filled.LocalFireDepartment,
        gradient = listOf(Color(0xFFE65100), Color(0xFFEF6C00))
    ),
    OnboardingPage(
        title = "Conquistas",
        subtitle = "Desbloqueie conquistas e suba de nível",
        icon = Icons.Filled.EmojiEvents,
        gradient = listOf(Color(0xFF4A148C), Color(0xFF6A1B9A))
    ),
    OnboardingPage(
        title = "Vamos Começar!",
        subtitle = "Configure seu perfil para treinos personalizados",
        icon = Icons.Filled.Rocket,
        gradient = listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (userProfile: UserProfile) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf(UserProfile()) }
    var showProfileSetup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (showProfileSetup) {
            ProfileSetupScreen(
                userProfile = userProfile,
                onProfileUpdate = { userProfile = it },
                onComplete = { onComplete(userProfile) }
            )
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPageContent(
                    page = onboardingPages[page],
                    isLastPage = page == onboardingPages.size - 1,
                    onNext = {
                        if (page < onboardingPages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        } else {
                            showProfileSetup = true
                        }
                    },
                    onSkip = { showProfileSetup = true }
                )
            }

            // Page indicator
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            // Skip button
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(
                    onClick = { showProfileSetup = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Text(
                        "Pular",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = page.gradient
                )
            )
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated icon
            Icon(
                page.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(iconScale)
            )

            Spacer(Modifier.height(48.dp))

            // Title
            Text(
                page.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Subtitle
            Text(
                page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(64.dp))

            // Next/Start button
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = page.gradient.first()
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    if (isLastPage) "Configurar Perfil" else "Próximo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
