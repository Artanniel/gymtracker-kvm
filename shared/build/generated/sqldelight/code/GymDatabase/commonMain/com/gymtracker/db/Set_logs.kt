package com.gymtracker.db

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Set_logs(
  public val id: Long,
  public val sessionId: Long,
  public val exerciseId: String,
  public val exerciseName: String,
  public val setNumber: Long,
  public val setType: String,
  public val repsTarget: String,
  public val weightKg: Double?,
  public val repsActual: Long?,
  public val completed: Long,
  public val restSeconds: Long?,
)
