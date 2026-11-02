package com.example.util

import com.example.data.model.Vendor

object CsvUtils {

    /**
     * Converts a list of vendors to standard CSV string
     */
    fun exportVendorsToCsv(vendors: List<Vendor>): String {
        val sb = java.lang.StringBuilder()
        // Header
        sb.append("نام شرکت,مدیرعامل,تلفن,موبایل,ایمیل,وبسایت,استان,شهر,آدرس,نوع فعالیت,دسته‌بندی‌ها,برچسب‌ها,امتیاز\n")
        for (v in vendors) {
            sb.append("\"${escapeCsv(v.name)}\",")
            sb.append("\"${escapeCsv(v.ceoName)}\",")
            sb.append("\"${escapeCsv(v.phone)}\",")
            sb.append("\"${escapeCsv(v.mobile)}\",")
            sb.append("\"${escapeCsv(v.email)}\",")
            sb.append("\"${escapeCsv(v.website)}\",")
            sb.append("\"${escapeCsv(v.province)}\",")
            sb.append("\"${escapeCsv(v.city)}\",")
            sb.append("\"${escapeCsv(v.address)}\",")
            sb.append("\"${escapeCsv(v.activityType)}\",")
            sb.append("\"${escapeCsv(v.categories)}\",")
            sb.append("\"${escapeCsv(v.tags)}\",")
            sb.append("${v.rating}\n")
        }
        return sb.toString()
    }

    /**
     * Parses CSV content into a list of Vendor objects
     */
    fun parseCsvToVendors(csvContent: String): List<Vendor> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        val result = mutableListOf<Vendor>()
        // Skip header
        for (i in 1 until lines.size) {
            val line = lines[i]
            val cols = parseCsvLine(line)
            if (cols.isNotEmpty() && cols[0].isNotBlank()) {
                val vendor = Vendor(
                    name = cols.getOrElse(0) { "" },
                    ceoName = cols.getOrElse(1) { "" },
                    phone = cols.getOrElse(2) { "" },
                    mobile = cols.getOrElse(3) { "" },
                    email = cols.getOrElse(4) { "" },
                    website = cols.getOrElse(5) { "" },
                    province = cols.getOrElse(6) { "تهران" },
                    city = cols.getOrElse(7) { "تهران" },
                    address = cols.getOrElse(8) { "" },
                    activityType = cols.getOrElse(9) { "تولیدکننده" },
                    categories = cols.getOrElse(10) { "صنعتی" },
                    tags = cols.getOrElse(11) { "" },
                    rating = cols.getOrElse(12) { "4.5" }.toFloatOrNull() ?: 4.5f,
                    description = "وارد شده از فایل اکسل / CSV"
                )
                result.add(vendor)
            }
        }
        return result
    }

    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"")
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val current = StringBuilder()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    current.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.clear()
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }
}
