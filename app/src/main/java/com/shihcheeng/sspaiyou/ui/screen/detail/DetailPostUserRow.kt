package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.utils.extra.toDp
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun DetailPostUserRow(
    modifier: Modifier = Modifier,
    avatar: String,
    nickname: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrlFor(avatar),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(MaterialTheme.typography.titleMedium.fontSize.toDp()),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
        )
        Text(
            text = nickname,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview
@Composable
fun DetailPostUserRowPreview() {
    DetailPostUserRow(
        nickname = "NICKNAME",
        avatar = ""
    )
}