package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.AppHeader
import com.example.ui.theme.NavyDeep
import com.example.ui.components.CatalogViewerDialog
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.PersianUtils

@Composable
fun CatalogScreen(
    viewModel: VendorViewModel,
    onVendorClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.filteredProducts.collectAsState()
    val selectedCategory by viewModel.selectedProductCategory.collectAsState()

    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var showFullCatalogViewer by remember { mutableStateOf(false) }

    val productCategories = listOf(
        "همه",
        "پمپ‌های سانتریفیوژ",
        "پمپ‌های عمودی",
        "پمپ‌های دنده‌ای",
        "پمپ‌های طبقاتی",
        "پمپ‌های شناور",
        "شیرآلات صنعتی"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "کاتالوگ محصولات",
            subtitle = "${PersianUtils.toPersianDigits(products.size)} محصول تخصصی"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Category Chips Row
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productCategories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedProductCategory.value = cat },
                            label = {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = NavyDeep
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Products Grid in Column
            val chunkedProducts = products.chunked(2)
            items(chunkedProducts) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CatalogProductGridCard(
                        product = pair[0],
                        modifier = Modifier.weight(1f),
                        onDetailClick = { selectedProductForDetail = pair[0] },
                        onFavoriteClick = { viewModel.toggleProductFavorite(pair[0]) }
                    )
                    if (pair.size > 1) {
                        CatalogProductGridCard(
                            product = pair[1],
                            modifier = Modifier.weight(1f),
                            onDetailClick = { selectedProductForDetail = pair[1] },
                            onFavoriteClick = { viewModel.toggleProductFavorite(pair[1]) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Full Company Catalog PDF Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable {
                            showFullCatalogViewer = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TealDark.copy(alpha = 0.85f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RedFavorite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "دانلود کاتالوگ جامع صنعتی",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "۲۴ صفحه استاندارد و جدول مشخصات هیدرولیکی (۵.۸ MB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TealLight
                            )
                        }
                    }
                }
            }

            // Factory Machinery Photo Gallery
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "گالری خطوط تولید و تجهیزات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listOf(R.drawable.industrial_pump_system, R.drawable.industrial_hero_banner)) { drawableId ->
                            Box(
                                modifier = Modifier
                                    .size(width = 160.dp, height = 110.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NavyDeep)
                            ) {
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = "Gallery item",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Product Detail Dialog
    if (selectedProductForDetail != null) {
        val prod = selectedProductForDetail!!
        AlertDialog(
            onDismissRequest = { selectedProductForDetail = null },
            title = {
                Text(
                    text = prod.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NavyDeep)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.industrial_pump_system),
                            contentDescription = prod.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "برند: ${prod.brand} | مدل: ${prod.model}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = prod.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مشخصات فنی:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = prod.specifications,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedProductForDetail = null
                        onVendorClick(prod.vendorId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("مشاهده اطلاعات وندور", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProductForDetail = null }) {
                    Text("بستن")
                }
            }
        )
    }

    if (showFullCatalogViewer) {
        CatalogViewerDialog(
            vendorName = "کاتالوگ جامع وندوریاب",
            catalogTitle = "کاتالوگ جامع صنعتی و تجهیزات تخصصی",
            catalogLocalPath = "",
            catalogPdfUrl = "",
            onDismiss = { showFullCatalogViewer = false }
        )
    }
}

@Composable
private fun CatalogProductGridCard(
    product: Product,
    modifier: Modifier = Modifier,
    onDetailClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyDeep)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.industrial_pump_system),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(NavyDeep.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "علاقه‌مندی",
                        tint = if (product.isFavorite) RedFavorite else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "مدل ${product.model}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDetailClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "مشاهده جزئیات",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
