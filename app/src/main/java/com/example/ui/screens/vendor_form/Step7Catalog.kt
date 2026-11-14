package com.example.ui.screens.vendor_form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CatalogItem
import com.example.ui.theme.BrandContainer
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.RedFavorite
import com.example.util.PersianUtils
import com.example.util.tf
import com.example.util.t

/**
 * Step 7: Catalog files. A vendor can have several catalogs / brochures (PDF).
 */
@Composable
fun Step7Catalog(
    catalogs: List<CatalogItem>,
    onTitleChange: (Int, String) -> Unit,
    onRemove: (Int) -> Unit,
    onView: (CatalogItem) -> Unit,
    onUploadPdfClick: () -> Unit
) {
    Text(
        text = t("کاتالوگ دیجیتال و اسناد فنی"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = t("کاتالوگ‌ها و بروشورهای فنی (PDF)"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (catalogs.isNotEmpty()) {
                    Text(
                        text = tf("{0} فایل", PersianUtils.toPersianDigits(catalogs.size)),
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = t("می‌توانید چند فایل PDF (کاتالوگ رسمی، مشخصات فنی، بروشور محصولات) برای این وندور ضمیمه کنید تا در صفحه وندور قابل نمایش و دانلود باشند."),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            catalogs.forEachIndexed { index, item ->
                key(item.path) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandContainer.copy(alpha = 0.55f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = RedFavorite, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = item.title,
                                onValueChange = { onTitleChange(index, it) },
                                label = { Text(t("عنوان کاتالوگ")) },
                                supportingText = { Text(item.fileName, maxLines = 1) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onView(item) }) {
                                Icon(Icons.Default.Visibility, contentDescription = t("مشاهده فایل"), tint = BrandPrimary)
                            }
                            IconButton(onClick = { onRemove(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = t("حذف فایل"), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onUploadPdfClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                if (catalogs.isEmpty()) {
                    Text(text = t("انتخاب و بارگذاری فایل‌های PDF"), fontWeight = FontWeight.Bold)
                } else {
                    Text(text = t("افزودن فایل‌های دیگر"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
