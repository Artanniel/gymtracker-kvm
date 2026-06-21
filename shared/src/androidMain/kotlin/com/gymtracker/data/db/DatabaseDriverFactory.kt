package com.gymtracker.data.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.gymtracker.db.GymDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual suspend fun createDriver(): SqlDriver =
        AndroidSqliteDriver(GymDatabase.Schema.synchronous(), context, "gymtracker.db")
}
