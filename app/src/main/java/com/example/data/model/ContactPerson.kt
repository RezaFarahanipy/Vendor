package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_persons")
data class ContactPerson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val name: String,
    val role: String = "مدیر فروش",
    val phone: String = "",
    val email: String = ""
)
