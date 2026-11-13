package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.SavedSearch: ImageVector
    get() {
        if (_SavedSearch != null) {
            return _SavedSearch!!
        }
        _SavedSearch = ImageVector.Builder(
            name = "SavedSearch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveToRelative(380f, 438f)
                lineToRelative(-55f, 43f)
                quadToRelative(-6f, 5f, -12f, 0.5f)
                reflectiveQuadToRelative(-4f, -11.5f)
                lineToRelative(21f, -68f)
                lineToRelative(-58f, -46f)
                quadToRelative(-5f, -5f, -2.5f, -11.5f)
                reflectiveQuadToRelative(9.5f, -6.5f)
                horizontalLineToRelative(69f)
                lineToRelative(22f, -69f)
                quadToRelative(2f, -7f, 10f, -7f)
                reflectiveQuadToRelative(10f, 7f)
                lineToRelative(22f, 69f)
                horizontalLineToRelative(69f)
                quadToRelative(7f, 0f, 9.5f, 6.5f)
                reflectiveQuadTo(488f, 356f)
                lineToRelative(-58f, 46f)
                lineToRelative(21f, 68f)
                quadToRelative(2f, 7f, -4f, 11.5f)
                reflectiveQuadToRelative(-12f, -0.5f)
                lineToRelative(-55f, -43f)
                close()
                moveTo(380f, 640f)
                quadToRelative(-109f, 0f, -184.5f, -75.5f)
                reflectiveQuadTo(120f, 380f)
                quadToRelative(0f, -109f, 75.5f, -184.5f)
                reflectiveQuadTo(380f, 120f)
                quadToRelative(109f, 0f, 184.5f, 75.5f)
                reflectiveQuadTo(640f, 380f)
                quadToRelative(0f, 44f, -14f, 83f)
                reflectiveQuadToRelative(-38f, 69f)
                lineToRelative(224f, 224f)
                quadToRelative(11f, 11f, 11f, 28f)
                reflectiveQuadToRelative(-11f, 28f)
                quadToRelative(-11f, 11f, -28f, 11f)
                reflectiveQuadToRelative(-28f, -11f)
                lineTo(532f, 588f)
                quadToRelative(-30f, 24f, -69f, 38f)
                reflectiveQuadToRelative(-83f, 14f)
                close()
                moveTo(380f, 560f)
                quadToRelative(75f, 0f, 127.5f, -52.5f)
                reflectiveQuadTo(560f, 380f)
                quadToRelative(0f, -75f, -52.5f, -127.5f)
                reflectiveQuadTo(380f, 200f)
                quadToRelative(-75f, 0f, -127.5f, 52.5f)
                reflectiveQuadTo(200f, 380f)
                quadToRelative(0f, 75f, 52.5f, 127.5f)
                reflectiveQuadTo(380f, 560f)
                close()
            }
        }.build()

        return _SavedSearch!!
    }

@Suppress("ObjectPropertyName")
private var _SavedSearch: ImageVector? = null
