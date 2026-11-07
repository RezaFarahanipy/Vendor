package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "contact_persons",
    foreignKeys = [
        ForeignKey(
            entity = Vendor::class,
            parentColumns = ["id"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vendorId")]
)
data class ContactPerson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val name: String,
    val role: String = "مدیر عامل",
    val mobile: String = "",
    val phone: String = "",
    val extension: String = "",
    val email: String = ""
)
