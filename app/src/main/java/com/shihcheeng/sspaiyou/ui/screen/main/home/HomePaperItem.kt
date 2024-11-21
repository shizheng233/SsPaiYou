package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.extra.timestampToChineseDate
import com.shihcheeng.sspaiyou.utils.extra.toDp

@Composable
fun HomePaperItem(
    modifier: Modifier = Modifier,
    timeSource: Long,
    list: List<String>,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.pi_paper),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = timeSource.timestampToChineseDate(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
            list.forEachIndexed { index, s ->
                ListItem(
                    headlineContent = {
                        Text(text = s)
                    },
                    leadingContent = {
                        Text(text = "0${index + 1}")
                    },
                    modifier = Modifier
                        .clickable { }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = onClick
                ) {
                    Text(
                        text = stringResource(R.string.go_to_paper_content),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_forward_24),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(LocalTextStyle.current.fontSize.toDp())
                    )
                }
            }
        }
    }
}

