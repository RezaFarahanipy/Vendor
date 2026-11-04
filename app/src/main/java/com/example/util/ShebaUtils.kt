package com.example.util

object ShebaUtils {

    // Iranian bank codes extracted from IBAN (characters 4..6 in IRxxBBBDDDDDDDDDDDDDDDDD)
    private val IRAN_BANKS = mapOf(
        "010" to "بانک مرکزی جمهوری اسلامی ایران",
        "011" to "بانک صنعت و معدن",
        "012" to "بانک ملت",
        "013" to "بانک رفاه کارگران",
        "014" to "بانک مسکن",
        "015" to "بانک سپه",
        "016" to "بانک کشاورزی",
        "017" to "بانک ملی ایران",
        "018" to "بانک تجارت",
        "019" to "بانک صادرات ایران",
        "020" to "بانک توسعه صادرات ایران",
        "021" to "پست بانک ایران",
        "022" to "بانک توسعه تعاون",
        "051" to "موسسه اعتباری توسعه",
        "052" to "بانک قوامین",
        "053" to "بانک کارآفرین",
        "054" to "بانک پارسیان",
        "055" to "بانک اقتصاد نوین",
        "056" to "بانک سامان",
        "057" to "بانک پاسارگاد",
        "058" to "بانک سرمایه",
        "059" to "بانک سینا",
        "060" to "بانک قرض‌الحسنه مهر ایران",
        "061" to "بانک شهر",
        "062" to "بانک آینده",
        "063" to "بانک انصار",
        "064" to "بانک گردشگری",
        "065" to "بانک حکمت ایرانیان",
        "066" to "بانک دی",
        "069" to "بانک ایران زمین",
        "070" to "بانک قرض‌الحسنه رسالت",
        "073" to "موسسه اعتباری کوثر",
        "075" to "بانک قرض‌الحسنه مهر ایران",
        "078" to "بانک خاورمیانه",
        "079" to "بانک قوامین",
        "080" to "موسسه اعتباری نور"
    )

    private val IRAN_BANKS_EN = mapOf(
        "010" to "Central Bank of Iran",
        "011" to "Sanat o Madan Bank",
        "012" to "Mellat Bank",
        "013" to "Refah Bank",
        "014" to "Maskan Bank",
        "015" to "Sepah Bank",
        "016" to "Keshavarzi Bank",
        "017" to "Melli Bank",
        "018" to "Tejarat Bank",
        "019" to "Saderat Bank",
        "020" to "Export Development Bank",
        "021" to "Post Bank",
        "022" to "Tose'e Ta'avon Bank",
        "051" to "Tose'e Credit Institute",
        "052" to "Ghavamin Bank",
        "053" to "KarAfarin Bank",
        "054" to "Parsian Bank",
        "055" to "EN Bank (Eqtesad Novin)",
        "056" to "Saman Bank",
        "057" to "Pasargad Bank",
        "058" to "Sarmayeh Bank",
        "059" to "Sina Bank",
        "060" to "Mehr Iran Bank",
        "061" to "Shahr Bank",
        "062" to "Ayandeh Bank",
        "063" to "Ansar Bank",
        "064" to "Gardeshgari Bank",
        "065" to "Hekmat Iranian Bank",
        "066" to "Dey Bank",
        "069" to "Iran Zamin Bank",
        "070" to "Resalat Bank",
        "073" to "Kowsar Credit Institute",
        "075" to "Mehr Iran Bank",
        "078" to "Middle East Bank",
        "079" to "Ghavamin Bank",
        "080" to "Noor Credit Institute"
    )

    /**
     * Cleans and normalizes the Sheba string.
     * Ensures uppercase, removes spaces/dashes, and ensures "IR" prefix.
     */
    fun normalizeSheba(raw: String): String {
        val clean = raw.trim().replace(" ", "").replace("-", "").uppercase()
        return if (clean.startsWith("IR")) clean else "IR$clean"
    }

    /**
     * Detects bank name from Iranian IBAN/Sheba.
     */
    fun detectBankName(rawSheba: String, isEnglish: Boolean = false): String? {
        val normalized = normalizeSheba(rawSheba)
        if (normalized.length >= 7) {
            val code = normalized.substring(4, 7)
            return if (isEnglish) IRAN_BANKS_EN[code] else IRAN_BANKS[code]
        }
        return null
    }

data class ShebaResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val detectedBankName: String? = null
)

    /**
     * Validates an Iranian Sheba string.
     * Returns a ShebaResult with isValid, errorMessage, and detectedBankName.
     */
    fun validateSheba(rawSheba: String, isEnglish: Boolean = false): ShebaResult {
        val trimmed = rawSheba.trim()
        if (trimmed.isBlank()) {
            return ShebaResult(isValid = true, errorMessage = null, detectedBankName = null)
        }

        val normalized = normalizeSheba(trimmed)

        if (!normalized.startsWith("IR")) {
            return ShebaResult(
                isValid = false,
                errorMessage = if (isEnglish) "IBAN must start with IR" else "شماره شبا باید با IR آغاز شود"
            )
        }

        val digits = normalized.substring(2)
        if (!digits.all { it.isDigit() }) {
            return ShebaResult(
                isValid = false,
                errorMessage = if (isEnglish) "IBAN must contain only numbers after IR" else "شماره شبا پس از IR فقط باید شامل ارقام باشد"
            )
        }

        if (normalized.length != 26) {
            return ShebaResult(
                isValid = false,
                errorMessage = if (isEnglish) "IBAN must be exactly 24 digits (found ${digits.length})"
                else "شماره شبا باید ۲۴ رقم باشد (تعداد واردشده: ${digits.length})"
            )
        }

        val bankName = detectBankName(normalized, isEnglish)
        if (bankName == null) {
            return ShebaResult(
                isValid = false,
                errorMessage = if (isEnglish) "Unknown Iranian bank code in IBAN"
                else "کد بانک در شماره شبا معتبر نمی‌باشد"
            )
        }

        return ShebaResult(isValid = true, errorMessage = null, detectedBankName = bankName)
    }

    /**
     * Formats Sheba in groups of 4 for clean readable display (e.g., IR12 0120 0000 ...)
     */
    fun formatDisplay(rawSheba: String): String {
        val normalized = normalizeSheba(rawSheba)
        return normalized.chunked(4).joinToString(" ")
    }
}
