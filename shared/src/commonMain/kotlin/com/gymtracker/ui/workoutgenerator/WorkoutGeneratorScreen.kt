package com.gymtracker.ui.workoutgenerator

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutGeneratorScreen(
    onStartWorkout: (WorkoutTemplate) -> Unit,
    onBack: () -> Unit,
    viewModel: WorkoutGeneratorViewModel = viewModel { WorkoutGeneratorViewModel() }
) {
    val generatedWorkout by viewModel.generatedWorkout.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val fitnessLevel by viewModel.fitnessLevel.collectAsState()
    val focusArea by viewModel.focusArea.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerador de Treinos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "IA de Treinos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Configure suas preferências e gere treinos personalizados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Fitness Level
            item {
                Column {
                    Text(
                        "Nível Fitness",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Difficulty.entries) { level ->
                            FilterChip(
                                selected = fitnessLevel == level,
                                onClick = { viewModel.updateFitnessLevel(level) },
                                label = { Text(level.displayName) }
                            )
                        }
                    }
                }
            }

            // Focus Area
            item {
                Column {
                    Text(
                        "Área Focal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MuscleRegion.entries) { region ->
                            FilterChip(
                                selected = focusArea == region,
                                onClick = { viewModel.updateFocusArea(region) },
                                label = { Text(region.displayName) }
                            )
                        }
                    }
                }
            }

            // Duration
            item {
                Column {
                    Text(
                        "Duração: $duration minutos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = duration.toFloat(),
                        onValueChange = { viewModel.updateDuration(it.toInt()) },
                        valueRange = 10f..60f,
                        steps = 9
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10 min", style = MaterialTheme.typography.bodySmall)
                        Text("30 min", style = MaterialTheme.typography.bodySmall)
                        Text("60 min", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Equipment
            item {
                Column {
                    Text(
                        "Equipamentos Disponíveis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Equipment.entries) { equipment ->
                            val isSelected = selectedEquipment.contains(equipment)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val newEquipment = if (isSelected) {
                                        selectedEquipment - equipment
                                    } else {
                                        selectedEquipment + equipment
                                    }
                                    viewModel.updateEquipment(newEquipment)
                                },
                                label = { Text(equipment.displayName) }
                            )
                        }
                    }
                }
            }

            // Generate Button
            item {
                Button(
                    onClick = { viewModel.generateWorkout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gerar Treino", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Generated Workout
            generatedWorkout?.let { workout ->
                item {
                    WorkoutResultCard(
                        workout = workout,
                        onStart = { onStartWorkout(workout) }
                    )
                }
            }

            // Weekly Plan Button
            item {
                OutlinedButton(
                    onClick = { viewModel.generateWeeklyPlan(3) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gerar Plano Semanal")
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun WorkoutResultCard(
    workout: WorkoutTemplate,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        workout.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        workout.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${workout.duration}min",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Exercícios", "${workout.exercises.size}")
                StatItem("Calorias", "${workout.caloriesBurned}")
                StatItem("Dificuldade", workout.difficulty.displayName)
            }

            Spacer(Modifier.height(16.dp))

            // Exercises List
            Text(
                "Exercícios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            workout.exercises.take(5).forEach { exercise ->
                ExerciseItem(exercise)
            }

            if (workout.exercises.size > 5) {
                Text(
                    "+ ${workout.exercises.size - 5} mais exercícios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            // Start Button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Iniciar Treino", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: WorkoutExercise) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    getExerciseIcon(exercise.exercise.category),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                exercise.exercise.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                "${exercise.sets}x${exercise.reps} • ${exercise.restSeconds}s descanso",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getExerciseIcon(category: ExerciseCategory) = when (category) {
    ExerciseCategory.STRENGTH -> Icons.Filled.FitnessCenter
    ExerciseCategory.CARDIO -> Icons.Filled.DirectionsRun
    ExerciseCategory.HIIT -> Icons.Filled.Timer
    ExerciseCategory.FLEXIBILITY -> Icons.Filled.SelfImprovement
    ExerciseCategory.PLYOMETRICS -> Icons.Filled.SportsMartialArts
    ExerciseCategory.BODYWEIGHT -> Icons.Filled.AccessibilityNew
}
