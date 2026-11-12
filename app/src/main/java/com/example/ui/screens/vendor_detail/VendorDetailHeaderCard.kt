package com.example.ui.screens.vendor_detail

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Vendor
import com.example.ui.theme.AmberRating
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.InkDeep
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils
import java.io.File
import java.util.Locale
import com.example.ui.theme.BrandViolet

/**
 * Modular Header Card for Vendor Details
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VendorDetailHeaderCard(vendor: Vendor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
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
                // Company Logo
                val hasLogo = vendor.logoUri.isNotBlank() && File(vendor.logoUri).exists()
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .then(
                            if (hasLogo) Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            else Modifier.background(Brush.linearGradient(listOf(BrandPrimary, BrandViolet)))
                        )
                        .border(
                            1.5.dp,
                            if (hasLogo) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) else BrandPrimary.copy(alpha = 0.6f),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasLogo) {
                        AsyncImage(
                            model = File(vendor.logoUri),
                            contentDescription = "لوگوی شرکت",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = vendor.name.take(2),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vendor.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                        if (vendor.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "تأیید شده",
                                tint = GreenVerified,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${vendor.activityType} • ${vendor.province}، ${vendor.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating Stars / Status
                    if (vendor.rating > 0f) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < vendor.rating.toInt()) AmberRating else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = PersianUtils.toPersianDigits(String.format(Locale.US, "%.1f", vendor.rating)),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (vendor.reviewCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${PersianUtils.toPersianDigits(vendor.reviewCount)} نظر)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "بدون ارزیابی امتیاز",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (vendor.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = vendor.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (vendor.categories.split(",") + vendor.tags.split(","))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
            }
        }
    }
}
