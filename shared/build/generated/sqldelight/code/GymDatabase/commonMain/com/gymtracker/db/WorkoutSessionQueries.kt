package com.gymtracker.db

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class WorkoutSessionQueries(
  driver: SqlDriver,
) : SuspendingTransacterImpl(driver) {
  public fun <T : Any> getAll(mapper: (
    id: Long,
    workoutId: String,
    workoutName: String,
    date: Long,
    notes: String,
  ) -> T): Query<T> = Query(-2_035_809_302, arrayOf("workout_sessions"), driver,
      "WorkoutSession.sq", "getAll",
      "SELECT workout_sessions.id, workout_sessions.workoutId, workout_sessions.workoutName, workout_sessions.date, workout_sessions.notes FROM workout_sessions ORDER BY date DESC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!
    )
  }

  public fun getAll(): Query<Workout_sessions> = getAll { id, workoutId, workoutName, date, notes ->
    Workout_sessions(
      id,
      workoutId,
      workoutName,
      date,
      notes
    )
  }

  public fun <T : Any> getByWorkout(workoutId: String, mapper: (
    id: Long,
    workoutId: String,
    workoutName: String,
    date: Long,
    notes: String,
  ) -> T): Query<T> = GetByWorkoutQuery(workoutId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!
    )
  }

  public fun getByWorkout(workoutId: String): Query<Workout_sessions> = getByWorkout(workoutId) {
      id, workoutId_, workoutName, date, notes ->
    Workout_sessions(
      id,
      workoutId_,
      workoutName,
      date,
      notes
    )
  }

  public fun <T : Any> getLast(workoutId: String, mapper: (
    id: Long,
    workoutId: String,
    workoutName: String,
    date: Long,
    notes: String,
  ) -> T): Query<T> = GetLastQuery(workoutId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!
    )
  }

  public fun getLast(workoutId: String): Query<Workout_sessions> = getLast(workoutId) { id,
      workoutId_, workoutName, date, notes ->
    Workout_sessions(
      id,
      workoutId_,
      workoutName,
      date,
      notes
    )
  }

  public fun <T : Any> getById(id: Long, mapper: (
    id: Long,
    workoutId: String,
    workoutName: String,
    date: Long,
    notes: String,
  ) -> T): Query<T> = GetByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getString(4)!!
    )
  }

  public fun getById(id: Long): Query<Workout_sessions> = getById(id) { id_, workoutId, workoutName,
      date, notes ->
    Workout_sessions(
      id_,
      workoutId,
      workoutName,
      date,
      notes
    )
  }

  public fun lastInsertId(): ExecutableQuery<Long> = Query(1_578_074_281, driver,
      "WorkoutSession.sq", "lastInsertId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun insert(
    workoutId: String,
    workoutName: String,
    date: Long,
    notes: String,
  ): Long {
    val result = driver.execute(-1_970_234_312, """
        |INSERT INTO workout_sessions (workoutId, workoutName, date, notes)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindString(0, workoutId)
          bindString(1, workoutName)
          bindLong(2, date)
          bindString(3, notes)
        }.await()
    notifyQueries(-1_970_234_312) { emit ->
      emit("workout_sessions")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun update(notes: String, id: Long): Long {
    val result = driver.execute(-1_625_288_120,
        """UPDATE workout_sessions SET notes = ? WHERE id = ?""", 2) {
          bindString(0, notes)
          bindLong(1, id)
        }.await()
    notifyQueries(-1_625_288_120) { emit ->
      emit("workout_sessions")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun delete(id: Long): Long {
    val result = driver.execute(-2_121_900_246, """DELETE FROM workout_sessions WHERE id = ?""", 1)
        {
          bindLong(0, id)
        }.await()
    notifyQueries(-2_121_900_246) { emit ->
      emit("set_logs")
      emit("workout_sessions")
    }
    return result
  }

  private inner class GetByWorkoutQuery<out T : Any>(
    public val workoutId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("workout_sessions", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("workout_sessions", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_031_765_455,
        """SELECT workout_sessions.id, workout_sessions.workoutId, workout_sessions.workoutName, workout_sessions.date, workout_sessions.notes FROM workout_sessions WHERE workoutId = ? ORDER BY date DESC""",
        mapper, 1) {
      bindString(0, workoutId)
    }

    override fun toString(): String = "WorkoutSession.sq:getByWorkout"
  }

  private inner class GetLastQuery<out T : Any>(
    public val workoutId: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("workout_sessions", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("workout_sessions", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_314_738_541,
        """SELECT workout_sessions.id, workout_sessions.workoutId, workout_sessions.workoutName, workout_sessions.date, workout_sessions.notes FROM workout_sessions WHERE workoutId = ? ORDER BY date DESC LIMIT 1""",
        mapper, 1) {
      bindString(0, workoutId)
    }

    override fun toString(): String = "WorkoutSession.sq:getLast"
  }

  private inner class GetByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("workout_sessions", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("workout_sessions", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_314_462_377,
        """SELECT workout_sessions.id, workout_sessions.workoutId, workout_sessions.workoutName, workout_sessions.date, workout_sessions.notes FROM workout_sessions WHERE id = ?""",
        mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "WorkoutSession.sq:getById"
  }
}
