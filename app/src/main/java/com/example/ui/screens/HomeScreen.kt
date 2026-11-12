package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Category
import com.example.ui.components.VendorCard
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberRating
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BrandContainer
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.HeroColors
import com.example.ui.theme.InkDeep
import com.example.ui.theme.MintContainer
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.VioletContainer
import com.example.ui.theme.animatedGradient
import com.example.ui.theme.pressScale
import androidx.compose.foundation.LocalIndication
import com.example.ui.theme.staggerIn
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.AppLanguage
import com.example.util.Localization
import com.example.util.PersianUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.theme.StatusBarStyle

@Composable
fun HomeScreen(
    viewModel: VendorViewModel,
    onOpenSearch: (String) -> Unit,
    onOpenAddVendor: () -> Unit,
    onVendorClick: (Long) -> Unit
) {
    StatusBarStyle(onGradient = true)
    val allVendors by viewModel.allVendors.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()
    val isFa = lang == AppLanguage.FA
    val shownCategories = remember(categories) { if (categories.isEmpty()) defaultCategories() else categories }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            HomeHero(
                isDark = isDark,
                lang = lang,
                vendorCount = allVendors.size,
                favoriteCount = allVendors.count { it.isFavorite },
                categoryCount = shownCategories.size,
                onToggleTheme = { viewModel.toggleDarkMode() },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenSearch = { onOpenSearch("") }
            )
        }

        item {
            SectionTitle(
                title = if (isFa) "دسته‌بندی‌ها" else "Categories",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(shownCategories) { index, cat ->
                    CategoryChipTile(
                        category = cat,
                        index = index,
                        modifier = Modifier.staggerIn(index),
                        onClick = { onOpenSearch(cat.name) }
                    )
                }
            }
        }

        item {
            val addInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .height(54.dp)
                    .pressScale(addInteraction)
                    .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = BrandPrimary, spotColor = BrandPrimary)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandGradient)
                    .clickable(interactionSource = addInteraction, indication = LocalIndication.current) { onOpenAddVendor() }
                    .testTag("add_vendor_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFa) "ثبت وندور جدید" else Localization.get("new_vendor_btn", lang),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(title = Localization.get("latest_vendors", lang))
                Text(
                    text = if (isFa) "${PersianUtils.toPersianDigits(allVendors.size)} وندور" else "${allVendors.size} Vendors",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (allVendors.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .staggerIn(0),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(BrandContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(34.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isFa) "هنوز وندوری ثبت نشده. اولین وندور را اضافه کنید!" else "No vendors yet. Add the first one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(allVendors, key = { _, vendor -> vendor.id }) { index, vendor ->
                VendorCard(
                    vendor = vendor,
                    onClick = { onVendorClick(vendor.id) },
                    onFavoriteClick = { viewModel.toggleVendorFavorite(vendor) },
                    modifier = Modifier
                        .animateItem()
                        .staggerIn(index)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(BrandGradient)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/** Animated gradient hero: branding, quick toggles, live stats and the search entry. */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun HomeHero(
    isDark: Boolean,
    lang: AppLanguage,
    vendorCount: Int,
    favoriteCount: Int,
    categoryCount: Int,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenSearch: () -> Unit
) {
    val isFa = lang == AppLanguage.FA
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .animatedGradient(HeroColors)
    ) {
        // Decorative translucent bubbles
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = (-40).dp)
                .size(170.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 30.dp)
                .size(130.dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vendor_app_icon),
                            contentDescription = Localization.get("app_title", lang),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Localization.get("app_title", lang),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = Localization.get("app_subtitle", lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFa) "EN" else "فا",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Crossfade(targetState = isDark, label = "theme_icon") { dark ->
                            Icon(
                                imageVector = if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Theme",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroStat(Icons.Default.Store, vendorCount, if (isFa) "وندور" else "Vendors", Modifier.weight(1f))
                HeroStat(Icons.Default.Favorite, favoriteCount, if (isFa) "نشان‌شده" else "Favorites", Modifier.weight(1f))
                HeroStat(Icons.Default.Widgets, categoryCount, if (isFa) "دسته" else "Categories", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            val searchInteraction = remember { MutableInteractionSource() }
            Surface(
                onClick = onOpenSearch,
                interactionSource = searchInteraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale(searchInteraction)
                    .testTag("home_search_bar"),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isFa) "جستجوی وندور، محصول یا شماره…" else "Search vendors, products or numbers…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStat(icon: ImageVector, value: Int, label: String, modifier: Modifier = Modifier) {
    val animated by animateIntAsState(targetValue = value, animationSpec = tween(900), label = "hero_stat")
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Text(
            text = PersianUtils.toPersianDigits(animated),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.9f),
            maxLines = 1
        )
    }
}

private val tilePalette = listOf(
    BrandContainer to BrandPrimary,
    VioletContainer to BrandViolet,
    SkyContainer to BlueAccent,
    MintContainer to GreenVerified,
    AmberContainer to AmberRating,
    RoseContainer to RedFavorite
)

@Composable
private fun CategoryChipTile(
    category: Category,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (container, accent) = tilePalette[index % tilePalette.size]
    val icon = when (category.name) {
        "صنعتی" -> Icons.Default.PrecisionManufacturing
        "ساختمان و عمرانی" -> Icons.Default.Apartment
        "آهن‌آلات", "فلزات و آهن‌آلات" -> Icons.Default.Hardware
        "ماشین‌آلات" -> Icons.Default.Agriculture
        "مواد غذایی" -> Icons.Default.Restaurant
        "مواد شیمیایی" -> Icons.Default.Science
        "برق و الکترونیک" -> Icons.Default.Bolt
        else -> Icons.Default.Business
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )
    Row(
        modifier = modifier
            .graphicsScale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .animateContentSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = category.name, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = InkDeep,
            maxLines = 1
        )
    }
}

private fun Modifier.graphicsScale(scale: Float): Modifier =
    this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))

private fun defaultCategories() = listOf(
    Category(name = "صنعتی", vendorCount = 0),
    Category(name = "ساختمان و عمرانی", vendorCount = 0),
    Category(name = "آهن‌آلات", vendorCount = 0),
    Category(name = "ماشین‌آلات", vendorCount = 0),
    Category(name = "مواد شیمیایی", vendorCount = 0),
    Category(name = "برق و الکترونیک", vendorCount = 0)
)
