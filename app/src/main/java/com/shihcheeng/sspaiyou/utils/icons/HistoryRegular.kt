package com.shihcheeng.sspaiyou.utils.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SSPaiPack.HistoryRegular: ImageVector
    get() {
        if (_HistoryRegular != null) {
            return _HistoryRegular!!
        }
        _HistoryRegular = ImageVector.Builder(
            name = "HistoryRegular",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 8f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-3f)
                verticalLineTo(8f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.292f, 8.497f)
                arcToRelative(8.957f, 8.957f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.928f, -2.862f)
                arcToRelative(9.004f, 9.004f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.55f, -2.452f)
                arcToRelative(9.09f, 9.09f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.626f, 0f)
                arcToRelative(8.965f, 8.965f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.552f, 2.453f)
                arcToRelative(9.048f, 9.048f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.928f, 2.86f)
                arcTo(8.963f, 8.963f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 12f)
                lineToRelative(0.001f, 0.025f)
                horizontalLineTo(2f)
                lineTo(5f, 16f)
                lineToRelative(3f, -3.975f)
                horizontalLineTo(6.001f)
                lineTo(6f, 12f)
                arcToRelative(6.957f, 6.957f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.195f, -3.913f)
                arcToRelative(7.066f, 7.066f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.891f, -1.892f)
                arcToRelative(7.034f, 7.034f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.503f, -1.054f)
                arcToRelative(7.003f, 7.003f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.269f, 5.445f)
                arcToRelative(7.117f, 7.117f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2.824f)
                arcToRelative(6.936f, 6.936f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.054f, 2.503f)
                curveToRelative(-0.25f, 0.371f, -0.537f, 0.72f, -0.854f, 1.036f)
                arcToRelative(7.058f, 7.058f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.225f, 1.501f)
                arcToRelative(6.98f, 6.98f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.313f, 0.408f)
                arcToRelative(7.117f, 7.117f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.823f, 0f)
                arcToRelative(6.957f, 6.957f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.501f, -1.053f)
                arcToRelative(7.066f, 7.066f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.037f, -0.855f)
                lineToRelative(-1.414f, 1.414f)
                arcTo(8.985f, 8.985f, 0f, isMoreThanHalf = false, isPositiveArc = false, 13f, 21f)
                arcToRelative(9.05f, 9.05f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.503f, -0.707f)
                arcToRelative(9.009f, 9.009f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.959f, -3.26f)
                arcTo(8.968f, 8.968f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22f, 12f)
                arcToRelative(8.928f, 8.928f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.708f, -3.503f)
                close()
            }
        }.build()

        return _HistoryRegular!!
    }

@Suppress("ObjectPropertyName")
private var _HistoryRegular: ImageVector? = null
