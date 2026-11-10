package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Modern High-Tech Palette (AI Studio Inspired: Tech Royal Blue & Crimson Red)
val NavyDeep = Color(0xFF0F172A)         // Deep Slate 900 (High-contrast topbars & dark mode base)
val NavyCard = Color(0xFF1E293B)         // Slate 800 (Card background in dark mode)
val NavySurface = Color(0xFF334155)      // Slate 700 (Elevated surfaces in dark mode)
val NavyBorder = Color(0xFF475569)       // Slate 600

// Modern Tech Blues (Replacing greenish teal)
val BluePrimary = Color(0xFF1D4ED8)      // Royal Blue 700 (Bold, professional, sharp)
val BlueVibrant = Color(0xFF2563EB)      // Electric Tech Blue 600
val BlueLight = Color(0xFF38BDF8)        // Sky Blue 400 (Cyan/Sky accent)
val BlueContainerLight = Color(0xFFEFF6FF) // Crisp Light Blue 50 tint
val BlueContainerDark = Color(0xFF1E3A8A)  // Deep Blue 900 container

// Modern Crimson & Coral Reds
val CrimsonPrimary = Color(0xFFE11D48)   // Rose 600 (Vibrant, modern red)
val CrimsonDark = Color(0xFFBE123C)      // Rose 700
val CrimsonLight = Color(0xFFFB7185)     // Rose 400
val CrimsonContainerLight = Color(0xFFFFF1F2) // Rose 50 container
val RedFavorite = CrimsonPrimary         // Heart & favorite color

// Backward-compatible color aliases so all screens compile seamlessly with new blue/red palette:
val TealPrimary = BlueVibrant            // Primary action is now vibrant blue
val TealLight = BlueLight                // Secondary sky blue accent
val TealDark = BluePrimary               // Dark high-contrast blue
val TealContainerLight = BlueContainerLight
val TealContainerDark = BlueContainerDark

// Status Colors
val AmberRating = Color(0xFFF59E0B)      // Amber 500 (Clean rating stars)
val BlueAccent = Color(0xFF0284C7)       // Ocean Blue (Map / Info)
val GreenVerified = Color(0xFF059669)    // Emerald 600 (Subtle verified badge)

// Neutral Light Mode (Crisp, High-Tech, High-Contrast)
val LightBg = Color(0xFFF8FAFC)          // Slate 50 (Ultra-clean, crisp background)
val LightCard = Color(0xFFFFFFFF)        // Pure White card
val LightCardBorder = Color(0xFFE2E8F0)  // Slate 200 (Hairline elegant border)
val LightTextPrimary = Color(0xFF0F172A) // Slate 900 (Crisp dark text)
val LightTextSecondary = Color(0xFF475569) // Slate 600
val LightTextTertiary = Color(0xFF94A3B8)  // Slate 400

// Neutral Dark Mode
val DarkBg = Color(0xFF0B132B)           // Modern deep tech navy
val DarkCard = Color(0xFF111D3B)         // Deep navy surface card
val DarkCardBorder = Color(0xFF1F3554)   // Clean dark border
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextTertiary = Color(0xFF64748B)
