package com.gymtracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.ui.theme.FitTrackPrimary
import com.gymtracker.ui.theme.FitTrackSurface
import com.gymtracker.ui.theme.FitTrackBackground
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.browser.window
import androidx.compose.ui.graphics.toComposeImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val vm: SettingsViewModel = viewModel { SettingsViewModel() }
    val configs by vm.configs.collectAsState()
    val activeWorkoutId by vm.activeWorkoutId.collectAsState()

    var editingDates by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingExercises by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações de Treino") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Gerenciar Treinos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(configs) { config ->
                WorkoutConfigCard(
                    config = config,
                    onToggleArchive = { vm.toggleArchive(config.workoutId) },
                    onSetActive = { vm.toggleActive(config.workoutId) },
                    onEditDates = { editingDates = config.workoutId },
                    onEditExercises = { editingExercises = config.workoutId },
                    onSetCoverImage = { vm.setCoverImage(config.workoutId, it) },
                    onSetYoutubeUrl = { vm.setYoutubeUrl(config.workoutId, it) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    editingDates?.let { workoutId ->
        val config = configs.find { it.workoutId == workoutId }
        if (config != null) {
            DatePickerDialog(
                config = config,
                onDismiss = { editingDates = null },
                onStartDateSet = { vm.setStartDate(workoutId, it) },
                onEndDateSet = { vm.setEndDate(workoutId, it) }
            )
        }
    }

    if (showCreateDialog) {
        CreateWorkoutDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, shortName ->
                vm.createWorkout(name, shortName)
                showCreateDialog = false
            }
        )
    }

    editingExercises?.let { workoutId ->
        val config = configs.find { it.workoutId == workoutId }
        if (config != null) {
            ExerciseEditorDialog(
                workoutId = workoutId,
                workoutName = config.workoutName,
                onDismiss = { editingExercises = null },
                vm = vm
            )
        }
    }
}

