package com.shihcheeng.sspaiyou.ui.screen.main.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.extra.timestampToChinese
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun HomeItem(
    modifier: Modifier = Modifier,
    image: String,
    title: String,
    author: String,
    avatar: String,
    summary: String,
    timeStamp: Long,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        AsyncImage(
            model = imageUrlFor(image),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 1f),
            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
                    .copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = imageUrlFor(avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                )
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
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
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
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
                .padding(top = 17.dp)
        ) {
            Text(
                text = "${timeStamp.calculateDay()} ${timeStamp.timestampToChinese()}",
                style = MaterialTheme.typography.labelMedium
                    .copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
fun HomeItemViewPreview() {
    HomeItem(
        image = "",
        title = "hahahaah",
        author = "HAHA",
        avatar = "",
        summary = "wow",
        timeStamp = 0L
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun HomeItemViewAltPreview() {
    HomeItemAlt(
        image = "",
        title = "hahahaah",
        timeStamp = 0L
    ) {

    }
}