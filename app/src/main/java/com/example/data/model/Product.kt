package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vendorId: Long,
    val name: String,
    val brand: String = "",
    val model: String = "",
    val technicalCode: String = "",
    val specifications: String = "",
    val description: String = "",
    val category: String = "",
    val imageDrawable: String = "industrial_pump_system",
    val catalogPdfUrl: String = "",
    val isFavorite: Boolean = false
)
