package com.example

import com.example.data.model.Vendor
import com.example.util.CsvUtils
import com.example.util.PersianUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPersianDigitsConversion() {
        val converted = PersianUtils.toPersianDigits("12345")
        assertEquals("۱۲۳۴۵", converted)

        val backToEnglish = PersianUtils.toEnglishDigits("۱۲۳۴۵")
        assertEquals("12345", backToEnglish)
    }

    @Test
    fun testPersianSearchNormalization() {
        // Arabic 'ي' should match Persian 'ی'
        assertTrue(PersianUtils.matchesSearch("پارس پمپ ایرانیان", "ايرانيان"))
        // Arabic 'ك' should match Persian 'ک'
        assertTrue(PersianUtils.matchesSearch("شرکت تجهیزات امید", "تجهيزات"))
        // ZWNJ (نیم‌فاصله) matching
        assertTrue(PersianUtils.matchesSearch("آب‌پارس", "آب پارس"))
    }

    @Test
    fun testCsvExportAndParse() {
        val vendors = listOf(
            Vendor(
                id = 1,
                name = "پارس پمپ",
                ceoName = "مهندس پارسا",
                phone = "021-77654321",
                city = "تهران"
            )
        )
        val csv = CsvUtils.exportVendorsToCsv(vendors)
        val parsed = CsvUtils.parseCsvToVendors(csv)
        assertEquals(1, parsed.size)
        assertEquals("پارس پمپ", parsed[0].name)
    }
}
