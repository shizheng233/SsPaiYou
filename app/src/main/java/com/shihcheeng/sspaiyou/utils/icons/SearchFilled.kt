package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.SearchFilled: ImageVector
    get() {
        if (_SearchFilled != null) {
            return _SearchFilled!!
        }
        _SearchFilled = ImageVector.Builder(
            name = "SearchFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14f, 3.072f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.32f, 11.834f)
                lineToRelative(5.387f, 5.387f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.414f, 1.414f)
                lineToRelative(-5.388f, -5.387f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, -0.905f, -13.249f)
            }
        }.build()

        return _SearchFilled!!
    }

@Suppress("ObjectPropertyName")
private var _SearchFilled: ImageVector? = null
