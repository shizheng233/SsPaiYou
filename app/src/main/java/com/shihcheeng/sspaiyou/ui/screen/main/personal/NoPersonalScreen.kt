package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.illustration.UndrawStepToTheSunNxqq

@Composable
fun NoPersonalScreen(
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = UndrawStepToTheSunNxqq,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .height(160.dp),
            )
            Text(
                text = stringResource(R.string.no_login),
                modifier = Modifier.padding(top = 16.dp)
            )
            FilledTonalButton(
                onClick = onLoginClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(R.string.now_login))
            }
        }
    }
}

