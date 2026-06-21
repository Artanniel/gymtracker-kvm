package com.gymtracker

import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.repository.DietRepository
import com.gymtracker.data.repository.GoalRepository
import com.gymtracker.data.repository.WorkoutRepository

object AppDependencies {
    val workoutRepo = WorkoutRepository()
    val goalRepo    = GoalRepository()
    val dietRepo    = DietRepository()

    suspend fun init(factory: DatabaseDriverFactory) {
        DatabaseProvider.init(factory)
    }
}
