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
import com.example.data.model.CatalogItem
import com.example.data.model.CatalogCodec
import com.example.data.model.catalogItems
import com.example.data.model.addressItems
import com.example.data.model.VendorAddress
import com.example.data.model.AddressCodec
import com.example.data.model.BankAccount
import com.example.data.model.BankAccountUtils

data class FullBackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val vendors: List<Vendor>,
    val contacts: List<ContactPerson>,
    val products: List<Product>
)

/** A vendor read from a CSV row together with its personnel and products. */
data class ImportedVendor(
    val vendor: Vendor,
    val contacts: List<ContactPerson> = emptyList(),
    val products: List<Product> = emptyList()
)

data class ImportSummary(
    val newCount: Int,
    val duplicateSkippedCount: Int,
    val vendorsToInsert: List<Vendor>,
    val imports: List<ImportedVendor> = emptyList()
)

object CsvUtils {

    const val UTF8_BOM = "\uFEFF"

    private const val ENTRY_SEP = " ## "

    private fun cell(text: String) = text.replace("|", "/").replace("##", "#").replace("\n", " ").trim()

    private val EXPORT_HEADERS = listOf(
        "نام شرکت", "مدیرعامل", "نوع فعالیت", "دسته‌بندی‌ها", "برچسب‌ها", "توضیحات",
        "تلفن ثابت", "سایر تلفن‌های ثابت", "تلفن همراه", "سایر شماره‌های همراه", "ایمیل", "وبسایت",
        "نوع آدرس", "استان", "شهر", "کد پستی", "آدرس", "آدرس‌های دیگر", "عرض جغرافیایی", "طول جغرافیایی",
        "کد اقتصادی", "شناسه ملی", "شماره ثبت",
        "نام بانک", "شماره شبا", "شماره حساب", "شعبه", "سایر حساب‌های بانکی",
        "پرسنل", "محصولات", "یادداشت‌ها", "امتیاز", "تأیید شده"
    )

