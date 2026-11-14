package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandContainer
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils
import com.example.util.t
import java.util.Locale

/** Extracts coordinates from pasted map links (Google Maps, Neshan, Balad) or raw "lat, lng" text. */
object MapLinkUtils {
    private val AT_COORDS_REGEX = Regex("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    private val Q_COORDS_REGEX = Regex("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    private val BALAD_COORDS_REGEX = Regex("lat=(-?\\d+\\.\\d+).*?lng=(-?\\d+\\.\\d+)")
    private val RAW_COORDS_REGEX = Regex("(-?\\d{1,2}\\.\\d{4,})[\\s,،]+(-?\\d{1,3}\\.\\d{4,})")

    fun extractCoordinates(text: String): Pair<Double, Double>? {
        val clean = PersianUtils.toEnglishDigits(text.trim())
        for (regex in listOf(AT_COORDS_REGEX, Q_COORDS_REGEX, BALAD_COORDS_REGEX, RAW_COORDS_REGEX)) {
            val m = regex.find(clean) ?: continue
            val lat = m.groupValues[1].toDoubleOrNull()
            val lng = m.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null && lat in -90.0..90.0 && lng in -180.0..180.0) return Pair(lat, lng)
        }
        return null
    }
}

private fun openMapAt(context: Context, lat: Double, lng: Double, label: String) {
    val geo = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, geo).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        val web = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
        try { context.startActivity(Intent(Intent.ACTION_VIEW, web).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) {}
    }
}

private fun searchOnMap(context: Context, query: String) {
    val geo = Uri.parse("geo:0,0?q=" + Uri.encode(query.ifBlank { "Iran" }))
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, geo).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        val web = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
        try { context.startActivity(Intent(Intent.ACTION_VIEW, web).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) {}
    }
}

/**
 * Compact, reliable location editor: shows the saved coordinates, opens them in the user's map app,
 * and lets the user paste a map link (or type coordinates) instead of a fake in-app map.
 */
@Composable
fun LocationPickerCard(
    latitude: Double,
    longitude: Double,
    onLocationChange: (lat: Double, lng: Double) -> Unit,
    vendorTitle: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val hasLocation = !(latitude == 0.0 && longitude == 0.0)
    var linkInput by remember { mutableStateOf("") }
    var linkError by remember { mutableStateOf<String?>(null) }
    var showManual by remember { mutableStateOf(false) }
    var latInput by remember(latitude) { mutableStateOf(if (hasLocation) String.format(Locale.US, "%.5f", latitude) else "") }
    var lngInput by remember(longitude) { mutableStateOf(if (hasLocation) String.format(Locale.US, "%.5f", longitude) else "") }

    fun applyLink(text: String) {
        linkInput = text
        if (text.isBlank()) { linkError = null; return }
        val coords = MapLinkUtils.extractCoordinates(text)
        if (coords != null) {
            linkError = null
            onLocationChange(coords.first, coords.second)
        } else {
            linkError = t("مختصاتی در این متن پیدا نشد")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrandContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = t("موقعیت روی نقشه"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hasLocation) t("موقعیت ثبت شده است") else t("هنوز موقعیتی ثبت نشده است"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (hasLocation) {
                Surface(shape = RoundedCornerShape(12.dp), color = BrandContainer) {
                    Text(
                        text = String.format(Locale.US, "%.5f ,  %.5f", latitude, longitude),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandDark
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { openMapAt(context, latitude, longitude, vendorTitle) },
                    enabled = hasLocation,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(t("نمایش روی نقشه"), maxLines = 1)
                }
                if (!readOnly) {
                    OutlinedButton(
                        onClick = { searchOnMap(context, vendorTitle) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(t("جستجو در نقشه"), maxLines = 1)
                    }
                }
            }

            if (!readOnly) {
                OutlinedTextField(
                    value = linkInput,
                    onValueChange = { applyLink(it) },
                    label = { Text(t("لینک نقشه (نشان، بلد، Google Maps)")) },
                    singleLine = true,
                    isError = linkError != null,
                    supportingText = {
                        Text(linkError ?: t("لینک یا مختصات را از برنامه نقشه کپی و اینجا بچسبانید"))
                    },
                    trailingIcon = {
                        IconButton(onClick = { clipboard.getText()?.text?.let { applyLink(it) } }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = t("چسباندن"))
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showManual = !showManual }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(t("ورود دستی مختصات"))
                    }
                    if (hasLocation) {
                        TextButton(onClick = { onLocationChange(0.0, 0.0); linkInput = ""; linkError = null }) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(t("پاک کردن"), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                AnimatedVisibility(visible = showManual) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = latInput,
                            onValueChange = { v ->
                                latInput = v
                                val la = PersianUtils.toEnglishDigits(v).toDoubleOrNull()
                                val lo = PersianUtils.toEnglishDigits(lngInput).toDoubleOrNull()
                                if (la != null && lo != null && la in -90.0..90.0 && lo in -180.0..180.0) onLocationChange(la, lo)
                            },
                            label = { Text(t("عرض جغرافیایی")) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lngInput,
                            onValueChange = { v ->
                                lngInput = v
                                val la = PersianUtils.toEnglishDigits(latInput).toDoubleOrNull()
                                val lo = PersianUtils.toEnglishDigits(v).toDoubleOrNull()
                                if (la != null && lo != null && la in -90.0..90.0 && lo in -180.0..180.0) onLocationChange(la, lo)
                            },
                            label = { Text(t("طول جغرافیایی")) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
