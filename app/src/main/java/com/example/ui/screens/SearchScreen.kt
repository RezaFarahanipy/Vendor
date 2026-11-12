package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.model.Category
import com.example.ui.theme.BrandDark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VendorCard
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.AppLanguage
import com.example.util.Localization
import com.example.util.PersianUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ui.theme.staggerIn
import com.example.ui.theme.StatusBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: VendorViewModel,
    onVendorClick: (Long) -> Unit
) {
    StatusBarStyle(onGradient = false)
    val query by viewModel.searchQuery.collectAsState()
    val filteredVendors by viewModel.filteredVendors.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val favoriteVendors by viewModel.favoriteVendors.collectAsState()
    val recentVendors by viewModel.recentVendors.collectAsState()
    val categories by viewModel.allCategories.collectAsState()

    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val isCardView by viewModel.isCardViewMode.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val selectedCity by viewModel.selectedCityFilter.collectAsState()
    val onlyVerified by viewModel.onlyVerifiedFilter.collectAsState()
    val onlyCatalog by viewModel.onlyCatalogFilter.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Auto-focus and open soft keyboard when entering Search screen
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(200)
        try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (_: Exception) {}
    }

    val categoryChips = remember(categories, lang) {
        val allLabel = if (lang == AppLanguage.FA) "همه" else "All"
        val names = if (categories.isNotEmpty()) categories.map { it.name } else listOf("صنعتی", "بازرگانی", "استیل", "صنایع غذایی", "برق")
        listOf(allLabel) + names
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Surface: Search Bar and Category Chips
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Search Input with auto-focus
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = {
                        Text(
                            text = if (lang == AppLanguage.FA) "جستجوی وندور، محصول، کد اقتصادی، شماره تماس..." else Localization.get("search_placeholder", lang),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = Localization.get("search", lang),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = if (lang == AppLanguage.FA) "پاک کردن" else "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .testTag("search_text_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categoryChips) { cat ->
                        val isSelected = selectedCategory == cat || (selectedCategory == "همه" && cat == (if (lang == AppLanguage.FA) "همه" else "All"))
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectedCategoryFilter.value = if (cat == "All" || cat == "همه") "همه" else cat
                            },
                            label = {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        }

        // Dashboard StatCards moved here as requested: "در صفحه جستجو کارتهای داشبورد قرار بده"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DashboardMiniStatCard(
                modifier = Modifier.weight(1f),
                title = if (lang == AppLanguage.FA) "کل وندورها" else Localization.get("stat_total_vendors", lang),
                count = if (lang == AppLanguage.FA) PersianUtils.toPersianDigits(allVendors.size) else allVendors.size.toString(),
                icon = Icons.Default.Apartment,
                iconTint = BrandPrimary,
                onClick = {
                    viewModel.searchQuery.value = ""
                    viewModel.selectedCategoryFilter.value = "همه"
                }
            )

            DashboardMiniStatCard(
                modifier = Modifier.weight(1f),
                title = if (lang == AppLanguage.FA) "علاقه‌مندی‌ها" else Localization.get("stat_favorites", lang),
                count = if (lang == AppLanguage.FA) PersianUtils.toPersianDigits(favoriteVendors.size) else favoriteVendors.size.toString(),
                icon = Icons.Default.Favorite,
                iconTint = RedFavorite,
                onClick = {
                    viewModel.currentTab.value = MainTab.FAVORITES
                }
            )

            DashboardMiniStatCard(
                modifier = Modifier.weight(1f),
                title = if (lang == AppLanguage.FA) "تماس‌های اخیر" else Localization.get("stat_recent_calls", lang),
                count = if (lang == AppLanguage.FA) PersianUtils.toPersianDigits(recentVendors.size) else recentVendors.size.toString(),
                icon = Icons.Default.Call,
                iconTint = Color(0xFF00A389),
                onClick = {}
            )
        }

        // Main Categories Grid transferred from Home Screen
        if (query.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.FA) "دسته‌بندی‌های اصلی" else Localization.get("main_categories", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (selectedCategory != "همه") {
                        TextButton(onClick = { viewModel.selectedCategoryFilter.value = "همه" }) {
                            Text(
                                text = if (lang == AppLanguage.FA) "نمایش همه" else Localization.get("all", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val displayCats = if (categories.isNotEmpty()) categories.take(6) else listOf(
                    Category(name = "صنعتی", vendorCount = 0),
                    Category(name = "ساختمان و عمرانی", vendorCount = 0),
                    Category(name = "آهن‌آلات", vendorCount = 0),
                    Category(name = "ماشین‌آلات", vendorCount = 0),
                    Category(name = "مواد شیمیایی", vendorCount = 0),
                    Category(name = "برق و الکترونیک", vendorCount = 0)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    displayCats.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { catItem ->
                                val isCatSelected = selectedCategory == catItem.name
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.selectedCategoryFilter.value = if (isCatSelected) "همه" else catItem.name
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCatSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isCatSelected) {
                                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                    } else {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                    },
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isCatSelected) 2.dp else 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val catIcon = when (catItem.name) {
                                            "صنعتی" -> Icons.Default.PrecisionManufacturing
                                            "ساختمان و عمرانی" -> Icons.Default.Apartment
                                            "آهن‌آلات", "فلزات و آهن‌آلات" -> Icons.Default.Hardware
                                            "ماشین‌آلات" -> Icons.Default.Agriculture
                                            "مواد غذایی" -> Icons.Default.Restaurant
                                            "مواد شیمیایی" -> Icons.Default.Science
                                            "برق و الکترونیک" -> Icons.Default.Bolt
                                            else -> Icons.Default.Business
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isCatSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = catIcon,
                                                contentDescription = null,
                                                tint = if (isCatSelected) Color.White else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = Localization.translateCategory(catItem.name, lang),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCatSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 11.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // View Mode + Sort bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cards vs List toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isCardView) BrandPrimary else Color.Transparent)
                        .clickable { viewModel.isCardViewMode.value = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.FA) "فهرست" else "List",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (!isCardView) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isCardView) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCardView) BrandPrimary else Color.Transparent)
                        .clickable { viewModel.isCardViewMode.value = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.FA) "کارت‌ها" else "Cards",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isCardView) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCardView) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Sort & Filter buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortOption.getLocalizedTitle(lang),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.getLocalizedTitle(lang), style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    viewModel.sortOption.value = option
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (onlyVerified || onlyCatalog || selectedCity != "همه") BrandPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Active clearable filter chips row
        val hasActiveFilters = selectedCategory != "همه" || selectedCity != "همه" || onlyVerified || onlyCatalog || query.isNotBlank()
        if (hasActiveFilters) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (query.isNotBlank()) {
                    item {
                        ActiveFilterChip(
                            label = "جستجو: $query",
                            onClear = { viewModel.searchQuery.value = "" }
                        )
                    }
                }
                if (selectedCategory != "همه") {
                    item {
                        ActiveFilterChip(
                            label = "دسته: $selectedCategory",
                            onClear = { viewModel.selectedCategoryFilter.value = "همه" }
                        )
                    }
                }
                if (selectedCity != "همه") {
                    item {
                        ActiveFilterChip(
                            label = "شهر: $selectedCity",
                            onClear = { viewModel.selectedCityFilter.value = "همه" }
                        )
                    }
                }
                if (onlyVerified) {
                    item {
                        ActiveFilterChip(
                            label = "فقط تأیید شده",
                            onClear = { viewModel.onlyVerifiedFilter.value = false }
                        )
                    }
                }
                if (onlyCatalog) {
                    item {
                        ActiveFilterChip(
                            label = "دارای کاتالوگ",
                            onClear = { viewModel.onlyCatalogFilter.value = false }
                        )
                    }
                }
                item {
                    TextButton(
                        onClick = {
                            viewModel.searchQuery.value = ""
                            viewModel.selectedCategoryFilter.value = "همه"
                            viewModel.selectedCityFilter.value = "همه"
                            viewModel.onlyVerifiedFilter.value = false
                            viewModel.onlyCatalogFilter.value = false
                        }
                    ) {
                        Text("پاک‌سازی همه", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Result count header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val countStr = if (lang == AppLanguage.FA) "${PersianUtils.toPersianDigits(filteredVendors.size)} وندور یافت شد" else "${filteredVendors.size} vendors found"
            Text(
                text = countStr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedCategory != "همه" || selectedCity != "همه" || onlyVerified || onlyCatalog) {
                TextButton(
                    onClick = {
                        viewModel.selectedCategoryFilter.value = "همه"
                        viewModel.selectedCityFilter.value = "همه"
                        viewModel.onlyVerifiedFilter.value = false
                        viewModel.onlyCatalogFilter.value = false
                    }
                ) {
                    Text(
                        text = if (lang == AppLanguage.FA) "پاک کردن فیلترها" else "Clear Filters",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Vendors List
        if (filteredVendors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (lang == AppLanguage.FA) "وندوری با مشخصات مورد نظر یافت نشد" else "No matching vendors found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == AppLanguage.FA) "کلمات جستجو یا فیلترهای انتخابی را تغییر دهید." else "Try modifying search terms or filter criteria.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filteredVendors, key = { _, v -> v.id }) { index, vendor ->
                    VendorCard(
                        modifier = Modifier.animateItem().staggerIn(index),
                        vendor = vendor,
                        onClick = { onVendorClick(vendor.id) },
                        onFavoriteClick = { viewModel.toggleVendorFavorite(vendor) }
                    )
                }
            }
        }
    }

    // Advanced Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.FA) "فیلترهای پیشرفته وندور" else "Advanced Vendor Filters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // City filter
                Text(
                    text = if (lang == AppLanguage.FA) "استان و شهر:" else "Province & City:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                val cities = listOf("همه", "تهران", "اصفهان", "مشهد", "شیراز", "کرج", "تبریز")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cities) { city ->
                        val isSelected = selectedCity == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedCityFilter.value = city },
                            label = { Text(city, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox: Only verified
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onlyVerifiedFilter.value = !onlyVerified }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = onlyVerified,
                        onCheckedChange = { viewModel.onlyVerifiedFilter.value = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FA) "فقط وندورهای تأیید شده و دارای پروانه" else "Only verified & licensed vendors",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Checkbox: Only with catalog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onlyCatalogFilter.value = !onlyCatalog }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = onlyCatalog,
                        onCheckedChange = { viewModel.onlyCatalogFilter.value = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.FA) "فقط وندورهای دارای کاتالوگ PDF" else "Only vendors with technical catalog PDF",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            viewModel.selectedCityFilter.value = "همه"
                            viewModel.onlyVerifiedFilter.value = false
                            viewModel.onlyCatalogFilter.value = false
                        }
                    ) {
                        Text(if (lang == AppLanguage.FA) "بازنشانی" else "Reset", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showFilterSheet = false
                                }
                            }
                        }
                    ) {
                        Text(if (lang == AppLanguage.FA) "اعمال فیلتر" else "Apply Filter", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMiniStatCard(
    title: String,
    count: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ActiveFilterChip(
    label: String,
    onClear: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "حذف فیلتر",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

