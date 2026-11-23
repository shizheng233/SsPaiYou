package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.EyeFill: ImageVector
    get() {
        if (_EyeFill != null) {
            return _EyeFill!!
        }
        _EyeFill = ImageVector.Builder(
            name = "EyeFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10.5f, 8f)
                arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5f, 0f)
                arcToRelative(2.5f, 2.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 0f)
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(0f, 8f)
                reflectiveCurveToRelative(3f, -5.5f, 8f, -5.5f)
                reflectiveCurveTo(16f, 8f, 16f, 8f)
                reflectiveCurveToRelative(-3f, 5.5f, -8f, 5.5f)
                reflectiveCurveTo(0f, 8f, 0f, 8f)
                moveToRelative(8f, 3.5f)
                arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -7f)
                arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 7f)
            }
        }.build()

        return _EyeFill!!
    }

@Suppress("ObjectPropertyName")
private var _EyeFill: ImageVector? = null
