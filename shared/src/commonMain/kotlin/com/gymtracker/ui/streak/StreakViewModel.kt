package com.gymtracker.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.model.Badge
import com.gymtracker.data.model.StreakData
import com.gymtracker.data.model.UserBadge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StreakViewModel : ViewModel() {

    private val streakRepo = AppDependencies.streakRepo

    private val _streakData = MutableStateFlow(StreakData())
    val streakData: StateFlow<StreakData> = _streakData

    private val _unlockedBadges = MutableStateFlow<List<UserBadge>>(emptyList())
    val unlockedBadges: StateFlow<List<UserBadge>> = _unlockedBadges

    private val _badgeProgress = MutableStateFlow<Map<Badge, Int>>(emptyMap())
    val badgeProgress: StateFlow<Map<Badge, Int>> = _badgeProgress

    fun load() {
        viewModelScope.launch {
            _streakData.value = streakRepo.getStreakData()
            _unlockedBadges.value = streakRepo.getUnlockedBadges()
            _badgeProgress.value = streakRepo.getBadgeProgress()
        }
    }
}
