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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BlueVibrant,
    onPrimary = Color.White,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C0519),
    onSecondaryContainer = Color(0xFFFFD1D9),
    tertiary = PurpleAccent,
    onTertiary = Color.White,
    tertiaryContainer = NavyCard,
    onTertiaryContainer = Color(0xFFE9D5FF),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder,
    error = CoralAccent
)

private val LightColorScheme = lightColorScheme(
    primary = BlueVibrant,
    onPrimary = Color.White,
    primaryContainer = BlueContainerLight,
    onPrimaryContainer = BluePrimary,
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = CoralContainerLight,
    onSecondaryContainer = CrimsonDark,
    tertiary = PurpleAccent,
    onTertiary = Color.White,
    tertiaryContainer = PurpleContainerLight,
    onTertiaryContainer = PurpleAccent,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = CoralAccent
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
