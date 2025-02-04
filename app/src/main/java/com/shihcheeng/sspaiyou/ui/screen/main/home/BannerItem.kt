package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun BannerItem(
    modifier: Modifier = Modifier,
    shape: Shape,
    image: String,
    title: String,
    onClick: () -> Unit
) {
    var mutableWidthOfCard by remember { mutableIntStateOf(0) }
    OutlinedCard(
        shape = shape,
        onClick = onClick,
        modifier = Modifier.onSizeChanged {
            mutableWidthOfCard = it.width
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            AsyncImage(
                model = imageUrlFor(image),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(11f / 6f),
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp)
                    .align(Alignment.TopStart),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}
