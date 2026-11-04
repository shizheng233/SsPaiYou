package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun PersonalScreenContent(
    username: String,
    followingCount: Int,
    beingDays: Int,
    imageUrl: String,
    onSetting: () -> Unit,
    onFavourite: () -> Unit,
    onMine: () -> Unit
) {

    val list = PersonalItem.createListInCompose()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            key = username.hashCode(),
            contentType = PersonalScreenTypeKey.AVATAR_HEADER
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        onClick = onMine
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = imageUrlFor(imageUrl),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.surfaceTint,
                            shape = CircleShape
                        ),
                    contentDescription = null,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    error = ColorPainter(MaterialTheme.colorScheme.errorContainer)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.baseline_navigate_next_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
        item(
            key = PersonalScreenTypeKey.ROW_BELOW_HEADER.hashCode(),
            contentType = PersonalScreenTypeKey.ROW_BELOW_HEADER
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(72.dp, Alignment.CenterHorizontally)
            ) {
                StatItem(
                    value = "${beingDays}天",
                    label = "会员时间"
                )
                StatItem(
                    value = followingCount.toString(),
                    label = "关注"
                )
                StatItem(
                    value = 0.toString(),
                    label = "粉丝"
                )
            }
        }
        item {
            HorizontalDivider(Modifier.padding(top = 8.dp))
        }
        items(
            items = list,
            key = { x ->
                "numbering_${x.resId}"
            },
            contentType = {
                PersonalScreenTypeKey.CLICK_ITEM
            }
        ) { item ->
            ListItem(
                headlineContent = {
                    Text(text = stringResource(item.resId))
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(item.drawable),
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable {
                    when (item) {
                        PersonalItemFav -> onFavourite()

                        PersonalItemSetting -> onSetting()

                        else -> {}
                    }
                }
            )
        }
        item {
            HorizontalDivider()
        }
        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(PersonalItemLogOut.resId))
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(PersonalItemLogOut.drawable),
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}


@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.titleMedium,
    labelStyle: TextStyle = MaterialTheme.typography.bodySmall,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = valueStyle,
            color = valueColor,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = labelStyle,
            color = labelColor,
            maxLines = 1,
        )
    }
}
