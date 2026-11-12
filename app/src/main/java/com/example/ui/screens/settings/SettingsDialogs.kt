package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandPrimary
import com.example.util.AppLanguage
import com.example.util.PersianUtils

@Composable
fun FeaturesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FeaturedPlayList, contentDescription = null, tint = BrandPrimary)
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
                FeatureItem("۱", "مدیریت جامع وندورها و تأمین‌کنندگان", "ثبت، ویرایش، حذف، برچسب‌گذاری، وضعیت تأییدیه رسمی و ثبت یادداشت‌های محرمانه داخلی برای هر شرکت.")
                FeatureItem("۲", "سیستم چندشماره‌ای تماس و ارتباطات", "پشتیبانی از چندین خط ثابت و چندین شماره همراه برای هر وندور به همراه دکمه‌های مستقیم تماس و پیامک.")
                FeatureItem("۳", "ارتباط سریع با پیام‌رسان‌های مطرح", "امکان ارسال مستقیم پیام در واتس‌اپ، تلگرام، بله و روبیکا به شماره‌های شرکت و تک‌تک پرسنل.")
                FeatureItem("۴", "مدیریت پرسنل و مدیران ارتباطی در فرم وندور", "ثبت و ویرایش مدیرعامل، مسئول فروش، کارشناسان فنی با نام، سمت، داخلی و شماره همراه در حین افزودن و ویرایش وندور.")
                FeatureItem("۵", "انتخاب و ویرایش لوگوی شرکت", "انتخاب لوگوی واقعی از گالری گوشی (Photo Picker استاندارد) و ذخیره‌سازی محلی به همراه نشان‌های اختصاری شکیل.")
                FeatureItem("۶", "آپلود و نمایش کاتالوگ دیجیتال (PDF)", "بارگذاری فایل‌های PDF کاتالوگ و بروشورها، پیش‌نمایش و باز کردن مستقیم با برنامه‌های PDFخوان و امکان اشتراک‌گذاری.")
                FeatureItem("۷", "مدیریت کامل محصولات و تجهیزات وندور", "امکان افزودن، ویرایش و حذف تک‌تک محصولات صنعتی، مدل‌ها، کدهای فنی و مشخصات هیدرولیکی/مکانیکی.")
                FeatureItem("۸", "موقعیت‌یابی تعاملی روی نقشه (GPS)", "نقشه لمسی تعاملی با قابلیت جابجایی پین، ورودی دستی طول و عرض جغرافیایی، انتخاب شهرک‌های صنعتی ایران و اتصال به نشان و بلد.")
                FeatureItem("۹", "اطلاعات مالی، بانکی و ثبتی", "امکان ثبت چندین حساب بانکی با نام بانک، شعبه، شماره حساب و شماره شبا با دکمه کپی، به همراه کد اقتصادی، شناسه ملی و شماره ثبت.")
                FeatureItem("۱۰", "مدیریت پویای انواع فعالیت و دسته‌بندی‌ها", "امکان تعریف، ویرایش و حذف انواع فعالیت (تولیدی، بازرگانی و...) و زمینه‌های صنعتی مستقیماً از بخش تنظیمات.")
                FeatureItem("۱۱", "موتور جستجوی بلادرنگ و فیلترهای پیشرفته", "جستجوی هوشمند با اصلاح ی/ک عربی و نیم‌فاصله، فیلتر شهر، تأییدیه، کاتالوگ و مرتب‌سازی بر اساس امتیاز و جدیدترین‌ها.")
                FeatureItem("۱۲", "دیتابیس بومی ۱۰۰٪ آفلاین (Room Database)", "ذخیره‌سازی کامل بر روی حافظه گوشی بدون نیاز به اینترنت، با پشتیبانی کامل از خروجی و ورودی فایل اکسل و CSV.")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
            ) {
                Text("بستن")
            }
        }
    )
}

@Composable
fun ManageActivityTypesDialog(
    allActivityTypes: List<ActivityType>,
    onAdd: (String) -> Unit,
    onUpdate: (ActivityType) -> Unit,
    onRequestDelete: (ActivityType) -> Unit,
    onDismiss: () -> Unit
) {
    var newActName by remember { mutableStateOf("") }
    var editingActType by remember { mutableStateOf<ActivityType?>(null) }
    var editActName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = BrandPrimary)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                onAdd(newActName.trim())
                                newActName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                    ) {
                        Text("افزودن", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                allActivityTypes.forEach { act ->
                    if (editingActType?.id == act.id) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = editActName,
                                onValueChange = { editActName = it },
                                label = { Text("ویرایش عنوان فعالیت") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { editingActType = null }) {
                                    Text("انصراف")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        if (editActName.isNotBlank()) {
                                            onUpdate(act.copy(name = editActName.trim()))
                                            editingActType = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                                ) {
                                    Text("ذخیره", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onRequestDelete(act) },
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
            TextButton(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}

@Composable
fun ManageCategoriesDialog(
    allCategories: List<Category>,
    onAdd: (String) -> Unit,
    onUpdate: (Category) -> Unit,
    onRequestDelete: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    var newCatName by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var editCatName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, contentDescription = null, tint = BrandPrimary)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                onAdd(newCatName.trim())
                                newCatName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                    ) {
                        Text("افزودن", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                allCategories.forEach { cat ->
                    if (editingCategory?.id == cat.id) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = editCatName,
                                onValueChange = { editCatName = it },
                                label = { Text("ویرایش عنوان زمینه فعالیت") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { editingCategory = null }) {
                                    Text("انصراف")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        if (editCatName.isNotBlank()) {
                                            onUpdate(cat.copy(name = editCatName.trim()))
                                            editingCategory = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
                                ) {
                                    Text("ذخیره", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onRequestDelete(cat) },
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
            TextButton(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}

@Composable
fun ConfirmDeleteActivityTypeDialog(
    act: ActivityType,
    lang: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.FA) "تأیید حذف نوع فعالیت" else "Confirm Delete Activity Type",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (lang == AppLanguage.FA) {
                    "آیا از حذف نوع فعالیت «${act.name}» اطمینان دارید؟ این عمل قابل بازگشت نیست."
                } else {
                    "Are you sure you want to delete '${act.name}'? This action cannot be undone."
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = RedFavorite)
            ) {
                Text(if (lang == AppLanguage.FA) "حذف" else "Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.FA) "انصراف" else "Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteCategoryDialog(
    cat: Category,
    lang: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.FA) "تأیید حذف زمینه فعالیت" else "Confirm Delete Category",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (lang == AppLanguage.FA) {
                    "آیا از حذف زمینه فعالیت «${cat.name}» اطمینان دارید؟ این عمل قابل بازگشت نیست."
                } else {
                    "Are you sure you want to delete '${cat.name}'? This action cannot be undone."
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = RedFavorite)
            ) {
                Text(if (lang == AppLanguage.FA) "حذف" else "Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.FA) "انصراف" else "Cancel")
            }
        }
    )
}

@Composable
fun DatabaseInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = BrandPrimary)
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
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
            ) {
                Text("متوجه شدم")
            }
        }
    )
}

@Composable
fun ResetSampleDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("بازیابی اطلاعات نمونه", fontWeight = FontWeight.Bold) },
        text = { Text("آیا مایلید اطلاعات اولیه و نمونه‌های آزمایشی مجدداً در دیتابیس بارگذاری شوند؟") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
            ) {
                Text("تأیید و بازیابی")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun AboutAppDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
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
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)
            ) {
                Text("بستن")
            }
        }
    )
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
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = PersianUtils.toPersianDigits(number),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
