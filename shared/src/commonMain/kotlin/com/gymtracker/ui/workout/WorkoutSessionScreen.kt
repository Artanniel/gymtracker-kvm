package com.gymtracker.ui.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.SetType
import com.gymtracker.util.formatTimer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    workoutId: String,
    onFinished: () -> Unit,
    onViewProgress: (exerciseId: String, exerciseName: String) -> Unit = { _, _ -> }
) {
    val vm: WorkoutSessionViewModel = viewModel(key = workoutId) { WorkoutSessionViewModel(workoutId) }
    val rows by vm.rows.collectAsState()
    val timerSecs by vm.timerSeconds.collectAsState()
    val timerRunning by vm.timerRunning.collectAsState()
    val notes by vm.notes.collectAsState()
    val workout = vm.workout

    // Group rows by exercise
    val exerciseGroups = remember(rows) {
        rows.groupBy { it.exerciseId }
            .map { (exerciseId, exerciseRows) ->
                ExerciseGroup(
                    exerciseId = exerciseId,
                    exerciseName = exerciseRows.first().exerciseName,
                    sets = exerciseRows
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onFinished) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
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
        Column(modifier = Modifier.padding(padding)) {
            // Tip do treino
            workout?.tip?.let { tip ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("DICA: $tip", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Timer de descanso
            if (timerRunning || timerSecs > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DESCANSO", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text(formatTimer(timerSecs), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = vm::stopTimer,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) { Text(if (timerRunning) "Parar" else "Pronto!", color = MaterialTheme.colorScheme.background) }
                    }
                }
            }

            // Horizontal pager for exercises
            if (exerciseGroups.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { exerciseGroups.size })
                val coroutineScope = rememberCoroutineScope()

                Box(modifier = Modifier.weight(1f)) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val group = exerciseGroups[page]
                        ExerciseCard(
                            group = group,
                            onDone = { exerciseId, setNumber, weight, reps ->
                                vm.updateSet(exerciseId, setNumber, weight, reps)
                            },
                            onViewProgress = { onViewProgress(group.exerciseId, group.exerciseName) }
                        )
                    }

                    // Page indicator + navigation arrows overlaid on top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .zIndex(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                                coroutineScope.launch { pagerState.animateScrollToPage(prev) }
                            },
                            enabled = pagerState.currentPage > 0
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Exercício anterior",
                                tint = if (pagerState.currentPage > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            "${pagerState.currentPage + 1} / ${exerciseGroups.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = {
                                val next = (pagerState.currentPage + 1).coerceAtMost(exerciseGroups.size - 1)
                                coroutineScope.launch { pagerState.animateScrollToPage(next) }
                            },
                            enabled = pagerState.currentPage < exerciseGroups.size - 1
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Próximo exercício",
                                tint = if (pagerState.currentPage < exerciseGroups.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Notas + Finalizar (always visible below pager)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                com.gymtracker.ui.components.FitTrackTextField(
                    value = notes,
                    onValueChange = vm::setNotes,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Observações (sono, estresse...)"
                )

                com.gymtracker.ui.components.FitTrackButton(
                    onClick = { vm.finish(); onFinished() },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    text = "Finalizar Treino"
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private data class ExerciseGroup(
    val exerciseId: String,
    val exerciseName: String,
    val sets: List<SetRowUiState>
)

@Composable
private fun ExerciseCard(
    group: ExerciseGroup,
    onDone: (exerciseId: String, setNumber: Long, weight: Double?, reps: Int?) -> Unit,
    onViewProgress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Exercise name header with progress link
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                group.exerciseName,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onViewProgress) {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Ver progresso",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Set cards
        group.sets.forEach { row ->
            SetRowCard(
                row = row,
                onDone = { weight, reps -> onDone(row.exerciseId, row.setNumber, weight, reps) }
            )
        }
    }
}

@Composable
fun SetRowCard(row: SetRowUiState, onDone: (Double?, Int?) -> Unit) {
    var weightInput by remember(row.exerciseId, row.setNumber) { mutableStateOf(row.weightKg?.toString() ?: "") }
    var repsInput by remember(row.exerciseId, row.setNumber) { mutableStateOf(row.repsActual?.toString() ?: "") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val typeColor = when (row.setType) {
                    SetType.HARD -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
                val typeLabel = when (row.setType) {
                    SetType.WARMUP  -> "Aquecimento"
                    SetType.WORKING -> "Working Set"
                    SetType.HARD    -> "Hard Set"
                }
                Text("S${row.setNumber} · $typeLabel", fontWeight = FontWeight.Bold, color = typeColor, modifier = Modifier.weight(1f))
                Text("${row.repsTarget} reps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Última sessão + badge de progressão
            Row(modifier = Modifier.padding(top = 2.dp)) {
                row.lastWeight?.let {
                    Text("Última vez: ${it}kg × ${row.lastReps ?: 0} reps",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f))
                }
                row.progressBadge?.let {
                    Text(it.first, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = it.second)
                }
                row.restRecorded?.let {
                    Text("Tempo: $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                com.gymtracker.ui.components.FitTrackTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    modifier = Modifier.weight(1f).padding(end = 6.dp),
                    placeholder = "Carga (kg)",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                com.gymtracker.ui.components.FitTrackTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    placeholder = "Reps",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Button(
                    onClick = { onDone(weightInput.toDoubleOrNull(), repsInput.toIntOrNull()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (row.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        contentColor = if (row.completed) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.height(48.dp).widthIn(min = 48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (row.completed) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Concluído",
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "OK",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
