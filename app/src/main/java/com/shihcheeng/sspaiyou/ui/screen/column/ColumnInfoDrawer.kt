package com.shihcheeng.sspaiyou.ui.screen.column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.Author
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable

@Composable
fun ColumnInfoDrawer(
    info: NetState<ColumnInfo>,
    onRetry: () -> Unit,
    onClose: () -> Unit,
    onUserClick: (String) -> Unit,
    onFollowChange: (Boolean) -> Unit,
    followPending: Boolean = false,
    followEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.column_info),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onClose) {
                Icon(
                    painterResource(R.drawable.column_close_24),
                    contentDescription = stringResource(R.string.column_close_info)
                )
            }
        }
        when (info) {
            NetState.Loading -> ColumnLoading(Modifier.weight(1f))
            is NetState.Error -> Error(error = info.throwable.localizedMessage, onClick = onRetry)
            is NetState.Success -> ColumnInfoContent(info.content, onUserClick, onFollowChange, followPending, followEnabled)
        }
    }
}

@Composable
private fun ColumnInfoContent(
    info: ColumnInfo,
    onUserClick: (String) -> Unit,
    onFollowChange: (Boolean) -> Unit,
    followPending: Boolean,
    followEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (info.banner.isNotBlank()) {
            AsyncImage(
                model = imageUrlForNullable(info.banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(MaterialTheme.shapes.extraLarge)
            )
        }
        Text(info.title, style = MaterialTheme.typography.headlineMedium)
        FilledTonalButton(
            onClick = { onFollowChange(!info.followed) },
            enabled = followEnabled && !followPending && info.id > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(if (info.followed) R.string.column_unfollow else R.string.add_to_list))
        }
        Text(
            info.intro.ifBlank { stringResource(R.string.no_desc) },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColumnStatistic(info.articleCount, stringResource(R.string.column_stat_articles))
            ColumnStatistic(info.followCount, stringResource(R.string.column_stat_followers))
            ColumnStatistic(info.authorCount, stringResource(R.string.column_stat_authors))
        }
        HorizontalDivider()
        Text(
            stringResource(R.string.column_chief_editor),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        ColumnUserItem(
            nickname = info.chiefEditor.nickname,
            avatar = info.chiefEditor.avatar,
            slug = info.chiefEditor.slug,
            onUserClick = onUserClick
        )
        if (info.followUsers.isNotEmpty()) {
            HorizontalDivider()
            Text(
                stringResource(R.string.column_follow_users),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            info.followUsers.forEach { user ->
                ColumnUserItem(user.nickname, user.avatar, user.slug, onUserClick)
            }
        }
    }
}

@Composable
private fun ColumnStatistic(count: Int, label: String) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(count.toString(), style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
internal fun ColumnUserItem(
    nickname: String,
    avatar: String,
    slug: String,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onUserClick(slug) },
        enabled = slug.isNotBlank(),
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minHeight = 56.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = imageUrlForNullable(avatar.takeIf { it.isNotBlank() }),
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Text(
                nickname,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ColumnInfoDrawerPreview() {
    AppTheme {
        Surface {
            ColumnInfoDrawer(
                info = NetState.Success(
                    ColumnInfo(
                        title = "科研利器",
                        intro = "分享科研、教学日常中使用的工具与技术",
                        chiefEditor = Author(nickname = "玉树芝兰", slug = "a5xddvxl"),
                        articleCount = 54,
                        followCount = 4305,
                        authorCount = 2
                    )
                ),
                onRetry = {},
                onClose = {},
                onUserClick = {},
                onFollowChange = {}
            )
        }
    }
}
