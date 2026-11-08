package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = NavyDeep,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealLight,
    secondary = TealLight,
    onSecondary = NavyDeep,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = TealLight,
    tertiary = NavySurface,
    onTertiary = DarkTextPrimary,
    tertiaryContainer = NavyCard,
    onTertiaryContainer = TealLight,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    error = RedFavorite
)

private val LightColorScheme = lightColorScheme(
    primary = TealDark,
    onPrimary = LightCard,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = TealDark,
    secondary = TealDark,
    onSecondary = LightCard,
    secondaryContainer = TealContainerLight,
    onSecondaryContainer = TealDark,
    tertiary = NavySurface,
    onTertiary = LightCard,
    tertiaryContainer = Color(0xFFD6E4F0),
    onTertiaryContainer = NavyDeep,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBg,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = RedFavorite
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
        content = content
    )
}
