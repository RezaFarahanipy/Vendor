package com.example.ui.screens.vendor_form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import com.example.util.PersianUtils
import java.util.Locale

/**
 * Step 8: Quality Assessment, Verified Status & Internal Notes
 */
@Composable
fun Step8Rating(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    isVerified: Boolean,
    onVerifiedChange: (Boolean) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Text(
        text = "ارزیابی کیفی، رتبه و یادداشت‌ها",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )

    // Rating & Quality Assessment Card
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberRating, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "امتیاز و ارزیابی عملکرد وندور",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmberRating.copy(alpha = 0.18f))
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "وندور معتمد و مورد تأیید تدارکات",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "نشان سبز وندور معتمد در کارت‌ها و جستجو درج می‌شود",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

    // Confidential Notes Card
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "یادداشت‌های محرمانه و ارزیابی ممیزی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "این یادداشت‌ها صرفاً برای استفاده داخلی تیم خرید و تدارکات است و برای طرف مقابل نمایش داده نمی‌شود.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("یادداشت‌های داخلی و شرایط همکاری") },
                placeholder = { Text("مثال: بازرسی خط تولید تأیید شد. شرایط تسویه ۴۵ روزه چک صیادی. کیفیت ساخت استاندارد.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
        }
    }
}
