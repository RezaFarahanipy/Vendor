package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.sample.SampleData
import androidx.room.withTransaction
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.example.util.CsvUtils
import com.example.util.PersianUtils
import kotlinx.coroutines.withContext

class VendorRepository(private val database: AppDatabase) {

    private val vendorDao = database.vendorDao()
    private val categoryDao = database.categoryDao()
    private val productDao = database.productDao()
    private val contactPersonDao = database.contactPersonDao()
    private val activityTypeDao = database.activityTypeDao()

    val allVendors: Flow<List<Vendor>> = vendorDao.getAllVendors()
    val favoriteVendors: Flow<List<Vendor>> = vendorDao.getFavoriteVendors()
    val recentVendors: Flow<List<Vendor>> = vendorDao.getRecentContactedVendors()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allContacts: Flow<List<ContactPerson>> = contactPersonDao.getAllContacts()
    val favoriteProducts: Flow<List<Product>> = productDao.getFavoriteProducts()
    val allActivityTypes: Flow<List<ActivityType>> = activityTypeDao.getAllActivityTypes()

    val totalVendorsCount: Flow<Int> = vendorDao.getVendorCount()
    val totalFavoritesCount: Flow<Int> = vendorDao.getFavoriteCount()
    val totalCategoriesCount: Flow<Int> = categoryDao.getCategoryCount()

    fun getVendorById(id: Long): Flow<Vendor?> = vendorDao.getVendorById(id)

    fun getProductsByVendor(vendorId: Long): Flow<List<Product>> =
        productDao.getProductsByVendor(vendorId)

    fun getContactsByVendor(vendorId: Long): Flow<List<ContactPerson>> =
        contactPersonDao.getContactsByVendor(vendorId)

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

    suspend fun ensureSampleDataLoaded() = withContext(Dispatchers.IO) {
        ensureBaselineTaxonomy()
    }

    suspend fun saveVendor(vendor: Vendor): Long = withContext(Dispatchers.IO) {
        if (vendor.id == 0L) {
            vendorDao.insertVendor(vendor)
        } else {
            vendorDao.updateVendor(vendor)
            vendor.id
        }
    }

    suspend fun saveVendorWithRelations(
        vendor: Vendor,
        contacts: List<ContactPerson>,
        products: List<Product>
    ): Long = withContext(Dispatchers.IO) {
        database.withTransaction {
            val savedVendorId = if (vendor.id == 0L) {
                vendorDao.insertVendor(vendor)
            } else {
                vendorDao.updateVendor(vendor)
                vendor.id
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

    suspend fun updateCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: Long) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategoryById(categoryId)
    }

    // Activity Types CRUD
    suspend fun addActivityType(name: String): Long = withContext(Dispatchers.IO) {
        activityTypeDao.insertActivityType(ActivityType(name = name.trim()))
    }

    suspend fun updateActivityType(activityType: ActivityType) = withContext(Dispatchers.IO) {
        activityTypeDao.updateActivityType(activityType)
    }

    suspend fun deleteActivityType(activityTypeId: Long) = withContext(Dispatchers.IO) {
        activityTypeDao.deleteActivityTypeById(activityTypeId)
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

    suspend fun restoreFullBackupJson(jsonString: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val backup = CsvUtils.parseFullBackupJson(jsonString) ?: return@withContext Pair(0, 0)
        database.withTransaction {
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
