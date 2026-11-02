package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
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
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.AppScreen
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(title = "تنظیمات و حساب کاربری")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // User Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(NavyDeep, TealDark))
                                )
                                .border(2.dp, TealPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ع‌ر",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "علی رضایی",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مدیر خرید و تأمین کالا",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "شرکت پترو پالایش آریا",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Left arrow in RTL
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dark Mode Toggle
            item {
                SettingsActionCard {
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
                            Text(
                                text = "حالت تاریک (Dark Mode)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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

            // Add / Manage Categories
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
                    subtitle = "افزودن دسته‌جمعی وندورها از اکسل",
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

            // Notification Settings
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Notifications,
                    title = "تنظیمات اعلان‌ها",
                    subtitle = "یادآوری آخرین تماس‌ها و استعلام‌های باز",
                    onClick = {
                        Toast.makeText(context, "تنظیمات اعلان‌ها فعال است", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // About App
            item {
                SettingsClickableCard(
                    icon = Icons.Default.Info,
                    title = "درباره وندورلیست",
                    subtitle = "نسخه ۱.۰ | طراحی شده با متریال ۳",
                    onClick = { showAboutDialog = true }
                )
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.currentScreen.value = AppScreen.ONBOARDING
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "خروج",
                            tint = RedFavorite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خروج از حساب کاربری",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RedFavorite
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
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
                    placeholder = { Text("نام دسته (مثال: ابزار دقیق، عایق‌ها)") },
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
                        text = "متن CSV را با فرمت نام شرکت,مدیرعامل,تلفن,موبایل,ایمیل,وبسایت,استان,شهر,آدرس,نوع فعالیت در این کادر جای‌گذاری کنید:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = csvInputText,
                        onValueChange = { csvInputText = it },
                        placeholder = { Text("سطرهای CSV را اینجا paste کنید...") },
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
            text = { Text("آیا می‌خواهید اطلاعات اولیه تأمین‌کنندگان نمونه و کاتالوگ‌ها مجدداً بارگذاری شوند؟") },
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
            title = { Text("اپلیکیشن وندورلیست (VendorList)", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("نسخه: ۱.۰.۰ (تولید نهایی)")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("سامانه بومی مدیریت وندورها و ارتباط مستقیم با تأمین‌کنندگان صنعتی.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("ویژگی‌ها:")
                    Text("• جستجوی پیشرفته فارسی با یکسان‌سازی حروف و ارقام")
                    Text("• بانک اطلاعاتی محلی آفلاین با معماری Room")
                    Text("• کاتالوگ محصولات با مشخصات فنی کامل")
                    Text("• خروجی و ورودی فایل استاندارد اکسل / CSV")
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
private fun SettingsActionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
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
            .clickable { onClick() },
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Left arrow for RTL
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
