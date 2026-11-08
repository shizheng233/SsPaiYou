package com.shihcheeng.sspaiyou.ui.screen.bullet.single

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownPadding
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.api.data.bullet.BulletUser
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.ReaderImage
import com.shihcheeng.sspaiyou.ui.compoents.ReaderMarkdownImage
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.screen.bullet.comment.BulletCommentItem
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailContent
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailImageTransform
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkdownParagraph
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypo
import com.shihcheeng.sspaiyou.ui.theme.toMarkDownColors
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun BulletSingleScreen(
    viewModel: BulletSingleViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
    onNavUp: () -> Unit
) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    val comments = viewModel.comments.collectAsLazyPagingItems()
    val setting = LocalComposeSetting.current
    val readerFontScale by setting.observe(SettingData.readerScale)
        .collectAsStateWithLifecycle(initialValue = 1.15f)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.onep)) },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        TriStateScreen(
            triState = data,
            padding = padding,
            onError = { Error(error = it.localizedMessage, onClick = viewModel::load) }
        ) { content ->
            BulletSingleScreenContent(
                content = content,
                readerFontScale = readerFontScale,
                comments = comments,
                onUserClick = onUserClick
            )
        }
    }
}

@Composable
private fun BulletSingleScreenContent(
    content: BulletSingleContent,
    readerFontScale: Float,
    comments: LazyPagingItems<BulletComment>,
    onUserClick: (String) -> Unit
) {
    val topic = content.topic
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (topic.banner.isNotBlank()) {
            item(key = "banner", contentType = "image") {
                ReaderImage(url = topic.banner, shape = RectangleShape)
            }
        }
        item(key = "header", contentType = "header") {
            Column(
                Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        stringResource(if (topic.type == 2) R.string.bullet_vote else R.string.bullet_discussion),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (topic.vol.isNotBlank()) {
                        Text(
                            stringResource(R.string.bullet_volume, topic.vol),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Text(
                    topic.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                topic.authors.forEach { author -> BulletAuthor(author, onUserClick) }
                Text(
                    stringResource(R.string.bullet_participations, topic.participationsCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        itemsIndexed(
            content.blocks,
            key = { index, _ -> "body_$index" },
            contentType = { _, block -> if (block is DetailContent.Image) "image" else "markdown" }
        ) { _, block ->
            BulletBodyBlock(
                block = block,
                modifier = Modifier.padding(horizontal = 16.dp),
                readerFontScale = readerFontScale
            )
        }
        if (topic.votes.isNotEmpty() || topic.voteTitle.isNotBlank()) {
            item(key = "votes", contentType = "votes") {
                BulletVoteResults(topic, Modifier.padding(horizontal = 16.dp))
            }
        }
        item(key = "comments_header", contentType = "comments_header") {
            Column {
                HorizontalDivider()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            stringResource(R.string.bullet_threads, topic.threadsCount),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            stringResource(R.string.bullet_comments_newest),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = comments::refresh,
                        enabled = comments.loadState.refresh !is LoadState.Loading
                    ) {
                        Text(stringResource(R.string.bullet_refresh_comments))
                    }
                }
            }
        }
        when (val refresh = comments.loadState.refresh) {
            is LoadState.Loading -> item(key = "comments_loading") { LoadingItem() }
            is LoadState.Error -> item(key = "comments_error") {
                ErrorItem(error = refresh.error.localizedMessage, onClick = comments::retry)
            }

            is LoadState.NotLoading -> if (comments.itemCount == 0) {
                item(key = "comments_empty") {
                    Text(
                        stringResource(R.string.bullet_comments_empty),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(
            count = comments.itemCount,
            key = comments.itemKey { "comment_${it.idHash}" },
            contentType = { "comment" }
        ) { index ->
            comments[index]?.let {
                BulletCommentItem(comment = it, onUserClick = onUserClick)
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            }
        }
        pagingTriState(
            paging = comments.loadState.append,
            onLoading = { LoadingItem() },
            onError = { ErrorItem(error = it.localizedMessage, onClick = comments::retry) }
        ) {}
    }
}

@Composable
private fun BulletAuthor(author: BulletUser, onUserClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = author.slug.isNotBlank()) { onUserClick(author.slug) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = imageUrlForNullable(author.avatar.takeIf { it.isNotBlank() }),
            contentDescription = null,
            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
            error = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Text(author.nickname, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun BulletBodyBlock(
    block: DetailContent,
    readerFontScale: Float,
    modifier: Modifier = Modifier
) {
    when (block) {
        is DetailContent.Image -> Column(modifier) {
            ReaderImage(
                url = block.url,
                contentDescription = block.caption,
                aspectRatio = block.aspectRatio
            )
            block.caption?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        is DetailContent.Markdown -> Markdown(
            content = block.text,
            modifier = modifier,
            imageTransformer = remember { DetailImageTransform() },
            colors = MaterialTheme.colorScheme.toMarkDownColors(),
            typography = sspaiReaderTypo(fontScale = readerFontScale),
            padding = markdownPadding(block = 6.dp, blockQuoteText = PaddingValues(8.dp)),
            components = markdownComponents(
                image = { ReaderMarkdownImage(content = it.content, node = it.node) },
                paragraph = {
                    DetailMarkdownParagraph(
                        content = it.content,
                        node = it.node,
                        style = it.typography.paragraph
                    )
                }
            )
        )
    }
}

@Composable
private fun BulletVoteResults(topic: BulletData, modifier: Modifier = Modifier) {
    val total =
        remember(topic.votes) { topic.votes.sumOf { it.voteCount.toLong().coerceAtLeast(0) } }
    val deadline = remember(topic.voteEndTime) {
        topic.voteEndTime.takeIf { it > 0L }?.let {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(it))
        }
    }
    Surface(
        modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.bullet_vote_results),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (topic.voteTitle.isNotBlank()) {
                Text(topic.voteTitle, style = MaterialTheme.typography.titleMedium)
            }
            deadline?.let {
                Text(
                    stringResource(R.string.bullet_vote_deadline, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            topic.votes.forEach { vote ->
                val progress = if (total > 0) (vote.voteCount.coerceAtLeast(0)
                    .toDouble() / total).toFloat() else 0f
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (vote.banner.isNotBlank()) ReaderImage(url = vote.banner)
                    Text(vote.title, style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        stringResource(R.string.bullet_vote_count, vote.voteCount, progress * 100f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (vote.isVoted) Text(
                        stringResource(R.string.bullet_vote_selected),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            if (topic.voted) Text(
                stringResource(R.string.bullet_voted),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                stringResource(R.string.bullet_vote_read_only),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
