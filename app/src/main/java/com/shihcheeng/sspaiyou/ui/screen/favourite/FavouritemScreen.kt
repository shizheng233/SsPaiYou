package com.shihcheeng.sspaiyou.ui.screen.favourite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R

@Composable
fun FavouritemScreen(
    viewModel: FavouriteViewModel = hiltViewModel(),
    onNavUp: () -> Unit,
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val currentUserSlug by viewModel.currentUserSlug.collectAsStateWithLifecycle()
    val favourites = viewModel.favourites.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fav)) },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = favourites::refresh,
                        enabled = !currentUserSlug.isNullOrBlank() &&
                            favourites.loadState.refresh !is LoadState.Loading
                    ) {
                        Icon(
                            painterResource(R.drawable.column_refresh_24),
                            contentDescription = stringResource(R.string.favourites_refresh)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (currentUserSlug.isNullOrBlank()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.favourites_login_required),
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                favouriteItems(favourites, onArticleClick, onUserClick)
            }
        }
    }
}
