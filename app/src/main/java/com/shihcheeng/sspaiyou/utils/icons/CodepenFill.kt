package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.CodepenFill: ImageVector
    get() {
        if (_CodepenFill != null) {
            return _CodepenFill!!
        }
        _CodepenFill = ImageVector.Builder(
            name = "CodepenFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 10.202f)
                lineTo(9.303f, 12f)
                lineTo(12f, 13.798f)
                lineTo(14.697f, 12f)
                lineTo(12f, 10.202f)
                close()
                moveTo(16.5f, 10.798f)
                lineTo(19.197f, 9f)
                lineTo(13f, 4.869f)
                verticalLineTo(8.465f)
                lineTo(16.5f, 10.798f)
                close()
                moveTo(20f, 10.868f)
                lineTo(18.303f, 12f)
                lineTo(20f, 13.132f)
                verticalLineTo(10.868f)
                close()
                moveTo(16.5f, 13.202f)
                lineTo(13f, 15.535f)
                verticalLineTo(19.132f)
                lineTo(19.197f, 15f)
                lineTo(16.5f, 13.202f)
                close()
                moveTo(11f, 8.465f)
                verticalLineTo(4.869f)
                lineTo(4.803f, 9f)
                lineTo(7.5f, 10.798f)
                lineTo(11f, 8.465f)
                close()
                moveTo(4.803f, 15f)
                lineTo(11f, 19.132f)
                verticalLineTo(15.535f)
                lineTo(7.5f, 13.202f)
                lineTo(4.803f, 15f)
                close()
                moveTo(5.697f, 12f)
                lineTo(4f, 10.868f)
                verticalLineTo(13.132f)
                lineTo(5.697f, 12f)
                close()
                moveTo(2f, 9f)
                curveTo(2f, 8.666f, 2.167f, 8.353f, 2.445f, 8.168f)
                lineTo(11.445f, 2.168f)
                curveTo(11.781f, 1.944f, 12.219f, 1.944f, 12.555f, 2.168f)
                lineTo(21.555f, 8.168f)
                curveTo(21.833f, 8.353f, 22f, 8.666f, 22f, 9f)
                verticalLineTo(15f)
                curveTo(22f, 15.334f, 21.833f, 15.647f, 21.555f, 15.832f)
                lineTo(12.555f, 21.832f)
                curveTo(12.219f, 22.056f, 11.781f, 22.056f, 11.445f, 21.832f)
                lineTo(2.445f, 15.832f)
                curveTo(2.167f, 15.647f, 2f, 15.334f, 2f, 15f)
                verticalLineTo(9f)
                close()
            }
        }.build()

        return _CodepenFill!!
    }

@Suppress("ObjectPropertyName")
private var _CodepenFill: ImageVector? = null
