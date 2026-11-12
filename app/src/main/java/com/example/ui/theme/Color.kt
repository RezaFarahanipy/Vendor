package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ───────────────────────────────────────────────────────────────────────────
// Fresh, cheerful & professional palette: vivid indigo → violet → pink accents
// on a soft lavender-white canvas. Semantic colors (rating, verified, favorite,
// error) are all distinct so each colour carries exactly one meaning.
// ───────────────────────────────────────────────────────────────────────────

// Brand
val BrandPrimary = Color(0xFF5850EC)   // Vivid indigo (white text contrast ≈ 5.6:1)
val BrandViolet = Color(0xFF7C3AED)    // Gradient partner
val BrandPink = Color(0xFFDB2777)      // Joyful gradient accent
val BrandLight = Color(0xFF818CF8)     // Highlights / dark-mode accent
val InkDeep = Color(0xFF1E1B4B)        // Deepest ink (text on pastel, dark placeholders)

// Soft pastel containers (light mode)
val BrandContainer = Color(0xFFE0E7FF)
val VioletContainer = Color(0xFFEDE9FE)
val SkyContainer = Color(0xFFE0F2FE)
val MintContainer = Color(0xFFD1FAE5)
val AmberContainer = Color(0xFFFEF3C7)
val RoseContainer = Color(0xFFFFE4E6)

// Accents with a single meaning each
val AmberRating = Color(0xFFF59E0B)    // Rating stars
val GreenVerified = Color(0xFF10B981)  // Verified / success
val BlueAccent = Color(0xFF0EA5E9)     // Map & navigation
val CyanAccent = Color(0xFF06B6D4)
val PurpleAccent = Color(0xFF8B5CF6)   // Catalog
val CoralAccent = Color(0xFFFF6B57)    // Warm highlight
val RedFavorite = Color(0xFFF43F5E)    // Favorite heart / delete
val ErrorRed = Color(0xFFDC2626)       // Validation errors only

// Neutrals – light
val LightBg = Color(0xFFF5F6FF)
val LightCard = Color(0xFFFFFFFF)
val LightCardBorder = Color(0xFFE4E7F7)
val LightTextPrimary = Color(0xFF1E1B4B)
val LightTextSecondary = Color(0xFF5B6080)
val LightTextTertiary = Color(0xFF8E93B0)

// Neutrals – dark
val DarkBg = Color(0xFF0F1020)
val DarkCard = Color(0xFF191B36)
val DarkCardBorder = Color(0xFF2B2E55)
val DarkTextPrimary = Color(0xFFF5F6FF)
val DarkTextSecondary = Color(0xFFA9AED0)
val DarkTextTertiary = Color(0xFF7C81A6)

// Legacy dark-surface names still referenced in a few places
val NavyDeep = InkDeep
val NavyCard = DarkCard
val NavyBorder = DarkCardBorder

/** Mirrors the active theme so non-composable color getters can adapt to dark mode. */
object ThemeState {
    var isDark by mutableStateOf(false)
}

/** Text/icon accent that stays readable on both light (deep indigo) and dark (light indigo) surfaces. */
val BrandDark: Color
    get() = if (ThemeState.isDark) BrandLight else Color(0xFF4338CA)

// Gradients
val BrandGradient: Brush = Brush.linearGradient(listOf(BrandPrimary, BrandViolet))
val HeroColors = listOf(BrandPrimary, BrandViolet, BrandPink)
