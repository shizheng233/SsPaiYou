package com.shihcheeng.sspaiyou.ui.screen.user.activityitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun UserActivityNormalItem(
    modifier: Modifier = Modifier,
    type: String = stringResource(R.string.type_obj_1),
    title: String,
    banner: String?,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = type,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 4.dp)
            )
            banner?.let {
                AsyncImage(
                    model = imageUrlFor(it),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(2f / 1f)
                        .padding(vertical = 8.dp)
                        .padding(end = 8.dp)
                        .clip(MaterialTheme.shapes.medium),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}