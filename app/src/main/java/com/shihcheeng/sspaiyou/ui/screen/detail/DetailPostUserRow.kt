package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailPostUserRow(
    modifier: Modifier = Modifier,
    nickname: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nickname,
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
                .copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 17.sp
                )
        )
    }
}

@Preview
@Composable
fun DetailPostUserRowPreview() {
    DetailPostUserRow(
        nickname = "NICKNAME"
    )
}