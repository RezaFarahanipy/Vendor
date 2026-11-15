package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.data.model.ContactPerson
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.repository.VendorRepository
import com.example.util.PersianUtils
import com.example.data.preferences.AppSettingsDataStore
import com.example.data.preferences.ThemeMode
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.util.tf
import com.example.util.t

enum class AppScreen {
    ONBOARDING,
    MAIN_TABS,
    VENDOR_DETAIL,
    ADD_EDIT_VENDOR,
    LOCATION_VIEW
}

enum class MainTab {
    HOME,
    SEARCH,
    FAVORITES,
    CATALOG,
    SETTINGS
}

/** A person matched by the search, together with the company he/she works for. */
data class PersonHit(val contact: ContactPerson, val vendor: Vendor)

/** A product matched by the search, together with the company that supplies it. */
data class ProductHit(val product: Product, val vendor: Vendor)

data class SearchHits(val people: List<PersonHit> = emptyList(), val products: List<ProductHit> = emptyList())

enum class SortOption(val title: String) {
    RELEVANCE("مرتبط‌ترین"),
    RATING("بالاترین امتیاز"),
    NEWEST("جدیدترین"),
    NAME("نام شرکت");

    fun getLocalizedTitle(lang: com.example.util.AppLanguage): String {
        return if (lang == com.example.util.AppLanguage.EN) {
            when (this) {
                RELEVANCE -> "Most Relevant"
                RATING -> "Top Rated"
                NEWEST -> "Newest"
                NAME -> "Company Name"
            }
        } else {
            title
        }
    }
}

data class VendorStats(
    val totalVendors: Int = 0,
    val totalCategories: Int = 0,
    val totalFavorites: Int = 0,
    val recentContacts: Int = 0
)

class VendorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VendorRepository(AppDatabase.getDatabase(application))
    private val settingsDataStore = AppSettingsDataStore(application)

    // Navigation and display state
    val currentScreen = MutableStateFlow(AppScreen.MAIN_TABS)
    val currentTab = MutableStateFlow(MainTab.HOME)
    val selectedVendorId = MutableStateFlow<Long?>(null)
    val editingVendor = MutableStateFlow<Vendor?>(null)

    // Persistent State (Theme, Language, CardView)
    val themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val isDarkMode = MutableStateFlow(false)
    val appLanguage = MutableStateFlow(com.example.util.AppLanguage.FA)
    val isCardViewMode = MutableStateFlow(true) // Cards vs List
    val isAppLockEnabled = MutableStateFlow(false)
    val isScreenshotBlockEnabled = MutableStateFlow(false)
    val isDynamicColorEnabled = MutableStateFlow(false)
    val isAppLocked = MutableStateFlow(false)
    val isSettingsLoaded = MutableStateFlow(false)

    fun setDynamicColorEnabled(enabled: Boolean) {
        isDynamicColorEnabled.value = enabled
        viewModelScope.launch {
            settingsDataStore.setDynamicColorEnabled(enabled)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        themeMode.value = mode
        isDarkMode.value = (mode == ThemeMode.DARK)
        viewModelScope.launch {
            settingsDataStore.setThemeMode(mode)
        }
    }

    fun toggleLanguage() {
        val nextLang = if (appLanguage.value == com.example.util.AppLanguage.FA) {
            com.example.util.AppLanguage.EN
        } else {
            com.example.util.AppLanguage.FA
        }
        appLanguage.value = nextLang
        viewModelScope.launch {
            settingsDataStore.setAppLanguage(nextLang)
        }
    }

    fun setCardViewMode(isCard: Boolean) {
        isCardViewMode.value = isCard
        viewModelScope.launch {
            settingsDataStore.setCardViewMode(isCard)
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        isAppLockEnabled.value = enabled
        if (!enabled) isAppLocked.value = false
        viewModelScope.launch {
            settingsDataStore.setAppLockEnabled(enabled)
        }
    }

    fun setScreenshotBlockEnabled(enabled: Boolean) {
        isScreenshotBlockEnabled.value = enabled
        viewModelScope.launch {
            settingsDataStore.setScreenshotBlockEnabled(enabled)
        }
    }

    fun unlockApp() {
        isAppLocked.value = false
    }

    fun lockApp() {
        if (isAppLockEnabled.value) {
            isAppLocked.value = true
        }
    }

    // Separate search queries with debounce (~250ms)
    val vendorSearchQuery = MutableStateFlow("")
    val productSearchQuery = MutableStateFlow("")
    // Alias for backwards compatibility
    val searchQuery get() = vendorSearchQuery

    val selectedCategoryFilter = MutableStateFlow("همه")
    val selectedActivityTypeFilter = MutableStateFlow("همه")
    val selectedCityFilter = MutableStateFlow("همه")
    val onlyVerifiedFilter = MutableStateFlow(false)
    val onlyCatalogFilter = MutableStateFlow(false)
    val sortOption = MutableStateFlow(SortOption.RELEVANCE)

    // Product screen category filter
    val selectedProductCategory = MutableStateFlow("همه")

    // User message / snackbar
    val userNotice = MutableStateFlow<String?>(null)

    // Data streams from repository
    val allVendors: StateFlow<List<Vendor>> = repository.allVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteVendors: StateFlow<List<Vendor>> = repository.favoriteVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentVendors: StateFlow<List<Vendor>> = repository.recentVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContacts: StateFlow<List<ContactPerson>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProducts: StateFlow<List<Product>> = repository.favoriteProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allActivityTypes: StateFlow<List<ActivityType>> = repository.allActivityTypes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(FlowPreview::class)
    private val debouncedVendorQuery = vendorSearchQuery.debounce(250L)

    @OptIn(FlowPreview::class)
    private val debouncedProductQuery = productSearchQuery.debounce(250L)

    // Filtered vendors reacting to search, category, activity type, city, verification, sort, personnel, and products
    val filteredVendors: StateFlow<List<Vendor>> = combine(
        allVendors,
        debouncedVendorQuery,
        selectedCategoryFilter,
        selectedActivityTypeFilter,
        selectedCityFilter,
        onlyVerifiedFilter,
        onlyCatalogFilter,
        sortOption,
        allContacts,
        allProducts
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val vendors = args[0] as List<Vendor>
        val query = args[1] as String
        val category = args[2] as String
        val actType = args[3] as String
        val city = args[4] as String
        val verifiedOnly = args[5] as Boolean
        val catalogOnly = args[6] as Boolean
        val sort = args[7] as SortOption
        @Suppress("UNCHECKED_CAST")
        val contacts = args[8] as List<ContactPerson>
        @Suppress("UNCHECKED_CAST")
        val products = args[9] as List<Product>

        val contactsByVendor = contacts.groupBy { it.vendorId }
        val productsByVendor = products.groupBy { it.vendorId }

        vendors.filter { vendor ->
            // Search query matching: vendor attributes, phones without dashes, personnel name/phone, products
            val matchesQuery = if (query.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.name, query) ||
                        PersianUtils.matchesSearch(vendor.description, query) ||
                        PersianUtils.matchesSearch(vendor.categories, query) ||
                        PersianUtils.matchesSearch(vendor.tags, query) ||
                        PersianUtils.matchesSearch(vendor.city, query) ||
                        PersianUtils.matchesSearch(vendor.province, query) ||
                        PersianUtils.matchesSearch(vendor.address, query) ||
                        PersianUtils.matchesSearch(vendor.notes, query) ||
                        PersianUtils.matchesSearch(vendor.economicCode, query) ||
                        PersianUtils.matchesSearch(vendor.nationalId, query) ||
                        PersianUtils.matchesSearch(vendor.registrationNumber, query) ||
                        PersianUtils.matchesSearch(vendor.ceoName, query) ||
                        PersianUtils.matchesPhone(vendor.phone, query) ||
                        PersianUtils.matchesPhone(vendor.mobile, query) ||
                        PersianUtils.matchesPhone(vendor.secondaryPhones, query) ||
                        PersianUtils.matchesPhone(vendor.additionalMobiles, query) ||
                        (contactsByVendor[vendor.id]?.any { c ->
                            PersianUtils.matchesSearch(c.name, query) ||
                                    PersianUtils.matchesSearch(c.role, query) ||
                                    PersianUtils.matchesPhone(c.mobile, query) ||
                                    PersianUtils.matchesPhone(c.phone, query)
                        } == true) ||
                        (productsByVendor[vendor.id]?.any { p ->
                            PersianUtils.matchesSearch(p.name, query) ||
                                    PersianUtils.matchesSearch(p.brand, query) ||
                                    PersianUtils.matchesSearch(p.model, query) ||
                                    PersianUtils.matchesSearch(p.technicalCode, query) ||
                                    PersianUtils.matchesSearch(p.specifications, query) ||
                                    PersianUtils.matchesSearch(p.description, query)
                        } == true)
            }

            // Category matching
            val matchesCategory = if (category == "همه" || category.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.categories, category)
            }

            // Activity Type matching
            val matchesActivityType = if (actType == "همه" || actType.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.activityType, actType)
            }

            // City matching
            val matchesCity = if (city == "همه" || city.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.city, city)
            }

            // Verified
            val matchesVerified = if (verifiedOnly) vendor.isVerified else true

            // Catalog
            val matchesCatalog = if (catalogOnly) (vendor.catalogPdfUrl.isNotBlank() || vendor.catalogLocalPath.isNotBlank()) else true

            matchesQuery && matchesCategory && matchesActivityType && matchesCity && matchesVerified && matchesCatalog
        }.let { list ->
            when (sort) {
                SortOption.RELEVANCE -> if (query.isBlank()) list else list.sortedByDescending { relevanceScore(it, query, contactsByVendor[it.id]) }
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
                SortOption.NAME -> list.sortedBy { it.name }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered products reacting to debounced product query and category
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        selectedProductCategory,
        debouncedProductQuery
    ) { products, cat, query ->
        products.filter { p ->
            val matchCat = if (cat == "همه") true else PersianUtils.matchesSearch(p.category, cat)
            val matchQuery = if (query.isBlank()) true else {
                PersianUtils.matchesSearch(p.name, query) ||
                        PersianUtils.matchesSearch(p.model, query) ||
                        PersianUtils.matchesSearch(p.specifications, query) ||
                        PersianUtils.matchesSearch(p.brand, query) ||
                        PersianUtils.matchesSearch(p.technicalCode, query) ||
                        PersianUtils.matchesSearch(p.description, query)
            }
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // People and products that match the query (shown as their own result sections with the company name)
    val searchHits: StateFlow<SearchHits> = combine(
        allVendors,
        allContacts,
        allProducts,
        debouncedVendorQuery
    ) { vendors, contacts, products, query ->
        if (query.isBlank()) {
            SearchHits()
        } else {
            val byId = vendors.associateBy { it.id }
            val nq = PersianUtils.normalize(query)
            val people = contacts.mapNotNull { c ->
                val v = byId[c.vendorId] ?: return@mapNotNull null
                val nameHit = PersianUtils.matchesSearch(c.name, query)
                val hit = nameHit ||
                        PersianUtils.matchesSearch(c.role, query) ||
                        PersianUtils.matchesSearch(c.email, query) ||
                        PersianUtils.matchesPhone(c.mobile, query) ||
                        PersianUtils.matchesPhone(c.phone, query)
                if (!hit) null else Triple(c, v, if (PersianUtils.normalize(c.name).startsWith(nq)) 2 else if (nameHit) 1 else 0)
            }.sortedByDescending { it.third }.take(30).map { PersonHit(it.first, it.second) }
            val prods = products.mapNotNull { p ->
                val v = byId[p.vendorId] ?: return@mapNotNull null
                val nameHit = PersianUtils.matchesSearch(p.name, query)
                val hit = nameHit ||
                        PersianUtils.matchesSearch(p.brand, query) ||
                        PersianUtils.matchesSearch(p.model, query) ||
                        PersianUtils.matchesSearch(p.technicalCode, query) ||
                        PersianUtils.matchesSearch(p.specifications, query)
                if (!hit) null else Triple(p, v, if (PersianUtils.normalize(p.name).startsWith(nq)) 2 else if (nameHit) 1 else 0)
            }.sortedByDescending { it.third }.take(30).map { ProductHit(it.first, it.second) }
            SearchHits(people, prods)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchHits())

    // Recent searches (persisted)
    val recentSearches: StateFlow<List<String>> = settingsDataStore.recentSearchesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun commitSearch(query: String) {
        viewModelScope.launch { settingsDataStore.addRecentSearch(query) }
    }

    fun clearRecentSearches() {
        viewModelScope.launch { settingsDataStore.clearRecentSearches() }
    }

    private fun relevanceScore(v: Vendor, q: String, people: List<ContactPerson>?): Int {
        val nq = PersianUtils.normalize(q)
        if (nq.isBlank()) return 0
        val name = PersianUtils.normalize(v.name)
        var score = 0
        if (name == nq) score += 120 else if (name.startsWith(nq)) score += 90 else if (name.contains(nq)) score += 60
        if (PersianUtils.matchesSearch(v.tags, q) || PersianUtils.matchesSearch(v.categories, q)) score += 25
        if (PersianUtils.matchesSearch(v.ceoName, q)) score += 20
        if (people?.any { PersianUtils.matchesSearch(it.name, q) } == true) score += 15
        if (PersianUtils.matchesSearch(v.city, q)) score += 8
        if (v.isVerified) score += 5
        return score + (v.rating * 2).toInt()
    }

    // Statistics
    val stats: StateFlow<VendorStats> = combine(
        allVendors,
        allCategories,
        favoriteVendors,
        recentVendors
    ) { vendors, cats, favs, recents ->
        VendorStats(
            totalVendors = vendors.size,
            totalCategories = cats.size,
            totalFavorites = favs.size,
            recentContacts = recents.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VendorStats())

    init {
        viewModelScope.launch {
            settingsDataStore.themeModeFlow.collect { mode ->
                themeMode.value = mode
                isDarkMode.value = (mode == ThemeMode.DARK)
            }
        }
        viewModelScope.launch {
            settingsDataStore.appLanguageFlow.collect { lang ->
                appLanguage.value = lang
            }
        }
        viewModelScope.launch {
            settingsDataStore.isCardViewModeFlow.collect { isCard ->
                isCardViewMode.value = isCard
            }
        }
        viewModelScope.launch {
            settingsDataStore.isAppLockEnabledFlow.collect { enabled ->
                // Cold start: lock right away when the app-lock feature is enabled
                if (!isSettingsLoaded.value && enabled) isAppLocked.value = true
                isAppLockEnabled.value = enabled
                isSettingsLoaded.value = true
            }
        }
        viewModelScope.launch {
            settingsDataStore.isScreenshotBlockEnabledFlow.collect { enabled ->
                isScreenshotBlockEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            settingsDataStore.isDynamicColorEnabledFlow.collect { enabled ->
                isDynamicColorEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            repository.ensureBaselineTaxonomy()
        }
    }

    fun openVendorDetail(vendorId: Long) {
        selectedVendorId.value = vendorId
        currentScreen.value = AppScreen.VENDOR_DETAIL
    }

    fun openAddVendor() {
        editingVendor.value = null
        currentScreen.value = AppScreen.ADD_EDIT_VENDOR
    }

    fun openEditVendor(vendor: Vendor) {
        editingVendor.value = vendor
        currentScreen.value = AppScreen.ADD_EDIT_VENDOR
    }

    fun openLocationView(vendorId: Long) {
        selectedVendorId.value = vendorId
        currentScreen.value = AppScreen.LOCATION_VIEW
    }

    fun navigateBack() {
        when (currentScreen.value) {
            AppScreen.LOCATION_VIEW -> currentScreen.value = AppScreen.VENDOR_DETAIL
            AppScreen.VENDOR_DETAIL,
            AppScreen.ADD_EDIT_VENDOR -> currentScreen.value = AppScreen.MAIN_TABS
            AppScreen.MAIN_TABS -> { /* Handled by OS or stay */ }
            AppScreen.ONBOARDING -> currentScreen.value = AppScreen.MAIN_TABS
        }
    }

    fun toggleVendorFavorite(vendor: Vendor) {
        viewModelScope.launch {
            repository.toggleVendorFavorite(vendor.id, vendor.isFavorite)
        }
    }

    fun toggleProductFavorite(product: Product) {
        viewModelScope.launch {
            repository.toggleProductFavorite(product.id, product.isFavorite)
        }
    }

    fun recordVendorContact(vendorId: Long) {
        viewModelScope.launch {
            repository.recordContact(vendorId)
        }
    }

    fun updateVendorNotes(vendorId: Long, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(vendorId, notes)
            userNotice.value = t("یادداشت با موفقیت ذخیره شد")
        }
    }

    fun saveVendorWithRelations(
        vendor: Vendor,
        contacts: List<ContactPerson>,
        products: List<Product>,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val savedId = repository.saveVendorWithRelations(vendor, contacts, products)
                userNotice.value = t("اطلاعات وندور و جزئیات با موفقیت ذخیره شد")
                onComplete(savedId)
            } catch (e: Exception) {
                userNotice.value = tf("خطا در ذخیره‌سازی: {0}", e.localizedMessage)
            }
        }
    }

    fun loadSampleData(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                repository.restoreSampleData()
                userNotice.value = t("داده‌های نمونه با موفقیت بارگذاری شدند")
                onComplete?.invoke()
            } catch (e: Exception) {
                userNotice.value = tf("خطا در بارگذاری داده‌های نمونه: {0}", e.localizedMessage)
            }
        }
    }

    fun deleteVendor(vendorId: Long) {
        viewModelScope.launch {
            repository.deleteVendor(vendorId)
            currentScreen.value = AppScreen.MAIN_TABS
            userNotice.value = t("وندور حذف شد")
        }
    }

    // Categories
    fun addCategory(name: String, icon: String = "category") {
        viewModelScope.launch {
            repository.addCategory(Category(name = name.trim(), iconName = icon))
            userNotice.value = t("دسته‌بندی جدید افزوده شد")
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            userNotice.value = t("دسته‌بندی ویرایش شد")
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            userNotice.value = t("دسته‌بندی حذف شد")
        }
    }

    // Activity Types
    fun addActivityType(name: String) {
        viewModelScope.launch {
            repository.addActivityType(name.trim())
            userNotice.value = t("نوع فعالیت جدید افزوده شد")
        }
    }

    fun updateActivityType(activityType: ActivityType) {
        viewModelScope.launch {
            repository.updateActivityType(activityType)
            userNotice.value = t("نوع فعالیت بروزرسانی شد")
        }
    }

    fun deleteActivityType(activityTypeId: Long) {
        viewModelScope.launch {
            repository.deleteActivityType(activityTypeId)
            userNotice.value = t("نوع فعالیت حذف شد")
        }
    }

    // Products CRUD
    fun saveProduct(product: Product, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveProduct(product)
            userNotice.value = t("اطلاعات محصول ذخیره شد")
            onComplete()
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            userNotice.value = t("محصول حذف شد")
        }
    }

    fun getProductsForVendor(vendorId: Long): kotlinx.coroutines.flow.Flow<List<Product>> =
        repository.getProductsByVendor(vendorId)

    // Contacts CRUD
    fun getContactsForVendor(vendorId: Long): kotlinx.coroutines.flow.Flow<List<ContactPerson>> =
        repository.getContactsByVendor(vendorId)

    fun saveContact(contact: ContactPerson, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveContact(contact)
            userNotice.value = t("اطلاعات پرسنل با موفقیت ثبت شد")
            onComplete()
        }
    }

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
            userNotice.value = t("پرسنل حذف شد")
        }
    }

    // Duplicate check
    suspend fun checkDuplicateVendor(
        name: String,
        nationalId: String = "",
        mobile: String = "",
        currentId: Long = 0L
    ): Vendor? {
        return repository.checkDuplicateVendor(name, nationalId, mobile, currentId)
    }

    // Export / Import / Reset
    fun exportToCsv(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.exportCsv()
            onSuccess(csv)
            userNotice.value = t("خروجی CSV آماده شد")
        }
    }

    fun importFromCsv(csv: String, onComplete: ((com.example.util.ImportSummary) -> Unit)? = null) {
        viewModelScope.launch {
            val summary = repository.importCsv(csv)
            userNotice.value = tf("تعداد {0} وندور جدید وارد شد. {1} مورد تکراری رد شد.", summary.newCount, summary.duplicateSkippedCount)
            onComplete?.invoke(summary)
        }
    }

    fun exportFullBackupJson(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportFullBackupJson()
            onSuccess(json)
            userNotice.value = t("فایل پشتیبان کامل JSON آماده شد")
        }
    }

    fun exportFullBackupZip(context: Context, outputStream: java.io.OutputStream, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.exportFullBackupZip(context, outputStream)
            userNotice.value = t("فایل پشتیبان کامل (ZIP شامل تصاویر و کاتالوگ‌ها) با موفقیت ذخیره شد")
            onComplete()
        }
    }

    fun restoreFullBackupStream(context: Context, inputStream: java.io.InputStream, onComplete: ((inserted: Int, skipped: Int) -> Unit)? = null) {
        viewModelScope.launch {
            val (inserted, skipped) = repository.restoreFullBackupStream(context, inputStream)
            userNotice.value = tf("پشتیبان بازیابی شد: {0} وندور جدید، {1} تکراری رد شد.", inserted, skipped)
            onComplete?.invoke(inserted, skipped)
        }
    }

    fun restoreFullBackupJson(jsonString: String, onComplete: ((inserted: Int, skipped: Int) -> Unit)? = null) {
        viewModelScope.launch {
            val (inserted, skipped) = repository.restoreFullBackupJson(jsonString)
            userNotice.value = tf("پشتیبان بازیابی شد: {0} وندور جدید، {1} تکراری رد شد.", inserted, skipped)
            onComplete?.invoke(inserted, skipped)
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.restoreSampleData()
            userNotice.value = t("اطلاعات پیش‌فرض بازیابی شدند")
        }
    }

    fun toggleDarkMode() {
        val nextMode = if (themeMode.value == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(nextMode)
    }

    fun clearNotice() {
        userNotice.value = null
    }
}
