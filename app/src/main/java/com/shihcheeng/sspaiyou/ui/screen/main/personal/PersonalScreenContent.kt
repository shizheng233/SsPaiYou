package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.illustration.UndrawTheWorldIsMineReJ5Cr
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
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    imageVector = UndrawTheWorldIsMineReJ5Cr,
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.Center)
                )
            }
        }
        item {
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
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = stringResource(R.string.followed, followingCount),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Badge {
                            Text(
                                text = stringResource(R.string.had_been_sspai, beingDays),
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.baseline_navigate_next_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
        item {
            HorizontalDivider(Modifier.padding(top = 8.dp))
        }
        items(list) { item ->
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
