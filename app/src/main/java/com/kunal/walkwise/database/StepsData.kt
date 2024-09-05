package com.kunal.walkwise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepsData(
    @PrimaryKey
    val id: String,
    val steps: Long
)
