package com.gymtracker.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.data.model.Workout
import com.gymtracker.data.model.WorkoutData
import com.gymtracker.data.model.GoalType
import com.gymtracker.ui.ads.NativeAdCard
import com.gymtracker.ui.animations.*
import com.gymtracker.ui.notifications.NotificationsViewModel
import com.gymtracker.ui.streak.StreakViewModel
import com.gymtracker.ui.streak.StreakCard
import com.gymtracker.ui.streak.BadgeGrid
import com.gymtracker.ui.settings.SettingsViewModel
import com.gymtracker.util.AvailabilityStatus
import com.gymtracker.util.checkAvailability
import com.gymtracker.util.formatDaysAgo
import com.gymtracker.util.decodeBase64ToImageBitmap

@Composable
fun HomeScreen(
    onStartWorkout: (String) -> Unit,
    onSettings: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onSync: () -> Unit = {},
    pendingSyncCount: Int = 0
) {
    val vm: HomeViewModel = viewModel { HomeViewModel() }
    val streakVm: StreakViewModel = viewModel { StreakViewModel() }
    val notificationsVm: NotificationsViewModel = viewModel { NotificationsViewModel() }
    val settingsVm: SettingsViewModel = viewModel { SettingsViewModel() }
    val lastDates by vm.lastDates.collectAsState()
    val goals by vm.goals.collectAsState()
    val streakData by streakVm.streakData.collectAsState()
    val unlockedBadges by streakVm.unlockedBadges.collectAsState()
    val unreadCount by notificationsVm.unreadCount.collectAsState()
    val settingsConfigs by settingsVm.configs.collectAsState()
    var earlyWorkout by remember { mutableStateOf<Pair<Workout, AvailabilityStatus.Waiting>?>(null) }
    var editingGoal by remember { mutableStateOf<GoalType?>(null) }

    LaunchedEffect(Unit) {
        vm.load()
        streakVm.load()
        notificationsVm.load()
        settingsVm.load()
    }

    val archivedIds = remember(settingsConfigs) {
        settingsConfigs.filter { it.isArchived }.map { it.workoutId }.toSet()
    }

    val activeIds = remember(settingsConfigs) {
        settingsConfigs.filter { it.isActive }.map { it.workoutId }.toSet()
    }

    val activeWorkouts = remember(WorkoutData.workouts, archivedIds, activeIds) {
        WorkoutData.workouts.filter { it.id !in archivedIds && it.id in activeIds }
    }

    val activeExtraWorkouts = remember(WorkoutData.extraWorkouts, archivedIds, activeIds) {
        WorkoutData.extraWorkouts.filter { it.id !in archivedIds && it.id in activeIds }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(AnimationDurations.NORMAL)) + slideInVertically(
                    initialOffsetY = { -it / 20 },
                    animationSpec = tween(AnimationDurations.NORMAL, easing = AnimationEasing.Decelerate)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Treino atual",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    Row {
                        BadgedBox(
                            badge = {
                                if (pendingSyncCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    ) {
                                        Text("$pendingSyncCount")
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = onSync) {
                                Icon(
                                    Icons.Filled.CloudSync,
                                    contentDescription = "Sincronização",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = onNotifications) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "Notificações",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = onSettings) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Configurações",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Streak Card
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(AnimationDurations.NORMAL, delayMillis = 100)) +
                        slideInVertically(
                            initialOffsetY = { it / 20 },
                            animationSpec = tween(AnimationDurations.NORMAL, easing = AnimationEasing.Decelerate)
                        )
            ) {
                StreakCard(streakData = streakData)
            }
        }

        // Badges Section
        if (unlockedBadges.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(AnimationDurations.NORMAL, delayMillis = 200)) +
                            slideInVertically(
                                initialOffsetY = { it / 20 },
                                animationSpec = tween(AnimationDurations.NORMAL, easing = AnimationEasing.Decelerate)
                            )
                ) {
                    BadgeGrid(badges = unlockedBadges)
                }
            }
        }

        if (activeWorkouts.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.FitnessCenter,
                    title = "Nenhum treino ativo",
                    subtitle = "Ative treinos nas configurações para começar"
                )
            }
        } else {
            itemsIndexed(activeWorkouts) { index, workout ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(AnimationDurations.NORMAL, delayMillis = index * 100)) +
                            slideInVertically(
                                initialOffsetY = { it / 20 },
                                animationSpec = tween(AnimationDurations.NORMAL, easing = AnimationEasing.Decelerate)
                            )
                ) {
                    val config = settingsConfigs.find { it.workoutId == workout.id }
                    WorkoutCard(
                        workout = workout,
                        lastDate = lastDates[workout.id],
                        coverImage = config?.coverImage,
                        youtubeUrl = config?.youtubeUrl,
                        onClick = { onStartWorkout(workout.id) }
                    )
                }
            }
        }

        // Ad placement between sections
        if (activeWorkouts.isNotEmpty()) {
            item {
                NativeAdCard(
                    headline = "Equipamentos para seu home gym",
                    description = "Monte seu espaço de treino com até 40% de desconto",
                    buttonText = "Ver ofertas"
                )
            }
        }

        item {
            Text("Treinos Extras",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 12.dp))
        }

        if (activeExtraWorkouts.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.SelfImprovement,
                    title = "Nenhum treino extra",
                    subtitle = "Treinos extras aparecerão aqui"
                )
            }
        } else {
            itemsIndexed(activeExtraWorkouts) { index, workout ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(AnimationDurations.NORMAL, delayMillis = index * 100)) +
                            slideInVertically(
                                initialOffsetY = { it / 20 },
                                animationSpec = tween(AnimationDurations.NORMAL, easing = AnimationEasing.Decelerate)
                            )
                ) {
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
            }
        }

        item {
            Text("Metas Diárias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 12.dp))
        }

        if (goals.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Filled.Add,
                    title = "Nenhuma meta definida",
                    subtitle = "Defina metas de água, proteína ou sono"
                )
            }
        } else {
            items(goals) { goal ->
                DailyGoalCard(
                    state = goal,
                    onSave = { actual -> vm.saveGoal(goal.type, goal.target, actual) },
                    onEditTarget = { editingGoal = goal.type }
                )
            }
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
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    lastDate: Long?,
    coverImage: String? = null,
    youtubeUrl: String? = null,
    onClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "cardScale")
    
    // Get workout icon based on name
    val workoutIcon = when {
        workout.name.contains("Corrida", ignoreCase = true) || 
        workout.name.contains("Cardio", ignoreCase = true) -> Icons.AutoMirrored.Filled.DirectionsRun
        workout.name.contains("Yoga", ignoreCase = true) || 
        workout.name.contains("Alongamento", ignoreCase = true) -> Icons.Filled.SelfImprovement
        else -> Icons.Filled.FitnessCenter
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 8.dp)
    ) {
        Column {
            if (coverImage != null) {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    // Try to decode base64 image
                    val imageBitmap = remember(coverImage) {
                        decodeBase64ToImageBitmap(coverImage)
                    }

                    if (imageBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = imageBitmap,
                            contentDescription = "Capa do treino",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback: show placeholder
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                workout.shortName,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Play button overlay for YouTube
                    if (youtubeUrl != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable {
                                    uriHandler.openUri(youtubeUrl)
                                },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Filled.PlayArrow,
                                    contentDescription = "Assistir vídeo",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            workoutIcon,
                            contentDescription = "Ícone do treino",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(workout.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(4.dp))
                    Text("${workout.exercises.size} exercícios", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatDaysAgo(lastDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Iniciar treino",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ExtraWorkoutCard(workout: Workout, lastDate: Long?, status: AvailabilityStatus, onClick: () -> Unit) {
    val availText = when (status) {
        is AvailabilityStatus.NeverDone, is AvailabilityStatus.Available -> "OK Disponível"
        is AvailabilityStatus.Waiting -> "Aguardar ${status.daysLeft} dia(s)"
    }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "extraCardScale")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = "Ícone do treino extra",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            state.type.icon,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(state.type.displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Meta: ${state.target} ${state.type.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onEditTarget) {
                    Text("META", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.gymtracker.ui.components.FitTrackTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f).height(50.dp),
                    placeholder = "Hoje (${state.type.unit})",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Spacer(Modifier.width(12.dp))
                com.gymtracker.ui.components.FitTrackButton(
                    text = "Salvar",
                    onClick = { input.toIntOrNull()?.let(onSave) }
                )
            }
            if (state.actual != null) {
                Spacer(Modifier.height(8.dp))
                val color = if (state.achieved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                val msg = if (state.achieved) "Meta atingida!" else "Faltam ${state.target - state.actual} ${state.type.unit}"
                Text(msg, color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
