package com.gymtracker.db

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.Long
import kotlin.String

public class SetLogQueries(
  driver: SqlDriver,
) : SuspendingTransacterImpl(driver) {
  public fun <T : Any> getBySession(sessionId: Long, mapper: (
    id: Long,
    sessionId: Long,
    exerciseId: String,
    exerciseName: String,
    setNumber: Long,
    setType: String,
    repsTarget: String,
    weightKg: Double?,
    repsActual: Long?,
    completed: Long,
    restSeconds: Long?,
  ) -> T): Query<T> = GetBySessionQuery(sessionId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getDouble(7),
      cursor.getLong(8),
      cursor.getLong(9)!!,
      cursor.getLong(10)
    )
  }

  public fun getBySession(sessionId: Long): Query<Set_logs> = getBySession(sessionId) { id,
      sessionId_, exerciseId, exerciseName, setNumber, setType, repsTarget, weightKg, repsActual,
      completed, restSeconds ->
    Set_logs(
      id,
      sessionId_,
      exerciseId,
      exerciseName,
      setNumber,
      setType,
      repsTarget,
      weightKg,
      repsActual,
      completed,
      restSeconds
    )
  }

  public fun <T : Any> getByExercise(exerciseId: String, mapper: (
    id: Long,
    sessionId: Long,
    exerciseId: String,
    exerciseName: String,
    setNumber: Long,
    setType: String,
    repsTarget: String,
    weightKg: Double?,
    repsActual: Long?,
    completed: Long,
    restSeconds: Long?,
  ) -> T): Query<T> = GetByExerciseQuery(exerciseId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getDouble(7),
      cursor.getLong(8),
      cursor.getLong(9)!!,
      cursor.getLong(10)
    )
  }

  public fun getByExercise(exerciseId: String): Query<Set_logs> = getByExercise(exerciseId) { id,
      sessionId, exerciseId_, exerciseName, setNumber, setType, repsTarget, weightKg, repsActual,
      completed, restSeconds ->
    Set_logs(
      id,
      sessionId,
      exerciseId_,
      exerciseName,
      setNumber,
      setType,
      repsTarget,
      weightKg,
      repsActual,
      completed,
      restSeconds
    )
  }

  public fun <T : Any> getProgressForSet(
    exerciseId: String,
    setNumber: Long,
    mapper: (
      id: Long,
      sessionId: Long,
      exerciseId: String,
      exerciseName: String,
      setNumber: Long,
      setType: String,
      repsTarget: String,
      weightKg: Double?,
      repsActual: Long?,
      completed: Long,
      restSeconds: Long?,
    ) -> T,
  ): Query<T> = GetProgressForSetQuery(exerciseId, setNumber) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getDouble(7),
      cursor.getLong(8),
      cursor.getLong(9)!!,
      cursor.getLong(10)
    )
  }

  public fun getProgressForSet(exerciseId: String, setNumber: Long): Query<Set_logs> =
      getProgressForSet(exerciseId, setNumber) { id, sessionId, exerciseId_, exerciseName,
      setNumber_, setType, repsTarget, weightKg, repsActual, completed, restSeconds ->
    Set_logs(
      id,
      sessionId,
      exerciseId_,
      exerciseName,
      setNumber_,
      setType,
      repsTarget,
      weightKg,
      repsActual,
      completed,
      restSeconds
    )
  }

  public fun lastInsertId(): ExecutableQuery<Long> = Query(-989_985_550, driver, "SetLog.sq",
      "lastInsertId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun insert(
    sessionId: Long,
    exerciseId: String,
    exerciseName: String,
    setNumber: Long,
    setType: String,
    repsTarget: String,
    weightKg: Double?,
    repsActual: Long?,
    completed: Long,
    restSeconds: Long?,
  ): Long {
    val result = driver.execute(-693_744_831, """
        |INSERT INTO set_logs (sessionId, exerciseId, exerciseName, setNumber, setType, repsTarget, weightKg, repsActual, completed, restSeconds)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 10) {
          bindLong(0, sessionId)
          bindString(1, exerciseId)
          bindString(2, exerciseName)
          bindLong(3, setNumber)
          bindString(4, setType)
          bindString(5, repsTarget)
          bindDouble(6, weightKg)
          bindLong(7, repsActual)
          bindLong(8, completed)
          bindLong(9, restSeconds)
        }.await()
    notifyQueries(-693_744_831) { emit ->
      emit("set_logs")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun update(
    weightKg: Double?,
    repsActual: Long?,
    completed: Long,
    restSeconds: Long?,
    sessionId: Long,
    exerciseId: String,
    setNumber: Long,
  ): Long {
    val result = driver.execute(-348_798_639, """
        |UPDATE set_logs SET weightKg = ?, repsActual = ?, completed = ?, restSeconds = ?
        |WHERE sessionId = ? AND exerciseId = ? AND setNumber = ?
        """.trimMargin(), 7) {
          bindDouble(0, weightKg)
          bindLong(1, repsActual)
          bindLong(2, completed)
          bindLong(3, restSeconds)
          bindLong(4, sessionId)
          bindString(5, exerciseId)
          bindLong(6, setNumber)
        }.await()
    notifyQueries(-348_798_639) { emit ->
      emit("set_logs")
    }
    return result
  }

  private inner class GetBySessionQuery<out T : Any>(
    public val sessionId: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("set_logs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("set_logs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_076_477_423,
        """SELECT set_logs.id, set_logs.sessionId, set_logs.exerciseId, set_logs.exerciseName, set_logs.setNumber, set_logs.setType, set_logs.repsTarget, set_logs.weightKg, set_logs.repsActual, set_logs.completed, set_logs.restSeconds FROM set_logs WHERE sessionId = ? ORDER BY exerciseId, setNumber""",
        mapper, 1) {
      bindLong(0, sessionId)
    }

    override fun toString(): String = "SetLog.sq:getBySession"
  }

  private inner class GetByExerciseQuery<out T : Any>(
    public val exerciseId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("set_logs", "workout_sessions", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("set_logs", "workout_sessions", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_640_182_205, """
    |SELECT sl.id, sl.sessionId, sl.exerciseId, sl.exerciseName, sl.setNumber, sl.setType, sl.repsTarget, sl.weightKg, sl.repsActual, sl.completed, sl.restSeconds FROM set_logs sl
    |INNER JOIN workout_sessions ws ON sl.sessionId = ws.id
    |WHERE sl.exerciseId = ? ORDER BY ws.date ASC
    """.trimMargin(), mapper, 1) {
      bindString(0, exerciseId)
    }

    override fun toString(): String = "SetLog.sq:getByExercise"
  }

  private inner class GetProgressForSetQuery<out T : Any>(
    public val exerciseId: String,
    public val setNumber: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("set_logs", "workout_sessions", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("set_logs", "workout_sessions", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-587_903_276, """
    |SELECT sl.id, sl.sessionId, sl.exerciseId, sl.exerciseName, sl.setNumber, sl.setType, sl.repsTarget, sl.weightKg, sl.repsActual, sl.completed, sl.restSeconds FROM set_logs sl
    |INNER JOIN workout_sessions ws ON sl.sessionId = ws.id
    |WHERE sl.exerciseId = ? AND sl.setNumber = ? ORDER BY ws.date ASC
    """.trimMargin(), mapper, 2) {
      bindString(0, exerciseId)
      bindLong(1, setNumber)
    }

    override fun toString(): String = "SetLog.sq:getProgressForSet"
  }
}
