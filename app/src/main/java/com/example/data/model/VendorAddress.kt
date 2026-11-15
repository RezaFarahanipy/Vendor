package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicLong

private val uidCounter = AtomicLong(1)

/** One address of a vendor (factory, head office, warehouse, showroom...) with its map location. */
data class VendorAddress(
    val type: String = "کارخانه / کارگاه",
    val province: String = "تهران",
    val city: String = "تهران",
    val postalCode: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    /** In-memory identity so list items keep their UI state while editing (not persisted). */
    val uid: Long = uidCounter.getAndIncrement()
) {
    /** Legacy single-string form kept on the vendor row: "[type] address (کد پستی: postal)". */
    fun composite(): String = when {
        postalCode.isNotBlank() && !address.contains("کد پستی") -> "[$type] ${address.trim()} (کد پستی: $postalCode)"
        type.isNotBlank() && !address.startsWith("[") -> "[$type] ${address.trim()}"
        else -> address.trim()
    }

    val hasLocation: Boolean get() = !(latitude == 0.0 && longitude == 0.0)
}

object AddressCodec {
    fun encode(items: List<VendorAddress>): String {
        val arr = JSONArray()
        items.forEach {
            arr.put(
                JSONObject()
                    .put("type", it.type)
                    .put("province", it.province)
                    .put("city", it.city)
                    .put("postalCode", it.postalCode)
                    .put("address", it.address)
                    .put("lat", it.latitude)
                    .put("lng", it.longitude)
            )
        }
        return arr.toString()
    }

    fun decode(json: String?): List<VendorAddress> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.optJSONObject(i) ?: return@mapNotNull null
                VendorAddress(
                    type = o.optString("type", ""),
                    province = o.optString("province", ""),
                    city = o.optString("city", ""),
                    postalCode = o.optString("postalCode", ""),
                    address = o.optString("address", ""),
                    latitude = o.optDouble("lat", 0.0),
                    longitude = o.optDouble("lng", 0.0)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

/** All addresses of the vendor; falls back to the legacy single-address fields for older records. */
fun Vendor.addressItems(): List<VendorAddress> {
    val list = AddressCodec.decode(addressesJson)
    if (list.isNotEmpty()) return list
    val hasLegacy = address.isNotBlank() || latitude != 0.0 || longitude != 0.0
    if (!hasLegacy) return emptyList()
    val type = if (address.startsWith("[") && address.contains("]")) address.substringAfter("[").substringBefore("]") else "کارخانه / کارگاه"
    val postal = if (address.contains("کد پستی:")) address.substringAfter("کد پستی:").filter { it.isDigit() }.take(10) else ""
    val plain = if (address.startsWith("[") && address.contains("]")) address.substringAfter("]").substringBefore("(کد پستی:").trim() else address
    return listOf(
        VendorAddress(type = type, province = province, city = city, postalCode = postal, address = plain, latitude = latitude, longitude = longitude)
    )
}
