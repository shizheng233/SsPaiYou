package com.shihcheeng.sspaiyou.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    gotoItem: (LocalRecordDataModel) -> Unit,
    onBacK: () -> Unit
) {

    val itemState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.history)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBacK
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        TriStateScreen(
            triState = itemState,
            onError = {
                Error(error = it.localizedMessage) {
                    viewModel.loadingHistory()
                }
            }
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = padding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = it,
                    contentType = { "history_item" },
                    key = { x -> "${x.ownerKey}::${x.postId}" }
                ) { item ->
                    HistoryItem(
                        title = item.title,
                        lastReadAtEpochMillis = item.lastReadAtEpochMillis,
                        authorName = item.authorName ?: "",
                        coverUrl = item.coverUrl,
                        readerProgress = item.readProgress,
                    ) {
                        gotoItem(item)
                    }
                }
            }
        }
    }

}
