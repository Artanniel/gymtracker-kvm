package com.gymtracker.ui.progress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.ExerciseProgress
import com.gymtracker.data.model.SuggestedLoad

@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = viewModel { ProgressViewModel() }
    val exercises by vm.exercises.collectAsState()
    val selected by vm.selectedProgress.collectAsState()

    LaunchedEffect(Unit) { vm.loadExercises() }

    if (selected != null && selected != ExerciseProgress.Empty) {
        ProgressDetailScreen(selected!!, onBack = { vm.clearSelection() })
    } else {
        ProgressListScreen(exercises, onSelect = { id, name -> vm.selectExercise(id, name) })
    }
}

@Composable
private fun ProgressListScreen(exercises: List<ExerciseEntry>, onSelect: (String, String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Evolução por Exercício",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Toque em um exercício para ver seu histórico de cargas e sugestão para o próximo treino.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(exercises) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(entry.id, entry.name) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(">>", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            entry.name, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            entry.workoutName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDetailScreen(progress: ExerciseProgress, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(progress.exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (progress.dataPoints.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhum dado de evolução ainda.\nComplete um treino para ver sua evolução!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    Text(
                        "Histórico de Cargas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                item { ProgressLineChart(progress) }

                item { ProgressTable(progress) }

                if (progress.suggestion != null) {
                    item { SuggestionCard(progress.suggestion) }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }
}

@Composable
private fun ProgressLineChart(progress: ExerciseProgress) {
    val points = progress.dataPoints
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Carga (kg) por sessão",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                if (points.isEmpty()) return@Canvas

                val maxW = points.maxOf { it.weightKg }
                val minW = points.minOf { it.weightKg }
                val range = (maxW - minW).coerceAtLeast(1.0)
                val left = 40f; val right = size.width - 8f; val top = 12f; val bottom = size.height - 24f

                val xStep = (right - left) / (points.size - 1).coerceAtLeast(1)
                val coords = points.mapIndexed { i, pt ->
                    val x = left + i * xStep
                    val y = bottom - ((pt.weightKg - minW) / range * (bottom - top)).toFloat()
                    Offset(x, y)
                }

                for (i in 0..4) {
                    val y = top + (bottom - top) * i / 4f
                    drawLine(Color(0xFF3A4A3A), Offset(left, y), Offset(right, y), strokeWidth = 0.5f)
                }

                val path = Path().apply { moveTo(coords.first().x, coords.first().y) }
                coords.drop(1).forEach { path.lineTo(it.x, it.y) }
                drawPath(path, Color(0xFF17CF17), style = Stroke(width = 3f))

                coords.forEach { drawCircle(Color(0xFF17CF17), 5f, it) }
            }
        }
    }
}

@Composable
private fun ProgressTable(progress: ExerciseProgress) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Últimas 5 Sessões",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            val last = progress.dataPoints.takeLast(5).reversed()
            last.forEach { pt ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${pt.weightKg} kg",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "× ${pt.repsActual} reps",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: SuggestedLoad) {
    val confidenceColor = when (suggestion.confidence) {
        SuggestedLoad.Confidence.HIGH -> Color(0xFF27AE60)
        SuggestedLoad.Confidence.MEDIUM -> Color(0xFFF2994A)
        SuggestedLoad.Confidence.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val confidenceLabel = when (suggestion.confidence) {
        SuggestedLoad.Confidence.HIGH -> "Alta confiança"
        SuggestedLoad.Confidence.MEDIUM -> "Média confiança"
        SuggestedLoad.Confidence.LOW -> "Baixa confiança"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "Sugestão para Próximo Treino",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Carga", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatWeight1(suggestion.suggestedWeightKg) + " kg",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Repetições", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${suggestion.suggestedReps} reps",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                suggestion.strategy,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(confidenceLabel, fontSize = 12.sp, color = confidenceColor)
        }
    }
}

private fun formatWeight1(value: Double): String {
    val intPart = value.toInt()
    val frac = ((value - intPart) * 10).toInt().coerceIn(0, 9)
    return "$intPart.$frac"
}
