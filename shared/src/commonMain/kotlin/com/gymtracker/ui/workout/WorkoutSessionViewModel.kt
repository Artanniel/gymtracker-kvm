package com.gymtracker.ui.workout

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.*
import com.gymtracker.db.Set_logs
import com.gymtracker.util.formatTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SetRowUiState(
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Long,
    val setType: SetType,
    val repsTarget: String,
    val weightKg: Double?,
    val repsActual: Int?,
    val completed: Boolean,
    val lastWeight: Double?,
    val lastReps: Int?,
    val progressBadge: Pair<String, Color>?,
    val restRecorded: String?
)

class WorkoutSessionViewModel(private val workoutId: String) : ViewModel() {

    private val repo = AppDependencies.workoutRepo
    var workout: Workout? = null
        private set

    private val _rows = MutableStateFlow<List<SetRowUiState>>(emptyList())
    val rows: StateFlow<List<SetRowUiState>> = _rows

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private var sessionId = -1L
    private var timerJob: Job? = null
    private var restStartMs = 0L
    private var restForSet: Pair<String, Long>? = null
    private var lastSessionLogs: List<Set_logs> = emptyList()
    private val currentLogs = mutableMapOf<Pair<String, Long>, Set_logs>()

    init {
        viewModelScope.launch { setup() }
    }

    private suspend fun setup() {
        val w = WorkoutData.getWorkoutById(workoutId) ?: return
        workout = w
        sessionId = repo.createSession(w.id, w.name)
        val lastSession = repo.getLastSession(w.id)
        if (lastSession != null && lastSession.id != sessionId) {
            lastSessionLogs = repo.getSetLogsBySession(lastSession.id)
        }
        w.exercises.forEach { ex ->
            ex.sets.forEach { sd ->
                repo.insertSetLog(sessionId, ex.id, ex.name, sd.setNumber.toLong(), sd.type.name, sd.repsTarget)
            }
        }
        buildRows()
    }

    private fun buildRows() {
        val w = workout ?: return
        val rows = mutableListOf<SetRowUiState>()
        w.exercises.forEach { ex ->
            ex.sets.forEach { sd ->
                val key = ex.id to sd.setNumber.toLong()
                val log = currentLogs[key]
                val last = lastSessionLogs.find { it.exerciseId == ex.id && it.setNumber == sd.setNumber.toLong() }
                val badge = buildBadge(log?.weightKg, log?.repsActual, last?.weightKg, last?.repsActual)
                rows.add(SetRowUiState(
                    exerciseId   = ex.id,
                    exerciseName = ex.name,
                    setNumber    = sd.setNumber.toLong(),
                    setType      = sd.type,
                    repsTarget   = sd.repsTarget,
                    weightKg     = log?.weightKg,
                    repsActual   = log?.repsActual?.toInt(),
                    completed    = (log?.completed ?: 0L) != 0L,
                    lastWeight   = last?.weightKg,
                    lastReps     = last?.repsActual?.toInt(),
                    progressBadge = badge,
                    restRecorded = log?.restSeconds?.let { formatTimer(it.toInt()) }
                ))
            }
        }
        _rows.value = rows
    }

    private fun buildBadge(w: Double?, r: Long?, lw: Double?, lr: Long?): Pair<String, Color>? {
        if (w == null || lw == null) return null
        val rr = r ?: 0L; val lr2 = lr ?: 0L
        return when {
            w > lw                  -> "▲ Progrediu" to Color(0xFF27AE60)
            w == lw && rr > lr2     -> "▲ Progrediu" to Color(0xFF27AE60)
            w == lw && rr == lr2    -> "● Manteve" to Color(0xFF666666)
            else                    -> "▼ Regrediu" to Color(0xFFE8362D)
        }
    }

    fun updateSet(exerciseId: String, setNumber: Long, weight: Double?, reps: Int?) {
        viewModelScope.launch {
            repo.updateSetLog(sessionId, exerciseId, setNumber, weight, reps?.toLong(), true, null)
            val key = exerciseId to setNumber
            // rebuild virtual log state from returned data
            currentLogs[key] = com.gymtracker.db.Set_logs(
                id = 0L, sessionId = sessionId, exerciseId = exerciseId,
                exerciseName = "", setNumber = setNumber,
                setType = "", repsTarget = "",
                weightKg = weight, repsActual = reps?.toLong(),
                completed = 1L, restSeconds = null
            )
            buildRows()
            startRestTimer(180, exerciseId, setNumber)
        }
    }

    fun startRestTimer(seconds: Int = 180, exerciseId: String? = null, setNumber: Long? = null) {
        timerJob?.cancel()
        _timerSeconds.value = seconds
        _timerRunning.value = true
        restStartMs = System.currentTimeMillis()
        restForSet = if (exerciseId != null && setNumber != null) exerciseId to setNumber else null
        timerJob = viewModelScope.launch {
            var rem = seconds
            while (rem > 0) { delay(1000); rem--; _timerSeconds.value = rem }
            _timerRunning.value = false
            saveRest()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerRunning.value = false
        _timerSeconds.value = 0
        viewModelScope.launch { saveRest() }
    }

    private suspend fun saveRest() {
        val (eid, sn) = restForSet ?: return
        val elapsed = ((System.currentTimeMillis() - restStartMs) / 1000L).toInt().coerceAtLeast(0)
        val key = eid to sn
        val log = currentLogs[key] ?: return
        currentLogs[key] = log.copy(restSeconds = elapsed.toLong())
        repo.updateSetLog(sessionId, eid, sn, log.weightKg, log.repsActual, true, elapsed.toLong())
        buildRows()
        restForSet = null
    }

    fun setNotes(text: String) { _notes.value = text }

    fun finish() {
        viewModelScope.launch {
            repo.updateSessionNotes(sessionId, _notes.value)
        }
    }
}
