package com.gymtracker.db

import kotlin.Long
import kotlin.String

public data class Checklist_logs(
  public val id: Long,
  public val itemId: String,
  public val dateKey: String,
  public val completed: Long,
)
