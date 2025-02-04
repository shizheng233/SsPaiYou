package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun UserActivityItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    actionString: String,
    actionContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape),
                model = imageUrlFor(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = actionString,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                actionContent()
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    }
}