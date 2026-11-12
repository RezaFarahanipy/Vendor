package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.sample.SampleData
import androidx.room.withTransaction
import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.example.util.CsvUtils
import com.example.util.PersianUtils
import com.example.data.model.BankAccount
import com.example.data.model.Tag
import com.example.data.model.VendorCategoryCrossRef
import com.example.data.model.VendorPhone
import com.example.data.model.VendorTagCrossRef
import kotlinx.coroutines.withContext

class VendorRepository(private val database: AppDatabase) {

    private val vendorDao = database.vendorDao()
    private val categoryDao = database.categoryDao()
    private val productDao = database.productDao()
    private val contactPersonDao = database.contactPersonDao()
    private val activityTypeDao = database.activityTypeDao()
    private val relationDao = database.relationDao()

    val allVendors: Flow<List<Vendor>> = vendorDao.getAllVendors()
    val favoriteVendors: Flow<List<Vendor>> = vendorDao.getFavoriteVendors()
    val recentVendors: Flow<List<Vendor>> = vendorDao.getRecentContactedVendors()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allContacts: Flow<List<ContactPerson>> = contactPersonDao.getAllContacts()
    val favoriteProducts: Flow<List<Product>> = productDao.getFavoriteProducts()
    val allActivityTypes: Flow<List<ActivityType>> = activityTypeDao.getAllActivityTypes()
    val allTags: Flow<List<Tag>> = relationDao.getAllTags()

    val totalVendorsCount: Flow<Int> = vendorDao.getVendorCount()
    val totalFavoritesCount: Flow<Int> = vendorDao.getFavoriteCount()
    val totalCategoriesCount: Flow<Int> = categoryDao.getCategoryCount()

    fun getVendorById(id: Long): Flow<Vendor?> = vendorDao.getVendorById(id)

    fun getProductsByVendor(vendorId: Long): Flow<List<Product>> =
        productDao.getProductsByVendor(vendorId)

    fun getContactsByVendor(vendorId: Long): Flow<List<ContactPerson>> =
        contactPersonDao.getContactsByVendor(vendorId)

    fun getPhonesByVendor(vendorId: Long): Flow<List<VendorPhone>> =
        relationDao.getPhonesForVendor(vendorId)

    fun getBankAccountsByVendor(vendorId: Long): Flow<List<BankAccount>> =
        relationDao.getBankAccountsForVendor(vendorId)

    fun getTagsByVendor(vendorId: Long): Flow<List<Tag>> =
        relationDao.getTagsForVendor(vendorId)

    fun getCategoriesByVendor(vendorId: Long): Flow<List<Category>> =
        relationDao.getCategoriesForVendor(vendorId)

    fun searchVendors(query: String): Flow<List<Vendor>> =
        vendorDao.searchVendors(query)

    suspend fun ensureBaselineTaxonomy() = withContext(Dispatchers.IO) {
        val currentTypes = allActivityTypes.first()
        if (currentTypes.isEmpty()) {
            activityTypeDao.insertActivityTypes(SampleData.initialActivityTypes)
        }
        val currentCategories = allCategories.first()
        if (currentCategories.isEmpty()) {
            categoryDao.insertCategories(SampleData.initialCategories)
        }
    }

