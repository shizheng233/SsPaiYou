package com.shihcheeng.sspaiyou.ui.screen.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    repository: UserRepository,
    localTokenManager: LocalTokenManager
) : ViewModel() {
    val currentUserSlug = localTokenManager.slugFlow(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val favourites = currentUserSlug
        .map { it?.takeIf(String::isNotBlank) }
        .distinctUntilChanged()
        .flatMapLatest { slug ->
            if (slug == null) flowOf(PagingData.empty<ColumnArticle>())
            else repository.getFavourites(slug)
        }
        .cachedIn(viewModelScope)
}
