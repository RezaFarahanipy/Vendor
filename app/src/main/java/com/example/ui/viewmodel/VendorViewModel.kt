package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.data.model.Vendor
import com.example.data.repository.VendorRepository
import com.example.util.PersianUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

enum class SortOption(val title: String) {
    RELEVANCE("مرتبط‌ترین"),
    RATING("بالاترین امتیاز"),
    NEWEST("جدیدترین"),
    NAME("نام شرکت")
}

data class VendorStats(
    val totalVendors: Int = 0,
    val totalCategories: Int = 0,
    val totalFavorites: Int = 0,
    val recentContacts: Int = 0
)

class VendorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VendorRepository(AppDatabase.getDatabase(application))

    // Navigation and display state
    val currentScreen = MutableStateFlow(AppScreen.MAIN_TABS)
    val currentTab = MutableStateFlow(MainTab.HOME)
    val selectedVendorId = MutableStateFlow<Long?>(null)
    val editingVendor = MutableStateFlow<Vendor?>(null)
    val isDarkMode = MutableStateFlow(false)

    // Search and filters
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("همه")
    val selectedCityFilter = MutableStateFlow("همه")
    val onlyVerifiedFilter = MutableStateFlow(false)
    val onlyCatalogFilter = MutableStateFlow(false)
    val sortOption = MutableStateFlow(SortOption.RELEVANCE)
    val isCardViewMode = MutableStateFlow(true) // Cards vs List

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

    val favoriteProducts: StateFlow<List<Product>> = repository.favoriteProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered vendors reacting to search, category, city, verification, and sort
    val filteredVendors: StateFlow<List<Vendor>> = combine(
        allVendors,
        searchQuery,
        selectedCategoryFilter,
        selectedCityFilter,
        onlyVerifiedFilter,
        onlyCatalogFilter,
        sortOption
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val vendors = args[0] as List<Vendor>
        val query = args[1] as String
        val category = args[2] as String
        val city = args[3] as String
        val verifiedOnly = args[4] as Boolean
        val catalogOnly = args[5] as Boolean
        val sort = args[6] as SortOption

        vendors.filter { vendor ->
            // Search query matching
            val matchesQuery = if (query.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.name, query) ||
                        PersianUtils.matchesSearch(vendor.description, query) ||
                        PersianUtils.matchesSearch(vendor.categories, query) ||
                        PersianUtils.matchesSearch(vendor.tags, query) ||
                        PersianUtils.matchesSearch(vendor.city, query) ||
                        PersianUtils.matchesSearch(vendor.phone, query) ||
                        PersianUtils.matchesSearch(vendor.ceoName, query)
            }

            // Category matching
            val matchesCategory = if (category == "همه" || category.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.categories, category)
            }

            // City matching
            val matchesCity = if (city == "همه" || city.isBlank()) true else {
                PersianUtils.matchesSearch(vendor.city, city)
            }

            // Verified
            val matchesVerified = if (verifiedOnly) vendor.isVerified else true

            // Catalog
            val matchesCatalog = if (catalogOnly) vendor.catalogPdfUrl.isNotBlank() else true

            matchesQuery && matchesCategory && matchesCity && matchesVerified && matchesCatalog
        }.let { list ->
            when (sort) {
                SortOption.RELEVANCE -> list
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
                SortOption.NAME -> list.sortedBy { it.name }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered products
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        selectedProductCategory,
        searchQuery
    ) { products, cat, query ->
        products.filter { p ->
            val matchCat = if (cat == "همه") true else PersianUtils.matchesSearch(p.category, cat)
            val matchQuery = if (query.isBlank()) true else {
                PersianUtils.matchesSearch(p.name, query) ||
                        PersianUtils.matchesSearch(p.model, query) ||
                        PersianUtils.matchesSearch(p.specifications, query) ||
                        PersianUtils.matchesSearch(p.brand, query)
            }
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            repository.ensureSampleDataLoaded()
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
            userNotice.value = "یادداشت با موفقیت ذخیره شد"
        }
    }

    fun saveVendor(vendor: Vendor, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.saveVendor(vendor)
            userNotice.value = "اطلاعات وندور با موفقیت ذخیره شد"
            onComplete()
        }
    }

    fun deleteVendor(vendorId: Long) {
        viewModelScope.launch {
            repository.deleteVendor(vendorId)
            currentScreen.value = AppScreen.MAIN_TABS
            userNotice.value = "وندور حذف شد"
        }
    }

    fun addCategory(name: String, icon: String = "category") {
        viewModelScope.launch {
            repository.addCategory(Category(name = name, iconName = icon))
            userNotice.value = "دسته‌بندی جدید افزوده شد"
        }
    }

    fun exportToCsv(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.exportCsv()
            onSuccess(csv)
            userNotice.value = "خروجی CSV آماده شد"
        }
    }

    fun importFromCsv(csv: String) {
        viewModelScope.launch {
            val count = repository.importCsv(csv)
            userNotice.value = "$count وندور از فایل با موفقیت وارد شد"
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.restoreSampleData()
            userNotice.value = "اطلاعات پیش‌فرض بازیابی شدند"
        }
    }

    fun clearNotice() {
        userNotice.value = null
    }
}
