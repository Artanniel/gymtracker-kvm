package com.gymtracker.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.AppDependencies
import com.gymtracker.data.notification.NotificationScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit) {
    val scheduler = remember { NotificationScheduler() }

    var enabled by remember { mutableStateOf(true) }
    var workoutHour by remember { mutableStateOf(7) }
    var workoutMinute by remember { mutableStateOf(0) }
    var waterReminder by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scheduler.requestPermission()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações de Notificações") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = MaterialTheme.shapes.medium) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notificações push", fontWeight = FontWeight.SemiBold)
                        Text("Receba lembretes e alertas", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
            }

            Card(shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lembrete de treino", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${workoutHour.toString().padStart(2, '0')}:${workoutMinute.toString().padStart(2, '0')}",
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Button(onClick = {
                            if (enabled) scheduler.scheduleWorkoutReminder(workoutHour, workoutMinute)
                        }, enabled = enabled) {
                            Text("Agendar")
                        }
                    }
                    Slider(
                        value = workoutHour.toFloat(),
                        onValueChange = { workoutHour = it.toInt() },
                        valueRange = 5f..22f,
                        steps = 16
                    )
                    Text("Horário do lembrete diário", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Card(shape = MaterialTheme.shapes.medium) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lembrete de água", fontWeight = FontWeight.SemiBold)
                        Text("A cada 2 horas durante o dia", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = waterReminder, onCheckedChange = {
                        waterReminder = it
                        if (enabled) {
                            if (it) scheduler.scheduleWaterReminder(2)
                            else scheduler.cancelWaterReminders()
                        }
                    }, enabled = enabled)
                }
            }

            Button(
                onClick = {
                    scheduler.cancelWorkoutReminders()
                    scheduler.cancelWaterReminders()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancelar todas as notificações")
            }
        }
    }
}
