package com.shihcheeng.sspaiyou.ui.screen.column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun ColumnArticleItem(
    article: ColumnArticle,
    onClick: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = remember(article.releasedTime) {
        article.releasedTime.takeIf { it > 0 }?.let {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(it))
        }
    }
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        if (article.banner.isNotBlank()) {
            AsyncImage(
                model = imageUrlForNullable(article.banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
            )
        }
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                article.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            ColumnUserItem(
                nickname = article.author.nickname,
                avatar = article.author.avatar,
                slug = article.author.slug,
                onUserClick = onUserClick
            )
            if (article.summary.isNotBlank()) {
                Text(
                    article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                date?.let { Text(it, style = MaterialTheme.typography.labelMedium) }
                Text(
                    stringResource(R.string.column_article_likes, article.likeCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    stringResource(R.string.column_article_comments, article.commentCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColumnArticleItemPreview() {
    AppTheme {
        ColumnArticleItem(
            article = ColumnArticle(
                title = "App+1 | 用 Remio 自动化、智能化管理知识",
                summary = "分享科研、教学日常中使用的工具与技术。",
                author = ColumnUser(nickname = "玉树芝兰"),
                releasedTime = 1748143658L,
                likeCount = 50,
                commentCount = 15
            ),
            onClick = {},
            onUserClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
