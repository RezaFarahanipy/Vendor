package com.example.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Lightweight runtime translation layer. UI text is authored in Persian; [t] returns the English
 * counterpart from [EnglishTranslations] when English is selected. Reading [Lang.isFa] inside a
 * composable makes it recompose automatically when the language changes.
 */
object Lang {
    var isFa by mutableStateOf(true)
}

/** Translates a Persian UI string to the active language (falls back to the original text). */
fun t(fa: String): String = if (Lang.isFa) fa else (EnglishTranslations.map[fa] ?: fa)

/** Same as [t] but fills `{0}`, `{1}`… placeholders with [args]. */
fun tf(fa: String, vararg args: Any?): String {
    var s = t(fa)
    args.forEachIndexed { i, a -> s = s.replace("{$i}", a?.toString() ?: "") }
    return s
}