@Composable
fun WorkoutConfigCard(
    config: WorkoutConfigUiState,
    onToggleArchive: () -> Unit,
    onSetActive: () -> Unit,
    onEditDates: () -> Unit,
    onEditExercises: () -> Unit,
    onSetCoverImage: (String?) -> Unit,
    onSetYoutubeUrl: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (config.isActive)
                FitTrackPrimary.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = if (config.isActive)
                        FitTrackPrimary.copy(alpha = 0.3f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            config.shortName,
                            color = if (config.isActive) FitTrackPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        config.workoutName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (config.isArchived) {
                        Text(
                            "Arquivado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (config.isActive) {
                        Text(
                            "Treino Atual",
                            style = MaterialTheme.typography.bodySmall,
                            color = FitTrackPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!config.isArchived) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = config.isActive,
                            onCheckedChange = { onSetActive() }
                        )
                        Text("Atual", style = MaterialTheme.typography.labelSmall)
                    }
                }

                FilterChip(
                    selected = config.isArchived,
                    onClick = onToggleArchive,
                    label = {
                        Text(
                            if (config.isArchived) "Desarquivar" else "Arquivar",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )

                FilterChip(
                    selected = false,
                    onClick = onEditDates,
                    label = { Text("Datas", style = MaterialTheme.typography.labelSmall) }
                )

                FilterChip(
                    selected = false,
                    onClick = onEditExercises,
                    label = { Text("Editar", style = MaterialTheme.typography.labelSmall) }
                )
            }

            val dateRange = formatDateRange(config.startDate, config.endDate)
            if (dateRange != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    dateRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            var showCoverInput by remember { mutableStateOf(false) }
            var showYoutubeInput by remember { mutableStateOf(false) }
            var coverInputValue by remember(config.coverImage) { mutableStateOf(config.coverImage ?: "") }
            var youtubeInputValue by remember(config.youtubeUrl) { mutableStateOf(config.youtubeUrl ?: "") }
            var coverUploadSuccess by remember { mutableStateOf(false) }

            // Cover image preview
            if (config.coverImage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Capa do treino:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    // Try to decode and show the cover image
                    val bitmap = remember(config.coverImage) {
                        try {
                            val cleanBase64 = if (config.coverImage!!.contains(",")) {
                                config.coverImage!!.substringAfter(",")
                            } else {
                                config.coverImage!!
                            }
                            val decoded = window.atob(cleanBase64)
                            val bytes = ByteArray(decoded.length) { decoded[it].code.toByte() }
                            org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap,
                            contentDescription = "Capa do treino",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            "Erro ao carregar capa",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Upload success confirmation
            if (coverUploadSuccess) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Capa salva com sucesso!",
                    style = MaterialTheme.typography.labelSmall,
                    color = FitTrackPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (config.youtubeUrl != null) {
                    Text("Video definido", style = MaterialTheme.typography.labelSmall, color = FitTrackPrimary)
                }
            }

            if (showCoverInput) {
                Spacer(Modifier.height(4.dp))
                com.gymtracker.ui.components.ImageCropUpload(
                    currentCover = config.coverImage,
                    onCoverSelected = { base64 ->
                        onSetCoverImage(base64)
                        showCoverInput = false
                        coverUploadSuccess = true
                    },
                    onDismiss = { showCoverInput = false }
                )
            }

            if (showYoutubeInput) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = youtubeInputValue,
                        onValueChange = { youtubeInputValue = it },
                        modifier = Modifier.weight(1f).height(48.dp),
                        placeholder = { Text("URL do YouTube") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = {
                        onSetYoutubeUrl(youtubeInputValue.ifBlank { null })
                        showYoutubeInput = false
                    }) { Text("OK") }
                }
            }

            if (!showCoverInput && !showYoutubeInput) {
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showCoverInput = true }) {
                        Text(if (config.coverImage != null) "Editar Capa" else "Adicionar Capa", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(onClick = { showYoutubeInput = true }) {
                        Text(if (config.youtubeUrl != null) "Editar Vídeo" else "Adicionar Vídeo", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    config: WorkoutConfigUiState,
    onDismiss: () -> Unit,
    onStartDateSet: (Long?) -> Unit,
    onEndDateSet: (Long?) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(true) }

    val startMillis = config.startDate
    val endMillis = config.endDate

    val initialStartDate = if (startMillis != null) {
        Instant.fromEpochMilliseconds(startMillis)
    } else {
        kotlinx.datetime.Clock.System.now()
    }

    val initialEndDate = if (endMillis != null) {
        Instant.fromEpochMilliseconds(endMillis)
    } else {
        kotlinx.datetime.Clock.System.now()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (showStartPicker) startMillis else endMillis
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (showStartPicker) "Data de Início" else "Data de Término",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = showStartPicker,
                        onClick = { showStartPicker = true },
                        label = { Text("Início") }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = !showStartPicker,
                        onClick = { showStartPicker = false },
                        label = { Text("Término") }
                    )
                }
                Spacer(Modifier.height(12.dp))
                DatePicker(state = datePickerState)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selected = datePickerState.selectedDateMillis
                if (showStartPicker) {
                    onStartDateSet(selected)
                    showStartPicker = false
                } else {
                    onEndDateSet(selected)
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun ExerciseEditorDialog(
    workoutId: String,
    workoutName: String,
    onDismiss: () -> Unit,
    vm: SettingsViewModel
) {
    var exercises by remember { mutableStateOf<List<ExerciseEditItem>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var addingExercise by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }

    LaunchedEffect(workoutId) {
        exercises = vm.loadExercises(workoutId)
        loaded = true
    }

    if (!loaded) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar: $workoutName", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(exercises.size) { idx ->
                    val exercise = exercises[idx]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    exercise.exerciseName,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = {
                                    exercises = exercises.toMutableList().also { it.removeAt(idx) }
                                }) {
                                    Text("Remover", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            exercise.sets.forEachIndexed { setIdx, setItem ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("S${setItem.setNumber}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(24.dp))

                                    // Set type dropdown
                                    var expanded by remember { mutableStateOf(false) }
                                    Box {
                                        FilterChip(
                                            selected = true,
                                            onClick = { expanded = true },
                                            label = { Text(setItem.setType, style = MaterialTheme.typography.labelSmall) }
                                        )
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            listOf("WARMUP", "WORKING", "HARD").forEach { type ->
                                                DropdownMenuItem(
                                                    text = { Text(type) },
                                                    onClick = {
                                                        exercises = exercises.toMutableList().also {
                                                            it[idx] = it[idx].copy(
                                                                sets = it[idx].sets.toMutableList().also { s ->
                                                                    s[setIdx] = s[setIdx].copy(setType = type)
                                                                }
                                                            )
                                                        }
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.width(4.dp))

                                    OutlinedTextField(
                                        value = setItem.repsTarget,
                                        onValueChange = { newReps ->
                                            exercises = exercises.toMutableList().also {
                                                it[idx] = it[idx].copy(
                                                    sets = it[idx].sets.toMutableList().also { s ->
                                                        s[setIdx] = s[setIdx].copy(repsTarget = newReps)
                                                    }
                                                )
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        placeholder = { Text("Reps") },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )

                                    Spacer(Modifier.width(4.dp))

                                    OutlinedTextField(
                                        value = setItem.suggestedWeight,
                                        onValueChange = { newWeight ->
                                            exercises = exercises.toMutableList().also {
                                                it[idx] = it[idx].copy(
                                                    sets = it[idx].sets.toMutableList().also { s ->
                                                        s[setIdx] = s[setIdx].copy(suggestedWeight = newWeight)
                                                    }
                                                )
                                            }
                                        },
                                        modifier = Modifier.width(64.dp).height(48.dp),
                                        placeholder = { Text("Kg") },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            // Add set button
                            TextButton(onClick = {
                                val nextSetNum = exercise.sets.size + 1
                                exercises = exercises.toMutableList().also {
                                    it[idx] = it[idx].copy(
                                        sets = it[idx].sets.toMutableList().also { s ->
                                            s.add(SetEditItem(nextSetNum, "WORKING", "10 a 12", ""))
                                        }
                                    )
                                }
                            }) {
                                Text("+ Adicionar Série", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Add exercise section
                if (addingExercise) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newExerciseName,
                                onValueChange = { newExerciseName = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Nome do exercício") },
                                singleLine = true
                            )
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = {
                                if (newExerciseName.isNotBlank()) {
                                    exercises = exercises + ExerciseEditItem(
                                        exerciseName = newExerciseName,
                                        sets = mutableListOf(
                                            SetEditItem(1, "WORKING", "10 a 12", "")
                                        )
                                    )
                                    newExerciseName = ""
                                    addingExercise = false
                                }
                            }) { Text("OK") }
                            TextButton(onClick = { addingExercise = false; newExerciseName = "" }) { Text("X") }
                        }
                    }
                } else {
                    item {
                        TextButton(onClick = { addingExercise = true }) {
                            Text("+ Adicionar Exercício")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                vm.saveExercises(workoutId, exercises)
                onDismiss()
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun CreateWorkoutDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, shortName: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shortName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criar Novo Treino", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do treino") },
                    placeholder = { Text("Treino E") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shortName,
                    onValueChange = { shortName = it },
                    label = { Text("Nome curto") },
                    placeholder = { Text("E") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, shortName) },
                enabled = name.isNotBlank() && shortName.isNotBlank()
            ) { Text("Criar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private fun formatDateRange(start: Long?, end: Long?): String? {
    if (start == null && end == null) return null
    val tz = TimeZone.currentSystemDefault()

    fun formatMillis(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(tz).date
        return "${localDate.dayOfMonth.toString().padStart(2, '0')}/${localDate.monthNumber.toString().padStart(2, '0')}/${localDate.year}"
    }

    return when {
        start != null && end != null -> "Válido: ${formatMillis(start)} até ${formatMillis(end)}"
        start != null -> "Início: ${formatMillis(start)}"
        end != null -> "Término: ${formatMillis(end)}"
        else -> null
    }
}
