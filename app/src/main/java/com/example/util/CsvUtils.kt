package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class FullBackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val vendors: List<Vendor>,
    val contacts: List<ContactPerson>,
    val products: List<Product>
)

data class ImportSummary(
    val newCount: Int,
    val duplicateSkippedCount: Int,
    val vendorsToInsert: List<Vendor>
)

object CsvUtils {

    const val UTF8_BOM = "\uFEFF"

    /**
     * Converts a list of vendors to standard CSV string with UTF-8 BOM for Microsoft Excel compatibility
     */
    fun exportVendorsToCsv(vendors: List<Vendor>): String {
        val sb = StringBuilder()
        sb.append(UTF8_BOM)
        // Full comprehensive Header
        val headers = listOf(
            "نام شرکت",
            "مدیرعامل",
            "نوع فعالیت",
            "دسته‌بندی‌ها",
            "برچسب‌ها",
            "تلفن ثابت",
            "سایر تلفن‌های ثابت",
            "تلفن همراه",
            "سایر شماره‌های همراه",
            "ایمیل",
            "وبسایت",
            "استان",
            "شهر",
            "آدرس",
            "کد اقتصادی",
            "شناسه ملی",
            "شماره ثبت",
            "حساب‌های بانکی",
            "یادداشت‌ها",
            "عرض جغرافیایی",
            "طول جغرافیایی",
            "امتیاز",
            "تأیید شده"
        )
        sb.append(headers.joinToString(",") { "\"${escapeCsv(it)}\"" }).append("\n")

        for (v in vendors) {
            val row = listOf(
                v.name,
                v.ceoName,
                v.activityType,
                v.categories,
                v.tags,
                v.phone,
                v.secondaryPhones,
                v.mobile,
                v.additionalMobiles,
                v.email,
                v.website,
                v.province,
                v.city,
                v.address,
                v.economicCode,
                v.nationalId,
                v.registrationNumber,
                v.bankAccounts,
                v.notes,
                v.latitude.toString(),
                v.longitude.toString(),
                v.rating.toString(),
                if (v.isVerified) "بله" else "خیر"
            )
            sb.append(row.joinToString(",") { "\"${escapeCsv(it)}\"" }).append("\n")
        }
        return sb.toString()
    }

    /**
     * Parses CSV content safely handling quotes, multi-line values, BOM and both comma/semicolon delimiters.
     */
    fun parseCsvToRows(csvContent: String): List<List<String>> {
        val cleanContent = if (csvContent.startsWith(UTF8_BOM)) csvContent.substring(UTF8_BOM.length) else csvContent
        if (cleanContent.isBlank()) return emptyList()

        // Auto-detect delimiter (, or ;)
        val firstLineSample = cleanContent.lineSequence().firstOrNull() ?: ""
        val semicolonCount = firstLineSample.count { it == ';' }
        val commaCount = firstLineSample.count { it == ',' }
        val delimiter = if (semicolonCount > commaCount) ';' else ','

        val rows = mutableListOf<List<String>>()
        val currentRow = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < cleanContent.length) {
            val c = cleanContent[i]
            when {
                c == '\"' -> {
                    if (inQuotes && i + 1 < cleanContent.length && cleanContent[i + 1] == '\"') {
                        currentField.append('\"')
                        i++ // Skip escaped quote
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == delimiter && !inQuotes -> {
                    currentRow.add(currentField.toString().trim())
                    currentField.clear()
                }
                (c == '\r' || c == '\n') && !inQuotes -> {
                    if (c == '\r' && i + 1 < cleanContent.length && cleanContent[i + 1] == '\n') {
                        i++ // Skip CRLF
                    }
                    currentRow.add(currentField.toString().trim())
                    currentField.clear()
                    if (currentRow.any { it.isNotBlank() }) {
                        rows.add(currentRow.toList())
                    }
                    currentRow.clear()
                }
                else -> {
                    currentField.append(c)
                }
            }
            i++
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString().trim())
            if (currentRow.any { it.isNotBlank() }) {
                rows.add(currentRow.toList())
            }
        }

        return rows
    }

