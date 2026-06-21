package com.gymtracker.db

import app.cash.sqldelight.SuspendingTransacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.gymtracker.db.shared.newInstance
import com.gymtracker.db.shared.schema
import kotlin.Unit

public interface GymDatabase : SuspendingTransacter {
  public val checklistLogQueries: ChecklistLogQueries

  public val goalLogQueries: GoalLogQueries

  public val setLogQueries: SetLogQueries

  public val workoutSessionQueries: WorkoutSessionQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.AsyncValue<Unit>>
      get() = GymDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): GymDatabase =
        GymDatabase::class.newInstance(driver)
  }
}
