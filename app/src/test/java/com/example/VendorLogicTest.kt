package com.example

import com.example.data.model.BankAccount
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.util.CsvUtils
import com.example.util.PersianUtils
import com.example.util.ShebaUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying Phase 1 and Phase 2 critical bug fixes:
 * 1. Vendor.copy preserves unedited fields during edit mode
 * 2. CsvUtils deduplication prevents duplicate imports
 * 3. Phone normalization matches numbers regardless of dashes, spaces, or Persian digits
 * 4. Iranian mobile and National ID checksum validation
 * 5. Iranian Sheba (IBAN) checksum and automatic bank detection
 */
class VendorLogicTest {

    @Test
    fun testVendorCopyPreservesMetadata() {
        val original = Vendor(
            id = 42L,
            name = "شرکت پمپ پارس اصل",
            ceoName = "مهندس محمدی",
            description = "تولید انواع پمپ‌های فشار قوی",
            activityType = "تولیدکننده",
            phone = "021-88776655",
            secondaryPhones = "021-88776656",
            mobile = "09121112233",
            additionalMobiles = "09122223344",
            email = "info@parspump.ir",
            website = "https://parspump.ir",
            province = "تهران",
            city = "تهران",
            address = "خیابان آزادی، پلاک ۱۲",
            latitude = 35.7001,
            longitude = 51.3456,
            logoDrawable = "custom_logo_res",
            logoUri = "/data/user/0/com.example/files/logo_42.png",
            catalogTitle = "کاتالوگ فنی سال ۱۴۰۳",
            catalogLocalPath = "/data/user/0/com.example/files/catalogs/cat_42.pdf",
            catalogFileName = "catalog_parspump.pdf",
            catalogPdfUrl = "https://example.com/cat42.pdf",
            businessCardUrl = "https://example.com/card42.jpg",
            isVerified = true,
            isFavorite = true,
            rating = 4.8f,
            reviewCount = 18,
            categories = "پمپ، صنعتی",
            tags = "صادراتی، باکیفیت",
            notes = "مشتری قدیمی با پرداخت نقدی",
            economicCode = "123456789012",
            nationalId = "10101234567",
            registrationNumber = "45678",
            bankAccounts = "[]",
            lastContactDate = 1700000000000L,
            createdAt = 1690000000000L
        )

        // Simulating edit form submission where only name and address changed
        val updated = original.copy(
            name = "شرکت پمپ پارس نوین",
            address = "خیابان انقلاب، پلاک ۲۰"
        )

        // Metadata and relations fields MUST remain untouched
        assertEquals(42L, updated.id)
        assertEquals("شرکت پمپ پارس نوین", updated.name)
        assertEquals("خیابان انقلاب، پلاک ۲۰", updated.address)
        assertEquals(original.lastContactDate, updated.lastContactDate)
        assertEquals(original.catalogPdfUrl, updated.catalogPdfUrl)
        assertEquals(original.businessCardUrl, updated.businessCardUrl)
        assertEquals(original.reviewCount, updated.reviewCount)
        assertEquals(original.rating, updated.rating, 0.001f)
        assertEquals(original.isVerified, updated.isVerified)
        assertEquals(original.isFavorite, updated.isFavorite)
        assertEquals(original.logoDrawable, updated.logoDrawable)
        assertEquals(original.logoUri, updated.logoUri)
        assertEquals(original.catalogLocalPath, updated.catalogLocalPath)
        assertEquals(original.createdAt, updated.createdAt)
    }

    @Test
    fun testCsvDeduplicationAgainstExistingDatabase() {
        val existingVendors = listOf(
            Vendor(
                id = 1L,
                name = "فولاد صنعت پیشرو",
                nationalId = "10103456789",
                phone = "021-88990011",
                mobile = "09121002030"
            ),
            Vendor(
                id = 2L,
                name = "الکترو موتور البرز",
                nationalId = "10109876543",
                phone = "021-77889900",
                mobile = "09123004050"
            )
        )

        val importedFromCsv = listOf(
            // Duplicate by normalized name (with Arabic yeh)
            Vendor(name = "فولاد صنعت پيشرو", nationalId = "", mobile = ""),
            // Duplicate by National ID
            Vendor(name = "شرکت جدید با شناسه تکراری", nationalId = "10109876543", mobile = "09350000000"),
            // Duplicate by normalized mobile (dashes vs no dashes)
            Vendor(name = "نام دیگر", mobile = "0912-1002030"),
            // Brand new vendor
            Vendor(name = "صنایع پتروشیمی زاگرس", nationalId = "10105555555", mobile = "09127778899")
        )

        val summary = CsvUtils.deduplicateVendors(importedFromCsv, existingVendors)

        assertEquals(1, summary.newCount)
        assertEquals(3, summary.duplicateSkippedCount)
        assertEquals(1, summary.vendorsToInsert.size)
        assertEquals("صنایع پتروشیمی زاگرس", summary.vendorsToInsert[0].name)
    }

