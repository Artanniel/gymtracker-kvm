package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.data.model.GoalType
import com.gymtracker.db.Goal_logs
import com.gymtracker.util.todayKey
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull

data class GoalState(
    val type: GoalType,
    val target: Int,
    val actual: Int?,
    val achieved: Boolean
)

class GoalRepository {

    private val db get() = DatabaseProvider.get()
    private val q get() = db.goalLogQueries

    suspend fun getTodayState(type: GoalType): GoalState {
        val config = q.getConfig(type.id).awaitAsOneOrNull()
        val target = config?.targetValue?.toInt() ?: type.defaultTarget
        val log = q.getLog(type.id, todayKey()).awaitAsOneOrNull()
        return GoalState(type, target, log?.actualValue?.toInt(), (log?.achieved ?: 0L) != 0L)
    }

    suspend fun saveTodayValue(type: GoalType, target: Int, actual: Int) {
        q.setConfig(type.id, target.toLong())
        val key = todayKey()
        val existing = q.getLog(type.id, key).awaitAsOneOrNull()
        q.insertOrReplaceLog(
            id = existing?.id ?: 0L,
            goalType = type.id,
            dateKey = key,
            actualValue = actual.toLong(),
            targetValue = target.toLong(),
            achieved = if (actual >= target) 1L else 0L
        )
    }

    suspend fun updateTarget(type: GoalType, target: Int) {
        q.setConfig(type.id, target.toLong())
    }

    suspend fun getRecentLogs(type: GoalType, limit: Int = 7): List<Goal_logs> =
        q.getRecentLogs(type.id, limit.toLong()).awaitAsList()
}
