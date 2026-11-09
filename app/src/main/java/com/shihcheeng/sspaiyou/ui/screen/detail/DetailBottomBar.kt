package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.icons.Bookmark
import com.shihcheeng.sspaiyou.utils.icons.BookmarkAdded
import com.shihcheeng.sspaiyou.utils.icons.FlashOn
import com.shihcheeng.sspaiyou.utils.icons.IosShare
import com.shihcheeng.sspaiyou.utils.icons.ListUnordered
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack

@Composable
fun DetailBottomBar(
    modifier: Modifier = Modifier,
    commentCount: Int,
    likeCount: Int,
    onShareCommit: () -> Unit,
    onBookmarkClick: () -> Unit,
    onTocClick: () -> Unit,
    onViewComments: () -> Unit,
    onLikeClick: () -> Unit,
    isLike: Boolean,
    isBookmarked: Boolean = false,
    bookmarkEnabled: Boolean = true,
    likeEnabled: Boolean = true
) {
    val color = MaterialTheme.colorScheme
    val typo = MaterialTheme.typography
    FlexibleBottomAppBar(
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 12.dp),
            shape = CircleShape,
            color = color.surfaceContainerHigh
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.comments),
                    modifier = Modifier.padding(start = 16.dp),
                    style = typo.labelMedium
                )
            }
        }

        IconButton(onClick = onViewComments) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_message_24),
                    contentDescription = null
                )
                Text(
                    text = "(${commentCount})",
                    style = typo.labelSmall
                )
            }
        }
        IconToggleButton(
            onCheckedChange = { onLikeClick() },
            checked = isLike,
            enabled = likeEnabled
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = if (isLike) {
                        painterResource(R.drawable.outline_flash_on_24)
                    } else {
                        rememberVectorPainter(SSPaiPack.FlashOn)
                    },
                    contentDescription = stringResource(
                        if (isLike) R.string.article_unlike else R.string.article_like
                    )
                )
                Text(
                    text = "(${likeCount})",
                    style = typo.labelSmall
                )
            }
        }
        IconToggleButton(
            checked = isBookmarked,
            onCheckedChange = { onBookmarkClick() },
            enabled = bookmarkEnabled
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isBookmarked) SSPaiPack.BookmarkAdded else SSPaiPack.Bookmark,
                    contentDescription = null
                )
                Text(
                    text = stringResource(
                        id = if (isBookmarked) {
                            R.string.article_favourited
                        } else {
                            R.string.favourite_of_user
                        }
                    ),
                    style = typo.labelSmall
                        .copy(
                            fontWeight = FontWeight.Thin,
                            fontSize = 10.sp
                        )
                )
            }
        }
        IconButton(onClick = onShareCommit) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = SSPaiPack.IosShare,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.share_text),
                    style = typo.labelSmall
                        .copy(
                            fontWeight = FontWeight.Thin,
                            fontSize = 10.sp
                        )
                )
            }
        }
        IconButton(onClick = onTocClick) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = SSPaiPack.ListUnordered,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.toc),
                    style = typo.labelSmall
                        .copy(
                            fontWeight = FontWeight.Thin,
                            fontSize = 10.sp
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun DetailBottomBarPreview() {
    AppTheme {
        DetailBottomBar(
            commentCount = 24,
            onViewComments = {},
            likeCount = 12,
            onShareCommit = {},
            onBookmarkClick = { },
            onTocClick = { },
            onLikeClick = {},
            isLike = false
        )
    }
}
