package com.example.ui.screens.vendor_form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ActivityType
import com.example.data.model.Category
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandPrimary
import java.io.File
import com.example.ui.theme.BrandViolet

/**
 * Step 1: Basic Information, Activity Type & Logo
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step1BaseInfo(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String? = null,
    logoUri: String,
    onLogoUriChange: (String) -> Unit,
    onPickLogo: () -> Unit,
    activityType: String,
    onActivityTypeChange: (String) -> Unit,
    allActivityTypes: List<ActivityType>,
    selectedCategories: Set<String>,
    onSelectedCategoriesChange: (Set<String>) -> Unit,
    allCategories: List<Category>,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Text(
        text = "اطلاعات پایه، نشان تجاری و لوگو",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Logo Card with Photo Picker & Badges
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            // Logo preview box
            val hasLogo = logoUri.isNotBlank() && File(logoUri).exists()
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (hasLogo) Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        else Modifier.background(Brush.linearGradient(listOf(BrandPrimary, BrandViolet)))
                    )
                    .border(
                        1.5.dp,
                        if (hasLogo) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) else BrandPrimary,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (hasLogo) {
                    AsyncImage(
                        model = File(logoUri),
                        contentDescription = "لوگوی وندور",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = if (name.isNotBlank()) name.take(2) else "لوگو",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "لوگو و نشان تجاری شرکت",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "امکان انتخاب تصویر از گالری یا نماد اختصاصی",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPickLogo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (logoUri.isNotBlank()) "تغییر لوگو" else "انتخاب لوگو", fontSize = 11.sp)
                    }

                    if (logoUri.isNotBlank()) {
                        OutlinedButton(
                            onClick = { onLogoUriChange("") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("حذف لوگو", fontSize = 11.sp, color = RedFavorite)
                        }
                    }
                }
            }
        }
    }

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("نام شرکت یا وندور *") },
        placeholder = { Text("مثال: بازرگانی پارس تجهیز") },
        isError = nameError != null,
        supportingText = if (nameError != null) { { Text(nameError, color = MaterialTheme.colorScheme.error) } } else null,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vendor_name_input")
    )

    // Dynamic Activity Types from Room Database
    Text(
        text = "نوع فعالیت شرکت (قابل انتخاب و مدیریت):",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val currentTypesList = if (allActivityTypes.isNotEmpty()) {
            allActivityTypes.map { it.name }
        } else {
            listOf("تولیدکننده", "واردکننده", "بازرگانی", "توزیع‌کننده مستقیم", "خدمات فنی و مهندسی", "پیمانکار صنعتی")
        }

        currentTypesList.forEach { opt ->
            val isSelected = activityType == opt
            FilterChip(
                selected = isSelected,
                onClick = { onActivityTypeChange(opt) },
                label = { Text(opt, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }

    // Dynamic Categories from Room Database
    Text(
        text = "زمینه‌های فعالیت و شاخه‌ها:",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val currentCatNames = if (allCategories.isNotEmpty()) {
            allCategories.map { it.name }
        } else {
            listOf("صنعتی", "ساختمان و عمرانی", "ماشین‌آلات", "مواد شیمیایی", "برق و الکترونیک", "پمپ", "استیل", "بازرگانی")
        }

        currentCatNames.forEach { cat ->
            val isSelected = selectedCategories.contains(cat)
            FilterChip(
                selected = isSelected,
                onClick = {
                    onSelectedCategoriesChange(
                        if (isSelected) selectedCategories - cat else selectedCategories + cat
                    )
                },
                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("توضیحات و زمینه تخصصی") },
        placeholder = { Text("معرفی خدمات، استانداردهای بین‌المللی، برندهای قابل تأمین...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2
    )
}
