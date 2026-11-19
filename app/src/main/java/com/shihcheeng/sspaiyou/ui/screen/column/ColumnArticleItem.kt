package com.shihcheeng.sspaiyou.ui.screen.column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.extra.click
import com.shihcheeng.sspaiyou.utils.extra.timestampToChinese
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable

@Composable
fun ColumnArticleItem(
    article: ColumnArticle,
    onClick: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .click{
                    onUserClick(article.author.slug)
                }
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = imageUrlForNullable(article.author.avatar.takeIf { it.isNotBlank() }),
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = article.author.nickname,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${article.releasedTime.calculateDay()} ${article.releasedTime.timestampToChinese()}",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(
                    top = 16.dp, bottom = 22.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (article.summary.isNotBlank()) {
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
                        .clip(MaterialTheme.shapes.medium),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_flash_on_24),
                    contentDescription = stringResource(R.string.column_article_likes, article.likeCount),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = article.likeCount.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(R.drawable.outline_message_24),
                    contentDescription = stringResource(R.string.column_article_comments, article.likeCount),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = article.likeCount.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
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
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}
