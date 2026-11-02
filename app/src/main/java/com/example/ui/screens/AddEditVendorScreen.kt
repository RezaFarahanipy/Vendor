package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vendor
import com.example.ui.components.AppHeader
import com.example.ui.theme.AmberRating
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.PersianUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditVendorScreen(
    viewModel: VendorViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val editingVendor by viewModel.editingVendor.collectAsState()
    val isEdit = editingVendor != null

    var currentStep by remember { mutableIntStateOf(1) }

    // Form fields
    var name by remember(editingVendor) { mutableStateOf(editingVendor?.name ?: "") }
    var ceoName by remember(editingVendor) { mutableStateOf(editingVendor?.ceoName ?: "") }
    var activityType by remember(editingVendor) { mutableStateOf(editingVendor?.activityType ?: "تولیدکننده") }
    var description by remember(editingVendor) { mutableStateOf(editingVendor?.description ?: "") }

    var selectedCategories by remember(editingVendor) {
        mutableStateOf(
            editingVendor?.categories?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet()
                ?: setOf("صنعتی", "استیل")
        )
    }
    var tagsText by remember(editingVendor) { mutableStateOf(editingVendor?.tags ?: "تأمین مستقیم, گارانتی معتبر") }

    var phone by remember(editingVendor) { mutableStateOf(editingVendor?.phone ?: "") }
    var mobile by remember(editingVendor) { mutableStateOf(editingVendor?.mobile ?: "") }
    var email by remember(editingVendor) { mutableStateOf(editingVendor?.email ?: "") }
    var website by remember(editingVendor) { mutableStateOf(editingVendor?.website ?: "") }
    var province by remember(editingVendor) { mutableStateOf(editingVendor?.province ?: "تهران") }
    var city by remember(editingVendor) { mutableStateOf(editingVendor?.city ?: "تهران") }
    var address by remember(editingVendor) { mutableStateOf(editingVendor?.address ?: "") }

    var catalogTitle by remember(editingVendor) {
        mutableStateOf(editingVendor?.catalogTitle ?: "کاتالوگ جامع محصولات و مشخصات فنی")
    }
    var notes by remember(editingVendor) { mutableStateOf(editingVendor?.notes ?: "") }
    var isVerified by remember(editingVendor) { mutableStateOf(editingVendor?.isVerified ?: true) }
    var rating by remember(editingVendor) { mutableFloatStateOf(editingVendor?.rating ?: 4.5f) }

    val activityOptions = listOf("تولیدکننده", "واردکننده", "توزیع‌کننده مستقیم", "خدمات مهندسی", "پیمانکار صنعتی")
    val availableCategories = listOf(
        "صنعتی", "ساختمان و عمرانی", "ماشین‌آلات", "مواد غذایی",
        "مواد شیمیایی", "برق و الکترونیک", "پمپ", "استیل", "شیرآلات", "ابزار دقیق", "مواد اولیه"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = if (isEdit) "ویرایش وندور" else "افزودن وندور جدید",
            subtitle = if (isEdit) name else "ثبت در پایگاه اطلاعاتی آفلاین",
            onBack = onBack
        )

        // Stepper Wizard Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepBadge(step = 1, currentStep = currentStep, label = "اطلاعات پایه")
                StepDivider(isPassed = currentStep > 1)
                StepBadge(step = 2, currentStep = currentStep, label = "زمینه فعالیت")
                StepDivider(isPassed = currentStep > 2)
                StepBadge(step = 3, currentStep = currentStep, label = "تماس و آدرس")
                StepDivider(isPassed = currentStep > 3)
                StepBadge(step = 4, currentStep = currentStep, label = "کاتالوگ")
            }
        }

        // Form Step Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (currentStep) {
                1 -> {
                    // Step 1: Base Info
                    Text(
                        text = "اطلاعات اولیه شرکت",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام شرکت یا واحد صنعتی *") },
                        placeholder = { Text("مثال: پارس پمپ") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vendor_name_input")
                    )

                    OutlinedTextField(
                        value = ceoName,
                        onValueChange = { ceoName = it },
                        label = { Text("نام مدیرعامل یا مسئول فروش") },
                        placeholder = { Text("مثال: مهندس رضایی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "نوع فعالیت شرکت:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activityOptions.forEach { opt ->
                            val isSelected = activityType == opt
                            FilterChip(
                                selected = isSelected,
                                onClick = { activityType = opt },
                                label = { Text(opt, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = NavyDeep
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات و خلاصه فعالیت شرکت") },
                        placeholder = { Text("زمینه تخصصی، استانداردهای تولید، ظرفیت تأمین...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                2 -> {
                    // Step 2: Categories and Tags
                    Text(
                        text = "دسته‌بندی‌ها و تخصص‌ها",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "شاخه‌های فعالیت مرتبط با این وندور را انتخاب کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableCategories.forEach { cat ->
                            val isSelected = selectedCategories.contains(cat)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategories = if (isSelected) {
                                        selectedCategories - cat
                                    } else {
                                        selectedCategories + cat
                                    }
                                },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = NavyDeep
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimary,
                                    selectedLabelColor = NavyDeep
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tagsText,
                        onValueChange = { tagsText = it },
                        label = { Text("برچسب‌ها و کلمات کلیدی (با کاما جدا کنید)") },
                        placeholder = { Text("پمپ استیل، گارانتی ۲ ساله، شیر کشویی...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                3 -> {
                    // Step 3: Contact & Address
                    Text(
                        text = "اطلاعات تماس و نشانی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("شماره تلفن ثابت") },
                        placeholder = { Text("021-77654321") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("شماره همراه") },
                        placeholder = { Text("0912-3456789") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("پست الکترونیک") },
                        placeholder = { Text("info@company.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text("وبسایت رسمی") },
                        placeholder = { Text("www.company.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = province,
                            onValueChange = { province = it },
                            label = { Text("استان") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("شهر") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("آدرس کامل کارخانه / دفتر مرکزی") },
                        placeholder = { Text("شهرک صنعتی، خیابان صنعت، پلاک...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                4 -> {
                    // Step 4: Catalog & Rating
                    Text(
                        text = "کاتالوگ و ارزیابی",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = catalogTitle,
                        onValueChange = { catalogTitle = it },
                        label = { Text("عنوان فایل کاتالوگ") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("یادداشت‌های داخلی و محرمانه") },
                        placeholder = { Text("توافقات مالی، سابقه همکاری، شرایط ارسال بار...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // Verified Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "وضعیت تأیید وندور",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "نمایش نشان سبز وندور معتبر",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isVerified,
                                onCheckedChange = { isVerified = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                            )
                        }
                    }

                    // Rating Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "امتیاز و ارزیابی وندور:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AmberRating,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = PersianUtils.toPersianDigits(String.format("%.1f", rating)),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Slider(
                            value = rating,
                            onValueChange = { rating = it },
                            valueRange = 1f..5f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = TealPrimary,
                                activeTrackColor = TealDark
                            )
                        )
                    }
                }
            }
        }

        // Navigation Buttons (Next / Prev / Submit)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward, // Right arrow for back in RTL
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مرحله قبل")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (currentStep < 4) {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "لطفاً نام شرکت را وارد کنید", Toast.LENGTH_SHORT).show()
                            } else {
                                currentStep++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = NavyDeep
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("next_step_button")
                    ) {
                        Text("مرحله بعد", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Left arrow for forward in RTL
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "لطفاً نام شرکت را وارد کنید", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val vendorToSave = Vendor(
                                id = editingVendor?.id ?: 0L,
                                name = name.trim(),
                                ceoName = ceoName.trim(),
                                description = description.trim(),
                                activityType = activityType,
                                categories = selectedCategories.joinToString(", "),
                                tags = tagsText.trim(),
                                phone = phone.trim(),
                                mobile = mobile.trim(),
                                email = email.trim(),
                                website = website.trim(),
                                province = province.trim(),
                                city = city.trim(),
                                address = address.trim(),
                                catalogTitle = catalogTitle.trim(),
                                notes = notes.trim(),
                                isVerified = isVerified,
                                rating = rating,
                                isFavorite = editingVendor?.isFavorite ?: false,
                                createdAt = editingVendor?.createdAt ?: System.currentTimeMillis()
                            )

                            viewModel.saveVendor(vendorToSave) {
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary,
                            contentColor = NavyDeep
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_vendor_button")
                    ) {
                        Text(
                            text = if (isEdit) "ذخیره تغییرات" else "ثبت نهایی وندور",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepBadge(step: Int, currentStep: Int, label: String) {
    val isDone = currentStep > step
    val isCurrent = currentStep == step

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCurrent -> TealPrimary
                        isDone -> TealDark
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = PersianUtils.toPersianDigits(step),
                    color = if (isCurrent) NavyDeep else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun StepDivider(isPassed: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .background(if (isPassed) TealDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}
