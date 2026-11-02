package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendors")
data class Vendor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ceoName: String = "",
    val description: String = "",
    val activityType: String = "تولیدکننده", // تولیدکننده, واردکننده, توزیع‌کننده مستقیم, خدمات فنی
    val phone: String = "",
    val mobile: String = "",
    val secondaryPhones: String = "",
    val email: String = "",
    val website: String = "",
    val province: String = "تهران",
    val city: String = "تهران",
    val address: String = "",
    val latitude: Double = 35.73576,
    val longitude: Double = 51.44678,
    val logoDrawable: String = "vendor_app_icon",
    val businessCardUrl: String = "",
    val catalogPdfUrl: String = "",
    val catalogTitle: String = "کاتالوگ جامع محصولات و مشخصات فنی",
    val isVerified: Boolean = true,
    val isFavorite: Boolean = false,
    val rating: Float = 4.8f,
    val reviewCount: Int = 32,
    val categories: String = "صنعتی, استیل",
    val tags: String = "پمپ سانتریفیوژ, گارانتی ۲ ساله, تأمین مستقیم",
    val notes: String = "",
    val lastContactDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
