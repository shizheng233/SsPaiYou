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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
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
    OutlinedCard(
        shape = shape,
        onClick = onClick
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
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun BannerPreview() {
    BannerItem(
        shape = MaterialTheme.shapes.medium,
        image = "",
        title = "HAHA"
    ){

    }
}
