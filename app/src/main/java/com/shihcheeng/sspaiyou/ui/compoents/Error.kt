package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.R

@Composable
fun Error(
    modifier: Modifier = Modifier,
    error: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = error ?: stringResource(R.string.unknown_error),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onClick) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    error: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = error ?: stringResource(R.string.unknown_error),
                textAlign = TextAlign.Center
            )
            Button(onClick = onClick) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}