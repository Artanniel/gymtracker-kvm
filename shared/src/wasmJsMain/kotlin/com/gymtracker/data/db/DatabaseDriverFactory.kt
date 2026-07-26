package com.gymtracker.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.gymtracker.db.GymDatabase
import org.w3c.dom.Worker

private val workerHref: String = js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url).href""")

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val driver = WebWorkerDriver(Worker(workerHref))
        GymDatabase.Schema.create(driver).await()
        return driver
    }
}
