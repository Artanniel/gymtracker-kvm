package com.gymtracker.ui.sync

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.data.sync.SyncManager
import com.gymtracker.data.sync.SyncState
import com.gymtracker.data.sync.ConnectivityMonitor
import com.gymtracker.data.sync.PendingSync
import com.gymtracker.data.sync.AppConfig
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStatusScreen(
    syncManager: SyncManager,
    connectivityMonitor: ConnectivityMonitor,
    onBack: () -> Unit
) {
    val syncState by syncManager.syncState.collectAsState()
    val pendingCount by syncManager.pendingCount.collectAsState()
    val isOnline by connectivityMonitor.isOnline.collectAsState()
    val scope = rememberCoroutineScope()
    
    var failedSyncs by remember { mutableStateOf<List<PendingSync>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        try {
            failedSyncs = syncManager.getFailedSyncs().map { db ->
                PendingSync(
                    id = db.id,
                    entityType = db.entity_type,
                    entityId = db.entity_id,
                    action = com.gymtracker.data.sync.SyncAction.valueOf(db.action),
                    payload = db.payload,
                    createdAt = db.created_at,
                    retryCount = db.retry_count.toInt(),
                    lastError = db.last_error,
                    synced = db.synced == 1L
                )
            }
        } catch (e: Exception) {
            println("Error loading failed syncs: ${e.message}")
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Sincronização",
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.Cloud,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111811)
                )
            )
        },
        containerColor = Color(0xFF111811)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
            item {
                SyncStatusCard(
                    isOnline = isOnline,
                    syncState = syncState,
                    pendingCount = pendingCount,
                    onSyncNow = {
                        scope.launch {
                            syncManager.processPendingSync()
                        }
                    }
                )
            }
            
            // Config Card
            item {
                AppConfigCard()
            }
            
            // Failed Syncs Section
            if (failedSyncs.isNotEmpty()) {
                item {
                    Text(
                        text = "Falhas de Sincronização",
                        color = Color(0xFFFF6B6B),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(failedSyncs) { sync ->
                    FailedSyncCard(
                        sync = sync,
                        onRetry = {
                            scope.launch {
                                syncManager.processPendingSync()
                                // Refresh list
                                failedSyncs = syncManager.getFailedSyncs().map { db ->
                                    PendingSync(
                                        id = db.id,
                                        entityType = db.entity_type,
                                        entityId = db.entity_id,
                                        action = com.gymtracker.data.sync.SyncAction.valueOf(db.action),
                                        payload = db.payload,
                                        createdAt = db.created_at,
                                        retryCount = db.retry_count.toInt(),
                                        lastError = db.last_error,
                                        synced = db.synced == 1L
                                    )
                                }
                            }
                        }
                    )
                }
            }
            
            // Info Section
            item {
                SyncInfoCard()
            }
        }
    }
}

@Composable
fun SyncStatusCard(
    isOnline: Boolean,
    syncState: SyncState,
    pendingCount: Int,
    onSyncNow: () -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = if (isOnline) Color(0xFF4CAF50) else Color(0xFFFF6B6B)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    if (isOnline) Icons.Filled.Cloud else Icons.Filled.CloudOff,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sync state info
            when (syncState) {
                is SyncState.Idle -> {
                    Text(
                        text = if (pendingCount > 0) {
                            "$pendingCount operações pendentes"
                        } else {
                            "Sincronizado"
                        },
                        color = Color(0xFFB0B0B0),
                        fontSize = 14.sp
                    )
                }
                is SyncState.Syncing -> {
                    Text(
                        text = "Sincronizando...",
                        color = Color(0xFF64B5F6),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF4CAF50)
                    )
                }
                is SyncState.Progress -> {
                    Text(
                        text = "Sincronizando: ${syncState.current}/${syncState.total}",
                        color = Color(0xFF64B5F6),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { syncState.current.toFloat() / syncState.total.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF4CAF50)
                    )
                }
                is SyncState.Error -> {
                    Text(
                        text = "Erro: ${syncState.message}",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sync button
            Button(
                onClick = onSyncNow,
                enabled = isOnline && pendingCount > 0 && syncState !is SyncState.Syncing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color(0xFF2A3A2A)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sincronizar Agora")
            }
        }
    }
}

@Composable
fun AppConfigCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Configuração",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Backend:",
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
                Text(
                    text = AppConfig.getBaseUrl(),
                    color = Color(0xFF64B5F6),
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status:",
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
                Text(
                    text = if (AppConfig.isBackendConfigured()) "Configurado" else "Não configurado",
                    color = if (AppConfig.isBackendConfigured()) Color(0xFF4CAF50) else Color(0xFFFFD93D),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FailedSyncCard(
    sync: PendingSync,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = sync.entityType,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                AssistChip(
                    onClick = onRetry,
                    label = { Text("Tentar novamente") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF3A2A2A)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ação: ${sync.action.name}",
                color = Color(0xFFB0B0B0),
                fontSize = 12.sp
            )
            
            Text(
                text = "Tentativas: ${sync.retryCount}/3",
                color = Color(0xFFB0B0B0),
                fontSize = 12.sp
            )
            
            sync.lastError?.let { error ->
                Text(
                    text = "Erro: $error",
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp
                )
            }
            
            Text(
                text = "Criado: ${formatTimestamp(sync.createdAt)}",
                color = Color(0xFFB0B0B0),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SyncInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Como funciona",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "• Dados são salvos localmente primeiro",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )
            
            Text(
                text = "• Sincronização automática quando online",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )
            
            Text(
                text = "• Máximo 3 tentativas antes de marcar como falha",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )
            
            Text(
                text = "• Em conflitos, o servidor tem prioridade",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.date} ${localDateTime.time}"
    } catch (e: Exception) {
        "Data desconhecida"
    }
}
