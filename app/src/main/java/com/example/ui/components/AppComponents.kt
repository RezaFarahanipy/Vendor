package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Product
import java.io.File
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vendor
import com.example.ui.theme.AmberRating
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.GreenVerified
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandLight
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalContentColor
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.AnimatedHeart
import com.example.ui.theme.pressScale
import com.example.ui.theme.StatusBarStyle

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    StatusBarStyle(onGradient = true)
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(BrandGradient)) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (onBack != null) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .testTag("back_button")
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!subtitle.isNullOrBlank()) {
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (actions != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            actions()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color = BrandPrimary,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)))),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = PersianUtils.toPersianDigits(value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

private val AVATAR_PALETTE = listOf(
    Color(0xFF5850EC), Color(0xFF7C3AED), Color(0xFFDB2777),
    Color(0xFFEA580C), Color(0xFF0D9488), Color(0xFF0284C7),
    Color(0xFF16A34A), Color(0xFFE11D48), Color(0xFFD97706)
)

fun getAvatarColor(name: String): Color {
    val hash = kotlin.math.abs(name.hashCode())
    return AVATAR_PALETTE[hash % AVATAR_PALETTE.size]
}

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun VendorCard(
    vendor: Vendor,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val avatarColor = remember(vendor.name) { getAvatarColor(vendor.name) }

    val cardInteraction = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        interactionSource = cardInteraction,
        modifier = modifier
            .fillMaxWidth()
            .pressScale(cardInteraction)
            .testTag("vendor_card_${vendor.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored Letter Avatar or Custom Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (vendor.logoUri.isNotBlank() && File(vendor.logoUri).exists()) {
                                Color.White
                            } else {
                                avatarColor
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (vendor.logoUri.isNotBlank() && File(vendor.logoUri).exists()) {
                        AsyncImage(
                            model = File(vendor.logoUri),
                            contentDescription = "لوگوی ${vendor.name}",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = vendor.name.trim().take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vendor.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (vendor.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "تأیید شده",
                                tint = GreenVerified,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = vendor.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = vendor.city,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (vendor.rating > 0f) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberRating,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = PersianUtils.toPersianDigits(String.format("%.1f", vendor.rating)),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    AnimatedHeart(isFavorite = vendor.isFavorite)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Categories & tags chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val chipItems = (vendor.categories.split(",") + vendor.tags.split(","))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .take(4)

                chipItems.forEach { chipText ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Contact Action Row (Call, WhatsApp, Route/Map) with 48dp touch targets
            val callNumber = vendor.phone.ifBlank { vendor.mobile }
            val waNumber = vendor.mobile.ifBlank { vendor.phone }
            val hasLocation = vendor.address.isNotBlank() || (vendor.latitude != 0.0 && vendor.longitude != 0.0)

            if (callNumber.isNotBlank() || waNumber.isNotBlank() || hasLocation) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (callNumber.isNotBlank()) {
                        IconButton(
                            onClick = { dialPhone(context, callNumber) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "تماس تلفنی با ${vendor.name}",
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (waNumber.isNotBlank()) {
                        IconButton(
                            onClick = {
                                com.example.util.MessengerUtils.openWhatsApp(
                                    context,
                                    waNumber,
                                    "سلام، از طریق اپلیکیشن وندوریاب با شما در ارتباط هستم."
                                )
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366).copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = "پیام واتس‌اپ به ${vendor.name}",
                                    tint = Color(0xFF25D366),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (hasLocation) {
                        IconButton(
                            onClick = {
                                if (vendor.latitude != 0.0 && vendor.longitude != 0.0) {
                                    openMap(context, vendor.latitude, vendor.longitude, vendor.name)
                                } else {
                                    openMap(context, "${vendor.city} ${vendor.address}")
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BlueAccent.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "مسیریابی به ${vendor.name}",
                                    tint = BlueAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactMessengerIcon(
    name: String,
    icon: ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CompactMessengerRow(
    mobile: String,
    phone: String = "",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val targetPhone = mobile.ifBlank { phone }
    if (targetPhone.isBlank()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // WhatsApp (#25D366)
        CompactMessengerIcon(
            name = "واتس‌اپ",
            icon = Icons.Default.Message,
            bgColor = Color(0xFF25D366),
            onClick = {
                com.example.util.MessengerUtils.openWhatsApp(
                    context,
                    targetPhone,
                    "سلام، از طریق اپلیکیشن وندوریاب با شما در ارتباط هستم."
                )
            }
        )

        // Telegram (#0088CC)
        CompactMessengerIcon(
            name = "تلگرام",
            icon = Icons.AutoMirrored.Filled.Send,
            bgColor = Color(0xFF0088CC),
            onClick = {
                com.example.util.MessengerUtils.openTelegram(context, targetPhone)
            }
        )

        // Bale (#00A389)
        CompactMessengerIcon(
            name = "بله",
            icon = Icons.Default.CheckCircle,
            bgColor = Color(0xFF00A389),
            onClick = {
                com.example.util.MessengerUtils.openBale(context, targetPhone)
            }
        )

        // Rubika (#792CEE)
        CompactMessengerIcon(
            name = "روبیکا",
            icon = Icons.Default.Favorite,
            bgColor = Color(0xFF792CEE),
            onClick = {
                com.example.util.MessengerUtils.openRubika(context, targetPhone)
            }
        )
    }
}

@Composable
fun MessengerActionBar(
    mobile: String,
    phone: String,
    modifier: Modifier = Modifier,
    companyName: String = "",
    onCallClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val targetPhone = mobile.ifBlank { phone }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactMessengerRow(mobile = mobile, phone = phone)
        }

        // Secondary Row: Direct Phone Call & SMS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (onCallClick != null) onCallClick()
                    else dialPhone(context, phone.ifBlank { mobile })
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تماس مستقیم", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { sendSms(context, mobile.ifBlank { phone }) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ارسال پیامک", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DirectContactActionsRow(
    phone: String,
    mobile: String,
    email: String,
    website: String,
    address: String,
    modifier: Modifier = Modifier
) {
    MessengerActionBar(
        mobile = mobile,
        phone = phone,
        modifier = modifier
    )
}

@Composable
fun ContactActionButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// System intents for real phone call, sms, web, map, and sharing
fun dialPhone(context: Context, number: String) {
    if (number.isBlank()) return
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.replace("-", "")}"))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun sendSms(context: Context, number: String) {
    if (number.isBlank()) return
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${number.replace("-", "")}"))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun sendEmail(context: Context, email: String, subject: String = "استعلام قیمت و اطلاعات وندور") {
    if (email.isBlank()) return
    try {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun openWebsite(context: Context, url: String) {
    if (url.isBlank()) return
    try {
        val fixedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedUrl))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun openMap(context: Context, query: String) {
    try {
        val uri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun openMap(context: Context, lat: Double, lng: Double, label: String = "") {
    try {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(" + Uri.encode(label) + ")")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        } catch (_: Exception) {}
    }
}

fun shareVendor(context: Context, vendor: Vendor) {
    try {
        val text = buildString {
            append("شرکت ${vendor.name}\n")
            append("نوع فعالیت: ${vendor.activityType}\n")
            if (vendor.phone.isNotBlank()) append("تلفن: ${vendor.phone}\n")
            if (vendor.mobile.isNotBlank()) append("همراه: ${vendor.mobile}\n")
            if (vendor.website.isNotBlank()) append("وبسایت: ${vendor.website}\n")
            if (vendor.address.isNotBlank()) append("آدرس: ${vendor.address}\n")
            append("\nاشتراک‌گذاری از طریق اپلیکیشن وندورلیست")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری مشخصات وندور"))
    } catch (_: Exception) {}
}

@Composable
fun ProductItemRow(
    product: Product,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageDrawable.isNotBlank() && File(product.imageDrawable).exists()) {
                    AsyncImage(
                        model = File(product.imageDrawable),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = product.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "مدل: ${product.model} | ${product.brand}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = product.specifications,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onFavoriteClick) {
                AnimatedHeart(isFavorite = product.isFavorite)
            }
        }
    }
}

