package com.gymtracker.data.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.gymtracker.db.GymDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val dbPath = System.getProperty("user.home") + "/.gymtracker/gymtracker.db"
        val dbFile = File(dbPath)
        val isNewDatabase = !dbFile.exists() || dbFile.length() == 0L
        dbFile.parentFile?.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
        val schema = GymDatabase.Schema.synchronous()
        if (isNewDatabase) {
            schema.create(driver)
            driver.execute(null, "PRAGMA user_version = ${schema.version}", 0)
        } else {
            val currentVersion = try {
                driver.executeQuery(null, "PRAGMA user_version", parameters = 0, mapper = { cursor ->
                    app.cash.sqldelight.db.QueryResult.Value(if (cursor.next().value) cursor.getLong(0) ?: 0L else 0L)
                }).value
            } catch (e: Exception) { schema.version }
            if (currentVersion < schema.version) {
                schema.migrate(driver, currentVersion, schema.version)
                driver.execute(null, "PRAGMA user_version = ${schema.version}", 0)
            }
        }
        return driver
    }
}

