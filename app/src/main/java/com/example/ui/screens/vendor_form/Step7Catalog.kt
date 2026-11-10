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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary

/**
 * Step 7: Catalog PDF Attachment
 */
@Composable
fun Step7Catalog(
    catalogTitle: String,
    onCatalogTitleChange: (String) -> Unit,
    catalogLocalPath: String,
    onUploadPdfClick: () -> Unit,
    onTestCatalogClick: () -> Unit
) {
    Text(
        text = "کاتالوگ دیجیتال و اسناد فنی",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ضمیمه کاتالوگ یا بروشور فنی (PDF)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "می‌توانید کاتالوگ رسمی، مشخصات فنی یا بروشور محصولات وندور را به صورت فایل PDF بارگذاری نمایید تا در صفحه اختصاصی وندور مستقیماً قابل نمایش و دانلود باشد.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = catalogTitle,
                onValueChange = onCatalogTitleChange,
                label = { Text("عنوان یا نسخه کاتالوگ") },
                placeholder = { Text("مثال: کاتالوگ جامع محصولات ۱۴۰۳") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (catalogLocalPath.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TealDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فایل کاتالوگ با موفقیت متصل شده است",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TealDark
                            )
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
                            Text("مشاهده فایل", fontSize = 11.sp)
                        }
                    }
                }
            }

            Button(
                onClick = onUploadPdfClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (catalogLocalPath.isBlank()) "انتخاب و بارگذاری فایل PDF از حافظه" else "تغییر یا جایگزینی فایل کاتالوگ",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
