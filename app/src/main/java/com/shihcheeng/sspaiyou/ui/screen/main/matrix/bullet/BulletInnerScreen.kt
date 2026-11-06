package com.shihcheeng.sspaiyou.ui.screen.main.matrix.bullet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.utils.imageUrlFor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.shihcheeng.sspaiyou.ui.compoents.Error as ErrorScreen

@Composable
fun BulletInnerScreen(
    modifier: Modifier = Modifier,
    viewModel: BulletViewModel = hiltViewModel(),
    onBulletClick: (Long) -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()
    TriStateScreen(
        modifier = modifier.fillMaxSize(),
        triState = data.loadState.refresh,
        onError = { error ->
            ErrorScreen(error = error.localizedMessage, onClick = data::retry)
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (data.itemCount == 0) {
                item(key = "empty") {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.bullet_empty))
                        TextButton(onClick = data::refresh) { Text(stringResource(R.string.retry)) }
                    }
                }
            }
            items(
                count = data.itemCount,
                key = data.itemKey { "${it.vol}:${it.createdAt}:${it.title}" },
                contentType = { "bullet" }
            ) { index ->
                data[index]?.let { BulletItem(topic = it, onBulletClick = onBulletClick) }
            }
            pagingTriState(
                paging = data.loadState.append,
                onLoading = { LoadingItem() },
                onError = { ErrorItem(error = it.localizedMessage, onClick = data::retry) }
            ) {}
        }
    }
}

@Composable
private fun BulletItem(
    topic: BulletData,
    modifier: Modifier = Modifier,
    onBulletClick: (Long) -> Unit
) {
    var expanded by rememberSaveable(topic.vol, topic.createdAt, topic.title) { mutableStateOf(false) }
    val body = remember(topic.body) {
        HtmlCompat.fromHtml(topic.body, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
    }
    val deadline = remember(topic.voteEndTime) {
        if (topic.voteEndTime > 0) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(topic.voteEndTime))
        } else null
    }
    Card(
        onClick = { onBulletClick(topic.createdAt) },
        modifier = modifier.fillMaxWidth()
    ) {
        if (topic.banner.isNotBlank()) {
            AsyncImage(
                model = imageUrlFor(topic.banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth().aspectRatio(2f)
            )
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(if (topic.type == 2) R.string.bullet_vote else R.string.bullet_discussion),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (topic.vol.isNotBlank()) {
                    Text(stringResource(R.string.bullet_volume, topic.vol), style = MaterialTheme.typography.labelMedium)
                }
            }
            Text(topic.title, style = MaterialTheme.typography.titleLarge)
            topic.authors.firstOrNull()?.let { author ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = imageUrlFor(author.avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                    )
                    Text(author.nickname, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (body.isNotBlank()) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(onClick = { expanded = !expanded }) {
                    Text(stringResource(if (expanded) R.string.bullet_collapse else R.string.bullet_expand))
                }
            }
            if (topic.voteTitle.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(topic.voteTitle, style = MaterialTheme.typography.titleSmall)
                        deadline?.let {
                            Text(stringResource(R.string.bullet_vote_deadline, it), style = MaterialTheme.typography.bodySmall)
                        }
                        if (topic.voted) {
                            Text(stringResource(R.string.bullet_voted), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(R.string.bullet_participations, topic.participationsCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    stringResource(R.string.bullet_threads, topic.threadsCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BulletItemPreview() {
    MaterialTheme {
        BulletItem(
            topic = BulletData(
                title = "Share your travel tips",
                vol = "304",
                type = 2,
                body = "<p>How do you make the most of your travel budget?</p>",
                voteTitle = "Which travel strategy do you prefer?",
                participationsCount = 206,
                threadsCount = 16
            ),
            modifier = Modifier.padding(16.dp),
            onBulletClick = {}
        )
    }
}
