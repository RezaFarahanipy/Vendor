package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.ui.components.AppHeader
import com.example.ui.components.sendEmail
import com.example.ui.screens.settings.*
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandLight
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.VendorViewModel
import androidx.compose.material.icons.filled.Language
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.preferences.ThemeMode
import com.example.util.AppLanguage
import com.example.util.Localization
import com.example.util.PersianUtils
import com.example.ui.theme.BrandViolet

@Composable
fun SettingsScreen(
    viewModel: VendorViewModel
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()
    val allActivityTypes by viewModel.allActivityTypes.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val isScreenshotBlockEnabled by viewModel.isScreenshotBlockEnabled.collectAsState()
    val isDynamicColorEnabled by viewModel.isDynamicColorEnabled.collectAsState()

    // Export CSV document launcher (UTF-8 with BOM for Excel)
    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            viewModel.exportToCsv { csvContent ->
                try {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(csvContent.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(context, "فایل اکسل با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در ذخیره‌سازی فایل: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Import CSV document launcher
    val importCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                }
                if (!content.isNullOrBlank()) {
                    viewModel.importFromCsv(content)
                } else {
                    Toast.makeText(context, "فایل انتخاب‌شده خالی است", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در خواندن فایل CSV: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Comprehensive ZIP Backup Export (includes DB records + logo files + PDF catalogs)
    val exportZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    viewModel.exportFullBackupZip(context, out) {
                        Toast.makeText(context, "فایل پشتیبان جامع (ZIP) با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در ذخیره‌سازی پشتیبان: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Comprehensive Backup Restore (supports new ZIP packages and legacy JSON files)
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    viewModel.restoreFullBackupStream(context, stream)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطا در خواندن فایل پشتیبان: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showFeaturesDialog by remember { mutableStateOf(false) }
    var showActivityTypeManageDialog by remember { mutableStateOf(false) }
    var showCategoryManageDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDatabaseInfoDialog by remember { mutableStateOf(false) }

    var activityTypeToDelete by remember { mutableStateOf<ActivityType?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(title = "تنظیمات و اطلاعات سامانه")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Full Features and Capabilities of the App
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable { showFeaturesDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsIconBadge(icon = Icons.Default.FeaturedPlayList, tint = BrandPrimary)

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (lang == AppLanguage.FA) "راهنما و قابلیت‌های برنامه" else Localization.get("features_guide", lang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BrandPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.FA) "۱۲ قابلیت" else "12 Features",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandDark,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (lang == AppLanguage.FA) "فهرست جامع امکانات تجاری، خروجی اکسل، کاتالوگ و امنیت" else "Complete guide to features, export, catalog and security",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "مشاهده",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Developer & Programmer Card (رضا فراهانی)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable {
                            sendEmail(context, "Rezafarahani2008@gmail.com", "پیام درباره اپلیکیشن وندوریاب")
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(BrandPrimary, BrandViolet))
                                    )
                                    .border(2.dp, BrandPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "رضا فراهانی",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "برنامه‌نویس و توسعه‌دهنده",
                                        tint = GreenVerified,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "طراح، برنامه‌نویس و توسعه‌دهنده ارشد",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rezafarahani2008@gmail.com",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Theme Selection Card (3-state: System, Light, Dark)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SettingsIconBadge(icon = Icons.Default.DarkMode, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "پوسته و حالت تاریک",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (themeMode == ThemeMode.DARK) "حالت تاریک فعال است" else "حالت روشن فعال است",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = themeMode == ThemeMode.LIGHT,
                                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                                label = {
                                    Text(
                                        text = "پوسته روشن",
                                        fontSize = 12.sp,
                                        fontWeight = if (themeMode == ThemeMode.LIGHT) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = themeMode == ThemeMode.DARK,
                                onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                                label = {
                                    Text(
                                        text = "پوسته تاریک",
                                        fontSize = 12.sp,
                                        fontWeight = if (themeMode == ThemeMode.DARK) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "رنگ‌بندی پویا (Material You)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "هماهنگی رنگ‌های برنامه با پس‌زمینه دستگاه",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = isDynamicColorEnabled,
                                    onCheckedChange = { viewModel.setDynamicColorEnabled(it) }
                                )
                            }
                        }
                    }
                }
            }

            // Language Selection Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            SettingsIconBadge(icon = Icons.Default.Language, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.FA) "زبان برنامه" else "App Language",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (lang == AppLanguage.FA) "فارسی (Persian) / انگلیسی (English)" else "English / Persian",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.defaultMinSize(minWidth = 76.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.FA) "English" else "فارسی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Security & Privacy Card (قفل بیومتریک و FLAG_SECURE)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SettingsIconBadge(icon = Icons.Default.Lock, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "امنیت و حریم خصوصی داده‌ها",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "محافظت از اطلاعات حساس بانکی، حقوقی و اسکرین‌شات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Biometric App Lock Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "قفل بیومتریک و پین برنامه",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "قفل خودکار برنامه پس از ۳۰ ثانیه خروج به پس‌زمینه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isAppLockEnabled,
                                onCheckedChange = { viewModel.setAppLockEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BrandPrimary,
                                    checkedTrackColor = BrandPrimary.copy(alpha = 0.4f)
                                )
                            )
                        }

                        // FLAG_SECURE Screenshot Block Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "جلوگیری از اسکرین‌شات (FLAG_SECURE)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "ممنوعیت عکس و فیلمبرداری از صفحه و مخفی‌سازی در برنامه‌های اخیر",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isScreenshotBlockEnabled,
                                onCheckedChange = { viewModel.setScreenshotBlockEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BrandPrimary,
                                    checkedTrackColor = BrandPrimary.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }

            // Manage Activity Types (نوع فعالیت: تولیدکننده، بازرگانی، واردکننده...)
            item {
                SettingsClickableCard(
                    icon = Icons.Default.BusinessCenter,
                    title = "مدیریت انواع فعالیت شرکت‌ها",
                    subtitle = "${PersianUtils.toPersianDigits(allActivityTypes.size)} نوع تعریف‌شده (افزودن، ویرایش و حذف)",
                    onClick = { showActivityTypeManageDialog = true }
                )
            }

            // Manage Categories (زمینه‌های فعالیت: صنعتی، استیل، مواد شیمیایی...)
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Category,
                    title = "مدیریت زمینه‌های فعالیت و دسته‌بندی‌ها",
                    subtitle = "${PersianUtils.toPersianDigits(allCategories.size)} دسته تعریف‌شده (افزودن، ویرایش و حذف)",
                    onClick = { showCategoryManageDialog = true }
                )
            }

            // Database & Storage Architecture Card
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Storage,
                    title = "محل ذخیره‌سازی داده‌ها (دیتابیس آفلاین)",
                    subtitle = "پایگاه داده رابطه‌ای Room Database (SQLite) + فایل CSV",
                    onClick = { showDatabaseInfoDialog = true }
                )
            }

            // Export to CSV / Excel
            item {
                SettingsClickableCard(
                    icon = Icons.Default.FileUpload,
                    title = "خروجی اکسل / CSV",
                    subtitle = "ذخیره فایل جدول اکسل استاندارد UTF-8 با BOM",
                    onClick = {
                        exportCsvLauncher.launch("vendoryab_vendors.csv")
                    }
                )
            }

            // Import from CSV / Excel
            item {
                SettingsClickableCard(
                    icon = Icons.Default.FileDownload,
                    title = "ورود اطلاعات از فایل اکسل / CSV",
                    subtitle = "انتخاب فایل و بارگذاری وندورها با رد کردن تکراری‌ها",
                    onClick = {
                        importCsvLauncher.launch(arrayOf("text/comma-separated-values", "text/csv", "application/vnd.ms-excel", "*/*"))
                    }
                )
            }

            // Full Comprehensive Backup Export (ZIP)
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Code,
                    title = "پشتیبان‌گیری کامل جامع (فایل ZIP)",
                    subtitle = "بسته‌بندی پایگاه داده به همراه تمام فایل‌های کاتالوگ PDF و لوگوها در یک فایل ZIP",
                    onClick = {
                        exportZipLauncher.launch("vendoryab_full_backup.zip")
                    }
                )
            }

            // Full Comprehensive Backup Restore (ZIP / JSON)
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Restore,
                    title = "بازیابی از فایل پشتیبان (ZIP یا JSON)",
                    subtitle = "بازگردانی کامل اطلاعات، کاتالوگ‌ها و لوگوها (پشتیبانی از ZIP و فایل‌های قدیمی JSON)",
                    onClick = {
                        importBackupLauncher.launch(arrayOf("application/zip", "application/json", "application/octet-stream", "*/*"))
                    }
                )
            }

            // Restore Sample Data
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Restore,
                    title = "بازیابی اطلاعات پیش‌فرض",
                    subtitle = "بارگذاری مجدد وندورها و نمونه‌های اولیه",
                    onClick = { showResetDialog = true }
                )
            }

            // About App
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Info,
                    title = "درباره وندوریاب",
                    subtitle = "نسخه ۱.۱.۰ | توسعه‌دهنده: رضا فراهانی",
                    onClick = { showAboutDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Full Features and Capabilities Dialog
    if (showFeaturesDialog) {
        FeaturesDialog(onDismiss = { showFeaturesDialog = false })
    }

    // Manage Activity Types Dialog (انواع فعالیت)
    if (showActivityTypeManageDialog) {
        ManageActivityTypesDialog(
            allActivityTypes = allActivityTypes,
            onAdd = { viewModel.addActivityType(it) },
            onUpdate = { viewModel.updateActivityType(it) },
            onRequestDelete = { activityTypeToDelete = it },
            onDismiss = { showActivityTypeManageDialog = false }
        )
    }

    // Manage Categories Dialog (زمینه‌های فعالیت)
    if (showCategoryManageDialog) {
        ManageCategoriesDialog(
            allCategories = allCategories,
            onAdd = { viewModel.addCategory(it) },
            onUpdate = { viewModel.updateCategory(it) },
            onRequestDelete = { categoryToDelete = it },
            onDismiss = { showCategoryManageDialog = false }
        )
    }

    // Confirmation Dialog for Activity Type Deletion
    activityTypeToDelete?.let { act ->
        ConfirmDeleteActivityTypeDialog(
            act = act,
            lang = lang,
            onConfirm = {
                viewModel.deleteActivityType(act.id)
                activityTypeToDelete = null
            },
            onDismiss = { activityTypeToDelete = null }
        )
    }

    // Confirmation Dialog for Category Deletion
    categoryToDelete?.let { cat ->
        ConfirmDeleteCategoryDialog(
            cat = cat,
            lang = lang,
            onConfirm = {
                viewModel.deleteCategory(cat.id)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }

    // Database Architecture explanation dialog
    if (showDatabaseInfoDialog) {
        DatabaseInfoDialog(onDismiss = { showDatabaseInfoDialog = false })
    }

    // Reset Dialog
    if (showResetDialog) {
        ResetSampleDataDialog(
            onConfirm = {
                showResetDialog = false
                viewModel.resetToSampleData()
            },
            onDismiss = { showResetDialog = false }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AboutAppDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
private fun SettingsClickableCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIconBadge(icon = icon, tint = BrandPrimary)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SettingsIconBadge(icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun shareCsvText(context: Context, csv: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, csv)
        type = "text/csv"
    }
    val shareIntent = Intent.createChooser(sendIntent, "ارسال و اشتراک‌گذاری داده‌های وندورها (CSV)")
    context.startActivity(shareIntent)
}
