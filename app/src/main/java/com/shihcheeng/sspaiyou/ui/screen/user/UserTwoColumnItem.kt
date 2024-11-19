package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun UserTwoColumnItem(
    modifier: Modifier = Modifier,
    leadingImage: String? = null,
    topText: String,
    bottomText: String,
    end: Boolean,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(vertical = 8.dp)
            .then(modifier)
    ) {
        if (leadingImage != null) {
            AsyncImage(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .aspectRatio(1f / 1f),
                model = leadingImage,
                contentDescription = null,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
        Column(
            modifier = Modifier
        ) {
            Text(
                text = topText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = bottomText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (!end) {
            VerticalDivider(modifier = Modifier.padding(start = 16.dp))
        }
    }
}