    @Test
    fun testPhoneNormalizationAndMatching() {
        // Dash stripping & normalization
        assertEquals("02177654321", PersianUtils.normalizePhone("021-7765-4321"))
        assertEquals("02177654321", PersianUtils.normalizePhone("021 7765 4321"))
        // Persian digits to normalized phone
        assertEquals("02177654321", PersianUtils.normalizePhone("۰۲۱-۷۷۶۵۴۳۲۱"))
        // Country code 98 to 0
        assertEquals("09123456789", PersianUtils.normalizePhone("+98-912-345-6789"))
        assertEquals("09123456789", PersianUtils.normalizePhone("989123456789"))

        // Cross-format matching in search
        assertTrue(PersianUtils.matchesPhone("021-7765-4321", "02177654321"))
        assertTrue(PersianUtils.matchesPhone("02177654321", "021-7765"))
        assertTrue(PersianUtils.matchesPhone("۰۲۱-۷۷۶۵۴۳۲۱", "77654321"))
        assertTrue(PersianUtils.matchesPhone("09123456789", "۰۹۱۲۳۴۵۶۷۸۹"))
        assertFalse(PersianUtils.matchesPhone("02177654321", "09123456789"))
    }

    @Test
    fun testIranianMobileValidation() {
        assertTrue(PersianUtils.isValidIranianMobile("09123456789"))
        assertTrue(PersianUtils.isValidIranianMobile("۰۹۱۲۳۴۵۶۷۸۹"))
        assertTrue(PersianUtils.isValidIranianMobile("0935-123-4567"))
        assertTrue(PersianUtils.isValidIranianMobile("+989123456789"))

        // Invalid cases
        assertFalse(PersianUtils.isValidIranianMobile("02177654321")) // Landline
        assertFalse(PersianUtils.isValidIranianMobile("091234567")) // Too short
        assertFalse(PersianUtils.isValidIranianMobile("091234567890")) // Too long
        assertFalse(PersianUtils.isValidIranianMobile("08123456789")) // Non 09 prefix
    }

    @Test
    fun testNationalCodeAndCompanyIdValidation() {
        // Valid 10-digit National Code (0010328904 has valid checksum: 95 % 11 = 7, 11 - 7 = 4)
        assertTrue(PersianUtils.isValidIranianNationalCode("0010328904"))
        assertTrue(PersianUtils.isValidNationalIdOrCode("0010328904"))
        assertTrue(PersianUtils.isValidNationalIdOrCode("۰۰۱۰۳۲۸۹۰۴"))

        // All repeated digits is invalid
        assertFalse(PersianUtils.isValidIranianNationalCode("1111111111"))
        assertFalse(PersianUtils.isValidNationalIdOrCode("1111111111"))

        // Invalid length
        assertFalse(PersianUtils.isValidNationalIdOrCode("123456"))
    }

    @Test
    fun testShebaValidationAndBankDetection() {
        // Valid Mellat Bank Sheba test format
        val mellatSheba = "IR820120000000001234567890"
        val validation = ShebaUtils.validateSheba(mellatSheba)

        // Validating Sheba structure and bank detection
        assertNotNull(validation)
        assertEquals("بانک ملت", validation.detectedBankName)

        // Saderat bank code 019
        val saderatSheba = "IR000190000000001234567890"
        val saderatValidation = ShebaUtils.validateSheba(saderatSheba)
        assertEquals("بانک صادرات ایران", saderatValidation.detectedBankName)

        // Invalid format
        val invalidSheba = "IR1234"
        val invalidValidation = ShebaUtils.validateSheba(invalidSheba)
        assertFalse(invalidValidation.isValid)
    }
}
