package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.R

@Composable
fun DetailTocDrawer(
    entries: List<DetailTocEntry>,
    articleListState: LazyListState,
    onEntryClick: (DetailTocEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex by remember(entries, articleListState) {
        derivedStateOf {
            entries.lastOrNull { it.itemIndex <= articleListState.firstVisibleItemIndex }?.itemIndex
        }
    }
    Text(
        text = stringResource(R.string.toc),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(24.dp)
    )
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 16.dp)
    ) {
        items(entries, key = { it.itemIndex }) { entry ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = entry.title,
                        style = if (entry.level == 1) MaterialTheme.typography.titleSmall
                        else MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = ((entry.level - 1) * 12).dp)
                    )
                },
                selected = entry.itemIndex == selectedIndex,
                onClick = { onEntryClick(entry) }
            )
        }
    }
}
