package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import kotlinx.coroutines.flow.Flow

@Composable
fun ArticleBlocksCommentDialog(
    comments: Flow<PagingData<ArticleComment>>,
    commentCount: Int,
    onDismiss: () -> Unit,
    onAvatarClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
) {
    ArticleCommentsSheet(
        comments = comments.collectAsLazyPagingItems(),
        commentCount = commentCount,
        onDismiss = onDismiss,
        onAvatarClick = onAvatarClick,
        onImageClick = onImageClick,
    )
}
