package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.shihcheeng.sspaiyou.R

@Composable
fun DetailReadingToolBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    titleShowable: Boolean,
    showProgressBar: Boolean,
    title: String?,
    progress: () -> Float,
    onNavBack: () -> Unit
) {

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                AnimatedVisibility(
                    visible = titleShowable && title != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = title.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(
                    onClick = onNavBack
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = stringResource(R.string.back_to_up)
                    )
                }
            },
        )
        AnimatedVisibility(
            visible = showProgressBar && title != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val progressDescription = stringResource(R.string.article_reading_progress)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = progressDescription },
                progress = progress
            )
        }
    }
}
