package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppHeader
import com.example.ui.components.ProductItemRow
import com.example.ui.components.VendorCard
import com.example.ui.theme.InkDeep
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.PersianUtils
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.ui.theme.staggerIn

enum class FavTab {
    VENDORS,
    PRODUCTS
}

@Composable
fun FavoritesScreen(
    viewModel: VendorViewModel,
    onVendorClick: (Long) -> Unit
) {
    val favoriteVendors by viewModel.favoriteVendors.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    var selectedTab by remember { mutableStateOf(FavTab.VENDORS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "علاقه‌مندی‌ها",
            subtitle = if (selectedTab == FavTab.VENDORS)
                "${PersianUtils.toPersianDigits(favoriteVendors.size)} وندور نشان‌شده"
            else
                "${PersianUtils.toPersianDigits(favoriteProducts.size)} محصول نشان‌شده"
        )

        // Segmented Tab Switch (وندورها / محصولات)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == FavTab.VENDORS) BrandPrimary else Color.Transparent)
                            .clickable { selectedTab = FavTab.VENDORS }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "وندورها",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == FavTab.VENDORS) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == FavTab.PRODUCTS) BrandPrimary else Color.Transparent)
                            .clickable { selectedTab = FavTab.PRODUCTS }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "محصولات",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == FavTab.PRODUCTS) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // List Content
        if (selectedTab == FavTab.VENDORS) {
            if (favoriteVendors.isEmpty()) {
                FavoritesEmptyState(message = "هنوز هیچ وندوری به لیست علاقه‌مندی‌ها اضافه نشده است.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(favoriteVendors, key = { _, v -> v.id }) { index, vendor ->
                        VendorCard(
                            modifier = Modifier.animateItem().staggerIn(index),
                            vendor = vendor,
                            onClick = { onVendorClick(vendor.id) },
                            onFavoriteClick = { viewModel.toggleVendorFavorite(vendor) }
                        )
                    }
                }
            }
        } else {
            if (favoriteProducts.isEmpty()) {
                FavoritesEmptyState(message = "هنوز هیچ محصولی به لیست علاقه‌مندی‌ها اضافه نشده است.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(favoriteProducts, key = { it.id }) { product ->
                        ProductItemRow(
                            product = product,
                            onFavoriteClick = { viewModel.toggleProductFavorite(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesEmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "لیست علاقه‌مندی‌ها خالی است",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
