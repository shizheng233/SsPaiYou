package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.ArrowBackIos: ImageVector
    get() {
        if (_ArrowBackIos != null) {
            return _ArrowBackIos!!
        }
        _ArrowBackIos = ImageVector.Builder(
            name = "ArrowBackIos",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(400f, 880f)
                lineTo(0f, 480f)
                lineToRelative(400f, -400f)
                lineToRelative(71f, 71f)
                lineToRelative(-329f, 329f)
                lineToRelative(329f, 329f)
                lineToRelative(-71f, 71f)
                close()
            }
        }.build()

        return _ArrowBackIos!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowBackIos: ImageVector? = null
