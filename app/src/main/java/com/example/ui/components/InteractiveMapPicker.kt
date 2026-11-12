package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.InkDeep
import com.example.ui.theme.RedFavorite
import com.example.ui.theme.BrandDark
import com.example.ui.theme.BrandLight
import com.example.ui.theme.BrandPrimary
import com.example.util.PersianUtils

data class IndustrialZone(
    val title: String,
    val province: String,
    val city: String,
    val lat: Double,
    val lng: Double
)

val POPULAR_ZONES = listOf(
    IndustrialZone("شهرک شمس‌آباد", "تهران", "تهران", 35.73576, 51.44678),
    IndustrialZone("شادآباد (بازار آهن)", "تهران", "تهران", 35.66210, 51.32040),
    IndustrialZone("شهرک محمودآباد", "اصفهان", "اصفهان", 32.65462, 51.66798),
    IndustrialZone("شهرک صنعتی توس", "خراسان رضوی", "مشهد", 36.29720, 59.60670),
    IndustrialZone("شهرک بزرگ شیراز", "فارس", "شیراز", 29.59176, 52.58369),
    IndustrialZone("شهرک صنعتی بهارستان", "البرز", "کرج", 35.84000, 50.93910),
    IndustrialZone("شهرک صنعتی پرند", "تهران", "رباط‌کریم", 35.43120, 50.98540),
    IndustrialZone("شهرک صنعتی البرز", "قزوین", "البرز", 36.21540, 50.05210)
)

object MapLinkUtils {
    private val AT_COORDS_REGEX = Regex("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    private val Q_COORDS_REGEX = Regex("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    private val BALAD_COORDS_REGEX = Regex("lat=(-?\\d+\\.\\d+).*?lng=(-?\\d+\\.\\d+)")
    private val RAW_COORDS_REGEX = Regex("(-?\\d{1,2}\\.\\d{4,})[\\s,،]+(-?\\d{1,3}\\.\\d{4,})")

    fun extractCoordinates(text: String): Pair<Double, Double>? {
        val clean = text.trim()
        AT_COORDS_REGEX.find(clean)?.let { m ->
            val lat = m.groupValues[1].toDoubleOrNull()
            val lng = m.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }
        Q_COORDS_REGEX.find(clean)?.let { m ->
            val lat = m.groupValues[1].toDoubleOrNull()
            val lng = m.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }
        BALAD_COORDS_REGEX.find(clean)?.let { m ->
            val lat = m.groupValues[1].toDoubleOrNull()
            val lng = m.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }
        RAW_COORDS_REGEX.find(clean)?.let { m ->
            val lat = m.groupValues[1].toDoubleOrNull()
            val lng = m.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) return Pair(lat, lng)
        }
        return null
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveMapPicker(
    latitude: Double,
    longitude: Double,
    onLocationChange: (lat: Double, lng: Double, province: String?, city: String?) -> Unit,
    modifier: Modifier = Modifier,
    vendorTitle: String = "موقعیت شرکت"
) {
    val context = LocalContext.current
    var latInput by remember(latitude) { mutableStateOf(String.format("%.5f", latitude)) }
    var lngInput by remember(longitude) { mutableStateOf(String.format("%.5f", longitude)) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var mapLinkInput by remember { mutableStateOf("") }
    var mapLinkError by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with current pin info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "موقعیت‌یاب تعاملی روی نقشه",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "روی نقشه لمس کنید یا مختصات را دقیق وارد نمایید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(vendorTitle)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مسیریابی", fontSize = 11.sp)
                }
            }

