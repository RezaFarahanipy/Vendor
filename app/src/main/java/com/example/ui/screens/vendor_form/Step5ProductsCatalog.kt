package com.example.ui.screens.vendor_form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.AmberRating
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils
import java.util.Locale

/**
 * Step 5: Products, Catalog attachment, Notes, Verification & Rating
 */
@Composable
fun Step5ProductsCatalog(
    vendorProductsList: SnapshotStateList<Product>,
    onAddProductClick: () -> Unit,
    onEditProductClick: (index: Int, product: Product) -> Unit,
    catalogTitle: String,
    onCatalogTitleChange: (String) -> Unit,
    catalogLocalPath: String,
    onUploadPdfClick: () -> Unit,
    onTestCatalogClick: () -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    isVerified: Boolean,
    onVerifiedChange: (Boolean) -> Unit,
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    Text(
        text = "محصولات، بارگذاری کاتالوگ و ارزیابی",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Products Management Section
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "محصولات و تجهیزات وندور (${PersianUtils.toPersianDigits(vendorProductsList.size)} محصول)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onAddProductClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary,
                contentColor = NavyDeep
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("افزودن محصول", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (vendorProductsList.isEmpty()) {
        Text(
            text = "هنوز محصولی ثبت نشده است. برای این وندور می‌توانید محصولات و کاتالوگ فنی ثبت کنید.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        vendorProductsList.forEachIndexed { index, prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prod.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${prod.brand} • مدل: ${prod.model.ifBlank { "-" }} • ${prod.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (prod.specifications.isNotBlank()) {
                            Text(
                                text = prod.specifications,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = { onEditProductClick(index, prod) }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = TealPrimary, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { vendorProductsList.removeAt(index) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = RedFavorite, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Catalog Upload & Management Card
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RedFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "فایل کاتالوگ و بروشور فنی (PDF)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (catalogLocalPath.isNotBlank()) "فایل بارگذاری شده آماده نمایش است" else "امکان آپلود مستقیم فایل PDF",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (catalogLocalPath.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = catalogTitle,
                onValueChange = onCatalogTitleChange,
                label = { Text("عنوان نمایشی کاتالوگ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUploadPdfClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = NavyDeep
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (catalogLocalPath.isNotBlank()) "تغییر فایل PDF" else "بارگذاری فایل PDF", fontSize = 11.sp)
                }

                Button(
                    onClick = onTestCatalogClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تست و نمایش کاتالوگ", fontSize = 11.sp)
                }
            }
        }
    }

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("یادداشت‌های داخلی و محرمانه") },
        placeholder = { Text("شرایط پرداخت، سابقه همکاری، نحوه ارسال...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2
    )

    // Verified toggle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "تأییدیه رسمی وندور (نمایش نشان سبز اعتبار)",
            style = MaterialTheme.typography.titleSmall
        )
        Switch(
            checked = isVerified,
            onCheckedChange = onVerifiedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
        )
    }

    // Rating
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("امتیاز و ارزیابی کیفیت:", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = AmberRating, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            if (rating > 0f) {
                Text(PersianUtils.toPersianDigits(String.format(Locale.US, "%.1f", rating)), fontWeight = FontWeight.Bold)
            } else {
                Text("بدون امتیاز", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    Slider(
        value = rating,
        onValueChange = onRatingChange,
        valueRange = 0f..5f,
        steps = 9,
        colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealDark)
    )
}
