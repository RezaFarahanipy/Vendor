package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.sample.SampleData
import com.example.util.CsvUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    suspend fun ensureSampleDataLoaded() = withContext(Dispatchers.IO) {
        val currentVendors = allVendors.first()
        if (currentVendors.isEmpty()) {
            vendorDao.insertVendors(SampleData.initialVendors)
            categoryDao.insertCategories(SampleData.initialCategories)
            productDao.insertProducts(SampleData.initialProducts)
            contactPersonDao.insertContacts(SampleData.initialContacts)
            activityTypeDao.insertActivityTypes(SampleData.initialActivityTypes)
        } else {
            val currentTypes = allActivityTypes.first()
            if (currentTypes.isEmpty()) {
                activityTypeDao.insertActivityTypes(SampleData.initialActivityTypes)
            }
        }
    }

    suspend fun saveVendor(vendor: Vendor): Long = withContext(Dispatchers.IO) {
        if (vendor.id == 0L) {
            vendorDao.insertVendor(vendor)
        } else {
            vendorDao.updateVendor(vendor)
            vendor.id
        }
    }

    suspend fun deleteVendor(vendorId: Long) = withContext(Dispatchers.IO) {
        vendorDao.deleteVendorById(vendorId)
        // Also delete associated contacts and products
        val contacts = contactPersonDao.getContactsByVendor(vendorId).first()
        contacts.forEach { contactPersonDao.deleteContactById(it.id) }
        val products = productDao.getProductsByVendor(vendorId).first()
        products.forEach { productDao.deleteProductById(it.id) }
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

    suspend fun checkDuplicate(name: String): Boolean = withContext(Dispatchers.IO) {
        vendorDao.findByName(name.trim()) != null
    }

    suspend fun exportCsv(): String = withContext(Dispatchers.IO) {
        val vendors = allVendors.first()
        CsvUtils.exportVendorsToCsv(vendors)
    }

    suspend fun importCsv(csvContent: String): Int = withContext(Dispatchers.IO) {
        val parsed = CsvUtils.parseCsvToVendors(csvContent)
        if (parsed.isNotEmpty()) {
            vendorDao.insertVendors(parsed)
        }
        parsed.size
    }

    suspend fun restoreSampleData() = withContext(Dispatchers.IO) {
        vendorDao.insertVendors(SampleData.initialVendors)
        categoryDao.insertCategories(SampleData.initialCategories)
        productDao.insertProducts(SampleData.initialProducts)
        contactPersonDao.insertContacts(SampleData.initialContacts)
        activityTypeDao.insertActivityTypes(SampleData.initialActivityTypes)
    }
}
