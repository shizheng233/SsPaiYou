package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun UserActionFollowUser(
    modifier: Modifier = Modifier,
    otherUserAvatarUrl: String,
    name: String,
    description: String
) {
    Card(
        modifier = Modifier.then(modifier)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            AsyncImage(
                model = imageUrlFor(otherUserAvatarUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W700
                )
                Text(
                    text = description.takeIf { x -> x.isNotEmpty() } ?: stringResource(R.string.no_desc),
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

