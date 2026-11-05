package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.ui.components.AppHeader
import com.example.ui.components.sendEmail
import com.example.ui.screens.settings.*
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel
import androidx.compose.material.icons.filled.Language
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.preferences.ThemeMode
import com.example.util.AppLanguage
import com.example.util.Localization
import com.example.util.PersianUtils

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

    // Full JSON Backup Export
    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportFullBackupJson { jsonContent ->
                try {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(jsonContent.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(context, "فایل پشتیبان کامل با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در ذخیره‌سازی پشتیبان: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Full JSON Backup Restore
    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                }
                if (!content.isNullOrBlank()) {
                    viewModel.restoreFullBackupJson(content)
                } else {
                    Toast.makeText(context, "فایل انتخاب‌شده خالی است", Toast.LENGTH_SHORT).show()
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
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TealPrimary.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsIconBadge(icon = Icons.Default.FeaturedPlayList, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "راهنما و قابلیت‌های کامل برنامه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مشاهده فهرست جامع ۱۲ قابلیت کلیدی و تجاری وندوریاب",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TealPrimary,
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
                                        Brush.linearGradient(listOf(NavyDeep, TealDark))
                                    )
                                    .border(2.dp, TealPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = TealPrimary,
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
                            SettingsIconBadge(icon = Icons.Default.DarkMode, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "پوسته و حالت تاریک",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (themeMode) {
                                        ThemeMode.SYSTEM -> "پیرو تنظیمات سیستم عامل"
                                        ThemeMode.LIGHT -> "حالت روشن تجاری"
                                        ThemeMode.DARK -> "حالت تاریک بهینه"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = themeMode == ThemeMode.SYSTEM,
                                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                                label = { Text("پیرو سیستم", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = themeMode == ThemeMode.LIGHT,
                                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                                label = { Text("روشن", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = themeMode == ThemeMode.DARK,
                                onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                                label = { Text("تاریک", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SettingsIconBadge(icon = Icons.Default.Language, tint = TealPrimary)
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

                        Button(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealPrimary,
                                contentColor = NavyDeep
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.FA) "English" else "فارسی",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
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

            // Full JSON Backup Export
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Code,
                    title = "پشتیبان‌گیری کامل (JSON)",
                    subtitle = "استخراج کامل وندورها، پرسنل و محصولات در قالب فایل JSON",
                    onClick = {
                        exportJsonLauncher.launch("vendoryab_full_backup.json")
                    }
                )
            }

            // Full JSON Backup Restore
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Restore,
                    title = "بازیابی از فایل پشتیبان (JSON)",
                    subtitle = "بازگردانی کامل کلیه اطلاعات از فایل پشتیبان JSON",
                    onClick = {
                        importJsonLauncher.launch(arrayOf("application/json", "*/*"))
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

            // Notifications
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Notifications,
                    title = "تنظیمات اعلان‌ها",
                    subtitle = "یادآوری تماس‌های پیگیری و استعلام‌ها",
                    onClick = {
                        Toast.makeText(context, "اعلان‌های برنامه فعال است", Toast.LENGTH_SHORT).show()
                    }
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
            SettingsIconBadge(icon = icon, tint = TealPrimary)
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
