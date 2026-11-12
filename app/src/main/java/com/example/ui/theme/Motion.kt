package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay

/** Springy press feedback: scales the element down while it is pressed. */
fun Modifier.pressScale(source: InteractionSource, pressedScale: Float = 0.97f): Modifier = composed {
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press_scale"
    )
    graphicsLayer { scaleX = scale; scaleY = scale }
}

/** Fade + rise + gentle scale entrance; items cascade by [index] (capped so long lists stay snappy). */
fun Modifier.staggerIn(index: Int, stepMs: Int = 55, maxSteps: Int = 8): Modifier = composed {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((index.coerceIn(0, maxSteps) * stepMs).toLong())
        progress.animateTo(1f, tween(durationMillis = 450, easing = FastOutSlowInEasing))
    }
    graphicsLayer {
        val p = progress.value
        alpha = p
        translationY = (1f - p) * 48f
        val s = 0.96f + 0.04f * p
        scaleX = s
        scaleY = s
    }
}

/** Slowly drifting gradient background (drawn in the draw phase – no recomposition per frame). */
fun Modifier.animatedGradient(colors: List<Color>, durationMs: Int = 9000): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "gradient")
    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMs, easing = LinearEasing), RepeatMode.Reverse),
        label = "gradient_shift"
    )
    drawBehind {
        val dx = size.width * shift.value
        drawRect(
            Brush.linearGradient(
                colors = colors,
                start = Offset(dx - size.width, 0f),
                end = Offset(dx + size.width, size.height)
            )
        )
    }
}

/** Heart that pops with a spring and cross-fades its color when toggled. */
@Composable
fun AnimatedHeart(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    offTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val scale = remember { Animatable(1f) }
    var firstRun by remember { mutableStateOf(true) }
    LaunchedEffect(isFavorite) {
        if (firstRun) {
            firstRun = false
        } else {
            scale.snapTo(0.55f)
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium))
        }
    }
    val tint by animateColorAsState(if (isFavorite) RedFavorite else offTint, label = "heart_tint")
    Icon(
        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = "علاقه‌مندی",
        tint = tint,
        modifier = modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
    )
}

/** Picks status-bar icon color for the screen on top: light icons over gradients / dark mode, dark icons otherwise. */
@Composable
fun StatusBarStyle(onGradient: Boolean) {
    val view = LocalView.current
    val lightIcons = onGradient || ThemeState.isDark
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !lightIcons
        }
    }
}
