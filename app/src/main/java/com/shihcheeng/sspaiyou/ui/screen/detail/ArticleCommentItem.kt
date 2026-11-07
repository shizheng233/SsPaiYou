package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentUser
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun ArticleCommentItem(
    comment: ArticleComment,
    onAvatarClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user = comment.user
    val nickname = user.nickname.ifBlank { stringResource(R.string.article_comment_unknown_user) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        enabled = user.slug.isNotBlank() && !user.deleted,
                        role = Role.Button,
                        onClickLabel = stringResource(R.string.article_comment_view_user, nickname),
                        onClick = { onAvatarClick(user.slug) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrlFor(user.avatar),
                    contentDescription = stringResource(R.string.article_comment_view_user, nickname),
                    placeholder = painterResource(R.drawable.baseline_person_outline_24),
                    error = painterResource(R.drawable.baseline_person_outline_24),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nickname,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (comment.isAuthor) CommentLabel(stringResource(R.string.article_comment_author))
                    if (comment.top) CommentLabel(stringResource(R.string.article_comment_pinned))
                    if (comment.chosen) CommentLabel(stringResource(R.string.article_comment_chosen))
                }
                val hidden = comment.blocked || comment.hide || comment.authorBlocked
                SelectionContainer {
                    Text(
                        text = if (hidden) stringResource(R.string.article_comment_hidden) else comment.comment.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (hidden) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (comment.createdAt > 0) {
                        Text(
                            text = comment.createdAt.calculateDay(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (comment.likesCount > 0) {
                        Text(
                            text = stringResource(R.string.article_comment_likes, comment.likesCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (comment.liked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentLabel(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleCommentItemPreview() {
    AppTheme {
        ArticleCommentItem(
            comment = ArticleComment(
                id = 1,
                comment = "在用 m4 的 MBA，期待下一代 Mac mini。\n小巧的桌面主机很适合日常使用。",
                user = ArticleCommentUser(nickname = "派友", slug = "sspai"),
                createdAt = 1793953905L,
                likesCount = 12,
                isAuthor = true
            ),
            onAvatarClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
