package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class PhoneType {
    LANDLINE,
    MOBILE
}

@Entity(
    tableName = "vendor_phones",
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
data class VendorPhone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val type: String, // LANDLINE, MOBILE
    val number: String,
    val label: String = ""
)
