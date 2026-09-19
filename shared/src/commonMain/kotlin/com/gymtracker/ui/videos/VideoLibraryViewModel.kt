package com.gymtracker.ui.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.db.Exercise_videos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoLibraryViewModel : ViewModel() {

    private val repo = AppDependencies.videoRepo

    private val _videos = MutableStateFlow<List<Exercise_videos>>(emptyList())
    val videos: StateFlow<List<Exercise_videos>> = _videos.asStateFlow()

    private val _selectedVideo = MutableStateFlow<Exercise_videos?>(null)
    val selectedVideo: StateFlow<Exercise_videos?> = _selectedVideo.asStateFlow()

    private val _muscleGroups = MutableStateFlow<List<String>>(emptyList())
    val muscleGroups: StateFlow<List<String>> = _muscleGroups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getAllVideosFlow().collect { list ->
                _videos.value = list
                _muscleGroups.value = list.map { it.muscleGroup }.distinct().sorted()
                _isLoading.value = false
            }
        }
    }

    fun filterByMuscle(muscleGroup: String) {
        viewModelScope.launch {
            if (muscleGroup.isBlank()) {
                repo.getAllVideosFlow().collect { _videos.value = it }
            } else {
                _videos.value = repo.search(muscleGroup)
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                repo.getAllVideosFlow().collect { _videos.value = it }
            } else {
                _videos.value = repo.search(query)
            }
        }
    }

    fun selectVideo(video: Exercise_videos) {
        _selectedVideo.value = video
    }

    fun clearSelection() {
        _selectedVideo.value = null
    }

    fun addCustomVideo(
        exerciseId: String,
        title: String,
        url: String,
        muscleGroup: String,
        category: String
    ) {
        viewModelScope.launch {
            repo.addVideo(exerciseId, title, url, null, null, null, muscleGroup, category, true)
        }
    }

    fun deleteVideo(id: Long) {
        viewModelScope.launch {
            repo.deleteVideo(id)
        }
    }
}
