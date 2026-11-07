package com.shihcheeng.sspaiyou.ui.screen.main.personal

import android.graphics.Bitmap
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.samplePixels
import com.materialkolor.material3.rememberDynamicColorScheme
import com.materialkolor.quantize.QuantizerCelebi
import com.materialkolor.score.Score
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun rememberProfileCardColorScheme(avatar: Bitmap?): ColorScheme {
    val fallback = MaterialTheme.colorScheme
    val seed by key(avatar) {
        produceState<Color?>(initialValue = null, avatar) {
            if (avatar != null) {
                value = withContext(Dispatchers.Default) { extractAvatarSeedColor(avatar) }
            }
        }
    }
    return seed?.let {
        rememberDynamicColorScheme(
            seedColor = it,
            isDark = fallback.surface.luminance() < fallback.onSurface.luminance(),
            style = PaletteStyle.Content
        )
    } ?: fallback
}

private fun extractAvatarSeedColor(avatar: Bitmap): Color? {
    // A small sample keeps quantization cheap; transparent pixels must not become black swatches.
    val pixels = avatar.asImageBitmap().samplePixels(sampleArea = 64 * 64).pixels
        .filter { it ushr 24 >= 128 }
        .toIntArray()
    if (pixels.isEmpty()) return null

    val populations = QuantizerCelebi.quantize(pixels, maxColors = 32)
    val dominant = populations.maxByOrNull { it.value }?.key ?: return null
    return Color(
        Score.score(
            colorsToPopulation = populations,
            desired = 1,
            // Monochrome avatars should keep their own color rather than the library's blue fallback.
            fallbackColorArgb = dominant
        ).first()
    )
}
