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
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel
import androidx.compose.material.icons.filled.Language
import com.example.util.AppLanguage
import com.example.util.Localization
import com.example.util.PersianUtils

@Composable
fun SettingsScreen(
    viewModel: VendorViewModel
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()
    val allActivityTypes by viewModel.allActivityTypes.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showFeaturesDialog by remember { mutableStateOf(false) }
    var showActivityTypeManageDialog by remember { mutableStateOf(false) }
    var showCategoryManageDialog by remember { mutableStateOf(false) }
    var showCsvImportDialog by remember { mutableStateOf(false) }
    var csvInputText by remember { mutableStateOf("") }
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

            // Dark Mode Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                            SettingsIconBadge(icon = Icons.Default.DarkMode, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "حالت تاریک (Dark Mode)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isDark) "فعال (کنتراست بالا و بهینه برای چشم)" else "غیرفعال (تم روشن تجاری)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.isDarkMode.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TealPrimary,
                                checkedTrackColor = TealDark
                            )
                        )
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

            // Export to CSV
            item {
                SettingsClickableCard(
                    icon = Icons.Default.FileUpload,
                    title = "خروجی اکسل / CSV",
                    subtitle = "دریافت فایل پشتیبان کامل اطلاعات وندورها",
                    onClick = {
                        viewModel.exportToCsv { csv ->
                            shareCsvText(context, csv)
                        }
                    }
                )
            }

            // Import from CSV
            item {
                SettingsClickableCard(
                    icon = Icons.Default.FileDownload,
                    title = "ورود اطلاعات از CSV",
                    subtitle = "افزودن دسته‌جمعی وندورها از فایل اکسل",
                    onClick = { showCsvImportDialog = true }
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
        AlertDialog(
            onDismissRequest = { showFeaturesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FeaturedPlayList, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قابلیت‌ها و امکانات کامل وندوریاب", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureItem(
                        number = "۱",
                        title = "مدیریت جامع وندورها و تأمین‌کنندگان",
                        desc = "ثبت، ویرایش، حذف، برچسب‌گذاری، وضعیت تأییدیه رسمی و ثبت یادداشت‌های محرمانه داخلی برای هر شرکت."
                    )
                    FeatureItem(
                        number = "۲",
                        title = "سیستم چندشماره‌ای تماس و ارتباطات",
                        desc = "پشتیبانی از چندین خط ثابت و چندین شماره همراه برای هر وندور به همراه دکمه‌های مستقیم تماس و پیامک."
                    )
                    FeatureItem(
                        number = "۳",
                        title = "ارتباط سریع با پیام‌رسان‌های مطرح",
                        desc = "امکان ارسال مستقیم پیام در واتس‌اپ، تلگرام، بله و روبیکا به شماره‌های شرکت و تک‌تک پرسنل."
                    )
                    FeatureItem(
                        number = "۴",
                        title = "مدیریت پرسنل و مدیران ارتباطی در فرم وندور",
                        desc = "ثبت و ویرایش مدیرعامل، مسئول فروش، کارشناسان فنی با نام، سمت، داخلی و شماره همراه در حین افزودن و ویرایش وندور."
                    )
                    FeatureItem(
                        number = "۵",
                        title = "انتخاب و ویرایش لوگوی شرکت",
                        desc = "انتخاب لوگوی واقعی از گالری گوشی (Photo Picker استاندارد) و ذخیره‌سازی محلی به همراه نشان‌های اختصاری شکیل."
                    )
                    FeatureItem(
                        number = "۶",
                        title = "آپلود و نمایش کاتالوگ دیجیتال (PDF)",
                        desc = "بارگذاری فایل‌های PDF کاتالوگ و بروشورها، پیش‌نمایش و باز کردن مستقیم با برنامه‌های PDFخوان و امکان اشتراک‌گذاری."
                    )
                    FeatureItem(
                        number = "۷",
                        title = "مدیریت کامل محصولات و تجهیزات وندور",
                        desc = "امکان افزودن، ویرایش و حذف تک‌تک محصولات صنعتی، مدل‌ها، کدهای فنی و مشخصات هیدرولیکی/مکانیکی."
                    )
                    FeatureItem(
                        number = "۸",
                        title = "موقعیت‌یابی تعاملی روی نقشه (GPS)",
                        desc = "نقشه لمسی تعاملی با قابلیت جابجایی پین، ورودی دستی طول و عرض جغرافیایی، انتخاب شهرک‌های صنعتی ایران و اتصال به نشان و بلد."
                    )
                    FeatureItem(
                        number = "۹",
                        title = "اطلاعات مالی، بانکی و ثبتی",
                        desc = "امکان ثبت چندین حساب بانکی با نام بانک، شعبه، شماره حساب و شماره شبا با دکمه کپی، به همراه کد اقتصادی، شناسه ملی و شماره ثبت."
                    )
                    FeatureItem(
                        number = "۱۰",
                        title = "مدیریت پویای انواع فعالیت و دسته‌بندی‌ها",
                        desc = "امکان تعریف، ویرایش و حذف انواع فعالیت (تولیدی، بازرگانی و...) و زمینه‌های صنعتی مستقیماً از بخش تنظیمات."
                    )
                    FeatureItem(
                        number = "۱۱",
                        title = "موتور جستجوی بلادرنگ و فیلترهای پیشرفته",
                        desc = "جستجوی هوشمند با اصلاح ی/ک عربی و نیم‌فاصله، فیلتر شهر، تأییدیه، کاتالوگ و مرتب‌سازی بر اساس امتیاز و جدیدترین‌ها."
                    )
                    FeatureItem(
                        number = "۱۲",
                        title = "دیتابیس بومی ۱۰۰٪ آفلاین (Room Database)",
                        desc = "ذخیره‌سازی کامل بر روی حافظه گوشی بدون نیاز به اینترنت، با پشتیبانی کامل از خروجی و ورودی فایل اکسل و CSV."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFeaturesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("بستن")
                }
            }
        )
    }

    // Manage Activity Types Dialog (انواع فعالیت)
    if (showActivityTypeManageDialog) {
        var newActName by remember { mutableStateOf("") }
        var editingActType by remember { mutableStateOf<ActivityType?>(null) }
        var editActName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showActivityTypeManageDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مدیریت انواع فعالیت شرکت‌ها", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "انواع فعالیت قابل انتخاب در فرم ثبت وندور:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Input to add new activity type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newActName,
                            onValueChange = { newActName = it },
                            placeholder = { Text("نوع جدید (مثال: بازرگانی)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newActName.isNotBlank()) {
                                    viewModel.addActivityType(newActName)
                                    newActName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                        ) {
                            Text("افزودن")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    allActivityTypes.forEach { act ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (editingActType?.id == act.id) {
                                OutlinedTextField(
                                    value = editActName,
                                    onValueChange = { editActName = it },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    if (editActName.isNotBlank()) {
                                        viewModel.updateActivityType(act.copy(name = editActName.trim()))
                                        editingActType = null
                                    }
                                }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "تأیید", tint = TealPrimary)
                                }
                            } else {
                                Text(
                                    text = act.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        editingActType = act
                                        editActName = act.name
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = TealPrimary, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { activityTypeToDelete = act },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActivityTypeManageDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }

    // Manage Categories Dialog (زمینه‌های فعالیت)
    if (showCategoryManageDialog) {
        var newCatName by remember { mutableStateOf("") }
        var editingCategory by remember { mutableStateOf<Category?>(null) }
        var editCatName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCategoryManageDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مدیریت زمینه‌های فعالیت و دسته‌ها", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "دسته‌بندی‌های صنعتی فعال در برنامه:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Add category input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newCatName,
                            onValueChange = { newCatName = it },
                            placeholder = { Text("نام زمینه (مثال: ابزار دقیق)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newCatName.isNotBlank()) {
                                    viewModel.addCategory(newCatName)
                                    newCatName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                        ) {
                            Text("افزودن")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    allCategories.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (editingCategory?.id == cat.id) {
                                OutlinedTextField(
                                    value = editCatName,
                                    onValueChange = { editCatName = it },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    if (editCatName.isNotBlank()) {
                                        viewModel.updateCategory(cat.copy(name = editCatName.trim()))
                                        editingCategory = null
                                    }
                                }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "تأیید", tint = TealPrimary)
                                }
                            } else {
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        editingCategory = cat
                                        editCatName = cat.name
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = TealPrimary, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { categoryToDelete = cat },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryManageDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }

    // Confirmation Dialog for Activity Type Deletion
    if (activityTypeToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityTypeToDelete = null },
            title = {
                Text(
                    text = if (lang == AppLanguage.FA) "تأیید حذف نوع فعالیت" else "Confirm Delete Activity Type",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (lang == AppLanguage.FA) {
                        "آیا از حذف نوع فعالیت «${activityTypeToDelete!!.name}» اطمینان دارید؟ این عمل قابل بازگشت نیست."
                    } else {
                        "Are you sure you want to delete '${activityTypeToDelete!!.name}'? This action cannot be undone."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteActivityType(activityTypeToDelete!!.id)
                        activityTypeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedFavorite)
                ) {
                    Text(if (lang == AppLanguage.FA) "حذف" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activityTypeToDelete = null }) {
                    Text(if (lang == AppLanguage.FA) "انصراف" else "Cancel")
                }
            }
        )
    }

    // Confirmation Dialog for Category Deletion
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text(
                    text = if (lang == AppLanguage.FA) "تأیید حذف زمینه فعالیت" else "Confirm Delete Category",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (lang == AppLanguage.FA) {
                        "آیا از حذف زمینه فعالیت «${categoryToDelete!!.name}» اطمینان دارید؟ این عمل قابل بازگشت نیست."
                    } else {
                        "Are you sure you want to delete '${categoryToDelete!!.name}'? This action cannot be undone."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(categoryToDelete!!.id)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedFavorite)
                ) {
                    Text(if (lang == AppLanguage.FA) "حذف" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text(if (lang == AppLanguage.FA) "انصراف" else "Cancel")
                }
            }
        )
    }

    // Database Architecture explanation dialog
    if (showDatabaseInfoDialog) {
        AlertDialog(
            onDismissRequest = { showDatabaseInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("محل و نحوه ذخیره‌سازی داده‌ها", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "اطلاعات به صورت کاملاً پایدار و آفلاین در دیتابیس بومی زیر ذخیره می‌شوند:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• دیتابیس محلی: Room Database مبتنی بر SQLite")
                    Text("• جدول‌های فعال: vendors (وندورها), contact_persons (پرسنل), categories (دسته‌ها), products (محصولات), activity_types (انواع فعالیت)")
                    Text("• ماندگاری: اطلاعات با بستن یا راه‌اندازی مجدد گوشی هرگز پاک نمی‌شوند.")
                    Text("• فایل‌های کاتالوگ و لوگوها: در پوشه اختصاصی امن برنامه (filesDir) به شکل آفلاین نگهداری می‌شوند.")
                    Text("• پشتیبان‌گیری: امکان استخراج فایل استاندارد CSV / Excel و ورود مجدد آن در هر زمان فراهم است.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDatabaseInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("متوجه شدم")
                }
            }
        )
    }

    // CSV Import Dialog
    if (showCsvImportDialog) {
        AlertDialog(
            onDismissRequest = { showCsvImportDialog = false },
            title = { Text("ورود اطلاعات وندورها از CSV", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "سطرهای CSV را در کادر زیر وارد کنید:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = csvInputText,
                        onValueChange = { csvInputText = it },
                        placeholder = { Text("نام شرکت,مدیرعامل,تلفن,موبایل,ایمیل,وبسایت,استان,شهر,آدرس,نوع فعالیت...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (csvInputText.isNotBlank()) {
                            viewModel.importFromCsv(csvInputText)
                            csvInputText = ""
                            showCsvImportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("وارد کردن داده‌ها")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCsvImportDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("بازیابی اطلاعات نمونه", fontWeight = FontWeight.Bold) },
            text = { Text("آیا مایلید اطلاعات اولیه و نمونه‌های آزمایشی مجدداً در دیتابیس بارگذاری شوند؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetToSampleData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("تأیید و بازیابی")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("سامانه وندوریاب (VendorYab)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("نسخه: ۱.۱.۰")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("برنامه‌نویس و توسعه‌دهنده:")
                    Text("رضا فراهانی", fontWeight = FontWeight.Bold)
                    Text("ایمیل: Rezafarahani2008@gmail.com")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("امکانات برجسته:")
                    Text("• سیستم چندشماره‌ای تماس و ارتباط در واتس‌اپ، تلگرام، بله و روبیکا")
                    Text("• نقشه تعاملی با موقعیت‌یاب لمسی GPS")
                    Text("• آپلود و نمایش کاتالوگ دیجیتال PDF")
                    Text("• ثبت شماره شبا، بانک، شعبه و کدهای ثبتی")
                    Text("• مدیریت و ویرایش محصولات و پرسنل وندور")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("بستن")
                }
            }
        )
    }
}

@Composable
private fun FeatureItem(number: String, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = PersianUtils.toPersianDigits(number),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
