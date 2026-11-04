package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.mikepenz.markdown.model.MarkdownImageState

internal class MarkdownImageStateImpl(override val density: Density) : MarkdownImageState {

    override var containerSize by mutableStateOf(Size.Unspecified)

    override var intrinsicImageSize by mutableStateOf(Size.Unspecified)

    override fun updateContainerSize(size: Size) {
        containerSize = size
    }

    override fun updateImageSize(size: Size) {
        intrinsicImageSize = size
    }


}

@Composable
internal fun rememberMarkdownImageState(): MarkdownImageState {
    val density = LocalDensity.current
    return remember(density) { MarkdownImageStateImpl(density) }
}