    /**
     * Parses CSV rows into Vendor objects, skipping header.
     */
    fun parseCsvToVendors(csvContent: String): List<Vendor> {
        val rows = parseCsvToRows(csvContent)
        if (rows.size <= 1) return emptyList()

        val vendors = mutableListOf<Vendor>()
        // Skip header at index 0
        for (i in 1 until rows.size) {
            val cols = rows[i]
            if (cols.isEmpty() || cols[0].isBlank()) continue

            val name = cols.getOrElse(0) { "" }.trim()
            val ceoName = cols.getOrElse(1) { "" }.trim()
            val activityType = cols.getOrElse(2) { "تولیدکننده" }.trim()
            val categories = cols.getOrElse(3) { "" }.trim()
            val tags = cols.getOrElse(4) { "" }.trim()
            val phone = cols.getOrElse(5) { "" }.trim()
            val secondaryPhones = cols.getOrElse(6) { "" }.trim()
            val mobile = cols.getOrElse(7) { "" }.trim()
            val additionalMobiles = cols.getOrElse(8) { "" }.trim()
            val email = cols.getOrElse(9) { "" }.trim()
            val website = cols.getOrElse(10) { "" }.trim()
            val province = cols.getOrElse(11) { "تهران" }.trim()
            val city = cols.getOrElse(12) { "تهران" }.trim()
            val address = cols.getOrElse(13) { "" }.trim()
            val economicCode = cols.getOrElse(14) { "" }.trim()
            val nationalId = cols.getOrElse(15) { "" }.trim()
            val registrationNumber = cols.getOrElse(16) { "" }.trim()
            val bankAccounts = cols.getOrElse(17) { "" }.trim()
            val notes = cols.getOrElse(18) { "" }.trim()
            val lat = cols.getOrElse(19) { "35.73576" }.toDoubleOrNull() ?: 35.73576
            val lng = cols.getOrElse(20) { "51.44678" }.toDoubleOrNull() ?: 51.44678
            val rating = cols.getOrElse(21) { "0" }.toFloatOrNull() ?: 0f
            val isVerifiedText = cols.getOrElse(22) { "خیر" }.trim()
            val isVerified = isVerifiedText == "بله" || isVerifiedText.equals("true", ignoreCase = true)

            vendors.add(
                Vendor(
                    id = 0L,
                    name = name,
                    ceoName = ceoName,
                    activityType = activityType,
                    categories = categories,
                    tags = tags,
                    phone = phone,
                    secondaryPhones = secondaryPhones,
                    mobile = mobile,
                    additionalMobiles = additionalMobiles,
                    email = email,
                    website = website,
                    province = province,
                    city = city,
                    address = address,
                    economicCode = economicCode,
                    nationalId = nationalId,
                    registrationNumber = registrationNumber,
                    bankAccounts = bankAccounts,
                    notes = notes,
                    latitude = lat,
                    longitude = lng,
                    rating = rating,
                    isVerified = isVerified,
                    description = "وارد شده از فایل اکسل / CSV",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
        return vendors
    }

    /**
     * Deduplicates imported vendors against existing database vendors
     */
    fun deduplicateVendors(incoming: List<Vendor>, existing: List<Vendor>): ImportSummary {
        val existingNormalizedNames = existing.map { PersianUtils.normalize(it.name).trim() }.filter { it.isNotBlank() }.toSet()
        val existingNationalIds = existing.map { it.nationalId.filter { c -> c.isDigit() } }.filter { it.isNotBlank() }.toSet()
        val existingMobiles = existing.map { PersianUtils.normalizePhone(it.mobile) }.filter { it.isNotBlank() }.toSet()

        val toInsert = mutableListOf<Vendor>()
        var duplicatesCount = 0

        val seenInBatchNames = mutableSetOf<String>()

        for (vendor in incoming) {
            val normName = PersianUtils.normalize(vendor.name).trim()
            val normNational = vendor.nationalId.filter { it.isDigit() }
            val normMobile = PersianUtils.normalizePhone(vendor.mobile)

            val isDuplicate = (normName.isNotBlank() && (normName in existingNormalizedNames || normName in seenInBatchNames)) ||
                    (normNational.isNotBlank() && normNational in existingNationalIds) ||
                    (normMobile.isNotBlank() && normMobile in existingMobiles)

            if (isDuplicate) {
                duplicatesCount++
            } else {
                toInsert.add(vendor)
                if (normName.isNotBlank()) seenInBatchNames.add(normName)
            }
        }

        return ImportSummary(
            newCount = toInsert.size,
            duplicateSkippedCount = duplicatesCount,
            vendorsToInsert = toInsert
        )
    }

    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"")
    }

    // ==========================================
    // Full JSON Backup & Restore Implementation
    // ==========================================

    fun exportFullBackupJson(
        vendors: List<Vendor>,
        contacts: List<ContactPerson>,
        products: List<Product>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("appName", "VendorYab")

        val vendorsArray = JSONArray()
        for (v in vendors) {
            val vo = JSONObject()
            vo.put("id", v.id)
            vo.put("name", v.name)
            vo.put("ceoName", v.ceoName)
            vo.put("description", v.description)
            vo.put("activityType", v.activityType)
            vo.put("phone", v.phone)
            vo.put("secondaryPhones", v.secondaryPhones)
            vo.put("mobile", v.mobile)
            vo.put("additionalMobiles", v.additionalMobiles)
            vo.put("email", v.email)
            vo.put("website", v.website)
            vo.put("province", v.province)
            vo.put("city", v.city)
            vo.put("address", v.address)
            vo.put("latitude", v.latitude)
            vo.put("longitude", v.longitude)
            vo.put("logoDrawable", v.logoDrawable)
            vo.put("logoUri", v.logoUri)
            vo.put("catalogTitle", v.catalogTitle)
            vo.put("catalogLocalPath", v.catalogLocalPath)
            vo.put("catalogFileName", v.catalogFileName)
            vo.put("isVerified", v.isVerified)
            vo.put("isFavorite", v.isFavorite)
            vo.put("rating", v.rating.toDouble())
            vo.put("reviewCount", v.reviewCount)
            vo.put("categories", v.categories)
            vo.put("tags", v.tags)
            vo.put("notes", v.notes)
            vo.put("economicCode", v.economicCode)
            vo.put("nationalId", v.nationalId)
            vo.put("registrationNumber", v.registrationNumber)
            vo.put("bankAccounts", v.bankAccounts)
            vo.put("createdAt", v.createdAt)
            vendorsArray.put(vo)
        }
        root.put("vendors", vendorsArray)

        val contactsArray = JSONArray()
        for (c in contacts) {
            val co = JSONObject()
            co.put("id", c.id)
            co.put("vendorId", c.vendorId)
            co.put("name", c.name)
            co.put("role", c.role)
            co.put("mobile", c.mobile)
            co.put("phone", c.phone)
            co.put("extension", c.extension)
            co.put("email", c.email)
            contactsArray.put(co)
        }
        root.put("contacts", contactsArray)

        val productsArray = JSONArray()
        for (p in products) {
            val po = JSONObject()
            po.put("id", p.id)
            po.put("vendorId", p.vendorId)
            po.put("name", p.name)
            po.put("brand", p.brand)
            po.put("model", p.model)
            po.put("technicalCode", p.technicalCode)
            po.put("specifications", p.specifications)
            po.put("description", p.description)
            po.put("category", p.category)
            po.put("isFavorite", p.isFavorite)
            productsArray.put(po)
        }
        root.put("products", productsArray)

        return root.toString(2)
    }

    fun parseFullBackupJson(jsonString: String): FullBackupData? {
        return try {
            val root = JSONObject(jsonString)
            val version = root.optInt("version", 1)
            val timestamp = root.optLong("timestamp", System.currentTimeMillis())

            val vendorsList = mutableListOf<Vendor>()
            val vendorsArray = root.optJSONArray("vendors") ?: JSONArray()
            for (i in 0 until vendorsArray.length()) {
                val vo = vendorsArray.getJSONObject(i)
                vendorsList.add(
                    Vendor(
                        id = vo.optLong("id", 0L),
                        name = vo.optString("name", ""),
                        ceoName = vo.optString("ceoName", ""),
                        description = vo.optString("description", ""),
                        activityType = vo.optString("activityType", "تولیدکننده"),
                        phone = vo.optString("phone", ""),
                        secondaryPhones = vo.optString("secondaryPhones", ""),
                        mobile = vo.optString("mobile", ""),
                        additionalMobiles = vo.optString("additionalMobiles", ""),
                        email = vo.optString("email", ""),
                        website = vo.optString("website", ""),
                        province = vo.optString("province", "تهران"),
                        city = vo.optString("city", "تهران"),
                        address = vo.optString("address", ""),
                        latitude = vo.optDouble("latitude", 35.73576),
                        longitude = vo.optDouble("longitude", 51.44678),
                        logoDrawable = vo.optString("logoDrawable", "vendor_app_icon"),
                        logoUri = vo.optString("logoUri", ""),
                        catalogTitle = vo.optString("catalogTitle", ""),
                        catalogLocalPath = vo.optString("catalogLocalPath", ""),
                        catalogFileName = vo.optString("catalogFileName", ""),
                        isVerified = vo.optBoolean("isVerified", false),
                        isFavorite = vo.optBoolean("isFavorite", false),
                        rating = vo.optDouble("rating", 0.0).toFloat(),
                        reviewCount = vo.optInt("reviewCount", 0),
                        categories = vo.optString("categories", ""),
                        tags = vo.optString("tags", ""),
                        notes = vo.optString("notes", ""),
                        economicCode = vo.optString("economicCode", ""),
                        nationalId = vo.optString("nationalId", ""),
                        registrationNumber = vo.optString("registrationNumber", ""),
                        bankAccounts = vo.optString("bankAccounts", ""),
                        createdAt = vo.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val contactsList = mutableListOf<ContactPerson>()
            val contactsArray = root.optJSONArray("contacts") ?: JSONArray()
            for (i in 0 until contactsArray.length()) {
                val co = contactsArray.getJSONObject(i)
                val cName = if (co.has("name")) co.optString("name", "") else co.optString("fullName", "")
                contactsList.add(
                    ContactPerson(
                        id = co.optLong("id", 0L),
                        vendorId = co.optLong("vendorId", 0L),
                        name = cName,
                        role = co.optString("role", "مدیر"),
                        mobile = co.optString("mobile", ""),
                        phone = co.optString("phone", ""),
                        extension = co.optString("extension", ""),
                        email = co.optString("email", "")
                    )
                )
            }

            val productsList = mutableListOf<Product>()
            val productsArray = root.optJSONArray("products") ?: JSONArray()
            for (i in 0 until productsArray.length()) {
                val po = productsArray.getJSONObject(i)
                val tCode = if (po.has("technicalCode")) po.optString("technicalCode", "") else po.optString("productCode", "")
                productsList.add(
                    Product(
                        id = po.optLong("id", 0L),
                        vendorId = po.optLong("vendorId", 0L),
                        name = po.optString("name", ""),
                        brand = po.optString("brand", ""),
                        model = po.optString("model", ""),
                        technicalCode = tCode,
                        specifications = po.optString("specifications", ""),
                        description = po.optString("description", ""),
                        category = po.optString("category", ""),
                        isFavorite = po.optBoolean("isFavorite", false)
                    )
                )
            }

            FullBackupData(
                version = version,
                timestamp = timestamp,
                vendors = vendorsList,
                contacts = contactsList,
                products = productsList
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Exports complete application state (vendors, contacts, products) along with
     * all local logo images and PDF catalog files packaged into a single ZIP archive.
     */
    fun exportFullBackupZip(
        context: Context,
        outputStream: OutputStream,
        vendors: List<Vendor>,
        contacts: List<ContactPerson>,
        products: List<Product>
    ) {
        val zipOut = ZipOutputStream(outputStream)
        val filesWritten = mutableSetOf<String>()

        val root = JSONObject()
        root.put("version", 2)
        root.put("timestamp", System.currentTimeMillis())
        root.put("appName", "VendorYab")

        val vendorsArray = JSONArray()
        for (v in vendors) {
            val vo = JSONObject()
            vo.put("id", v.id)
            vo.put("name", v.name)
            vo.put("ceoName", v.ceoName)
            vo.put("description", v.description)
            vo.put("activityType", v.activityType)
            vo.put("phone", v.phone)
            vo.put("secondaryPhones", v.secondaryPhones)
            vo.put("mobile", v.mobile)
            vo.put("additionalMobiles", v.additionalMobiles)
            vo.put("email", v.email)
            vo.put("website", v.website)
            vo.put("province", v.province)
            vo.put("city", v.city)
            vo.put("address", v.address)
            vo.put("latitude", v.latitude)
            vo.put("longitude", v.longitude)
            vo.put("logoDrawable", v.logoDrawable)
            vo.put("logoUri", v.logoUri)
            vo.put("catalogTitle", v.catalogTitle)
            vo.put("catalogLocalPath", v.catalogLocalPath)
            vo.put("catalogFileName", v.catalogFileName)
            vo.put("isVerified", v.isVerified)
            vo.put("isFavorite", v.isFavorite)
            vo.put("rating", v.rating.toDouble())
            vo.put("reviewCount", v.reviewCount)
            vo.put("categories", v.categories)
            vo.put("tags", v.tags)
            vo.put("notes", v.notes)
            vo.put("economicCode", v.economicCode)
            vo.put("nationalId", v.nationalId)
            vo.put("registrationNumber", v.registrationNumber)
            vo.put("bankAccounts", v.bankAccounts)
            vo.put("createdAt", v.createdAt)

            // Pack logo file if present locally
            if (v.logoUri.isNotBlank()) {
                val logoFile = resolveLocalFile(context, v.logoUri)
                if (logoFile != null && logoFile.exists() && logoFile.isFile) {
                    val entryName = "files/logos/v_${v.id}_${logoFile.name}"
                    if (filesWritten.add(entryName)) {
                        zipOut.putNextEntry(ZipEntry(entryName))
                        logoFile.inputStream().use { input -> input.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                    vo.put("relativeLogoPath", entryName)
                }
            }

            // Pack catalog PDF if present locally
            if (v.catalogLocalPath.isNotBlank()) {
                val catFile = File(v.catalogLocalPath)
                if (catFile.exists() && catFile.isFile) {
                    val entryName = "files/catalogs/v_${v.id}_${catFile.name}"
                    if (filesWritten.add(entryName)) {
                        zipOut.putNextEntry(ZipEntry(entryName))
                        catFile.inputStream().use { input -> input.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                    vo.put("relativeCatalogPath", entryName)
                }
            }

            vendorsArray.put(vo)
        }
        root.put("vendors", vendorsArray)

        val contactsArray = JSONArray()
        for (c in contacts) {
            val co = JSONObject()
            co.put("id", c.id)
            co.put("vendorId", c.vendorId)
            co.put("name", c.name)
            co.put("role", c.role)
            co.put("mobile", c.mobile)
            co.put("phone", c.phone)
            co.put("extension", c.extension)
            co.put("email", c.email)
            contactsArray.put(co)
        }
        root.put("contacts", contactsArray)

        val productsArray = JSONArray()
        for (p in products) {
            val po = JSONObject()
            po.put("id", p.id)
            po.put("vendorId", p.vendorId)
            po.put("name", p.name)
            po.put("brand", p.brand)
            po.put("model", p.model)
            po.put("technicalCode", p.technicalCode)
            po.put("specifications", p.specifications)
            po.put("description", p.description)
            po.put("category", p.category)
            po.put("isFavorite", p.isFavorite)
            productsArray.put(po)
        }
        root.put("products", productsArray)

        // Write backup.json root descriptor
        zipOut.putNextEntry(ZipEntry("backup.json"))
        zipOut.write(root.toString(2).toByteArray(Charsets.UTF_8))
        zipOut.closeEntry()

        zipOut.finish()
    }

    /**
     * Imports a full backup package, automatically detecting whether the input
     * is a comprehensive ZIP archive (with bundled logo & PDF files) or a legacy JSON file.
     */
    fun importFullBackupZipOrJson(context: Context, inputStream: InputStream): FullBackupData? {
        val bufferedInput = if (inputStream is BufferedInputStream) inputStream else BufferedInputStream(inputStream)
        bufferedInput.mark(16)
        val header = ByteArray(4)
        val bytesRead = bufferedInput.read(header)
        bufferedInput.reset()

        val isZip = bytesRead == 4 &&
                header[0] == 0x50.toByte() &&
                header[1] == 0x4B.toByte() &&
                header[2] == 0x03.toByte() &&
                header[3] == 0x04.toByte()

        return if (isZip) {
            parseZipBackup(context, bufferedInput)
        } else {
            val jsonText = bufferedInput.bufferedReader(Charsets.UTF_8).readText()
            parseFullBackupJson(jsonText)
        }
    }

    private fun parseZipBackup(context: Context, inputStream: InputStream): FullBackupData? {
        val zipIn = ZipInputStream(inputStream)
        var jsonContent: String? = null
        val extractedFiles = mutableMapOf<String, String>()

        var entry = zipIn.nextEntry
        while (entry != null) {
            val name = entry.name
            if (name == "backup.json") {
                val baos = ByteArrayOutputStream()
                val buffer = ByteArray(4096)
                var len: Int
                while (zipIn.read(buffer).also { len = it } > 0) {
                    baos.write(buffer, 0, len)
                }
                jsonContent = baos.toString(Charsets.UTF_8.name())
            } else if (name.startsWith("files/") && !entry.isDirectory) {
                val relativePath = name.removePrefix("files/")
                val targetFile = File(context.filesDir, relativePath)
                targetFile.parentFile?.mkdirs()
                FileOutputStream(targetFile).use { fos ->
                    val buffer = ByteArray(4096)
                    var len: Int
                    while (zipIn.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                }
                extractedFiles[name] = targetFile.absolutePath
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }

        if (jsonContent.isNullOrBlank()) return null
        val baseData = parseFullBackupJson(jsonContent) ?: return null

        val root = try { JSONObject(jsonContent) } catch (_: Exception) { null }
        val vendorsArray = root?.optJSONArray("vendors")

        val remappedVendors = baseData.vendors.mapIndexed { index, vendor ->
            var updated = vendor
            val vo = vendorsArray?.optJSONObject(index)
            if (vo != null) {
                val relLogo = vo.optString("relativeLogoPath", "")
                if (relLogo.isNotBlank() && extractedFiles.containsKey(relLogo)) {
                    val localFile = File(extractedFiles[relLogo]!!)
                    updated = updated.copy(logoUri = Uri.fromFile(localFile).toString())
                }
                val relCat = vo.optString("relativeCatalogPath", "")
                if (relCat.isNotBlank() && extractedFiles.containsKey(relCat)) {
                    val localCatFile = File(extractedFiles[relCat]!!)
                    updated = updated.copy(catalogLocalPath = localCatFile.absolutePath)
                }
            }
            updated
        }

        return baseData.copy(vendors = remappedVendors)
    }

    private fun resolveLocalFile(context: Context, pathOrUri: String): File? {
        if (pathOrUri.isBlank()) return null
        return try {
            if (pathOrUri.startsWith("file://")) {
                val uri = Uri.parse(pathOrUri)
                uri.path?.let { File(it) }
            } else if (pathOrUri.startsWith("/")) {
                File(pathOrUri)
            } else {
                val fileInFilesDir = File(context.filesDir, pathOrUri)
                if (fileInFilesDir.exists()) fileInFilesDir else null
            }
        } catch (_: Exception) {
            null
        }
    }
}