            // Interactive Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE5ECEF))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            ) {
                // Interactive Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(latitude, longitude, zoomLevel) {
                            detectTapGestures { offset ->
                                // Convert canvas tap offset to lat/lng offsets around center
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f

                                val dx = (offset.x - centerX) / (size.width * zoomLevel)
                                val dy = (offset.y - centerY) / (size.height * zoomLevel)

                                // 1 unit in canvas ~ 0.05 degrees
                                val newLng = longitude + (dx * 0.06)
                                val newLat = latitude - (dy * 0.06)

                                onLocationChange(newLat, newLng, null, null)
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Industrial zone grid lines
                    val step = 30f * zoomLevel
                    var x = (w / 2f) % step
                    while (x < w) {
                        drawLine(
                            color = Color(0xFFD0DBE2),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f
                        )
                        x += step
                    }

                    var y = (h / 2f) % step
                    while (y < h) {
                        drawLine(
                            color = Color(0xFFD0DBE2),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += step
                    }

                    // Simulated Main Industrial Arterial Roads
                    val roadPaint = Color(0xFFFCFDFD)
                    val roadBorder = Color(0xFFB5C4CF)

                    // Major highway
                    drawLine(
                        color = roadBorder,
                        start = Offset(0f, h * 0.45f),
                        end = Offset(w, h * 0.55f),
                        strokeWidth = 16f
                    )
                    drawLine(
                        color = roadPaint,
                        start = Offset(0f, h * 0.45f),
                        end = Offset(w, h * 0.55f),
                        strokeWidth = 12f
                    )

                    // Secondary cross road
                    drawLine(
                        color = roadBorder,
                        start = Offset(w * 0.35f, 0f),
                        end = Offset(w * 0.45f, h),
                        strokeWidth = 12f
                    )
                    drawLine(
                        color = roadPaint,
                        start = Offset(w * 0.35f, 0f),
                        end = Offset(w * 0.45f, h),
                        strokeWidth = 8f
                    )

                    // Industrial lot zones
                    drawRoundRect(
                        color = Color(0xFFD6E3EB),
                        topLeft = Offset(w * 0.08f, h * 0.12f),
                        size = androidx.compose.ui.geometry.Size(w * 0.22f, h * 0.28f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        color = Color(0xFFD6E3EB),
                        topLeft = Offset(w * 0.52f, h * 0.60f),
                        size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.30f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )

                    // Center Location Target Pin
                    val pinCenterX = w / 2f
                    val pinCenterY = h / 2f

                    // Ripple pulse under pin
                    drawCircle(
                        color = BrandPrimary.copy(alpha = 0.25f),
                        radius = 28f,
                        center = Offset(pinCenterX, pinCenterY)
                    )
                    drawCircle(
                        color = BrandPrimary.copy(alpha = 0.45f),
                        radius = 16f,
                        center = Offset(pinCenterX, pinCenterY)
                    )

                    // Pin base point
                    drawCircle(
                        color = InkDeep,
                        radius = 5f,
                        center = Offset(pinCenterX, pinCenterY)
                    )
                }

                // Interactive Pin Overlay at the exact center
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InkDeep,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "موقعیت پین: ${PersianUtils.toPersianDigits(String.format("%.4f", latitude))}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Pin",
                            tint = RedFavorite,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                // Floating Zoom / Re-center buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { if (zoomLevel < 3.0f) zoomLevel += 0.4f },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { if (zoomLevel > 0.6f) zoomLevel -= 0.4f },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Link Paste & Auto Coordinate Extraction
            OutlinedTextField(
                value = mapLinkInput,
                onValueChange = { input ->
                    mapLinkInput = input
                    val coords = MapLinkUtils.extractCoordinates(input)
                    if (coords != null) {
                        mapLinkError = null
                        latInput = String.format("%.5f", coords.first)
                        lngInput = String.format("%.5f", coords.second)
                        onLocationChange(coords.first, coords.second, null, null)
                        android.widget.Toast.makeText(context, "مختصات نقشه از لینک استخراج شد", android.widget.Toast.LENGTH_SHORT).show()
                    } else if (input.isNotBlank() && (input.startsWith("http") || input.contains(","))) {
                        mapLinkError = "مختصات جغرافیایی در لینک یافت نشد"
                    } else {
                        mapLinkError = null
                    }
                },
                label = { Text("الصاق لینک نقشه (نشان، بلد، Google Maps)") },
                placeholder = { Text("لینک نشان، بلد یا گوگل‌مپ را اینجا الصاق کنید") },
                isError = mapLinkError != null,
                supportingText = if (mapLinkError != null) { { Text(mapLinkError!!, color = MaterialTheme.colorScheme.error) } } else null,
                leadingIcon = {
                    Icon(Icons.Default.Map, contentDescription = null, tint = BrandPrimary)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Numeric Coordinate Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latInput,
                    onValueChange = {
                        latInput = it
                        val parsed = it.toDoubleOrNull()
                        if (parsed != null && parsed in -90.0..90.0) {
                            onLocationChange(parsed, longitude, null, null)
                        }
                    },
                    label = { Text("عرض جغرافیایی (Lat)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = lngInput,
                    onValueChange = {
                        lngInput = it
                        val parsed = it.toDoubleOrNull()
                        if (parsed != null && parsed in -180.0..180.0) {
                            onLocationChange(latitude, parsed, null, null)
                        }
                    },
                    label = { Text("طول جغرافیایی (Lng)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
