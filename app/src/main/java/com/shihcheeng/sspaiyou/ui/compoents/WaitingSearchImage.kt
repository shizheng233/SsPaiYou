package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shihcheeng.sspaiyou.ui.compoents.illustration.UndrawSearchingEverywhereTffi
import com.shihcheeng.sspaiyou.ui.theme.AppTheme

@Composable
fun WaitingSearchImage(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.then(modifier),
        shape = MaterialTheme.shapes.medium
    ) {
        Image(
            imageVector = UndrawSearchingEverywhereTffi,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun WaitingSearchImagePreivew() {
    AppTheme(darkTheme = false,dynamicColor = false) {
        WaitingSearchImage()
    }
}