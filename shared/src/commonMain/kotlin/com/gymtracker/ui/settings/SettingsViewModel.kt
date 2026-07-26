package com.gymtracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.WorkoutData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutConfigUiState(
    val workoutId: String,
    val workoutName: String,
    val shortName: String,
    val isArchived: Boolean,
    val isActive: Boolean,
    val startDate: Long?,
    val endDate: Long?,
    val coverImage: String? = null,
    val youtubeUrl: String? = null
)

data class ExerciseEditItem(
    val exerciseName: String,
    val sets: MutableList<SetEditItem>
)

data class SetEditItem(
    val setNumber: Int,
    var setType: String,
    var repsTarget: String,
    var suggestedWeight: String
)

class SettingsViewModel : ViewModel() {

    private val workoutRepo = AppDependencies.workoutRepo

    private val _configs = MutableStateFlow<List<WorkoutConfigUiState>>(emptyList())
    val configs: StateFlow<List<WorkoutConfigUiState>> = _configs.asStateFlow()

    private val _activeWorkoutId = MutableStateFlow<String?>(null)
    val activeWorkoutId: StateFlow<String?> = _activeWorkoutId.asStateFlow()

    fun load() {
        viewModelScope.launch {
            workoutRepo.ensureAllConfigsExist()
            refreshConfigs()
        }
    }

    private suspend fun refreshConfigs() {
        val dbConfigs = workoutRepo.getWorkoutConfigs()
        val dbMap = dbConfigs.associateBy { it.workoutId }

        val states = WorkoutData.allWorkouts.map { workout ->
            val cfg = dbMap[workout.id]
            WorkoutConfigUiState(
                workoutId = workout.id,
                workoutName = workout.name,
                shortName = workout.shortName,
                isArchived = cfg?.isArchived == 1L,
                isActive = cfg?.isActive == 1L,
                startDate = cfg?.startDate,
                endDate = cfg?.endDate,
                coverImage = cfg?.coverImage,
                youtubeUrl = cfg?.youtubeUrl
            )
        }
        _configs.value = states
        _activeWorkoutId.value = dbConfigs.find { it.isActive == 1L }?.workoutId
    }

    fun toggleArchive(workoutId: String) {
        viewModelScope.launch {
            val current = _configs.value.find { it.workoutId == workoutId } ?: return@launch
            val newArchived = !current.isArchived
            workoutRepo.setArchived(workoutId, newArchived)
            if (newArchived && current.isActive) {
                workoutRepo.deactivateAll()
            }
            refreshConfigs()
        }
    }

    fun toggleActive(workoutId: String) {
        viewModelScope.launch {
            val config = workoutRepo.getWorkoutConfig(workoutId)
            if (config != null) {
                val newActive = if (config.isActive == 1L) 0L else 1L
                workoutRepo.setActiveState(workoutId, newActive)
                refreshConfigs()
            }
        }
    }

    fun setStartDate(workoutId: String, date: Long?) {
        viewModelScope.launch {
            val current = _configs.value.find { it.workoutId == workoutId } ?: return@launch
            workoutRepo.setDates(workoutId, date, current.endDate)
            refreshConfigs()
        }
    }

    fun setEndDate(workoutId: String, date: Long?) {
        viewModelScope.launch {
            val current = _configs.value.find { it.workoutId == workoutId } ?: return@launch
            workoutRepo.setDates(workoutId, current.startDate, date)
            refreshConfigs()
        }
    }

    fun setCoverImage(workoutId: String, coverImage: String?) {
        viewModelScope.launch {
            workoutRepo.setCoverImage(workoutId, coverImage)
            refreshConfigs()
        }
    }

    fun setYoutubeUrl(workoutId: String, youtubeUrl: String?) {
        viewModelScope.launch {
            workoutRepo.setYoutubeUrl(workoutId, youtubeUrl)
            refreshConfigs()
        }
    }

    fun checkAndSwitchWorkout() {
        viewModelScope.launch {
            workoutRepo.checkAndSwitchWorkout()
            refreshConfigs()
        }
    }

    fun createWorkout(name: String, shortName: String) {
        viewModelScope.launch {
            val id = "custom_${shortName.lowercase()}_${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
            val workout = com.gymtracker.data.model.Workout(
                id = id,
                name = name,
                shortName = shortName,
                exercises = emptyList()
            )
            com.gymtracker.data.model.WorkoutData.addCustomWorkout(workout)
            workoutRepo.createWorkoutConfig(id)
            refreshConfigs()
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            workoutRepo.deleteWorkoutConfig(workoutId)
            com.gymtracker.data.model.WorkoutData.removeCustomWorkout(workoutId)
            refreshConfigs()
        }
    }

    suspend fun loadExercises(workoutId: String): List<ExerciseEditItem> {
        val dbExercises = workoutRepo.getExercisesByWorkout(workoutId)
        if (dbExercises.isEmpty()) return emptyList()

        return dbExercises.groupBy { it.exerciseName }.map { (name, rows) ->
            ExerciseEditItem(
                exerciseName = name,
                sets = rows.map {
                    SetEditItem(
                        setNumber = it.setNumber.toInt(),
                        setType = it.setType,
                        repsTarget = it.repsTarget,
                        suggestedWeight = it.suggestedWeight?.toString() ?: ""
                    )
                }.toMutableList()
            )
        }.toMutableList()
    }

    fun saveExercises(workoutId: String, exercises: List<ExerciseEditItem>) {
        viewModelScope.launch {
            workoutRepo.deleteExercisesByWorkout(workoutId)
            exercises.forEach { exercise ->
                exercise.sets.forEach { set ->
                    workoutRepo.insertExercise(
                        workoutId = workoutId,
                        exerciseId = "${workoutId}_${exercise.exerciseName.replace(" ", "_").lowercase()}",
                        exerciseName = exercise.exerciseName,
                        setNumber = set.setNumber.toLong(),
                        setType = set.setType,
                        repsTarget = set.repsTarget,
                        suggestedWeight = set.suggestedWeight.toDoubleOrNull()
                    )
                }
            }
        }
    }
}
