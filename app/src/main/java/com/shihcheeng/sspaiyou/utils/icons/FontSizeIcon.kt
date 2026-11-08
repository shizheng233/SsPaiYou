package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.FontSizeIcon: ImageVector
    get() {
        if (_FontSizeIcon != null) {
            return _FontSizeIcon!!
        }
        _FontSizeIcon = ImageVector.Builder(
            name = "FontSizeIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.246f, 15f)
                horizontalLineTo(4.754f)
                lineTo(2.754f, 20f)
                horizontalLineTo(0.6f)
                lineTo(7f, 4f)
                horizontalLineTo(9f)
                lineTo(15.4f, 20f)
                horizontalLineTo(13.246f)
                lineTo(11.246f, 15f)
                close()
                moveTo(10.446f, 13f)
                lineTo(8f, 6.885f)
                lineTo(5.554f, 13f)
                horizontalLineTo(10.446f)
                close()
                moveTo(21f, 12.535f)
                verticalLineTo(12f)
                horizontalLineTo(23f)
                verticalLineTo(20f)
                horizontalLineTo(21f)
                verticalLineTo(19.465f)
                curveTo(20.412f, 19.805f, 19.729f, 20f, 19f, 20f)
                curveTo(16.791f, 20f, 15f, 18.209f, 15f, 16f)
                curveTo(15f, 13.791f, 16.791f, 12f, 19f, 12f)
                curveTo(19.729f, 12f, 20.412f, 12.195f, 21f, 12.535f)
                close()
                moveTo(19f, 18f)
                curveTo(20.105f, 18f, 21f, 17.105f, 21f, 16f)
                curveTo(21f, 14.895f, 20.105f, 14f, 19f, 14f)
                curveTo(17.896f, 14f, 17f, 14.895f, 17f, 16f)
                curveTo(17f, 17.105f, 17.896f, 18f, 19f, 18f)
                close()
            }
        }.build()

        return _FontSizeIcon!!
    }

@Suppress("ObjectPropertyName")
private var _FontSizeIcon: ImageVector? = null
