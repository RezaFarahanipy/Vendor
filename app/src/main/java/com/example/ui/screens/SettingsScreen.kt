package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.AppHeader
import com.example.ui.components.sendEmail
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel

@Composable
fun SettingsScreen(
    viewModel: VendorViewModel
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showCsvImportDialog by remember { mutableStateOf(false) }
    var csvInputText by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDatabaseInfoDialog by remember { mutableStateOf(false) }

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

            // Database & Storage Architecture Card
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Storage,
                    title = "محل ذخیره‌سازی داده‌ها (دیتابیس آفلاین)",
                    subtitle = "پایگاه داده رابطه‌ای Room Database (SQLite) + فایل CSV",
                    onClick = { showDatabaseInfoDialog = true }
                )
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

            // Manage Categories
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Category,
                    title = "مدیریت دسته‌بندی‌ها",
                    subtitle = "افزودن و ویرایش شاخه‌های صنعتی",
                    onClick = { showCategoryDialog = true }
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
                    Text("• جدول‌های فعال: vendors (وندورها), contact_persons (پرسنل), categories (دسته‌ها), products (محصولات)")
                    Text("• ماندگاری: اطلاعات با بستن یا راه‌اندازی مجدد گوشی هرگز پاک نمی‌شوند.")
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

    // Add Category Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("افزودن دسته‌بندی جدید", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("نام دسته (مثال: تجهیزات ابزار دقیق، بازرگانی)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addCategory(newCategoryName.trim())
                            newCategoryName = ""
                            showCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = NavyDeep)
                ) {
                    Text("افزودن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("انصراف")
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
                    Text("امکانات:")
                    Text("• مدیریت و ثبت پرسنل با ارسال مستقیم به تلگرام، واتس‌اپ، بله و روبیکا")
                    Text("• جستجوی فوق‌سریع فارسی با اصلاح هوشمند ارقام و حروف")
                    Text("• پایگاه اطلاعاتی Room Database محلی")
                    Text("• مدیریت کاتالوگ و گالری محصولات")
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
