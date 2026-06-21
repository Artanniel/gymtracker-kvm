package com.gymtracker.data.db

import app.cash.sqldelight.db.SqlDriver
import com.gymtracker.db.GymDatabase

object DatabaseProvider {
    private var db: GymDatabase? = null

    suspend fun init(factory: DatabaseDriverFactory): GymDatabase {
        if (db == null) {
            val driver: SqlDriver = factory.createDriver()
            db = GymDatabase(driver)
        }
        return db!!
    }

    fun get(): GymDatabase = db ?: error("DatabaseProvider.init() não foi chamado")
}
