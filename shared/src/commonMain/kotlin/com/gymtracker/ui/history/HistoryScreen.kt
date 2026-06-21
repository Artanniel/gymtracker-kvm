package com.gymtracker.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymtracker.db.Workout_sessions
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {
    val vm: HistoryViewModel = viewModel { HistoryViewModel() }
    val sessions by vm.sessions.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Column {
        Text("Histórico de Treinos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(20.dp))

        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Nenhum treino registrado ainda.\nInicie um treino na tela principal!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sessions) { session -> SessionCard(session) }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun SessionCard(session: Workout_sessions) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(session.workoutName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(sdf.format(Date(session.date)), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            if (session.notes.isNotBlank()) {
                Text("\"${session.notes}\"", style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}
