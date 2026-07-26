package com.gymtracker.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    userProfile: UserProfile,
    onProfileUpdate: (UserProfile) -> Unit,
    onComplete: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = listOf("Dados Pessoais", "Nível", "Objetivos", "Preferências")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pages[currentPage]) },
                navigationIcon = {
                    if (currentPage > 0) {
                        IconButton(onClick = { currentPage-- }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentPage + 1).toFloat() / pages.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Page content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "pageContent"
            ) { page ->
                when (page) {
                    0 -> PersonalInfoPage(userProfile, onProfileUpdate)
                    1 -> FitnessLevelPage(userProfile, onProfileUpdate)
                    2 -> GoalsPage(userProfile, onProfileUpdate)
                    3 -> PreferencesPage(userProfile, onProfileUpdate)
                }
            }

            Spacer(Modifier.weight(1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentPage > 0) {
                    OutlinedButton(
                        onClick = { currentPage-- },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Voltar")
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onProfileUpdate(userProfile.copy(hasCompletedOnboarding = true))
                            onComplete()
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (currentPage < pages.size - 1) "Próximo" else "Começar!",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PersonalInfoPage(profile: UserProfile, onUpdate: (UserProfile) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Conte-nos sobre você",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = profile.name,
            onValueChange = { onUpdate(profile.copy(name = it)) },
            label = { Text("Seu nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )

        OutlinedTextField(
            value = profile.age.toString(),
            onValueChange = { it.toIntOrNull()?.let { age -> onUpdate(profile.copy(age = age)) } },
            label = { Text("Idade") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Cake, contentDescription = null) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = profile.weight.toString(),
                onValueChange = { it.toDoubleOrNull()?.let { w -> onUpdate(profile.copy(weight = w)) } },
                label = { Text("Peso (kg)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.MonitorWeight, contentDescription = null) }
            )

            OutlinedTextField(
                value = profile.height.toString(),
                onValueChange = { it.toIntOrNull()?.let { h -> onUpdate(profile.copy(height = h)) } },
                label = { Text("Altura (cm)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Height, contentDescription = null) }
            )
        }
    }
}

@Composable
fun FitnessLevelPage(profile: UserProfile, onUpdate: (UserProfile) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Qual seu nível de experiência?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        FitnessLevel.entries.forEach { level ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdate(profile.copy(fitnessLevel = level)) },
                colors = CardDefaults.cardColors(
                    containerColor = if (profile.fitnessLevel == level) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (profile.fitnessLevel == level) {
                    CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
                } else {
                    null
                }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        level.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.fitnessLevel == level) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        level.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GoalsPage(profile: UserProfile, onUpdate: (UserProfile) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Quais são seus objetivos?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Selecione todos que se aplicam",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(FitnessGoal.entries.size) { index ->
                val goal = FitnessGoal.entries[index]
                val isSelected = profile.goals.contains(goal)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newGoals = if (isSelected) {
                                profile.goals - goal
                            } else {
                                profile.goals + goal
                            }
                            onUpdate(profile.copy(goals = newGoals))
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(goal.icon, fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            goal.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesPage(profile: UserProfile, onUpdate: (UserProfile) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(
          "Suas preferências",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Workout days per week
        Column {
            Text(
                "Quantos dias por semana você quer treinar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { days ->
                    FilterChip(
                        selected = profile.workoutDaysPerWeek == days,
                        onClick = { onUpdate(profile.copy(workoutDaysPerWeek = days)) },
                        label = { Text("$days") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Preferred workout time
        Column {
            Text(
                "Horário preferido para treinar?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WorkoutTime.entries.forEach { time ->
                    FilterChip(
                        selected = profile.preferredWorkoutTime == time,
                        onClick = { onUpdate(profile.copy(preferredWorkoutTime = time)) },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(time.icon)
                                Text(time.displayName, style = MaterialTheme.typography.labelSmall)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
