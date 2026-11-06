package com.shihcheeng.sspaiyou.ui.screen.main.home

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun BannerItem(
    modifier: Modifier = Modifier,
    image: String,
    title: String,
    onlyImage: Boolean,
    onClick: () -> Unit
) {
    var imagePainter by remember(image) { mutableStateOf<Painter?>(null) }
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(360 / 274f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrlFor(image),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                onSuccess = { imagePainter = it.painter },
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            if (!onlyImage) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    imagePainter?.let { painter ->
                        // Reuse the loaded image; mask only the blur layer, not the sharp image.
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer {
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .drawWithCache {
                                    val mask = Brush.verticalGradient(
                                        0f to Color.Transparent,
                                        0.4f to Color.Transparent,
                                        0.72f to Color.Black.copy(alpha = 0.65f),
                                        1f to Color.Black
                                    )
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(mask, blendMode = BlendMode.DstIn)
                                    }
                                }
                                .blur(14.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.4f to Color.Transparent,
                                0.65f to Color.Black.copy(alpha = 0.12f),
                                0.82f to Color.Black.copy(alpha = 0.32f),
                                1f to Color.Black.copy(alpha = 0.58f)
                            )
                        )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                        .copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }
    }
}
