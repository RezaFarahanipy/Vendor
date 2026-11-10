package com.example.ui.screens.vendor_form

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberRating
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.util.PersianUtils
import java.util.Locale

/**
 * Step 7: Catalog PDF Attachment, Notes, Verification & Rating
 */
@Composable
fun Step7RatingCatalog(
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
        text = "کاتالوگ فنی، یادداشت و ارزیابی کیفی",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // PDF Catalog Attachment Card
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ضمیمه کاتالوگ یا بروشور فنی (PDF)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = catalogTitle,
                onValueChange = onCatalogTitleChange,
                label = { Text("عنوان یا نسخه کاتالوگ") },
                placeholder = { Text("کاتالوگ جامع محصولات ۱۴۰۳") },
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
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TealDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فایل کاتالوگ انتخاب و ذخیره شده است",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TealDark
                            )
                        }

                        Button(
                            onClick = onTestCatalogClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealDark,
                                contentColor = androidx.compose.ui.graphics.Color.White
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

    // Rating & Quality Assessment
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberRating, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ارزیابی کیفی و امتیاز وندور",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberRating.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${PersianUtils.toPersianDigits(String.format(Locale.US, "%.1f", rating))} از ۵",
                        fontWeight = FontWeight.Bold,
                        color = AmberRating,
                        fontSize = 13.sp
                    )
                }
            }

            Slider(
                value = rating,
                onValueChange = onRatingChange,
                valueRange = 0f..5f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = AmberRating,
                    activeTrackColor = AmberRating,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Verified Status Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "وندور مورد تأیید و معتمد (تأییدیه واحد تدارکات)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "با فعال‌سازی، نشان سبز رنگ وندور معتمد در کارت‌ها و جستجو درج می‌شود",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isVerified,
                    onCheckedChange = onVerifiedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = GreenVerified,
                        checkedTrackColor = GreenVerified.copy(alpha = 0.35f)
                    )
                )
            }
        }
    }

    // Notes
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("یادداشت‌های داخلی و توضیحات ممیزی") },
        placeholder = { Text("نکات ارزیابی بازرسی کارخانه، شرایط پرداخت و تسویه حساب، سرعت تحویل کالا...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
    )
}
