package com.shihcheeng.sspaiyou.ui.screen.bullet.comment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.bullet.BulletUser
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.ui.compoents.ReaderImage
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** A non-scrolling comment item. [onUserClick] receives the author's nonblank slug. */
@Composable
fun BulletCommentItem(
    comment: BulletComment,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val zone = ZoneId.systemDefault()
    val date = remember(comment.createdAt, locale, zone) {
        if (comment.createdAt > 0L) {
            runCatching {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", locale)
                    .withZone(zone)
                    .format(Instant.ofEpochSecond(comment.createdAt))
            }.getOrNull()
        } else null
    }
    val authorName = comment.author.nickname.ifBlank {
        stringResource(R.string.bullet_comment_unknown_author)
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                        enabled = comment.author.slug.isNotBlank(),
                        role = Role.Button,
                        onClickLabel = stringResource(R.string.bullet_comment_view_author, authorName),
                        onClick = { onUserClick(comment.author.slug) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = imageUrlForNullable(comment.author.avatar.takeIf { it.isNotBlank() }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = date ?: stringResource(R.string.bullet_comment_unknown_date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (comment.top || comment.chosen) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOfNotNull(
                    R.string.bullet_comment_top.takeIf { comment.top },
                    R.string.bullet_comment_chosen.takeIf { comment.chosen }
                ).forEach { label ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = stringResource(label),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (comment.blocked) {
            Text(
                text = stringResource(R.string.bullet_comment_hidden),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            if (comment.body.isNotBlank()) {
                SelectionContainer {
                    Text(
                        text = comment.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                comment.files.forEachIndexed { index, file ->
                    if (file.filePath.isNotBlank()) {
                        ReaderImage(
                            url = file.filePath,
                            contentDescription = stringResource(R.string.bullet_comment_attachment, index + 1)
                        )
                    }
                }
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.bullet_comment_likes, comment.likesCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.bullet_comment_replies, comment.commentsCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun BulletCommentItemPreview() {
    val comment = BulletComment(
        author = BulletUser(slug = "sample-author", nickname = "Sample Author"),
        body = "A useful travel tip.\n\n**These markers stay plain text.**\nA second line with more detail.",
        createdAt = 1_783_065_600L,
        top = true,
        chosen = true,
        likesCount = 24,
        commentsCount = 3
    )
    AppTheme(dynamicColor = false) {
        Surface {
            Column {
                BulletCommentItem(comment = comment, onUserClick = {})
                BulletCommentItem(comment = comment.copy(blocked = true), onUserClick = {})
            }
        }
    }
}
