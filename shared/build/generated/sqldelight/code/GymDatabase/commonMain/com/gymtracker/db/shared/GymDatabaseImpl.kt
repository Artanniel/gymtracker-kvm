package com.gymtracker.db.shared

import app.cash.sqldelight.SuspendingTransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.gymtracker.db.ChecklistLogQueries
import com.gymtracker.db.GoalLogQueries
import com.gymtracker.db.GymDatabase
import com.gymtracker.db.SetLogQueries
import com.gymtracker.db.SyncTableQueries
import com.gymtracker.db.WorkoutConfigQueries
import com.gymtracker.db.WorkoutSessionQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<GymDatabase>.schema: SqlSchema<QueryResult.AsyncValue<Unit>>
  get() = GymDatabaseImpl.Schema

internal fun KClass<GymDatabase>.newInstance(driver: SqlDriver): GymDatabase =
    GymDatabaseImpl(driver)

private class GymDatabaseImpl(
  driver: SqlDriver,
) : SuspendingTransacterImpl(driver),
    GymDatabase {
  override val checklistLogQueries: ChecklistLogQueries = ChecklistLogQueries(driver)

  override val goalLogQueries: GoalLogQueries = GoalLogQueries(driver)

  override val setLogQueries: SetLogQueries = SetLogQueries(driver)

  override val syncTableQueries: SyncTableQueries = SyncTableQueries(driver)

  override val workoutConfigQueries: WorkoutConfigQueries = WorkoutConfigQueries(driver)

  override val workoutSessionQueries: WorkoutSessionQueries = WorkoutSessionQueries(driver)

  public object Schema : SqlSchema<QueryResult.AsyncValue<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.AsyncValue<Unit> = QueryResult.AsyncValue {
      driver.execute(null, """
          |CREATE TABLE checklist_logs (
          |    id        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    itemId    TEXT NOT NULL,
          |    dateKey   TEXT NOT NULL,
          |    completed INTEGER NOT NULL DEFAULT 0,
          |    UNIQUE(itemId, dateKey)
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE goal_configs (
          |    goalType    TEXT NOT NULL PRIMARY KEY,
          |    targetValue INTEGER NOT NULL
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE goal_logs (
          |    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    goalType    TEXT NOT NULL,
          |    dateKey     TEXT NOT NULL,
          |    actualValue INTEGER NOT NULL,
          |    targetValue INTEGER NOT NULL,
          |    achieved    INTEGER NOT NULL DEFAULT 0,
          |    UNIQUE(goalType, dateKey)
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE set_logs (
          |    id           INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    sessionId    INTEGER NOT NULL,
          |    exerciseId   TEXT NOT NULL,
          |    exerciseName TEXT NOT NULL,
          |    setNumber    INTEGER NOT NULL,
          |    setType      TEXT NOT NULL,
          |    repsTarget   TEXT NOT NULL,
          |    weightKg     REAL,
          |    repsActual   INTEGER,
          |    completed    INTEGER NOT NULL DEFAULT 0,
          |    restSeconds  INTEGER,
          |    FOREIGN KEY (sessionId) REFERENCES workout_sessions(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE pending_sync (
          |    id            TEXT NOT NULL PRIMARY KEY,
          |    entity_type   TEXT NOT NULL,        -- 'workout_session', 'set_log', 'goal_log', 'user_profile'
          |    entity_id     TEXT NOT NULL,        -- ID da entidade local
          |    action        TEXT NOT NULL,        -- 'CREATE', 'UPDATE', 'DELETE'
          |    payload       TEXT NOT NULL,        -- JSON serializado da entidade
          |    created_at    INTEGER NOT NULL,     -- epoch millis
          |    retry_count   INTEGER NOT NULL DEFAULT 0,
          |    last_error    TEXT,
          |    synced        INTEGER NOT NULL DEFAULT 0
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE workout_configs (
          |    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    workoutId   TEXT NOT NULL UNIQUE,
          |    isArchived  INTEGER NOT NULL DEFAULT 0,
          |    startDate   INTEGER,
          |    endDate     INTEGER,
          |    isActive    INTEGER NOT NULL DEFAULT 0,
          |    coverImage  TEXT,
          |    youtubeUrl  TEXT
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE workout_exercises (
          |    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    workoutId       TEXT NOT NULL,
          |    exerciseId      TEXT NOT NULL,
          |    exerciseName    TEXT NOT NULL,
          |    setNumber       INTEGER NOT NULL,
          |    setType         TEXT NOT NULL,
          |    repsTarget      TEXT NOT NULL,
          |    suggestedWeight REAL,
          |    FOREIGN KEY (workoutId) REFERENCES workout_configs(workoutId) ON DELETE CASCADE
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, """
          |CREATE TABLE workout_sessions (
          |    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    workoutId   TEXT NOT NULL,
          |    workoutName TEXT NOT NULL,
          |    date        INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000),
          |    notes       TEXT NOT NULL DEFAULT ''
          |)
          """.trimMargin(), 0).await()
      driver.execute(null, "CREATE INDEX idx_set_logs_session ON set_logs(sessionId)", 0).await()
      driver.execute(null,
          "CREATE INDEX idx_pending_sync_entity ON pending_sync(entity_type, entity_id)", 0).await()
      driver.execute(null, "CREATE INDEX idx_pending_sync_synced ON pending_sync(synced)",
          0).await()
      driver.execute(null, "CREATE INDEX idx_pending_sync_created ON pending_sync(created_at)",
          0).await()
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.AsyncValue<Unit> = QueryResult.AsyncValue {
    }
  }
}
