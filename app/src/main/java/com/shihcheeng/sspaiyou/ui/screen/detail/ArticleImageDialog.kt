package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shihcheeng.sspaiyou.R

@Composable
fun ArticleImageDialog(url: String, onDismiss: () -> Unit) {
    val visibility = remember(url) { MutableTransitionState(false).apply { targetState = true } }
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val dismiss = { visibility.targetState = false }
    // Keep the dialog (and its image source) alive until the exit transition has finished.
    LaunchedEffect(visibility.isIdle, visibility.currentState, visibility.targetState) {
        if (visibility.isIdle && !visibility.currentState && !visibility.targetState) {
            currentOnDismiss()
        }
    }
    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        AnimatedVisibility(
            visibleState = visibility,
            enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.96f),
            exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.96f)
        ) {
            val title = stringResource(R.string.article_image_fullscreen)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .semantics { paneTitle = title }
            ) {
                ArticleZoomableImage(url = url, modifier = Modifier.fillMaxSize(), onTap = dismiss)
                IconButton(
                    onClick = dismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(all = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.column_close_24),
                        contentDescription = stringResource(R.string.article_image_close),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
