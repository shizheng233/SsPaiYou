package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.activities.detail.DetailViewModel
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.utils.NetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onAvatarClick: (String) -> Unit,
    onNavBack: () -> Unit
) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val state = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val index by remember { derivedStateOf { state.firstVisibleItemIndex } }
                    AnimatedVisibility(
                        visible = index != 0 && data is NetState.Success,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = (data as NetState.Success).content.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = onNavBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        },
        bottomBar = {

        }
    ) { padding ->
        TriStateScreen(
            triState = data,
            padding = PaddingValues(
                top = padding.calculateTopPadding()
            ),
            onError = {
                Error(error = it.localizedMessage, onClick = viewModel::load)
            }
        ) {
            DetailScreenContent(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                state = state,
                title = it.title,
                author = it.author,
                content = it.body.trimIndent(),
                onAvatarClick = onAvatarClick
            )
        }
    }
}
