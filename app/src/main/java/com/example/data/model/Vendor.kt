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
    val activityType: String = "تولیدکننده", // تولیدکننده, بازرگانی, واردکننده, ...
    val phone: String = "",
    val mobile: String = "",
    val secondaryPhones: String = "", // Additional landline numbers (comma-separated)
    val additionalMobiles: String = "", // Additional mobile numbers (comma-separated)
    val email: String = "",
    val website: String = "",
    val province: String = "تهران",
    val city: String = "تهران",
    val address: String = "",
    val latitude: Double = 35.73576,
    val longitude: Double = 51.44678,
    val logoDrawable: String = "vendor_app_icon",
    val logoUri: String = "", // Saved file path or content URI
    val businessCardUrl: String = "",
    val catalogPdfUrl: String = "",
    val catalogLocalPath: String = "", // Local offline PDF file path
    val catalogFileName: String = "",
    val catalogTitle: String = "کاتالوگ جامع محصولات و مشخصات فنی",
    val isVerified: Boolean = true,
    val isFavorite: Boolean = false,
    val rating: Float = 4.8f,
    val reviewCount: Int = 32,
    val categories: String = "صنعتی, استیل",
    val tags: String = "پمپ سانتریفیوژ, گارانتی ۲ ساله, تأمین مستقیم",
    val notes: String = "",
    val economicCode: String = "", // کد اقتصادی
    val nationalId: String = "", // شناسه ملی
    val registrationNumber: String = "", // شماره ثبت
    val bankAccounts: String = "", // Serialized BankAccounts via BankAccountUtils
    val lastContactDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getAllPhones(): List<String> = buildList {
        if (phone.isNotBlank()) add(phone.trim())
        if (secondaryPhones.isNotBlank()) {
            addAll(
                secondaryPhones
                    .split(",", "،", "\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            )
        }
    }.distinct()

    fun getAllMobiles(): List<String> = buildList {
        if (mobile.isNotBlank()) add(mobile.trim())
        if (additionalMobiles.isNotBlank()) {
            addAll(
                additionalMobiles
                    .split(",", "،", "\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            )
        }
    }.distinct()

    fun getBankAccountsList(): List<BankAccount> {
        return BankAccountUtils.parse(bankAccounts)
    }
}
