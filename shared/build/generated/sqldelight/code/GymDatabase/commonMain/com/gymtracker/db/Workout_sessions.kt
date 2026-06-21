package com.gymtracker.db

import kotlin.Long
import kotlin.String

public data class Workout_sessions(
  public val id: Long,
  public val workoutId: String,
  public val workoutName: String,
  public val date: Long,
  public val notes: String,
)
