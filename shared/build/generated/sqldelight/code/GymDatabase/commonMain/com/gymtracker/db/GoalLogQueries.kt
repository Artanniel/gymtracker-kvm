package com.gymtracker.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class GoalLogQueries(
  driver: SqlDriver,
) : SuspendingTransacterImpl(driver) {
  public fun <T : Any> getConfig(goalType: String, mapper: (goalType: String,
      targetValue: Long) -> T): Query<T> = GetConfigQuery(goalType) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!
    )
  }

  public fun getConfig(goalType: String): Query<Goal_configs> = getConfig(goalType) { goalType_,
      targetValue ->
    Goal_configs(
      goalType_,
      targetValue
    )
  }

  public fun <T : Any> getLog(
    goalType: String,
    dateKey: String,
    mapper: (
      id: Long,
      goalType: String,
      dateKey: String,
      actualValue: Long,
      targetValue: Long,
      achieved: Long,
    ) -> T,
  ): Query<T> = GetLogQuery(goalType, dateKey) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!
    )
  }

  public fun getLog(goalType: String, dateKey: String): Query<Goal_logs> = getLog(goalType,
      dateKey) { id, goalType_, dateKey_, actualValue, targetValue, achieved ->
    Goal_logs(
      id,
      goalType_,
      dateKey_,
      actualValue,
      targetValue,
      achieved
    )
  }

  public fun <T : Any> getRecentLogs(
    goalType: String,
    `value`: Long,
    mapper: (
      id: Long,
      goalType: String,
      dateKey: String,
      actualValue: Long,
      targetValue: Long,
      achieved: Long,
    ) -> T,
  ): Query<T> = GetRecentLogsQuery(goalType, value) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!
    )
  }

  public fun getRecentLogs(goalType: String, value_: Long): Query<Goal_logs> =
      getRecentLogs(goalType, value_) { id, goalType_, dateKey, actualValue, targetValue,
      achieved ->
    Goal_logs(
      id,
      goalType_,
      dateKey,
      actualValue,
      targetValue,
      achieved
    )
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun setConfig(goalType: String, targetValue: Long): Long {
    val result = driver.execute(265_817_885,
        """INSERT OR REPLACE INTO goal_configs (goalType, targetValue) VALUES (?, ?)""", 2) {
          bindString(0, goalType)
          bindLong(1, targetValue)
        }.await()
    notifyQueries(265_817_885) { emit ->
      emit("goal_configs")
    }
    return result
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun insertOrReplaceLog(
    id: Long?,
    goalType: String,
    dateKey: String,
    actualValue: Long,
    targetValue: Long,
    achieved: Long,
  ): Long {
    val result = driver.execute(511_258_323, """
        |INSERT OR REPLACE INTO goal_logs (id, goalType, dateKey, actualValue, targetValue, achieved)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindLong(0, id)
          bindString(1, goalType)
          bindString(2, dateKey)
          bindLong(3, actualValue)
          bindLong(4, targetValue)
          bindLong(5, achieved)
        }.await()
    notifyQueries(511_258_323) { emit ->
      emit("goal_logs")
    }
    return result
  }

  private inner class GetConfigQuery<out T : Any>(
    public val goalType: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("goal_configs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("goal_configs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(480_434_961,
        """SELECT goal_configs.goalType, goal_configs.targetValue FROM goal_configs WHERE goalType = ?""",
        mapper, 1) {
      bindString(0, goalType)
    }

    override fun toString(): String = "GoalLog.sq:getConfig"
  }

  private inner class GetLogQuery<out T : Any>(
    public val goalType: String,
    public val dateKey: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("goal_logs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("goal_logs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_040_643_541,
        """SELECT goal_logs.id, goal_logs.goalType, goal_logs.dateKey, goal_logs.actualValue, goal_logs.targetValue, goal_logs.achieved FROM goal_logs WHERE goalType = ? AND dateKey = ?""",
        mapper, 2) {
      bindString(0, goalType)
      bindString(1, dateKey)
    }

    override fun toString(): String = "GoalLog.sq:getLog"
  }

  private inner class GetRecentLogsQuery<out T : Any>(
    public val goalType: String,
    public val `value`: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("goal_logs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("goal_logs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-299_977_991,
        """SELECT goal_logs.id, goal_logs.goalType, goal_logs.dateKey, goal_logs.actualValue, goal_logs.targetValue, goal_logs.achieved FROM goal_logs WHERE goalType = ? ORDER BY dateKey DESC LIMIT ?""",
        mapper, 2) {
      bindString(0, goalType)
      bindLong(1, value)
    }

    override fun toString(): String = "GoalLog.sq:getRecentLogs"
  }
}
