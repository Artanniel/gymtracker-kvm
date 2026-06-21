package com.gymtracker.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class ChecklistLogQueries(
  driver: SqlDriver,
) : SuspendingTransacterImpl(driver) {
  public fun <T : Any> getForDate(dateKey: String, mapper: (
    id: Long,
    itemId: String,
    dateKey: String,
    completed: Long,
  ) -> T): Query<T> = GetForDateQuery(dateKey) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun getForDate(dateKey: String): Query<Checklist_logs> = getForDate(dateKey) { id, itemId,
      dateKey_, completed ->
    Checklist_logs(
      id,
      itemId,
      dateKey_,
      completed
    )
  }

  public fun <T : Any> getOne(
    itemId: String,
    dateKey: String,
    mapper: (
      id: Long,
      itemId: String,
      dateKey: String,
      completed: Long,
    ) -> T,
  ): Query<T> = GetOneQuery(itemId, dateKey) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun getOne(itemId: String, dateKey: String): Query<Checklist_logs> = getOne(itemId,
      dateKey) { id, itemId_, dateKey_, completed ->
    Checklist_logs(
      id,
      itemId_,
      dateKey_,
      completed
    )
  }

  /**
   * @return The number of rows updated.
   */
  public suspend fun insertOrReplace(
    id: Long?,
    itemId: String,
    dateKey: String,
    completed: Long,
  ): Long {
    val result = driver.execute(-1_123_745_452, """
        |INSERT OR REPLACE INTO checklist_logs (id, itemId, dateKey, completed)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindLong(0, id)
          bindString(1, itemId)
          bindString(2, dateKey)
          bindLong(3, completed)
        }.await()
    notifyQueries(-1_123_745_452) { emit ->
      emit("checklist_logs")
    }
    return result
  }

  private inner class GetForDateQuery<out T : Any>(
    public val dateKey: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("checklist_logs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("checklist_logs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(879_648_165,
        """SELECT checklist_logs.id, checklist_logs.itemId, checklist_logs.dateKey, checklist_logs.completed FROM checklist_logs WHERE dateKey = ?""",
        mapper, 1) {
      bindString(0, dateKey)
    }

    override fun toString(): String = "ChecklistLog.sq:getForDate"
  }

  private inner class GetOneQuery<out T : Any>(
    public val itemId: String,
    public val dateKey: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("checklist_logs", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("checklist_logs", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-485_843_212,
        """SELECT checklist_logs.id, checklist_logs.itemId, checklist_logs.dateKey, checklist_logs.completed FROM checklist_logs WHERE itemId = ? AND dateKey = ? LIMIT 1""",
        mapper, 2) {
      bindString(0, itemId)
      bindString(1, dateKey)
    }

    override fun toString(): String = "ChecklistLog.sq:getOne"
  }
}
