package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_types")
data class ActivityType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