    /**
     * Converts vendors (with personnel and products) to a CSV string with UTF-8 BOM for Microsoft Excel.
     * Multi-value cells use " ## " between entries and "|" between the parts of one entry:
     *  - other addresses: type|province|city|postal code|address|lat|lng
     *  - other bank accounts: bank|account|IBAN|branch
     *  - personnel: name|role|mobile|phone|extension|email|gender
     *  - products: name|brand|model|technical code|category|specifications
     */
    fun exportVendorsToCsv(
        vendors: List<Vendor>,
        contacts: List<ContactPerson> = emptyList(),
        products: List<Product> = emptyList()
    ): String {
        val contactsByVendor = contacts.groupBy { it.vendorId }
        val productsByVendor = products.groupBy { it.vendorId }
        val sb = StringBuilder()
        sb.append(UTF8_BOM)
        sb.append(EXPORT_HEADERS.joinToString(",") { "\"${escapeCsv(it)}\"" }).append("\n")

        for (v in vendors) {
            val addresses = v.addressItems()
            val a1 = addresses.firstOrNull() ?: VendorAddress(province = v.province, city = v.city)
            val otherAddresses = addresses.drop(1).joinToString(ENTRY_SEP) {
                listOf(it.type, it.province, it.city, it.postalCode, it.address, it.latitude.toString(), it.longitude.toString())
                    .joinToString("|") { f -> cell(f) }
            }
            val banks = v.getBankAccountsList()
            val b1 = banks.firstOrNull()
            val otherBanks = banks.drop(1).joinToString(ENTRY_SEP) {
                listOf(it.bankName, it.accountNumber, it.iban, it.branch).joinToString("|") { f -> cell(f) }
            }
            val people = (contactsByVendor[v.id] ?: emptyList()).joinToString(ENTRY_SEP) {
                listOf(it.name, it.role, it.mobile, it.phone, it.extension, it.email, it.gender).joinToString("|") { f -> cell(f) }
            }
            val prods = (productsByVendor[v.id] ?: emptyList()).joinToString(ENTRY_SEP) {
                listOf(it.name, it.brand, it.model, it.technicalCode, it.category, it.specifications).joinToString("|") { f -> cell(f) }
            }
            val row = listOf(
                v.name, v.ceoName, v.activityType, v.categories, v.tags, v.description,
                v.phone, v.secondaryPhones, v.mobile, v.additionalMobiles, v.email, v.website,
                a1.type, a1.province, a1.city, a1.postalCode, a1.address, otherAddresses,
                if (a1.hasLocation) a1.latitude.toString() else "", if (a1.hasLocation) a1.longitude.toString() else "",
                v.economicCode, v.nationalId, v.registrationNumber,
                b1?.bankName.orEmpty(), b1?.iban.orEmpty(), b1?.accountNumber.orEmpty(), b1?.branch.orEmpty(), otherBanks,
                people, prods, v.notes, v.rating.toString(), if (v.isVerified) "بله" else "خیر"
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
    private fun normHeader(h: String): String =
        PersianUtils.normalize(h).replace("\u200c", "").replace("\u200f", "").replace(" ", "").lowercase()

    private fun fixMobile(raw: String): String {
        val d = PersianUtils.toEnglishDigits(raw).filter { it.isDigit() }
        return if (d.length == 10 && d.startsWith("9")) "0$d" else d // Excel drops the leading zero
    }

    private fun fixLandline(raw: String): String {
        val d = PersianUtils.toEnglishDigits(raw).filter { it.isDigit() }
        if (d.isEmpty()) return ""
        val full = if (d.length == 10 && !d.startsWith("0")) "0$d" else d
        return PersianUtils.formatLandlinePhone(full)
    }

    private fun fixPhoneList(raw: String, f: (String) -> String): String =
        raw.split(',', '،', ';', '\n').map { f(it.trim()) }.filter { it.isNotBlank() }.joinToString(", ")

    private fun parseDouble(raw: String): Double =
        PersianUtils.toEnglishDigits(raw.replace('٫', '.').replace('،', '.')).trim().toDoubleOrNull() ?: 0.0

    /** Reads vendors from CSV by column NAME (so older/shorter files still work), including personnel and products. */
    fun parseCsvToImports(csvContent: String): List<ImportedVendor> {
        val rows = parseCsvToRows(csvContent)
        if (rows.size <= 1) return emptyList()
        val header = rows[0].map { normHeader(it) }
        fun idx(vararg names: String): Int {
            for (n in names) {
                val i = header.indexOf(normHeader(n))
                if (i >= 0) return i
            }
            return -1
        }
        val cName = idx("نام شرکت", "نام وندور").let { if (it < 0) 0 else it }
        val cCeo = idx("مدیرعامل"); val cActivity = idx("نوع فعالیت"); val cCats = idx("دسته‌بندی‌ها", "دسته بندی ها")
        val cTags = idx("برچسب‌ها"); val cDesc = idx("توضیحات")
        val cPhone = idx("تلفن ثابت"); val cPhone2 = idx("سایر تلفن‌های ثابت")
        val cMobile = idx("تلفن همراه"); val cMobile2 = idx("سایر شماره‌های همراه")
        val cEmail = idx("ایمیل"); val cWeb = idx("وبسایت")
        val cAddrType = idx("نوع آدرس"); val cProv = idx("استان"); val cCity = idx("شهر")
        val cPostal = idx("کد پستی"); val cAddr = idx("آدرس"); val cAddr2 = idx("آدرس‌های دیگر")
        val cLat = idx("عرض جغرافیایی"); val cLng = idx("طول جغرافیایی")
        val cEco = idx("کد اقتصادی"); val cNat = idx("شناسه ملی"); val cReg = idx("شماره ثبت")
        val cBankName = idx("نام بانک"); val cIban = idx("شماره شبا", "شبا"); val cAcc = idx("شماره حساب"); val cBranch = idx("شعبه")
        val cBank2 = idx("سایر حساب‌های بانکی"); val cBankLegacy = idx("حساب‌های بانکی")
        val cPeople = idx("پرسنل"); val cProducts = idx("محصولات"); val cNotes = idx("یادداشت‌ها")
        val cRating = idx("امتیاز"); val cVerified = idx("تأیید شده", "تایید شده")

        val result = mutableListOf<ImportedVendor>()
        for (i in 1 until rows.size) {
            val r = rows[i]
            if (r.isEmpty() || r.all { it.isBlank() }) continue
            fun col(c: Int, def: String = ""): String = if (c >= 0) r.getOrElse(c) { def }.trim() else def
            val name = col(cName)
            if (name.isBlank()) continue

            val province = col(cProv, "تهران").ifBlank { "تهران" }
            val city = col(cCity, "تهران").ifBlank { "تهران" }
            val lat = parseDouble(col(cLat)); val lng = parseDouble(col(cLng))
            val postal = PersianUtils.toEnglishDigits(col(cPostal)).filter { it.isDigit() }.take(10)
            val rawAddress = col(cAddr)
            val addrType = col(cAddrType)
            val first = VendorAddress(
                type = addrType.ifBlank { "کارخانه / کارگاه" }, province = province, city = city,
                postalCode = postal, address = rawAddress, latitude = lat, longitude = lng
            )
            // Legacy files keep the whole text in the address column; new files are assembled from the parts.
            val composite = if (cAddrType >= 0 || cPostal >= 0) first.composite() else rawAddress

            val extraAddresses = col(cAddr2).split(ENTRY_SEP.trim()).mapNotNull { entry ->
                val f = entry.trim().split("|").map { it.trim() }
                if (f.all { it.isBlank() }) null else VendorAddress(
                    type = f.getOrElse(0) { "" }.ifBlank { "کارخانه / کارگاه" }, province = f.getOrElse(1) { "" },
                    city = f.getOrElse(2) { "" }, postalCode = PersianUtils.toEnglishDigits(f.getOrElse(3) { "" }).filter { it.isDigit() }.take(10),
                    address = f.getOrElse(4) { "" }, latitude = parseDouble(f.getOrElse(5) { "" }), longitude = parseDouble(f.getOrElse(6) { "" })
                )
            }

            val banks = mutableListOf<BankAccount>()
            if (col(cBankName).isNotBlank() || col(cIban).isNotBlank() || col(cAcc).isNotBlank()) {
                banks.add(BankAccount(
                    bankName = col(cBankName), accountNumber = PersianUtils.toEnglishDigits(col(cAcc)),
                    iban = PersianUtils.toEnglishDigits(col(cIban)).uppercase().replace(" ", ""), branch = col(cBranch)
                ))
            }
            col(cBank2).split(ENTRY_SEP.trim()).forEach { entry ->
                val f = entry.trim().split("|").map { it.trim() }
                if (f.any { it.isNotBlank() }) banks.add(BankAccount(
                    bankName = f.getOrElse(0) { "" }, accountNumber = PersianUtils.toEnglishDigits(f.getOrElse(1) { "" }),
                    iban = PersianUtils.toEnglishDigits(f.getOrElse(2) { "" }).uppercase().replace(" ", ""), branch = f.getOrElse(3) { "" }
                ))
            }
            val bankSerialized = if (banks.isNotEmpty()) BankAccountUtils.serialize(banks) else col(cBankLegacy)

            val verifiedText = col(cVerified)
            var vendor = Vendor(
                id = 0L,
                name = name,
                ceoName = col(cCeo),
                activityType = col(cActivity, "تولیدکننده").ifBlank { "تولیدکننده" },
                categories = col(cCats),
                tags = col(cTags),
                phone = fixLandline(col(cPhone)),
                secondaryPhones = fixPhoneList(col(cPhone2)) { fixLandline(it) },
                mobile = fixMobile(col(cMobile)),
                additionalMobiles = fixPhoneList(col(cMobile2)) { fixMobile(it) },
                email = col(cEmail),
                website = col(cWeb),
                province = province,
                city = city,
                address = composite,
                latitude = lat,
                longitude = lng,
                economicCode = PersianUtils.toEnglishDigits(col(cEco)).filter { it.isDigit() },
                nationalId = PersianUtils.toEnglishDigits(col(cNat)).filter { it.isDigit() },
                registrationNumber = PersianUtils.toEnglishDigits(col(cReg)),
                bankAccounts = bankSerialized,
                notes = col(cNotes),
                rating = PersianUtils.toEnglishDigits(col(cRating, "0")).toFloatOrNull() ?: 0f,
                isVerified = verifiedText == "بله" || verifiedText.equals("true", ignoreCase = true),
                description = col(cDesc).ifBlank { "وارد شده از فایل اکسل / CSV" },
                createdAt = System.currentTimeMillis()
            )
            if (extraAddresses.isNotEmpty()) {
                val firstItem = vendor.addressItems().firstOrNull() ?: first
                vendor = vendor.copy(addressesJson = AddressCodec.encode(listOf(firstItem) + extraAddresses))
            }

            val contacts = col(cPeople).split(ENTRY_SEP.trim()).mapNotNull { entry ->
                val f = entry.trim().split("|").map { it.trim() }
                val nm = f.getOrElse(0) { "" }
                if (nm.isBlank()) null else ContactPerson(
                    vendorId = 0L, name = nm, role = f.getOrElse(1) { "" },
                    mobile = fixMobile(f.getOrElse(2) { "" }), phone = fixLandline(f.getOrElse(3) { "" }),
                    extension = PersianUtils.toEnglishDigits(f.getOrElse(4) { "" }).filter { it.isDigit() },
                    email = f.getOrElse(5) { "" }, gender = f.getOrElse(6) { "" }.takeIf { it == "آقا" || it == "خانم" } ?: ""
                )
            }
            val prods = col(cProducts).split(ENTRY_SEP.trim()).mapNotNull { entry ->
                val f = entry.trim().split("|").map { it.trim() }
                val nm = f.getOrElse(0) { "" }
                if (nm.isBlank()) null else Product(
                    vendorId = 0L, name = nm, brand = f.getOrElse(1) { "" }, model = f.getOrElse(2) { "" },
                    technicalCode = f.getOrElse(3) { "" }, category = f.getOrElse(4) { "" }, specifications = f.getOrElse(5) { "" }
                )
            }
            result.add(ImportedVendor(vendor, contacts, prods))
        }
        return result
    }

    /**
     * Deduplicates imported vendors against existing database vendors
     */
    fun deduplicateImports(incoming: List<ImportedVendor>, existing: List<Vendor>): ImportSummary {
        val existingNormalizedNames = existing.map { PersianUtils.normalize(it.name).trim() }.filter { it.isNotBlank() }.toSet()
        val existingNationalIds = existing.map { it.nationalId.filter { c -> c.isDigit() } }.filter { it.isNotBlank() }.toSet()
        val existingMobiles = existing.map { PersianUtils.normalizePhone(it.mobile) }.filter { it.isNotBlank() }.toSet()

        val toInsert = mutableListOf<ImportedVendor>()
        var duplicatesCount = 0
        val seenInBatchNames = mutableSetOf<String>()

        for (item in incoming) {
            val vendor = item.vendor
            val normName = PersianUtils.normalize(vendor.name).trim()
            val normNational = vendor.nationalId.filter { it.isDigit() }
            val normMobile = PersianUtils.normalizePhone(vendor.mobile)

            val isDuplicate = (normName.isNotBlank() && (normName in existingNormalizedNames || normName in seenInBatchNames)) ||
                    (normNational.isNotBlank() && normNational in existingNationalIds) ||
                    (normMobile.isNotBlank() && normMobile in existingMobiles)

            if (isDuplicate) {
                duplicatesCount++
            } else {
                toInsert.add(item)
                if (normName.isNotBlank()) seenInBatchNames.add(normName)
            }
        }

        return ImportSummary(
            newCount = toInsert.size,
            duplicateSkippedCount = duplicatesCount,
            vendorsToInsert = toInsert.map { it.vendor },
            imports = toInsert
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
            vo.put("catalogFilesJson", v.catalogFilesJson)
            vo.put("addressesJson", v.addressesJson)
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
                    co.put("gender", c.gender)
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
                        catalogFilesJson = vo.optString("catalogFilesJson", "[]"),
                        addressesJson = vo.optString("addressesJson", "[]"),
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
                        gender = co.optString("gender", ""),
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
            vo.put("catalogFilesJson", v.catalogFilesJson)
            vo.put("addressesJson", v.addressesJson)
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

            // Pack all catalog files (multiple) if present locally
            val catalogsOut = JSONArray()
            v.catalogItems().forEachIndexed { idx, item ->
                val catFile = if (item.path.isNotBlank()) File(item.path) else null
                if (catFile != null && catFile.exists() && catFile.isFile) {
                    val entryName = "files/catalogs/v_${v.id}_${idx}_${catFile.name}"
                    if (filesWritten.add(entryName)) {
                        zipOut.putNextEntry(ZipEntry(entryName))
                        catFile.inputStream().use { input -> input.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                    catalogsOut.put(
                        JSONObject()
                            .put("title", item.title)
                            .put("fileName", item.fileName)
                            .put("relativePath", entryName)
                    )
                }
            }
            if (catalogsOut.length() > 0) {
                vo.put("catalogs", catalogsOut)
                vo.put("relativeCatalogPath", catalogsOut.getJSONObject(0).getString("relativePath"))
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
                    co.put("gender", c.gender)
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
                val catArr = vo.optJSONArray("catalogs")
                if (catArr != null && catArr.length() > 0) {
                    val restored = ArrayList<CatalogItem>()
                    for (ci in 0 until catArr.length()) {
                        val co = catArr.optJSONObject(ci) ?: continue
                        val extracted = extractedFiles[co.optString("relativePath", "")] ?: continue
                        restored.add(CatalogItem(co.optString("title", ""), co.optString("fileName", ""), File(extracted).absolutePath))
                    }
                    if (restored.isNotEmpty()) {
                        updated = updated.copy(
                            catalogFilesJson = CatalogCodec.encode(restored),
                            catalogLocalPath = restored[0].path,
                            catalogFileName = restored[0].fileName,
                            catalogTitle = restored[0].title.ifBlank { updated.catalogTitle }
                        )
                    }
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
