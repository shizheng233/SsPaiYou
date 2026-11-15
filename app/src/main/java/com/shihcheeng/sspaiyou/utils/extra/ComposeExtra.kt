package com.shihcheeng.sspaiyou.utils.extra

import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

val SystemBarStyle.Companion.TRANSPARENT: SystemBarStyle
    get() = auto(
        lightScrim = Color.TRANSPARENT,
        darkScrim = Color.TRANSPARENT
    )

/**
 * 这是没有波纹动画的Clickable实现。
 */
fun Modifier.click(
    enabled: Boolean = true,
    onClick: () -> Unit
) = clickable(enabled = enabled, onClick = onClick, indication = null, interactionSource = null)


@Composable
fun TextUnit.toDp(): Dp {
    return with(LocalDensity.current) {
        this@toDp.toDp()
    }
}

