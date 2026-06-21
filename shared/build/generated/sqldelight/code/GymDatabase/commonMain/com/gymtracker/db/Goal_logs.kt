package com.gymtracker.db

import kotlin.Long
import kotlin.String

public data class Goal_logs(
  public val id: Long,
  public val goalType: String,
  public val dateKey: String,
  public val actualValue: Long,
  public val targetValue: Long,
  public val achieved: Long,
)