    suspend fun saveVendorWithRelations(
        vendor: Vendor,
        contacts: List<ContactPerson>,
        products: List<Product>
    ): Long = withContext(Dispatchers.IO) {
        database.withTransaction {
            val searchIdx = PersianUtils.generateSearchIndex(
                name = vendor.name,
                ceoName = vendor.ceoName,
                city = vendor.city,
                categories = vendor.categories,
                tags = vendor.tags,
                phone = vendor.phone,
                mobile = vendor.mobile,
                secondaryPhones = vendor.secondaryPhones,
                additionalMobiles = vendor.additionalMobiles,
                economicCode = vendor.economicCode,
                nationalId = vendor.nationalId
            )
            val vendorWithIndex = vendor.copy(searchIndex = searchIdx)

            val savedVendorId = if (vendorWithIndex.id == 0L) {
                vendorDao.insertVendor(vendorWithIndex)
            } else {
                vendorDao.updateVendor(vendorWithIndex)
                vendorWithIndex.id
            }

            // Sync contacts: remove deleted ones, insert/update remaining
            val existingContacts = contactPersonDao.getContactsByVendor(savedVendorId).first()
            val currentContactIds = contacts.mapNotNull { if (it.id > 0L) it.id else null }.toSet()
            for (existing in existingContacts) {
                if (existing.id !in currentContactIds) {
                    contactPersonDao.deleteContactById(existing.id)
                }
            }
            for (c in contacts) {
                val toSave = c.copy(vendorId = savedVendorId)
                if (toSave.id == 0L) {
                    contactPersonDao.insertContact(toSave)
                } else {
                    contactPersonDao.updateContact(toSave)
                }
            }

            // Sync products: remove deleted ones, insert/update remaining
            val existingProducts = productDao.getProductsByVendor(savedVendorId).first()
            val currentProductIds = products.mapNotNull { if (it.id > 0L) it.id else null }.toSet()
            for (existing in existingProducts) {
                if (existing.id !in currentProductIds) {
                    productDao.deleteProductById(existing.id)
                }
            }
            for (p in products) {
                val toSave = p.copy(vendorId = savedVendorId)
                if (toSave.id == 0L) {
                    productDao.insertProduct(toSave)
                } else {
                    productDao.updateProduct(toSave)
                }
            }

            // Sync normalized vendor_phones
            relationDao.deleteVendorPhonesByVendorId(savedVendorId)
            val phonesToInsert = mutableListOf<VendorPhone>()
            if (vendor.phone.isNotBlank()) {
                phonesToInsert.add(VendorPhone(vendorId = savedVendorId, type = "LANDLINE", number = vendor.phone.trim(), label = "تلفن ثابت"))
            }
            if (vendor.mobile.isNotBlank()) {
                phonesToInsert.add(VendorPhone(vendorId = savedVendorId, type = "MOBILE", number = vendor.mobile.trim(), label = "موبایل اصلی"))
            }
            if (vendor.secondaryPhones.isNotBlank()) {
                vendor.secondaryPhones.split(",", "،", "\n").map { it.trim() }.filter { it.isNotBlank() }.forEach { sec ->
                    phonesToInsert.add(VendorPhone(vendorId = savedVendorId, type = "LANDLINE", number = sec, label = "تلفن فرعی"))
                }
            }
            if (vendor.additionalMobiles.isNotBlank()) {
                vendor.additionalMobiles.split(",", "،", "\n").map { it.trim() }.filter { it.isNotBlank() }.forEach { mob ->
                    phonesToInsert.add(VendorPhone(vendorId = savedVendorId, type = "MOBILE", number = mob, label = "موبایل فرعی"))
                }
            }
            if (phonesToInsert.isNotEmpty()) {
                relationDao.insertVendorPhones(phonesToInsert)
            }

            // Sync normalized bank_accounts
            relationDao.deleteBankAccountsByVendorId(savedVendorId)
            val accountsList = vendor.getBankAccountsList()
            if (accountsList.isNotEmpty()) {
                val bankEntities = accountsList.map {
                    it.copy(id = 0L, vendorId = savedVendorId)
                }
                relationDao.insertBankAccounts(bankEntities)
            }

            // Sync tags
            relationDao.deleteVendorTagsByVendorId(savedVendorId)
            if (vendor.tags.isNotBlank()) {
                val tagList = vendor.tags.split(",", "،", "\n").map { it.trim() }.filter { it.isNotBlank() }.distinct()
                val tagCrossRefs = mutableListOf<VendorTagCrossRef>()
                for (tagName in tagList) {
                    val existingTag = relationDao.findTagByName(tagName)
                    val tagId = existingTag?.id ?: relationDao.insertTag(Tag(name = tagName))
                    tagCrossRefs.add(VendorTagCrossRef(vendorId = savedVendorId, tagId = tagId))
                }
                if (tagCrossRefs.isNotEmpty()) {
                    relationDao.insertVendorTags(tagCrossRefs)
                }
            }

            // Sync categories
            relationDao.deleteVendorCategoriesByVendorId(savedVendorId)
            if (vendor.categories.isNotBlank()) {
                val catNames = vendor.categories.split(",", "،", "\n").map { it.trim() }.filter { it.isNotBlank() }.distinct()
                val allCats = categoryDao.getAllCategories().first()
                val catCrossRefs = catNames.mapNotNull { cName ->
                    allCats.firstOrNull { it.name.trim() == cName }?.let { matchedCat ->
                        VendorCategoryCrossRef(vendorId = savedVendorId, categoryId = matchedCat.id)
                    }
                }
                if (catCrossRefs.isNotEmpty()) {
                    relationDao.insertVendorCategories(catCrossRefs)
                }
            }

            savedVendorId
        }
    }

