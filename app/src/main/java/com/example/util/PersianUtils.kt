package com.example.util

/**
 * Persian language utilities for normalized smart search and digit conversion.
 * Regexes are pre-compiled as singleton vals for high performance.
 */
object PersianUtils {

    private val DIACRITICS_REGEX = Regex("[\u064B-\u065F\u0670]")
    private val SPACES_REGEX = Regex("\\s+")
    private val NON_DIGITS_REGEX = Regex("[^0-9]")
    private val IRANIAN_MOBILE_REGEX = Regex("^09\\d{9}$")
    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /**
     * Converts English numbers ('0'..'9') to Persian numbers ('۰'..'۹')
     */
    fun toPersianDigits(text: String): String {
        val sb = java.lang.StringBuilder()
        for (char in text) {
            if (char in '0'..'9') {
                sb.append(PERSIAN_DIGITS[char - '0'])
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
     * - Removes Arabic/Persian diacritics
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

        // Remove ZWNJ and extra spacing
        s = s.replace("\u200c", "")
        s = s.replace("\u200f", "")
        s = s.replace("\u00a0", " ")

        // Remove diacritics using pre-compiled regex
        s = s.replace(DIACRITICS_REGEX, "")

        // Normalize digits
        s = toEnglishDigits(s)

        // Normalize multiple spaces into single space using pre-compiled regex
        s = s.replace(SPACES_REGEX, " ")

        return s
    }

    /**
     * Normalizes phone numbers by converting digits to English and stripping dashes, spaces, and separators.
     */
    fun normalizePhone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        var english = toEnglishDigits(phone)
        english = english.replace(NON_DIGITS_REGEX, "")
        if (english.startsWith("98") && english.length > 10) {
            english = "0" + english.substring(2)
        }
        return english
    }

    /**
     * Checks if target phone number matches query even when one has dashes and the other doesn't
     */
    fun matchesPhone(targetPhone: String?, query: String?): Boolean {
        if (query.isNullOrBlank()) return true
        if (targetPhone.isNullOrBlank()) return false
        val cleanTarget = normalizePhone(targetPhone)
        val cleanQuery = normalizePhone(query)
        if (cleanQuery.isBlank()) return false
        return cleanTarget.contains(cleanQuery)
    }

    /**
     * Validates 11-digit Iranian mobile numbers (e.g. 09123456789)
     */
    fun isValidIranianMobile(mobile: String?): Boolean {
        if (mobile.isNullOrBlank()) return false
        val clean = normalizePhone(mobile)
        return clean.matches(IRANIAN_MOBILE_REGEX)
    }

    /**
     * Validates 10-digit Iranian National Code (کد ملی) or 11-digit Company National ID (شناسه ملی)
     */
    fun isValidNationalIdOrCode(input: String?): Boolean {
        if (input.isNullOrBlank()) return false
        val digits = toEnglishDigits(input).filter { it.isDigit() }
        return when (digits.length) {
            10 -> isValidIranianNationalCode(digits)
            11 -> isValidIranianCompanyNationalId(digits)
            else -> false
        }
    }

    /**
     * 10-digit Iranian National Code checksum validation
     */
    fun isValidIranianNationalCode(code: String): Boolean {
        if (code.length != 10) return false
        if (code.all { it == code[0] }) return false
        val checkDigit = code[9].digitToInt()
        var sum = 0
        for (i in 0 until 9) {
            sum += code[i].digitToInt() * (10 - i)
        }
        val remainder = sum % 11
        return if (remainder < 2) checkDigit == remainder else checkDigit == (11 - remainder)
    }

    /**
     * 11-digit Iranian Legal Entity National ID (شناسه ملی شرکت) checksum validation
     */
    fun isValidIranianCompanyNationalId(id: String): Boolean {
        if (id.length != 11) return false
        val checkDigit = id[10].digitToInt()
        val tenth = id[9].digitToInt()
        val p = tenth + 2
        val multipliers = intArrayOf(29, 27, 23, 19, 17, 29, 27, 23, 19, 17)
        var sum = 0
        for (i in 0 until 10) {
            sum += (id[i].digitToInt() + p) * multipliers[i]
        }
        val remainder = sum % 11
        val expected = if (remainder == 10) 0 else remainder
        return checkDigit == expected
    }

    /**
     * Checks if normalized text contains normalized query (with space/ZWNJ tolerance)
     */
    fun matchesSearch(text: String?, query: String?): Boolean {
        if (query.isNullOrBlank()) return true
        if (text.isNullOrBlank()) return false
        val nText = normalizeForSearch(text)
        val nQuery = normalizeForSearch(query)
        if (nText.contains(nQuery)) return true
        val noSpaceText = nText.replace(" ", "")
        val noSpaceQuery = nQuery.replace(" ", "")
        return noSpaceText.contains(noSpaceQuery)
    }

    fun normalize(input: String?): String = normalizeForSearch(input)

    /**
     * Generates a pre-normalized search index string for fast indexed searching
     */
    fun generateSearchIndex(
        name: String,
        ceoName: String = "",
        city: String = "",
        categories: String = "",
        tags: String = "",
        phone: String = "",
        mobile: String = "",
        secondaryPhones: String = "",
        additionalMobiles: String = "",
        economicCode: String = "",
        nationalId: String = ""
    ): String {
        val tokens = mutableListOf<String>()
        if (name.isNotBlank()) tokens.add(normalizeForSearch(name))
        if (ceoName.isNotBlank()) tokens.add(normalizeForSearch(ceoName))
        if (city.isNotBlank()) tokens.add(normalizeForSearch(city))
        if (categories.isNotBlank()) tokens.add(normalizeForSearch(categories))
        if (tags.isNotBlank()) tokens.add(normalizeForSearch(tags))

        val cleanPhone = normalizePhone(phone)
        if (cleanPhone.isNotBlank()) tokens.add(cleanPhone)
        val cleanMobile = normalizePhone(mobile)
        if (cleanMobile.isNotBlank()) tokens.add(cleanMobile)

        if (secondaryPhones.isNotBlank()) {
            secondaryPhones.split(",", "،", "\n").forEach {
                val p = normalizePhone(it)
                if (p.isNotBlank()) tokens.add(p)
            }
        }
        if (additionalMobiles.isNotBlank()) {
            additionalMobiles.split(",", "،", "\n").forEach {
                val m = normalizePhone(it)
                if (m.isNotBlank()) tokens.add(m)
            }
        }
        if (economicCode.isNotBlank()) tokens.add(toEnglishDigits(economicCode).filter { it.isDigit() })
        if (nationalId.isNotBlank()) tokens.add(toEnglishDigits(nationalId).filter { it.isDigit() })

        return tokens.joinToString(" ")
    }
}
