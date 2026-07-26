package com.gymtracker.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Ad placement positions in the app
enum class AdPosition {
    HOME_BETWEEN_SECTIONS,    // Between workouts and extras
    HOME_BOTTOM,              // Bottom of home screen
    AFTER_WORKOUT,            // After completing a workout
    HISTORY_LIST,             // In history screen
    PROGRESS_SCREEN,          // In progress screen
    REST_TIMER                // During rest timer (rewarded)
}

// Ad types
enum class AdType {
    BANNER,                   // Standard banner (320x50)
    NATIVE,                   // Native ad that matches app design
    INTERSTITIAL,             // Full screen between actions
    REWARDED_VIDEO,           // Video for rewards
    MEDIUM_RECTANGLE          // Medium rectangle (300x250)
}

@Composable
fun GoogleAdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX", // Replace with real ID
    adType: AdType = AdType.BANNER
) {
    // Placeholder for Google AdMob integration
    // In production, use: https://github.com/googleads/admob-android-sample
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Publicidade",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    when (adType) {
                        AdType.BANNER -> "Banner Ad (320x50)"
                        AdType.NATIVE -> "Native Ad"
                        AdType.INTERSTITIAL -> "Interstitial Ad"
                        AdType.REWARDED_VIDEO -> "Rewarded Video"
                        AdType.MEDIUM_RECTANGLE -> "Medium Rectangle (300x250)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun NativeAdCard(
    modifier: Modifier = Modifier,
    headline: String = "Suplementos para seu treino",
    description: String = "Descubra os melhores produtos para potencializar seus resultados",
    buttonText: String = "Saiba mais",
    onAdClick: () -> Unit = {}
) {
    // Native ad that matches the app's design
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Ad badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Patrocinado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    "Google Ads",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Ad content
            Text(
                headline,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // CTA button
            Button(
                onClick = onAdClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun RewardedAdPlaceholder(
    modifier: Modifier = Modifier,
    rewardDescription: String = "Desbloqueie um treino bônus",
    onWatchAd: () -> Unit = {}
) {
    // Rewarded ad - user watches video for reward
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Recompensa disponível!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                rewardDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onWatchAd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Assistir vídeo para ganhar")
            }
        }
    }
}

// Strategic ad placement component
@Composable
fun StrategicAdPlacement(
    position: AdPosition,
    modifier: Modifier = Modifier
) {
    when (position) {
        AdPosition.HOME_BETWEEN_SECTIONS -> {
            NativeAdCard(
                modifier = modifier.padding(horizontal = 16.dp),
                headline = "Equipamentos para seu home gym",
                description = "Monte seu espaço de treino com até 40% de desconto",
                buttonText = "Ver ofertas"
            )
        }
        
        AdPosition.HOME_BOTTOM -> {
            GoogleAdBanner(
                modifier = modifier.padding(horizontal = 16.dp),
                adType = AdType.BANNER
            )
        }
        
        AdPosition.AFTER_WORKOUT -> {
            RewardedAdPlaceholder(
                modifier = modifier.padding(horizontal = 16.dp),
                rewardDescription = "Ganhe um treino bônus de 15 minutos",
                onWatchAd = { /* TODO: Show rewarded ad */ }
            )
        }
        
        AdPosition.HISTORY_LIST -> {
            GoogleAdBanner(
                modifier = modifier,
                adType = AdType.NATIVE
            )
        }
        
        AdPosition.PROGRESS_SCREEN -> {
            NativeAdCard(
                modifier = modifier.padding(horizontal = 16.dp),
                headline = "Acompanhe seu progresso com precisão",
                description = "Relatórios detalhados com plano premium",
                buttonText = "Experimentar grátis"
            )
        }
        
        AdPosition.REST_TIMER -> {
            RewardedAdPlaceholder(
                modifier = modifier.padding(horizontal = 16.dp),
                rewardDescription = "Ganhe +30 segundos de descanso",
                onWatchAd = { /* TODO: Show rewarded ad */ }
            )
        }
    }
}
