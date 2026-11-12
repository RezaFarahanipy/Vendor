package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import android.app.Activity

private val DarkColorScheme = darkColorScheme(
    primary = BrandLight,
    onPrimary = InkDeep,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFA78BFA),
    onSecondary = InkDeep,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFF2DD4BF),
    onTertiary = Color(0xFF042F2E),
    tertiaryContainer = Color(0xFF134E4A),
    onTertiaryContainer = Color(0xFFCCFBF1),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF23264A),
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF3D4170),
    outlineVariant = DarkCardBorder,
    surfaceContainerLowest = DarkBg,
    surfaceContainerLow = Color(0xFF15172E),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = Color(0xFF20234A),
    surfaceContainerHighest = Color(0xFF272A55)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandContainer,
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = BrandViolet,
    onSecondary = Color.White,
    secondaryContainer = VioletContainer,
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = Color(0xFF0F766E),
    onTertiary = Color.White,
    tertiaryContainer = MintContainer,
    onTertiaryContainer = Color(0xFF065F46),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = RoseContainer,
    onErrorContainer = Color(0xFF991B1B),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFEEF0FC),
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFC7CBE6),
    outlineVariant = LightCardBorder,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8F9FF),
    surfaceContainer = Color(0xFFF1F2FF),
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(0xFFE8EAFD)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun VendorListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Keep non-composable color getters (BrandDark) in sync before children compose.
    remember(darkTheme) { ThemeState.isDark = darkTheme; darkTheme }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
