package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.BookmarkAdded: ImageVector
    get() {
        if (_BookmarkAdded != null) {
            return _BookmarkAdded!!
        }
        _BookmarkAdded = ImageVector.Builder(
            name = "BookmarkAdded",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(713f, 360f)
                lineTo(600f, 247f)
                lineToRelative(56f, -57f)
                lineToRelative(57f, 57f)
                lineToRelative(141f, -142f)
                lineToRelative(57f, 57f)
                lineToRelative(-198f, 198f)
                close()
                moveTo(200f, 840f)
                verticalLineToRelative(-640f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(280f, 120f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                lineTo(280f, 200f)
                verticalLineToRelative(518f)
                lineToRelative(200f, -86f)
                lineToRelative(200f, 86f)
                verticalLineToRelative(-278f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(400f)
                lineTo(480f, 720f)
                lineTo(200f, 840f)
                close()
                moveTo(280f, 200f)
                horizontalLineToRelative(240f)
                horizontalLineToRelative(-240f)
                close()
            }
        }.build()

        return _BookmarkAdded!!
    }

@Suppress("ObjectPropertyName")
private var _BookmarkAdded: ImageVector? = null
