package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Dimension
import coil.size.Size
import coil.size.SizeResolver
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

@Composable
fun ReaderImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    aspectRatio: Float? = null,
    imageLoader: ImageLoader = LocalContext.current.imageLoader,
    shape: Shape = MaterialTheme.shapes.medium,
    onImageClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val imageUrl = imageUrlForNullable(url)
    val openImageLabel = stringResource(R.string.article_image_open)
    var decodedRatio by rememberSaveable(url) { mutableFloatStateOf(0f) }
    val ratio = decodedRatio.takeIf { it > 0f && it.isFinite() }
        ?: aspectRatio?.takeIf { it > 0f && it.isFinite() }
        ?: (16f / 9f)
    val width = remember { MutableStateFlow(0) }
    val request = remember(context, url) {
        ImageRequest.Builder(context)
            .data(imageUrlForNullable(url))
            // Decode for the reader width, without squeezing tall images into the placeholder height.
            .size(SizeResolver { Size(width.first { it > 0 }, Dimension.Undefined) })
            .crossfade(false)
            .build()
    }
    AsyncImage(
        model = request,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceContainer),
        error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio)
            .onSizeChanged { width.value = it.width }
            .clip(shape)
            .then(
                if (onImageClick != null && !imageUrl.isNullOrBlank()) {
                    Modifier.clickable(role = Role.Button, onClickLabel = openImageLabel) {
                        onImageClick(imageUrl)
                    }
                } else Modifier
            ),
        onSuccess = { state ->
            val drawable = state.result.drawable
            if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                decodedRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight
            }
        }
    )
}
