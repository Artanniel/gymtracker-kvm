package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.model.ProgressStatus
import com.gymtracker.data.model.WorkoutData
import com.gymtracker.data.sync.ConnectivityMonitor
import com.gymtracker.data.sync.SyncAction
import com.gymtracker.data.sync.SyncManager
import com.gymtracker.db.Set_logs
import com.gymtracker.db.Workout_configs
import com.gymtracker.db.Workout_exercises
import com.gymtracker.db.Workout_sessions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.gymtracker.data.model.ExerciseProgress
import com.gymtracker.data.model.SuggestedLoad
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Repository híbrido que combina:
 * - Local DB (sempre, offline-first)
 * - Sync Manager (enfileira para backend)
 * 
 * Padrão:
 * 1. Salva no local DB (rápido, sempre funciona)
 * 2. Enfileira para sync (se online)
 * 3. Sync processa fila e envia ao backend
 */
class HybridWorkoutRepository(
    private val syncManager: SyncManager,
    private val connectivity: ConnectivityMonitor
) {
    private val db get() = DatabaseProvider.get()
    private val sessionQ get() = db.workoutSessionQueries
    private val setQ get() = db.setLogQueries
    private val configQ get() = db.workoutConfigQueries
    
    // ═══════════════════════════════════════════════════════════════════
    // SESSIONS (Leitura - sempre local)
    // ═══════════════════════════════════════════════════════════════════
    
    fun getAllSessionsFlow(): Flow<List<Workout_sessions>> =
        sessionQ.getAll().asFlow().mapToList(Dispatchers.Default)
    
    fun getSessionsByWorkoutFlow(workoutId: String): Flow<List<Workout_sessions>> =
        sessionQ.getByWorkout(workoutId).asFlow().mapToList(Dispatchers.Default)
    
    suspend fun getLastSession(workoutId: String): Workout_sessions? =
        sessionQ.getLast(workoutId).awaitAsOneOrNull()
    
    suspend fun getSessionById(id: Long): Workout_sessions? =
        sessionQ.getById(id).awaitAsOneOrNull()
    
    // ═══════════════════════════════════════════════════════════════════
    // SESSIONS (Escrita - local + sync)
    // ═══════════════════════════════════════════════════════════════════
    
    suspend fun createSession(workoutId: String, workoutName: String): Long {
        // 1. Salva local (sempre)
        val now = Clock.System.now().toEpochMilliseconds()
        sessionQ.insert(workoutId, workoutName, now, "")
        val localId = sessionQ.lastInsertId().awaitAsOne()
        
        // 2. Enfileira sync
        val payload = buildJsonObject {
            put("id", localId)
            put("workoutId", workoutId)
            put("workoutName", workoutName)
            put("date", now)
            put("notes", "")
        }.toString()
        
        syncManager.queueForSync(
            entityType = "workout_session",
            entityId = localId.toString(),
            action = SyncAction.CREATE,
            payload = payload
        )
        
        return localId
    }
    
    suspend fun updateSessionNotes(id: Long, notes: String) {
        // 1. Atualiza local
        sessionQ.update(notes, id)
        
        // 2. Enfileira sync
        val payload = buildJsonObject {
            put("id", id)
            put("notes", notes)
        }.toString()
        
        syncManager.queueForSync(
            entityType = "workout_session",
            entityId = id.toString(),
            action = SyncAction.UPDATE,
            payload = payload
        )
    }
    
    suspend fun deleteSession(id: Long) {
        // 1. Deleta local
        sessionQ.delete(id)
        
        // 2. Enfileira sync
        syncManager.queueForSync(
            entityType = "workout_session",
            entityId = id.toString(),
            action = SyncAction.DELETE,
            payload = ""
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // SET LOGS (Leitura - sempre local)
    // ═══════════════════════════════════════════════════════════════════
    
    fun getSetLogsBySessionFlow(sessionId: Long): Flow<List<Set_logs>> =
        setQ.getBySession(sessionId).asFlow().mapToList(Dispatchers.Default)
    
    suspend fun getSetLogsBySession(sessionId: Long): List<Set_logs> =
        setQ.getBySession(sessionId).awaitAsList()
    
    suspend fun getExercisesBySession(sessionId: Long): List<Pair<String, String>> =
        setQ.getExercisesBySession(sessionId).awaitAsList().map { it.exerciseId to it.exerciseName }
    
    // ═══════════════════════════════════════════════════════════════════
    // SET LOGS (Escrita - local + sync)
    // ═══════════════════════════════════════════════════════════════════
    
    suspend fun insertSetLog(
        sessionId: Long,
        exerciseId: String,
        exerciseName: String,
        setNumber: Long,
        setType: String,
        repsTarget: String
    ): Long {
        // 1. Salva local
        setQ.insert(sessionId, exerciseId, exerciseName, setNumber, setType, repsTarget, null, null, 0, null)
        val localId = setQ.lastInsertId().awaitAsOne()
        
        // 2. Enfileira sync
        val payload = buildJsonObject {
            put("id", localId)
            put("sessionId", sessionId)
            put("exerciseId", exerciseId)
            put("exerciseName", exerciseName)
            put("setNumber", setNumber)
            put("setType", setType)
            put("repsTarget", repsTarget)
        }.toString()
        
        syncManager.queueForSync(
            entityType = "set_log",
            entityId = localId.toString(),
            action = SyncAction.CREATE,
            payload = payload
        )
        
        return localId
    }
    
    suspend fun updateSetLog(
        sessionId: Long,
        exerciseId: String,
        setNumber: Long,
        weightKg: Double?,
        repsActual: Long?,
        completed: Boolean,
        restSeconds: Long?
    ) {
        // 1. Atualiza local
        setQ.update(weightKg, repsActual, if (completed) 1L else 0L, restSeconds, sessionId, exerciseId, setNumber)
        
        // 2. Enfileira sync
        val payload = buildJsonObject {
            put("sessionId", sessionId)
            put("exerciseId", exerciseId)
            put("setNumber", setNumber)
            put("weightKg", weightKg ?: 0.0)
            put("repsActual", repsActual ?: 0)
            put("completed", completed)
            put("restSeconds", restSeconds ?: 0)
        }.toString()
        
        syncManager.queueForSync(
            entityType = "set_log",
            entityId = "${sessionId}_${exerciseId}_${setNumber}",
            action = SyncAction.UPDATE,
            payload = payload
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PROGRESS (Leitura - sempre local)
    // ═══════════════════════════════════════════════════════════════════
    
    suspend fun getProgressForSet(exerciseId: String, setNumber: Long): List<Set_logs> =
        setQ.getProgressForSet(exerciseId, setNumber).awaitAsList()
    
    suspend fun getExerciseProgress(exerciseId: String, setName: String): ExerciseProgress {
        val rows = db.setLogQueries.getByExerciseWithDate(exerciseId).awaitAsList()
        if (rows.isEmpty()) return ExerciseProgress.Empty
        
        val points = rows.map { 
            ExerciseProgress.DataPoint(it.weightKg!!, it.repsActual!!.toLong().toInt(), it.sessionDate) 
        }
        if (points.isEmpty()) return ExerciseProgress.Empty
        
        val suggestion = computeSuggestedLoad(points)
        return ExerciseProgress(exerciseId, setName, points, suggestion)
    }
    
    private fun computeSuggestedLoad(points: List<ExerciseProgress.DataPoint>): SuggestedLoad {
        val workingPoints = points.filter { it.weightKg > 0.0 && it.repsActual > 0 }
        if (workingPoints.size < 2) {
            val last = points.last()
            return SuggestedLoad(
                suggestedWeightKg = last.weightKg,
                suggestedReps = last.repsActual,
                strategy = "Mantido (precisa de ≥2 sessões)",
                confidence = SuggestedLoad.Confidence.LOW
            )
        }
        
        val weights = workingPoints.map { it.weightKg }
        val reps = workingPoints.map { it.repsActual.toDouble() }
        
        val weightMean = weights.average()
        val repMean = reps.average()
        val n = weights.size.coerceAtLeast(1).toDouble()
        val weightTrend = (weights.last() - weights.first()) / n
        val repTrend = (reps.last() - reps.first()) / n
        
        val suggestedWeight = weightMean + weightTrend * 2
        val suggestedReps = (repMean + repTrend).toInt().coerceAtLeast(1)
        
        val confidence = when {
            workingPoints.size >= 5 -> SuggestedLoad.Confidence.HIGH
            workingPoints.size >= 3 -> SuggestedLoad.Confidence.MEDIUM
            else -> SuggestedLoad.Confidence.LOW
        }
        
        val strategy = when {
            weightTrend > 0.5 && suggestedWeight > weights.last() -> "↑ Progressão de carga sugerida"
            weightTrend < -0.5 -> "⚠ Carga em regressão — manter peso atual"
            repTrend > 0.5 && suggestedReps > reps.last().toInt() -> "↑ Volume sugerido aumentado"
            weightTrend in -0.5..0.5 -> "→ Peso estável — manter volume"
            else -> "Mantido"
        }
        
        return SuggestedLoad(
            suggestedWeightKg = (suggestedWeight * 100).toInt() / 100.0,
            suggestedReps = suggestedReps,
            strategy = strategy,
            confidence = confidence
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // WORKOUT CONFIGS (Leitura - sempre local)
    // ═══════════════════════════════════════════════════════════════════
    
    suspend fun getWorkoutConfigs(): List<Workout_configs> =
        configQ.getAll().awaitAsList()
    
    suspend fun getActiveWorkoutConfig(): Workout_configs? =
        configQ.getActive().awaitAsOneOrNull()
    
    suspend fun ensureAllConfigsExist() {
        WorkoutData.allWorkouts.forEach { workout ->
            val existing = configQ.getByWorkoutId(workout.id).awaitAsOneOrNull()
            if (existing == null) {
                configQ.insertOrReplace(workout.id, 0L, null, null, 0L)
            }
        }
    }
    
    suspend fun createWorkoutConfig(workoutId: String) {
        configQ.insertConfig(workoutId)
    }
    
    suspend fun deleteWorkoutConfig(workoutId: String) {
        configQ.deleteConfig(workoutId)
    }
    
    suspend fun getExercisesByWorkout(workoutId: String): List<Workout_exercises> =
        configQ.getExercisesByWorkout(workoutId).awaitAsList()
    
    suspend fun insertExercise(
        workoutId: String,
        exerciseId: String,
        exerciseName: String,
        setNumber: Long,
        setType: String,
        repsTarget: String,
        suggestedWeight: Double?
    ) {
        configQ.insertExercise(workoutId, exerciseId, exerciseName, setNumber, setType, repsTarget, suggestedWeight)
    }
    
    suspend fun deleteExercisesByWorkout(workoutId: String) {
        configQ.deleteExercisesByWorkout(workoutId)
    }
    
    suspend fun deleteExerciseByName(workoutId: String, exerciseName: String) {
        configQ.deleteExerciseByName(workoutId, exerciseName)
    }
    
    suspend fun setActiveWorkout(workoutId: String) {
        configQ.activate(workoutId)
    }
    
    suspend fun setActiveState(workoutId: String, isActive: Long) {
        if (isActive == 1L) {
            configQ.activate(workoutId)
        } else {
            configQ.setActiveState(0L, workoutId)
        }
    }
    
    suspend fun getWorkoutConfig(workoutId: String): Workout_configs? =
        configQ.getByWorkoutId(workoutId).awaitAsOneOrNull()
    
    suspend fun deactivateAll() {
        configQ.deactivateAll()
    }
    
    suspend fun setArchived(workoutId: String, archived: Boolean) {
        configQ.setArchived(if (archived) 1L else 0L, workoutId)
    }
    
    suspend fun setDates(workoutId: String, startDate: Long?, endDate: Long?) {
        configQ.setDates(startDate, endDate, workoutId)
    }
    
    suspend fun setCoverImage(workoutId: String, coverImage: String?) {
        configQ.setCoverImage(coverImage, workoutId)
    }
    
    suspend fun setYoutubeUrl(workoutId: String, youtubeUrl: String?) {
        configQ.setYoutubeUrl(youtubeUrl, workoutId)
    }
    
    suspend fun checkAndSwitchWorkout() {
        val now = Clock.System.now().toEpochMilliseconds()
        val configs = getWorkoutConfigs()
        
        val active = configs.find { it.isActive == 1L }
        
        if (active?.endDate != null && active.endDate < now) {
            val next = configs
                .filter { it.startDate != null && it.startDate > now && it.isArchived == 0L }
                .minByOrNull { it.startDate!! }
            
            if (next != null) {
                deactivateAll()
                setActiveWorkout(next.workoutId)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PROGRESS HELPER
    // ═══════════════════════════════════════════════════════════════════
    
    fun computeProgressStatus(
        weightKg: Double?,
        repsActual: Long?,
        lastWeight: Double?,
        lastReps: Long?
    ): ProgressStatus {
        if (weightKg == null || lastWeight == null) return ProgressStatus.NONE
        val r = repsActual ?: 0L
        val lr = lastReps ?: 0L
        return when {
            weightKg > lastWeight -> ProgressStatus.UP
            weightKg == lastWeight && r > lr -> ProgressStatus.UP
            weightKg == lastWeight && r == lr -> ProgressStatus.SAME
            else -> ProgressStatus.DOWN
        }
    }
}
