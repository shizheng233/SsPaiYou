package com.shihcheeng.sspaiyou.ui.screen.favourite

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@StringRes
internal fun ColumnArticle.favouriteTypeLabel(): Int = when {
    podcastDuration > 0 -> R.string.favourite_type_podcast
    belongToMember -> R.string.favourite_type_member_article
    !free -> R.string.favourite_type_paid_article
    else -> R.string.type_obj_1
}

@Composable
internal fun FavouriteArticleItem(
    article: ColumnArticle,
    onClick: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(article.favouriteTypeLabel()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (article.author.nickname.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable(
                                enabled = article.author.slug.isNotBlank(),
                                onClickLabel = stringResource(
                                    R.string.article_comment_view_user,
                                    article.author.nickname
                                ),
                                onClick = { onUserClick(article.author.slug) }
                            )
                            .heightIn(min = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = imageUrlForNullable(article.author.avatar.takeIf(String::isNotBlank)),
                            contentDescription = null,
                            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                            error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(24.dp).clip(CircleShape)
                        )
                        Text(
                            text = article.author.nickname,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (article.banner.isNotBlank()) {
                AsyncImage(
                    model = imageUrlForNullable(article.banner),
                    contentDescription = null,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FavouriteArticleItemPreview() {
    AppTheme {
        FavouriteArticleItem(
            article = ColumnArticle(
                title = "App+1 | 用 Remio 自动化、智能化管理知识",
                banner = "preview-cover",
                free = true,
                author = ColumnUser(nickname = "玉树芝兰", slug = "a5xddvxl")
            ),
            onClick = {},
            onUserClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
