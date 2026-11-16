package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

/** Known messenger apps with the package names whose real launcher icons are shown when installed. */
enum class Messenger(val packages: List<String>, val fallbackColor: Color, val fallbackLetter: String, val label: String) {
    WHATSAPP(listOf("com.whatsapp", "com.whatsapp.w4b"), Color(0xFF25D366), "W", "WhatsApp"),
    TELEGRAM(listOf("org.telegram.messenger", "org.telegram.messenger.web"), Color(0xFF229ED9), "T", "Telegram"),
    BALE(listOf("ir.nasim"), Color(0xFF00A389), "B", "Bale"),
    RUBIKA(listOf("app.rbmain.a", "ir.resaneh1.iptv"), Color(0xFF792CEE), "R", "Rubika")
}

/** Loads the launcher icon of the first installed package, or null when none is installed. */
@Composable
fun rememberAppIcon(packages: List<String>): ImageBitmap? {
    val context = LocalContext.current
    return remember(packages) {
        packages.firstNotNullOfOrNull { pkg ->
            try {
                context.packageManager.getApplicationIcon(pkg).toBitmap(144, 144).asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
}

/** Icon-only messenger button (uses the app's own icon; falls back to a colored brand letter). */
@Composable
fun MessengerIconButton(
    messenger: Messenger,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    onClick: () -> Unit
) {
    val icon = rememberAppIcon(messenger.packages)
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(11.dp))
            .background(if (icon == null) messenger.fallbackColor else Color.Transparent)
            .clickable(onClick = onClick)
            .semantics { contentDescription = messenger.label },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Image(bitmap = icon, contentDescription = messenger.label, modifier = Modifier.fillMaxSize())
        } else {
            Text(text = messenger.fallbackLetter, color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}
