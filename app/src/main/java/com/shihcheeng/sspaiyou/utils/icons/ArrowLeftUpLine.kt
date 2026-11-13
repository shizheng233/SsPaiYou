package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.ArrowLeftUpLine: ImageVector
    get() {
        if (_ArrowLeftUpLine != null) {
            return _ArrowLeftUpLine!!
        }
        _ArrowLeftUpLine = ImageVector.Builder(
            name = "ArrowLeftUpLine",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9.414f, 8f)
                lineTo(18.021f, 16.607f)
                lineTo(16.607f, 18.021f)
                lineTo(8f, 9.414f)
                verticalLineTo(17f)
                horizontalLineTo(6f)
                verticalLineTo(6f)
                horizontalLineTo(17f)
                verticalLineTo(8f)
                horizontalLineTo(9.414f)
                close()
            }
        }.build()

        return _ArrowLeftUpLine!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowLeftUpLine: ImageVector? = null
