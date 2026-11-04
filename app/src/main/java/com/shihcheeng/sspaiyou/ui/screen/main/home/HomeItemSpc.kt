package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.extra.timestampToChinese
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun HomeItemSpc(
    modifier: Modifier = Modifier,
    image: String,
    title: String,
    summary: String,
    avatar: String,
    author: String,
    timeStamp: Long,
    onClick: () -> Unit
) {
    val rememberColor = MaterialTheme.colorScheme.surfaceContainer
    OutlinedCard(
        modifier = Modifier
            .then(modifier),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0.1f to Color.Transparent,
                                1f to rememberColor,
                            )
                        )
                    },
                model = imageUrlFor(image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = imageUrlFor(avatar),
                        modifier = Modifier
                            .size(20.dp)
                            .aspectRatio(1f / 1f)
                            .clip(CircleShape),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${timeStamp.calculateDay()} ${timeStamp.timestampToChinese()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Text(
                    text = summary,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HomeItemLarge(
    modifier: Modifier = Modifier,
    image: String,
    title: String,
    summary: String,
    avatar: String,
    author: String,
    timeColor: String,
    timeStamp: Long,
    commentCount: Int,
    thunderCount: Int,
    onClick: () -> Unit
) {
    val colors = rememberHomeItemColors(timeColor)
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = colors.surface,
        contentColor = colors.onSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(6.5f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = imageUrlFor(avatar),
                            modifier = Modifier
                                .size(20.dp)
                                .aspectRatio(1f / 1f)
                                .clip(CircleShape),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                        )
                        Text(
                            text = author,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 6.dp),
                            color = colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${timeStamp.calculateDay()} ${timeStamp.timestampToChinese()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                AsyncImage(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .fillMaxWidth()
                        .weight(3.5f)
                        .aspectRatio(150f / 110f),
                    model = imageUrlFor(image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
            Text(
                text = summary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_message_24),
                    contentDescription = "message",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = commentCount.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant,
                )
                Icon(
                    painter = painterResource(R.drawable.outline_flash_on_24),
                    contentDescription = "message",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp).size(16.dp)
                )
                Text(
                    text = thunderCount.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeItemLargePreview() {
    HomeItemLarge(
        image = "",
        title = "被量化的心跳：HRV是身体的晴雨表，还是制造焦虑的数字游戏？",
        summary = "这些设备最好的用途，不是让你更依赖它们，而是帮助你重新学会倾听自己。然后，在某个清晨，你不再需要看任何数字，就知道今天会是美好的一天。",
        avatar = "",
        author = "Sport",
        timeStamp = 0L,
        timeColor = "#495759",
        commentCount = 11,
        thunderCount = 12,
        onClick = {

        }
    )
}
