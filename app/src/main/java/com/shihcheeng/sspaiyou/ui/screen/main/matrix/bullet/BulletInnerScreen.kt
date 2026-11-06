package com.shihcheeng.sspaiyou.ui.screen.main.matrix.bullet

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.api.data.bullet.BulletUser
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.utils.imageUrlFor
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                data[index]?.let { topic ->
                    BulletItem(
                        topic = topic,
                        // The official all-topics feed treats its first item as ongoing.
                        isOngoing = index == 0,
                        onBulletClick = onBulletClick
                    )
                }
            }
            pagingTriState(
                paging = data.loadState.append,
                onLoading = { LoadingItem() },
                onError = { ErrorItem(error = it.localizedMessage, onClick = data::retry) }
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BulletItem(
    topic: BulletData,
    isOngoing: Boolean,
    modifier: Modifier = Modifier,
    onBulletClick: (Long) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isDarkTheme = colors.surface.luminance() < 0.5f
    val containerColor = if (isDarkTheme) colors.surfaceContainerHigh else colors.inverseSurface
    val contentColor = if (isDarkTheme) colors.onSurface else colors.inverseOnSurface
    val supportingColor = contentColor.copy(alpha = 0.72f)
    val isVote = topic.type == 2
    val typeIcon = if (isVote) R.drawable.outline_poll_24 else R.drawable.outline_message_24
    val typeColor = if (isVote) colors.tertiaryFixedDim else colors.primaryFixedDim

    Card(
        onClick = { onBulletClick(topic.createdAt) },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(0.65f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = typeColor.copy(alpha = 0.16f),
                        contentColor = typeColor,
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(typeIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                    Text(
                        text = stringResource(if (isVote) R.string.bullet_vote else R.string.bullet_discussion),
                        style = MaterialTheme.typography.labelMedium,
                        color = typeColor,
                        maxLines = 1
                    )
                    if (topic.vol.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.bullet_volume, topic.vol),
                            style = MaterialTheme.typography.labelSmall,
                            color = supportingColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (topic.participationUsers.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            topic.participationUsers.take(3).forEach { user ->
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(colors.secondaryContainer)
                                        .border(2.dp, containerColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_person_24),
                                        contentDescription = null,
                                        tint = colors.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    AsyncImage(
                                        model = imageUrlFor(user.avatar),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = stringResource(
                            R.string.bullet_participations,
                            topic.participationsCount
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = supportingColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (isOngoing) {
                    Button(
                        onClick = { onBulletClick(topic.createdAt) },
                        shapes = ButtonDefaults.shapes(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primaryFixed,
                            contentColor = colors.onPrimaryFixed
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.bullet_join_discussion),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_forward_thin_24),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(0.35f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large)
                    .background(colors.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (topic.banner.isNotBlank()) {
                    AsyncImage(
                        model = imageUrlFor(topic.banner),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(colors.secondaryContainer),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(typeIcon),
                        contentDescription = null,
                        tint = colors.onSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Light", showBackground = true, widthDp = 360)
@Preview(
    name = "Dark",
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(name = "Large text", showBackground = true, widthDp = 320, fontScale = 1.5f)
@Composable
private fun BulletItemPreview() {
    MaterialTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val topic = BulletData(
                    title = "假期出门玩想省钱，你有哪些实操技巧？",
                    vol = "304",
                    type = 2,
                    participationsCount = 206,
                    participationUsers = List(3) { BulletUser(id = it) }
                )
                BulletItem(topic = topic, isOngoing = true, onBulletClick = {})
                BulletItem(
                    topic = topic.copy(title = "十月买了什么好东西？", vol = "303", type = 1),
                    isOngoing = false,
                    onBulletClick = {}
                )
                BulletItem(
                    topic = topic.copy(
                        title = "分享你的开源工具",
                        vol = "302",
                        participationsCount = 0,
                        participationUsers = emptyList()
                    ),
                    isOngoing = false,
                    onBulletClick = {}
                )
            }
        }
    }
}