    suspend fun deleteVendor(vendorId: Long) = withContext(Dispatchers.IO) {
        val vendor = vendorDao.getVendorById(vendorId).first()
        database.withTransaction {
            val contacts = contactPersonDao.getContactsByVendor(vendorId).first()
            contacts.forEach { contactPersonDao.deleteContactById(it.id) }
            val products = productDao.getProductsByVendor(vendorId).first()
            products.forEach { productDao.deleteProductById(it.id) }
            vendorDao.deleteVendorById(vendorId)
        }

        // Clean up associated local files to avoid orphan files
        if (vendor != null) {
            if (vendor.logoUri.isNotBlank()) {
                try {
                    val file = File(vendor.logoUri)
                    if (file.exists() && file.isFile) file.delete()
                } catch (_: Exception) {}
            }
            if (vendor.catalogLocalPath.isNotBlank()) {
                try {
                    val file = File(vendor.catalogLocalPath)
                    if (file.exists() && file.isFile) file.delete()
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun toggleVendorFavorite(vendorId: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        vendorDao.setFavorite(vendorId, !currentFavorite)
    }

    suspend fun toggleProductFavorite(productId: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        productDao.setFavorite(productId, !currentFavorite)
    }

    suspend fun updateNotes(vendorId: Long, notes: String) = withContext(Dispatchers.IO) {
        vendorDao.updateNotes(vendorId, notes)
    }

    suspend fun recordContact(vendorId: Long) = withContext(Dispatchers.IO) {
        vendorDao.recordContact(vendorId, System.currentTimeMillis())
    }

    // Products CRUD
    suspend fun saveProduct(product: Product): Long = withContext(Dispatchers.IO) {
        if (product.id == 0L) {
            productDao.insertProduct(product)
        } else {
            productDao.updateProduct(product)
            product.id
        }
    }

    suspend fun deleteProduct(productId: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProductById(productId)
    }

    // Contacts CRUD
    suspend fun saveContact(contact: ContactPerson): Long = withContext(Dispatchers.IO) {
        if (contact.id == 0L) {
            contactPersonDao.insertContact(contact)
        } else {
            contactPersonDao.updateContact(contact)
            contact.id
        }
    }

    suspend fun deleteContact(contactId: Long) = withContext(Dispatchers.IO) {
        contactPersonDao.deleteContactById(contactId)
    }

    // Categories CRUD
    suspend fun addCategory(category: Category): Long = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    private fun splitList(raw: String): List<String> =
        raw.split(',', '،', '\n').map { it.trim() }.filter { it.isNotEmpty() }

    private fun rebuildSearchIndex(v: Vendor): String = PersianUtils.generateSearchIndex(
        name = v.name,
        ceoName = v.ceoName,
        city = v.city,
        categories = v.categories,
        tags = v.tags,
        phone = v.phone,
        mobile = v.mobile,
        secondaryPhones = v.secondaryPhones,
        additionalMobiles = v.additionalMobiles,
        economicCode = v.economicCode,
        nationalId = v.nationalId
    )

    /** Renames a category and keeps every vendor that uses it in sync (names are stored on the vendor row). */
    suspend fun updateCategory(category: Category) = withContext(Dispatchers.IO) {
        database.withTransaction {
            val oldName = categoryDao.getAllCategories().first().firstOrNull { it.id == category.id }?.name?.trim().orEmpty()
            val newName = category.name.trim()
            categoryDao.updateCategory(category)
            if (oldName.isNotEmpty() && newName.isNotEmpty() && oldName != newName) {
                vendorDao.getAllVendors().first().forEach { v ->
                    val tokens = splitList(v.categories)
                    if (oldName in tokens) {
                        val renamed = tokens.map { if (it == oldName) newName else it }.distinct().joinToString(", ")
                        val updated = v.copy(categories = renamed)
                        vendorDao.updateVendor(updated.copy(searchIndex = rebuildSearchIndex(updated)))
                    }
                }
            }
        }
    }

    suspend fun canDeleteCategory(categoryId: Long): Boolean = withContext(Dispatchers.IO) {
        val countInCrossRef = relationDao.getVendorCountForCategoryId(categoryId)
        if (countInCrossRef > 0) return@withContext false
        val cat = allCategories.first().firstOrNull { it.id == categoryId } ?: return@withContext true
        val countInVendors = allVendors.first().count { cat.name.trim() in splitList(it.categories) }
        countInVendors == 0
    }

    suspend fun deleteCategory(categoryId: Long): Boolean = withContext(Dispatchers.IO) {
        if (!canDeleteCategory(categoryId)) {
            return@withContext false
        }
        categoryDao.deleteCategoryById(categoryId)
        true
    }

    // Activity Types CRUD
    suspend fun addActivityType(name: String): Long = withContext(Dispatchers.IO) {
        activityTypeDao.insertActivityType(ActivityType(name = name.trim()))
    }

    suspend fun updateActivityType(activityType: ActivityType) = withContext(Dispatchers.IO) {
        activityTypeDao.updateActivityType(activityType)
    }

    suspend fun canDeleteActivityType(activityTypeId: Long): Boolean = withContext(Dispatchers.IO) {
        val type = allActivityTypes.first().firstOrNull { it.id == activityTypeId } ?: return@withContext true
        val count = allVendors.first().count { it.activityTypeId == activityTypeId || it.activityType == type.name }
        count == 0
    }

    suspend fun deleteActivityType(activityTypeId: Long): Boolean = withContext(Dispatchers.IO) {
        if (!canDeleteActivityType(activityTypeId)) {
            return@withContext false
        }
        activityTypeDao.deleteActivityTypeById(activityTypeId)
        true
    }

    suspend fun checkDuplicateVendor(
        name: String,
        nationalId: String = "",
        mobile: String = "",
        currentVendorId: Long = 0L
    ): Vendor? = withContext(Dispatchers.IO) {
        val normName = PersianUtils.normalize(name).trim()
        val normNational = nationalId.trim().filter { it.isDigit() }
        val normMobile = PersianUtils.normalizePhone(mobile)
        val all = allVendors.first()
        all.firstOrNull { v ->
            if (v.id == currentVendorId) return@firstOrNull false
            val matchName = normName.isNotBlank() && PersianUtils.normalize(v.name).trim() == normName
            val matchNational = normNational.isNotBlank() && v.nationalId.filter { it.isDigit() } == normNational
            val matchMobile = normMobile.isNotBlank() && PersianUtils.normalizePhone(v.mobile) == normMobile
            matchName || matchNational || matchMobile
        }
    }

    suspend fun exportCsv(): String = withContext(Dispatchers.IO) {
        val vendors = allVendors.first()
        CsvUtils.exportVendorsToCsv(vendors)
    }

    suspend fun importCsv(csvContent: String): com.example.util.ImportSummary = withContext(Dispatchers.IO) {
        val parsed = CsvUtils.parseCsvToVendors(csvContent)
        val currentVendors = allVendors.first()
        val summary = CsvUtils.deduplicateVendors(parsed, currentVendors)
        if (summary.vendorsToInsert.isNotEmpty()) {
            database.withTransaction {
                vendorDao.insertVendors(summary.vendorsToInsert)
            }
        }
        summary
    }

    suspend fun exportFullBackupJson(): String = withContext(Dispatchers.IO) {
        val vendors = allVendors.first()
        val contacts = allContacts.first()
        val products = allProducts.first()
        CsvUtils.exportFullBackupJson(vendors, contacts, products)
    }

    suspend fun exportFullBackupZip(context: Context, outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val vendors = allVendors.first()
        val contacts = allContacts.first()
        val products = allProducts.first()
        CsvUtils.exportFullBackupZip(context, outputStream, vendors, contacts, products)
    }

    suspend fun restoreFullBackupStream(context: Context, inputStream: InputStream): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val backup = CsvUtils.importFullBackupZipOrJson(context, inputStream) ?: return@withContext Pair(0, 0)
        restoreBackupData(backup)
    }

    suspend fun restoreFullBackupJson(jsonString: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val backup = CsvUtils.parseFullBackupJson(jsonString) ?: return@withContext Pair(0, 0)
        restoreBackupData(backup)
    }

    private suspend fun restoreBackupData(backup: com.example.util.FullBackupData): Pair<Int, Int> {
        return database.withTransaction {
            val existing = allVendors.first()
            val existingNormalizedNames = existing.map { PersianUtils.normalize(it.name).trim() }.filter { it.isNotBlank() }.toSet()
            val existingNationalIds = existing.map { it.nationalId.filter { c -> c.isDigit() } }.filter { it.isNotBlank() }.toSet()

            var insertedVendors = 0
            var skipped = 0

            for (v in backup.vendors) {
                val normName = PersianUtils.normalize(v.name).trim()
                val normNational = v.nationalId.filter { it.isDigit() }
                if (normName in existingNormalizedNames || (normNational.isNotBlank() && normNational in existingNationalIds)) {
                    skipped++
                    continue
                }
                val oldId = v.id
                val newVendorId = vendorDao.insertVendor(v.copy(id = 0L))
                insertedVendors++

                backup.contacts.filter { it.vendorId == oldId }.forEach { c ->
                    contactPersonDao.insertContact(c.copy(id = 0L, vendorId = newVendorId))
                }
                backup.products.filter { it.vendorId == oldId }.forEach { p ->
                    productDao.insertProduct(p.copy(id = 0L, vendorId = newVendorId))
                }
            }
            Pair(insertedVendors, skipped)
        }
    }

    suspend fun restoreSampleData() = withContext(Dispatchers.IO) {
        database.withTransaction {
            ensureBaselineTaxonomy()
            SampleData.initialVendors.forEach { v ->
                val newVendorId = vendorDao.insertVendor(v.copy(id = 0L))
                SampleData.initialContacts.filter { it.vendorId == v.id }.forEach { c ->
                    contactPersonDao.insertContact(c.copy(id = 0L, vendorId = newVendorId))
                }
                SampleData.initialProducts.filter { it.vendorId == v.id }.forEach { p ->
                    productDao.insertProduct(p.copy(id = 0L, vendorId = newVendorId))
                }
            }
        }
    }
}
