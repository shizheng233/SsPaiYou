package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.github.panpf.zoomimage.CoilZoomState
import com.github.panpf.zoomimage.rememberCoilZoomState
import com.shihcheeng.sspaiyou.R

@Composable
fun ArticleZoomableImage(
    url: String,
    modifier: Modifier = Modifier,
    zoomState: CoilZoomState = rememberCoilZoomState(),
    imageLoader: ImageLoader = LocalContext.current.imageLoader,
    onTap: () -> Unit = {}
) {
    val context = LocalContext.current
    var retryAttempt by remember(url) { mutableIntStateOf(0) }
    key(url, retryAttempt) {
        var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
        val request = remember(context, url) {
            ImageRequest.Builder(context)
                .data(url)
                // Decode only a viewport-sized base image. ZoomImage reads full-resolution tiles
                // from Coil's cached source; do not request Size.ORIGINAL for huge images.
                .crossfade(false)
                .build()
        }
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CoilZoomAsyncImage(
                model = request,
                imageLoader = imageLoader,
                contentDescription = stringResource(R.string.article_image_fullscreen),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                zoomState = zoomState,
                onState = { imageState = it },
                onTap = { onTap() }
            )
            when (imageState) {
                is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                is AsyncImagePainter.State.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.article_image_load_error), color = Color.White)
                        TextButton(onClick = { retryAttempt++ }) {
                            Text(stringResource(R.string.retry), color = Color.White)
                        }
                    }
                }

                is AsyncImagePainter.State.Success -> Unit
            }
        }
    }
}
