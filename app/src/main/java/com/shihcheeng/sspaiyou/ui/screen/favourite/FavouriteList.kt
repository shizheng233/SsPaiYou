package com.shihcheeng.sspaiyou.ui.screen.favourite

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem

internal fun LazyListScope.favouriteItems(
    favourites: LazyPagingItems<ColumnArticle>,
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    when (val refresh = favourites.loadState.refresh) {
        is LoadState.Loading -> item(key = "favourite_refresh_loading", contentType = "loading") {
            LoadingItem(Modifier.padding(24.dp))
        }
        is LoadState.Error -> item(key = "favourite_refresh_error", contentType = "error") {
            ErrorItem(error = refresh.error.localizedMessage, onClick = favourites::retry)
        }
        is LoadState.NotLoading -> if (favourites.itemCount == 0) {
            item(key = "empty_favourites", contentType = "empty") {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.user_favourites_empty),
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    items(
        count = favourites.itemCount,
        key = favourites.itemKey { "favourite_${it.id}" },
        contentType = { "favourite_article" }
    ) { index ->
        favourites[index]?.let { article ->
            FavouriteArticleItem(
                article = article,
                onClick = {
                    onArticleClick(
                        if (!article.free && article.slug.isNotBlank()) article.slug
                        else article.id.toString()
                    )
                },
                onUserClick = onUserClick,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
    if (favourites.itemCount > 0) {
        when (val append = favourites.loadState.append) {
            is LoadState.Loading -> item(key = "favourite_append_loading", contentType = "loading") {
                LoadingItem(Modifier.padding(24.dp))
            }
            is LoadState.Error -> item(key = "favourite_append_error", contentType = "error") {
                ErrorItem(error = append.error.localizedMessage, onClick = favourites::retry)
            }
            is LoadState.NotLoading -> Unit
        }
    }
}
