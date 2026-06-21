package com.gymtracker.data.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.gymtracker.db.GymDatabase

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver =
        NativeSqliteDriver(GymDatabase.Schema.synchronous(), "gymtracker.db")
}
