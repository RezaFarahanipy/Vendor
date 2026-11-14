package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

/** One attached catalog / brochure file of a vendor. */
data class CatalogItem(
    val title: String,
    val fileName: String,
    val path: String
)

/** Stores a vendor's catalog list as a JSON array inside a single text column. */
object CatalogCodec {
    fun encode(items: List<CatalogItem>): String {
        val arr = JSONArray()
        items.forEach {
            arr.put(JSONObject().put("title", it.title).put("fileName", it.fileName).put("path", it.path))
        }
        return arr.toString()
    }

    fun decode(json: String?): List<CatalogItem> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.optJSONObject(i) ?: return@mapNotNull null
                val path = o.optString("path", "")
                if (path.isBlank()) null else CatalogItem(o.optString("title", ""), o.optString("fileName", ""), path)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

/** All catalogs of the vendor; falls back to the legacy single-file fields for older records. */
fun Vendor.catalogItems(): List<CatalogItem> {
    val list = CatalogCodec.decode(catalogFilesJson)
    if (list.isNotEmpty()) return list
    return if (catalogLocalPath.isNotBlank()) {
        listOf(CatalogItem(catalogTitle, catalogFileName, catalogLocalPath))
    } else emptyList()
}
