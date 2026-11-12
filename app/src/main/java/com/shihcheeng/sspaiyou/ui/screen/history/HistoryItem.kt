package com.shihcheeng.sspaiyou.ui.screen.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.extra.calculateDay
import com.shihcheeng.sspaiyou.utils.extra.timestampToChinese
import kotlin.math.roundToInt

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    title: String,
    coverUrl: String?,
    authorName: String,
    readerProgress: Float,
    lastReadAtEpochMillis: Long,
    onClick: () -> Unit = {}
) {

    val colorScheme = MaterialTheme.colorScheme
    val typo = MaterialTheme.typography
    val shape = MaterialTheme.shapes

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = "${(lastReadAtEpochMillis / 1000).calculateDay()} ${(lastReadAtEpochMillis / 1000).timestampToChinese()}",
                    style = typo.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = title,
                    style = typo.titleMedium,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = authorName,
                    style = typo.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1 / 1f)
                    .clip(shape.medium),
                placeholder = ColorPainter(colorScheme.secondaryContainer),
                contentScale = ContentScale.Crop
            )
        }
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 2.dp),
            progress = { readerProgress }
        )
        Text(
            text = "${(readerProgress * 100).roundToInt()}%",
            color = colorScheme.onSurface,
            style = typo.labelSmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
                .align(Alignment.End)
        )
    }
}
