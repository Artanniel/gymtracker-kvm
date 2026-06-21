package com.gymtracker.data.repository

import com.gymtracker.data.db.DatabaseProvider
import com.gymtracker.util.todayKey
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull

class DietRepository {

    private val db get() = DatabaseProvider.get()
    private val q get() = db.checklistLogQueries

    suspend fun getTodayChecklist(): Map<String, Boolean> =
        q.getForDate(todayKey()).awaitAsList().associate { it.itemId to (it.completed != 0L) }

    suspend fun toggle(itemId: String, value: Boolean) {
        val key = todayKey()
        val existing = q.getOne(itemId, key).awaitAsOneOrNull()
        q.insertOrReplace(
            id = existing?.id ?: 0L,
            itemId = itemId,
            dateKey = key,
            completed = if (value) 1L else 0L
        )
    }
}
