package com.example.util

/**
 * Persian language utilities for normalized smart search and digit conversion
 */
object PersianUtils {

    /**
     * Converts English numbers ('0'..'9') to Persian numbers ('۰'..'۹')
     */
    fun toPersianDigits(text: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = java.lang.StringBuilder()
        for (char in text) {
            if (char in '0'..'9') {
                sb.append(persianDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Number): String {
        return toPersianDigits(number.toString())
    }

    /**
     * Converts Persian numbers ('۰'..'۹') or Arabic numbers to English numbers ('0'..'9')
     */
    fun toEnglishDigits(text: String): String {
        val sb = java.lang.StringBuilder()
        for (char in text) {
            when (char) {
                '۰', '٠' -> sb.append('0')
                '۱', '١' -> sb.append('1')
                '۲', '٢' -> sb.append('2')
                '۳', '٣' -> sb.append('3')
                '۴', '٤' -> sb.append('4')
                '۵', '٥' -> sb.append('5')
                '۶', '٦' -> sb.append('6')
                '۷', '٧' -> sb.append('7')
                '۸', '٨' -> sb.append('8')
                '۹', '٩' -> sb.append('9')
                else -> sb.append(char)
            }
        }
        return sb.toString()
    }

    /**
     * Persian search normalization:
     * - Unifies Arabic 'ي' and Persian 'ی'
     * - Unifies Arabic 'ك' and Persian 'ک'
     * - Unifies Arabic 'ة' and 'ه'
     * - Unifies 'آ' and 'ا'
     * - Normalizes or removes ZWNJ (\u200c)
     * - Removes Arabic/Persian diacritics (َ, ِ, ُ, ً, ٍ, ٌ, ّ, ْ)
     * - Converts digits to uniform english digits for matching
     */
    fun normalizeForSearch(input: String?): String {
        if (input.isNullOrBlank()) return ""
        var s = input.trim().lowercase()

        // Replace Arabic letters with Persian
        s = s.replace('ي', 'ی')
        s = s.replace('ك', 'ک')
        s = s.replace('ة', 'ه')
        s = s.replace('ؤ', 'و')
        s = s.replace('إ', 'ا')
        s = s.replace('أ', 'ا')
        s = s.replace('آ', 'ا')

        // Remove ZWNJ (Zero-width non-joiner) and extra spacing
        s = s.replace("\u200c", "")
        s = s.replace("\u200f", "")
        s = s.replace("\u00a0", " ")

        // Remove diacritics
        s = s.replace(Regex("[\u064B-\u065F\u0670]"), "")

        // Normalize digits
        s = toEnglishDigits(s)

        // Normalize multiple spaces into single space
        s = s.replace(Regex("\\s+"), " ")

        return s
    }

    /**
     * Checks if a target string contains a query using Persian normalization
     */
    fun matchesSearch(target: String?, query: String?): Boolean {
        if (query.isNullOrBlank()) return true
        if (target.isNullOrBlank()) return false
        val normalizedTarget = normalizeForSearch(target)
        val normalizedQuery = normalizeForSearch(query)
        return normalizedTarget.contains(normalizedQuery) ||
                normalizedTarget.replace(" ", "").contains(normalizedQuery.replace(" ", ""))
    }
}
