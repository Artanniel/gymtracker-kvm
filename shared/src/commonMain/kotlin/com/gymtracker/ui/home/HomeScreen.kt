package com.gymtracker.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.GoalType
import com.gymtracker.data.model.Workout
import com.gymtracker.data.model.WorkoutData
import com.gymtracker.util.AvailabilityStatus
import com.gymtracker.util.checkAvailability
import com.gymtracker.util.formatDaysAgo

@Composable
fun HomeScreen(onStartWorkout: (String) -> Unit) {
    val vm: HomeViewModel = viewModel { HomeViewModel() }
    val lastDates by vm.lastDates.collectAsState()
    val goals by vm.goals.collectAsState()
    var earlyWorkout by remember { mutableStateOf<Pair<Workout, AvailabilityStatus.Waiting>?>(null) }
    var editingGoal by remember { mutableStateOf<GoalType?>(null) }

    LaunchedEffect(Unit) { vm.load() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Periodização Mês 1",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp))
        }

        items(WorkoutData.workouts) { workout ->
            WorkoutCard(
                workout = workout,
                lastDate = lastDates[workout.id],
                onClick = { onStartWorkout(workout.id) }
            )
        }

        item {
            Text("Treinos Extras",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp))
        }

        items(WorkoutData.extraWorkouts) { workout ->
            val lastMs = lastDates[workout.id]
            val status = checkAvailability(lastMs, workout.minRestDays, workout.idealRestDays)
            ExtraWorkoutCard(
                workout = workout,
                lastDate = lastMs,
                status = status,
                onClick = {
                    if (status is AvailabilityStatus.Waiting) {
                        earlyWorkout = workout to status
                    } else {
                        onStartWorkout(workout.id)
                    }
                }
            )
        }

        item {
            Text("Metas Diárias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp))
        }

        items(goals) { goal ->
            DailyGoalCard(
                state = goal,
                onSave = { actual -> vm.saveGoal(goal.type, goal.target, actual) },
                onEditTarget = { editingGoal = goal.type }
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }

    // Diálogo: treinar antes do prazo
    earlyWorkout?.let { (workout, status) ->
        AlertDialog(
            onDismissRequest = { earlyWorkout = null },
            title = { Text(workout.name) },
            text = { Text("Você treinou isso recentemente. O ideal é esperar ${workout.minRestDays} dias (faltam ${status.daysLeft}). Quer treinar mesmo assim?") },
            confirmButton = {
                TextButton(onClick = { earlyWorkout = null; onStartWorkout(workout.id) }) { Text("Treinar mesmo assim") }
            },
            dismissButton = {
                TextButton(onClick = { earlyWorkout = null }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo: editar meta
    editingGoal?.let { type ->
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { editingGoal = null },
            title = { Text("Nova meta de ${type.displayName}") },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Meta (${type.unit})") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    input.toIntOrNull()?.let { vm.updateTarget(type, it) }
                    editingGoal = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { editingGoal = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun WorkoutCard(workout: Workout, lastDate: Long?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(workout.shortName, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("${workout.exercises.size} exercícios", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDaysAgo(lastDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ExtraWorkoutCard(workout: Workout, lastDate: Long?, status: AvailabilityStatus, onClick: () -> Unit) {
    val availText = when (status) {
        is AvailabilityStatus.NeverDone, is AvailabilityStatus.Available -> "✅ Disponível"
        is AvailabilityStatus.Waiting -> "⏳ Libera em ${status.daysLeft} dia(s)"
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(3.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(workout.shortName, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("${workout.exercises.size} exercícios", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDaysAgo(lastDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(availText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold,
                    color = if (status is AvailabilityStatus.Waiting) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DailyGoalCard(
    state: com.gymtracker.data.repository.GoalState,
    onSave: (Int) -> Unit,
    onEditTarget: () -> Unit
) {
    var input by remember(state.type) { mutableStateOf(state.actual?.toString() ?: "") }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(state.type.icon, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(state.type.displayName, fontWeight = FontWeight.Bold)
                    Text("Meta: ${state.target} ${state.type.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onEditTarget) { Icon(Icons.Default.Edit, "Editar meta") }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Hoje (${state.type.unit})") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { input.toIntOrNull()?.let(onSave) }) { Text("Salvar") }
            }
            if (state.actual != null) {
                Spacer(Modifier.height(6.dp))
                val color = if (state.achieved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                val msg = if (state.achieved) "✅ Meta atingida!" else "Faltam ${state.target - state.actual} ${state.type.unit}"
                Text(msg, color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
