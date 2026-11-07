package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.ListUnordered: ImageVector
    get() {
        if (_ListUnordered != null) {
            return _ListUnordered!!
        }
        _ListUnordered = ImageVector.Builder(
            name = "ListUnordered",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(8f, 4f)
                horizontalLineTo(21f)
                verticalLineTo(6f)
                horizontalLineTo(8f)
                verticalLineTo(4f)
                close()
                moveTo(4.5f, 6.5f)
                curveTo(3.672f, 6.5f, 3f, 5.828f, 3f, 5f)
                curveTo(3f, 4.172f, 3.672f, 3.5f, 4.5f, 3.5f)
                curveTo(5.328f, 3.5f, 6f, 4.172f, 6f, 5f)
                curveTo(6f, 5.828f, 5.328f, 6.5f, 4.5f, 6.5f)
                close()
                moveTo(4.5f, 13.5f)
                curveTo(3.672f, 13.5f, 3f, 12.828f, 3f, 12f)
                curveTo(3f, 11.172f, 3.672f, 10.5f, 4.5f, 10.5f)
                curveTo(5.328f, 10.5f, 6f, 11.172f, 6f, 12f)
                curveTo(6f, 12.828f, 5.328f, 13.5f, 4.5f, 13.5f)
                close()
                moveTo(4.5f, 20.4f)
                curveTo(3.672f, 20.4f, 3f, 19.728f, 3f, 18.9f)
                curveTo(3f, 18.072f, 3.672f, 17.4f, 4.5f, 17.4f)
                curveTo(5.328f, 17.4f, 6f, 18.072f, 6f, 18.9f)
                curveTo(6f, 19.728f, 5.328f, 20.4f, 4.5f, 20.4f)
                close()
                moveTo(8f, 11f)
                horizontalLineTo(21f)
                verticalLineTo(13f)
                horizontalLineTo(8f)
                verticalLineTo(11f)
                close()
                moveTo(8f, 18f)
                horizontalLineTo(21f)
                verticalLineTo(20f)
                horizontalLineTo(8f)
                verticalLineTo(18f)
                close()
            }
        }.build()

        return _ListUnordered!!
    }

@Suppress("ObjectPropertyName")
private var _ListUnordered: ImageVector? = null
