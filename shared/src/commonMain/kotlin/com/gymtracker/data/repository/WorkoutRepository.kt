package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.model.ProgressStatus
import com.gymtracker.data.model.WorkoutData
import com.gymtracker.db.Set_logs
import com.gymtracker.db.Workout_configs
import com.gymtracker.db.Workout_exercises
import com.gymtracker.db.Workout_sessions
import com.gymtracker.util.todayKey
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

class WorkoutRepository {

    private val db get() = DatabaseProvider.get()
    private val sessionQ get() = db.workoutSessionQueries
    private val setQ get() = db.setLogQueries

    // ── Sessions ──────────────────────────────────────────────────────────────

    fun getAllSessionsFlow(): Flow<List<Workout_sessions>> =
        sessionQ.getAll().asFlow().mapToList(Dispatchers.Default)

    fun getSessionsByWorkoutFlow(workoutId: String): Flow<List<Workout_sessions>> =
        sessionQ.getByWorkout(workoutId).asFlow().mapToList(Dispatchers.Default)

    suspend fun getLastSession(workoutId: String): Workout_sessions? =
        sessionQ.getLast(workoutId).awaitAsOneOrNull()

    suspend fun createSession(workoutId: String, workoutName: String): Long {
        sessionQ.insert(workoutId, workoutName, Clock.System.now().toEpochMilliseconds(), "")
        return sessionQ.lastInsertId().awaitAsOne()
    }

    suspend fun updateSessionNotes(id: Long, notes: String) =
        sessionQ.update(notes, id)

    suspend fun deleteSession(id: Long) =
        sessionQ.delete(id)

    // ── SetLogs ───────────────────────────────────────────────────────────────

    fun getSetLogsBySessionFlow(sessionId: Long): Flow<List<Set_logs>> =
        setQ.getBySession(sessionId).asFlow().mapToList(Dispatchers.Default)

    suspend fun getSetLogsBySession(sessionId: Long): List<Set_logs> =
        setQ.getBySession(sessionId).awaitAsList()

    suspend fun getExercisesBySession(sessionId: Long): List<Pair<String, String>> =
        setQ.getExercisesBySession(sessionId).awaitAsList().map { it.exerciseId to it.exerciseName }

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

    // ── Progress by exercise ──────────────────────────────────────────────────

    suspend fun getExerciseProgress(exerciseId: String, setName: String): ExerciseProgress {
        val rows = db.setLogQueries.getByExerciseWithDate(exerciseId).awaitAsList()
        if (rows.isEmpty()) return ExerciseProgress.Empty

        val points = rows.map { ExerciseProgress.DataPoint(it.weightKg!!, it.repsActual!!.toLong().toInt(), it.sessionDate) }
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

    // ── Workout Configs ───────────────────────────────────────────────────────

    private val configQ get() = db.workoutConfigQueries

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

    // ── Workout Exercises ──────────────────────────────────────────────────────

    suspend fun getExercisesByWorkout(workoutId: String): List<Workout_exercises> =
        configQ.getExercisesByWorkout(workoutId).awaitAsList()

    suspend fun insertExercise(
        workoutId: String, exerciseId: String, exerciseName: String,
        setNumber: Long, setType: String, repsTarget: String, suggestedWeight: Double?
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
