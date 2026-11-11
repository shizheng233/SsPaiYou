package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
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
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentFile
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentReply
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentUser
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun ArticleCommentItem(
    comment: ArticleComment,
    onAvatarClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImageClick: ((String) -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column {
            CommentEntry(
                user = comment.user,
                content = comment.comment,
                files = comment.commentFiles,
                createdAt = comment.createdAt,
                liked = comment.liked,
                likesCount = comment.likesCount,
                isAuthor = comment.isAuthor,
                top = comment.top,
                chosen = comment.chosen,
                hidden = comment.blocked || comment.hide || comment.authorBlocked,
                onAvatarClick = onAvatarClick,
                onImageClick = onImageClick
            )
            if (comment.replyShow && comment.reply.isNotEmpty()) {
                Surface(
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.article_comment_replies, comment.reply.size),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
                        )
                        comment.reply.forEachIndexed { index, reply ->
                            key(reply.id) {
                                if (index > 0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                                CommentEntry(
                                    user = reply.user,
                                    content = reply.comment,
                                    files = reply.commentFiles,
                                    createdAt = reply.createdAt,
                                    liked = reply.liked,
                                    likesCount = reply.likesCount,
                                    isAuthor = reply.isAuthor,
                                    top = reply.top,
                                    chosen = reply.chosen,
                                    hidden = reply.blocked || reply.hide || reply.authorBlocked,
                                    onAvatarClick = onAvatarClick,
                                    onImageClick = onImageClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentEntry(
    user: ArticleCommentUser,
    content: String,
    files: List<ArticleCommentFile>,
    createdAt: Long,
    liked: Boolean,
    likesCount: Int,
    isAuthor: Boolean,
    top: Boolean,
    chosen: Boolean,
    hidden: Boolean,
    onAvatarClick: (String) -> Unit,
    onImageClick: ((String) -> Unit)?
) {
    val nickname = user.nickname.ifBlank { stringResource(R.string.article_comment_unknown_user) }
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
                if (isAuthor) CommentLabel(stringResource(R.string.article_comment_author))
                if (top) CommentLabel(stringResource(R.string.article_comment_pinned))
                if (chosen) CommentLabel(stringResource(R.string.article_comment_chosen))
            }
            SelectionContainer {
                if (hidden) {
                    Text(
                        text = stringResource(R.string.article_comment_hidden),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ArticleCommentMarkdown(content = content, modifier = Modifier.fillMaxWidth())
                }
            }
            if (!hidden && files.any { it.filePath.isNotBlank() }) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    files.forEachIndexed { index, file ->
                        if (file.filePath.isNotBlank()) {
                            val imageUrl = imageUrlFor(file.filePath)
                            val openImageLabel = stringResource(R.string.article_image_open)
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = stringResource(R.string.article_comment_attachment, index + 1),
                                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                                error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(80.dp)
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.small)
                                    .then(
                                        if (onImageClick != null) {
                                            Modifier.clickable(role = Role.Button, onClickLabel = openImageLabel) {
                                                onImageClick(imageUrl)
                                            }
                                        } else Modifier
                                    )
                            )
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (createdAt > 0) {
                    Text(
                        text = createdAt.calculateDay(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (likesCount > 0) {
                    Text(
                        text = stringResource(R.string.article_comment_likes, likesCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (liked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ArticleCommentItemPreview() {
    AppTheme {
        ArticleCommentItem(
            comment = ArticleComment(
                id = 1,
                comment = "UI 绘制得还挺不错，每套主题有对应的夜间模式吗？",
                user = ArticleCommentUser(nickname = "hAPPydOg", slug = "ub29du0l"),
                createdAt = 1793953905L,
                likesCount = 12,
                commentFiles = listOf(
                    ArticleCommentFile(
                        id = 6699,
                        filePath = "https://cdnfile.sspai.com/2026/11/11/b851697920c4ca86651ca46d6bdbe8d3.png"
                    )
                ),
                replyShow = true,
                reply = listOf(
                    ArticleCommentReply(
                        id = 396692,
                        comment = "Dark mode 调满意了再加回去😆\n也可以先试试 <a href=\"https://minesweeper.cn/\">网页版扫雷</a>。",
                        user = ArticleCommentUser(nickname = "bakamio", slug = "vst7qe4v"),
                        isAuthor = true,
                        createdAt = 1794292474L
                    )
                )
            ),
            onAvatarClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
