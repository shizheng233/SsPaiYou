package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import soup.compose.material.motion.animation.materialFadeThrough

@Composable
fun Pager(
    modifier: Modifier = Modifier,
    saveableStateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    index: Int,
    content: @Composable AnimatedContentScope.(index: Int) -> Unit
) {
    AnimatedContent(
        targetState = index,
        modifier = modifier
            .fillMaxSize(),
        label = "pager_inner",
        transitionSpec = {
            materialFadeThrough(300)
        }
    ) { indexInner ->
        saveableStateHolder.SaveableStateProvider("page_inner_as_$indexInner") {
            content(indexInner)
        }
    }
}