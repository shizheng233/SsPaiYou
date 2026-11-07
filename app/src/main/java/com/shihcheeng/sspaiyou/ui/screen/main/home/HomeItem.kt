package com.shihcheeng.sspaiyou.ui.screen.main.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.extra.timestampToChinese
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun HomeItemNoBanner(
    modifier: Modifier = Modifier,
    title: String,
    author: String,
    avatar: String,
    timeStamp: Long,
    cornerName: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrlFor(avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape),
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Text(
                text = author,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = 4.dp, end = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${timeStamp.calculateDay()} ${timeStamp.timestampToChinese()}",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
                contentColor = MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.sspai_community_icon_1),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(12.dp)
                )
            }
            Text(
                text = cornerName,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun HomeItemAlt(
    modifier: Modifier = Modifier,
    image: String,
    title: String,
    timeStamp: Long,
    author: String,
    avatar: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(modifier)
    ) {

        AsyncImage(
            model = imageUrlFor(image),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .height(90.dp)
                .aspectRatio(94f / 76f),
            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
        )

        Column(
            modifier = Modifier
                .padding(
                    top = 17.dp,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = imageUrlFor(avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                )
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelMedium
                        .copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 4.dp, end = 6.dp)
                )
                Text(
                    text = "${timeStamp.calculateDay()} ${timeStamp.timestampToChinese()}",
                    style = MaterialTheme.typography.labelMedium
                        .copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
                    .copy(
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    ),
                modifier = Modifier
                    .padding(top = 4.dp, end = 16.dp)
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeItemNoBannerPreview() {
    HomeItemNoBanner(
        title = "没有封面，也可以让内容本身成为阅读的主角",
        author = "少数派",
        avatar = "",
        timeStamp = 0L,
        cornerName = "Matrix精选"
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun HomeItemViewAltPreview() {
    HomeItemAlt(
        image = "",
        title = "hahahaah",
        timeStamp = 0L,
        author = "你好",
        avatar = ""
    ) {

    }
}
