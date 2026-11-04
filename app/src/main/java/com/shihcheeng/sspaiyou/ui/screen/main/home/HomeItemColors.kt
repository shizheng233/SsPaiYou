package com.shihcheeng.sspaiyou.ui.screen.main.home

import android.util.LruCache
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.graphics.toColorInt
import com.materialkolor.hct.Hct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Immutable
internal data class HomeItemColors(
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color
)

private val homeItemColorCache = LruCache<Pair<Int, Boolean>, HomeItemColors>(64)

@Composable
internal fun rememberHomeItemColors(themeColor: String): HomeItemColors {
    val scheme = MaterialTheme.colorScheme
    val fallback = HomeItemColors(scheme.surfaceContainer, scheme.onSurface, scheme.onSurfaceVariant)
    val seed = remember(themeColor) {
        runCatching { themeColor.trim().toColorInt() }.getOrNull()
    } ?: return fallback
    // Follow the app's actual theme, including previews and manual theme overrides.
    val isDark = scheme.surface.luminance() < scheme.onSurface.luminance()
    return key(seed, isDark) {
        val cacheKey = seed to isDark
        val cached = remember(cacheKey) { homeItemColorCache.get(cacheKey) }
        val colors by produceState(cached, cacheKey) {
            if (value == null) {
                value = withContext(Dispatchers.Default) {
                    val hct = Hct.fromInt(seed)
                    // Preserve the article hue; limit chroma for a quiet reading surface.
                    val chroma = hct.chroma.coerceAtMost(24.0)
                    HomeItemColors(
                        surface = Color(Hct.from(hct.hue, chroma, if (isDark) 18.0 else 94.0).toInt()),
                        onSurface = Color(Hct.from(hct.hue, chroma.coerceAtMost(8.0), if (isDark) 94.0 else 10.0).toInt()),
                        onSurfaceVariant = Color(Hct.from(hct.hue, chroma.coerceAtMost(12.0), if (isDark) 80.0 else 34.0).toInt())
                    ).also { homeItemColorCache.put(cacheKey, it) }
                }
            }
        }
        colors ?: fallback
    }
}
