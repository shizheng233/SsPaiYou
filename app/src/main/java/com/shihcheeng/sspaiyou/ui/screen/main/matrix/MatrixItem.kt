package com.shihcheeng.sspaiyou.ui.screen.main.matrix

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.IconWithText
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun MatrixItem(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String,
    avatarUrl: String,
    author: String,
    flash: String,
    message: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.then(modifier),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = imageUrlFor(avatarUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(
                                with(LocalDensity.current) {
                                    MaterialTheme.typography.labelMedium.fontSize.toDp()
                                }
                            ),
                        placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconWithText(
                        icon = painterResource(R.drawable.outline_flash_on_24),
                        text = flash
                    )
                    Spacer(Modifier.width(24.dp))
                    IconWithText(
                        icon = painterResource(R.drawable.outline_message_24),
                        text = message
                    )
                }
            }
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrlFor(imageUrl),
                    modifier = Modifier
                        .aspectRatio(1.2f / 1f),
                    contentDescription = null,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}