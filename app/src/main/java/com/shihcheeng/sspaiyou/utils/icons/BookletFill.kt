package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.BookletFill: ImageVector
    get() {
        if (_BookletFill != null) {
            return _BookletFill!!
        }
        _BookletFill = ImageVector.Builder(
            name = "BookletFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(8f, 2f)
                verticalLineTo(22f)
                horizontalLineTo(4f)
                verticalLineTo(18f)
                horizontalLineTo(2f)
                verticalLineTo(16f)
                horizontalLineTo(4f)
                verticalLineTo(13f)
                horizontalLineTo(2f)
                verticalLineTo(11f)
                horizontalLineTo(4f)
                verticalLineTo(8f)
                horizontalLineTo(2f)
                verticalLineTo(6f)
                horizontalLineTo(4f)
                verticalLineTo(2f)
                horizontalLineTo(8f)
                close()
                moveTo(20.005f, 2f)
                curveTo(21.107f, 2f, 22f, 2.898f, 22f, 3.991f)
                verticalLineTo(20.009f)
                curveTo(22f, 21.109f, 21.107f, 22f, 20.005f, 22f)
                horizontalLineTo(10f)
                verticalLineTo(2f)
                horizontalLineTo(20.005f)
                close()
            }
        }.build()

        return _BookletFill!!
    }

@Suppress("ObjectPropertyName")
private var _BookletFill: ImageVector? = null
