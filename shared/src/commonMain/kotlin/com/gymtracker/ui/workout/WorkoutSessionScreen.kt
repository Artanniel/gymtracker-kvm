package com.gymtracker.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.SetType
import com.gymtracker.util.formatTimer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(workoutId: String, onFinished: () -> Unit) {
    val vm: WorkoutSessionViewModel = viewModel(key = workoutId) { WorkoutSessionViewModel(workoutId) }
    val rows by vm.rows.collectAsState()
    val timerSecs by vm.timerSeconds.collectAsState()
    val timerRunning by vm.timerRunning.collectAsState()
    val notes by vm.notes.collectAsState()
    val workout = vm.workout

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onFinished) { Icon(Icons.Default.ArrowBack, "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(padding)
        ) {
            // Tip do treino
            workout?.tip?.let { tip ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Text("💡 $tip", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Timer de descanso
            if (timerRunning || timerSecs > 0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⏱ Descanso", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
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
            }

            // Linhas de exercício e série
            var lastExercise = ""
            rows.forEach { row ->
                if (row.exerciseName != lastExercise) {
                    lastExercise = row.exerciseName
                    item(key = "header_${row.exerciseName}") {
                        Text(
                            row.exerciseName,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
                        )
                    }
                }
                item(key = "${row.exerciseId}_${row.setNumber}") {
                    SetRowCard(
                        row = row,
                        onDone = { weight, reps ->
                            vm.updateSet(row.exerciseId, row.setNumber, weight, reps)
                        }
                    )
                }
            }

            // Notas
            item {
                Spacer(Modifier.height(8.dp))
                com.gymtracker.ui.components.FitTrackTextField(
                    value = notes,
                    onValueChange = vm::setNotes,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Observações (sono, estresse...)"
                )
            }

            // Finalizar
            item {
                com.gymtracker.ui.components.FitTrackButton(
                    onClick = { vm.finish(); onFinished() },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    text = "Finalizar Treino ✓"
                )
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SetRowCard(row: SetRowUiState, onDone: (Double?, Int?) -> Unit) {
    var weightInput by remember(row.exerciseId, row.setNumber) { mutableStateOf(row.weightKg?.toString() ?: "") }
    var repsInput by remember(row.exerciseId, row.setNumber) { mutableStateOf(row.repsActual?.toString() ?: "") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
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
                    SetType.HARD    -> "Hard Set 🔥"
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
                    Text("⏱ $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                com.gymtracker.ui.components.FitTrackTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    modifier = Modifier.weight(1f).padding(end = 6.dp).height(50.dp),
                    placeholder = "Carga (kg)",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                com.gymtracker.ui.components.FitTrackTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    modifier = Modifier.weight(1f).padding(end = 8.dp).height(50.dp),
                    placeholder = "Reps",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Button(
                    onClick = { onDone(weightInput.toDoubleOrNull(), repsInput.toIntOrNull()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (row.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        contentColor = if (row.completed) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.height(50.dp)
                ) { Text(if (row.completed) "✓" else "OK") }
            }
        }
    }
}
