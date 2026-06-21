package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.model.ProgressStatus
import com.gymtracker.db.Set_logs
import com.gymtracker.db.Workout_sessions
import com.gymtracker.util.todayKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class WorkoutRepository {

    private val db get() = DatabaseProvider.get()
    private val sessionQ get() = db.workoutSessionQueries
    private val setQ get() = db.setLogQueries

    // ── Sessions ──────────────────────────────────────────────────────────────

    fun getAllSessionsFlow(): Flow<List<Workout_sessions>> =
        sessionQ.getAll().asFlow().mapToList(Dispatchers.IO)

    fun getSessionsByWorkoutFlow(workoutId: String): Flow<List<Workout_sessions>> =
        sessionQ.getByWorkout(workoutId).asFlow().mapToList(Dispatchers.IO)

    suspend fun getLastSession(workoutId: String): Workout_sessions? =
        sessionQ.getLast(workoutId).awaitAsOneOrNull()

    suspend fun createSession(workoutId: String, workoutName: String): Long {
        sessionQ.insert(workoutId, workoutName, System.currentTimeMillis().toLong(), "")
        return sessionQ.lastInsertId().awaitAsOne()
    }

    suspend fun updateSessionNotes(id: Long, notes: String) =
        sessionQ.update(notes, id)

    suspend fun deleteSession(id: Long) =
        sessionQ.delete(id)

    // ── SetLogs ───────────────────────────────────────────────────────────────

    fun getSetLogsBySessionFlow(sessionId: Long): Flow<List<Set_logs>> =
        setQ.getBySession(sessionId).asFlow().mapToList(Dispatchers.IO)

    suspend fun getSetLogsBySession(sessionId: Long): List<Set_logs> =
        setQ.getBySession(sessionId).awaitAsList()

    suspend fun insertSetLog(
        sessionId: Long, exerciseId: String, exerciseName: String,
        setNumber: Long, setType: String, repsTarget: String
    ): Long {
        setQ.insert(sessionId, exerciseId, exerciseName, setNumber, setType, repsTarget, null, null, 0, null)
        return setQ.lastInsertId().awaitAsOne()
    }

    suspend fun updateSetLog(
        sessionId: Long, exerciseId: String, setNumber: Long,
        weightKg: Double?, repsActual: Long?, completed: Boolean, restSeconds: Long?
    ) = setQ.update(weightKg, repsActual, if (completed) 1L else 0L, restSeconds, sessionId, exerciseId, setNumber)

    suspend fun getProgressForSet(exerciseId: String, setNumber: Long): List<Set_logs> =
        setQ.getProgressForSet(exerciseId, setNumber).awaitAsList()

    // ── Progress helper ───────────────────────────────────────────────────────

    fun computeProgressStatus(
        weightKg: Double?, repsActual: Long?,
        lastWeight: Double?, lastReps: Long?
    ): ProgressStatus {
        if (weightKg == null || lastWeight == null) return ProgressStatus.NONE
        val r = repsActual ?: 0L
        val lr = lastReps ?: 0L
        return when {
            weightKg > lastWeight          -> ProgressStatus.UP
            weightKg == lastWeight && r > lr -> ProgressStatus.UP
            weightKg == lastWeight && r == lr -> ProgressStatus.SAME
            else                           -> ProgressStatus.DOWN
        }
    }
}
