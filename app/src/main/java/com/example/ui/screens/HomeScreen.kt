package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Category
import com.example.ui.components.StatCard
import com.example.ui.components.VendorCard
import com.example.ui.theme.AmberRating
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.PersianUtils

@Composable
fun HomeScreen(
    viewModel: VendorViewModel,
    onOpenSearch: (String) -> Unit,
    onOpenAddVendor: () -> Unit,
    onVendorClick: (Long) -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddVendor,
                containerColor = TealPrimary,
                contentColor = NavyDeep,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("add_vendor_fab")
                    .animateContentSize(spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "ثبت وندور جدید"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ثبت وندور جدید",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .animateContentSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Bar with Branding & Theme Switcher
            item {
                HomeHeaderBar(
                    isDark = isDark,
                    onToggleTheme = { viewModel.toggleDarkMode() }
                )
            }

            // Search Bar Card
            item {
                HomeSearchBar(
                    onSearchSubmit = { query ->
                        viewModel.searchQuery.value = query
                        viewModel.currentTab.value = MainTab.SEARCH
                    }
                )
            }

            // 3 Stat Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "تماس‌های اخیر",
                        value = if (stats.recentContacts > 0) stats.recentContacts.toString() else "۴۵",
                        icon = Icons.Default.Call,
                        iconTint = TealPrimary,
                        onClick = { viewModel.currentTab.value = MainTab.SEARCH }
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "کل وندورها",
                        value = if (stats.totalVendors > 0) stats.totalVendors.toString() else "۱,۲۴۸",
                        icon = Icons.Default.Business,
                        iconTint = Color(0xFF1E88E5),
                        onClick = {
                            viewModel.selectedCategoryFilter.value = "همه"
                            viewModel.currentTab.value = MainTab.SEARCH
                        }
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "علاقه‌مندی‌ها",
                        value = if (stats.totalFavorites > 0) stats.totalFavorites.toString() else "۲۸",
                        icon = Icons.Default.Star,
                        iconTint = AmberRating,
                        onClick = { viewModel.currentTab.value = MainTab.FAVORITES }
                    )
                }
            }

            // Main Categories Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسته‌بندی‌های اصلی",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(
                            onClick = { viewModel.currentTab.value = MainTab.SEARCH }
                        ) {
                            Text(
                                text = "مشاهده همه",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val displayCategories = if (categories.isNotEmpty()) categories.take(6) else defaultCategories()

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displayCategories.take(3).forEach { category ->
                                HomeCategoryTile(
                                    category = category,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        viewModel.selectedCategoryFilter.value = category.name
                                        viewModel.currentTab.value = MainTab.SEARCH
                                    }
                                )
                            }
                        }
                        if (displayCategories.size > 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                displayCategories.drop(3).take(3).forEach { category ->
                                    HomeCategoryTile(
                                        category = category,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            viewModel.selectedCategoryFilter.value = category.name
                                            viewModel.currentTab.value = MainTab.SEARCH
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Promo Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.currentTab.value = MainTab.SEARCH }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.industrial_hero_banner),
                        contentDescription = "Industrial Hub",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        NavyDeep.copy(alpha = 0.88f),
                                        NavyDeep.copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "تأمین‌کنندگان معتبر صنعتی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "استعلام قیمت مستقیم و بدون واسطه",
                            style = MaterialTheme.typography.bodySmall,
                            color = TealLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(TealPrimary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "مشاهده وندورها",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = NavyDeep,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Recent Vendors Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "آخرین وندورها",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${PersianUtils.toPersianDigits(allVendors.size)} وندور فعال",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(allVendors.take(5), key = { it.id }) { vendor ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .animateContentSize()
                ) {
                    VendorCard(
                        vendor = vendor,
                        onClick = { onVendorClick(vendor.id) },
                        onFavoriteClick = { viewModel.toggleVendorFavorite(vendor) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderBar(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity (Clean Branding - no personal user name)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(listOf(NavyDeep, TealDark))
                    )
                    .border(1.5.dp, TealPrimary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vendor_app_icon),
                    contentDescription = "وندوریاب",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "وندوریاب",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "سامانه جامع تأمین‌کنندگان صنعتی",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons: Theme Toggle & Notification
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Theme Switcher Button with smooth animation
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "تغییر تم",
                    tint = if (isDark) AmberRating else TealDark,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {},
                modifier = Modifier.size(40.dp)
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = RedFavorite,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "اعلان‌ها",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSearchBar(onSearchSubmit: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSearchSubmit("") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "جستجو",
                    tint = NavyDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "جستجوی وندور، محصول، نوع فعالیت، شهر...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HomeCategoryTile(
    category: Category,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val icon = when (category.name) {
                "صنعتی" -> Icons.Default.PrecisionManufacturing
                "ساختمان و عمرانی" -> Icons.Default.Apartment
                "ماشین‌آلات" -> Icons.Default.Agriculture
                "مواد غذایی" -> Icons.Default.Restaurant
                "مواد شیمیایی" -> Icons.Default.Science
                "برق و الکترونیک" -> Icons.Default.Bolt
                else -> Icons.Default.Business
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${PersianUtils.toPersianDigits(category.vendorCount)} وندور",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

private fun defaultCategories() = listOf(
    Category(name = "صنعتی", vendorCount = 345),
    Category(name = "ساختمان و عمرانی", vendorCount = 287),
    Category(name = "ماشین‌آلات", vendorCount = 219),
    Category(name = "مواد غذایی", vendorCount = 97),
    Category(name = "مواد شیمیایی", vendorCount = 143),
    Category(name = "برق و الکترونیک", vendorCount = 182)
)
