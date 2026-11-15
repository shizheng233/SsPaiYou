package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.mutableNetStateOf
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel internal constructor(
    private val repository: UserRepository,
    savedStateHandle: SavedStateHandle,
    observeToken: (CoroutineScope) -> StateFlow<String?>,
    observeSlug: (CoroutineScope) -> StateFlow<String?>
) : ViewModel() {
    @Inject
    constructor(
        repository: UserRepository,
        localTokenManager: LocalTokenManager,
        savedStateHandle: SavedStateHandle
    ) : this(repository, savedStateHandle, localTokenManager::tokenFlow, localTokenManager::slugFlow)

    val currentUserSlug = observeSlug(viewModelScope)
    val token = observeToken(viewModelScope)
    val followPending = repository.followPending
    private val _followError = MutableStateFlow(false)
    val followError = _followError.asStateFlow()
    private var loadJob: Job? = null
    private var followVersion = 0
    private var latestFollowed: Boolean? = null

    private val _slug = savedStateHandle.get<String>("slug").orEmpty()
    val slug = MutableStateFlow(_slug).asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val data = slug.filter { it.isNotBlank() }.flatMapLatest {
        repository.getData(it)
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val favourites = slug.filter { it.isNotBlank() }.flatMapLatest {
        repository.getFavourites(it)
    }.cachedIn(viewModelScope)

    private val _data = mutableNetStateOf<ThirdUserDataModel>()
    val dataOfUser = _data.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    init {
        viewModelScope.launch {
            token.drop(1).collect {
                latestFollowed = null
                followVersion++
                _followError.value = false
                load()
            }
        }
        viewModelScope.launch {
            repository.followChanges.collect { (slug, followed) ->
                if (slug == _slug) {
                    followVersion++
                    latestFollowed = followed
                    val current = _data.value
                    if (current is NetState.Success) {
                        _data.value = NetState.Success(current.content.withFollowed(followed))
                    }
                }
            }
        }
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val tokenAtStart = token.value
            val versionAtStart = followVersion
            _data.value = NetState.Loading
            try {
                val loaded = repository.getUserInfo(slug = _slug)
                if (tokenAtStart != token.value) return@launch
                val followed = latestFollowed
                _data.value = NetState.Success(
                    if (versionAtStart != followVersion && followed != null) loaded.withFollowed(followed)
                    else loaded
                )
                _title.value = loaded.data.nickname
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackOnlyDebug()
                _data.value = NetState.Error(e)
            }
        }
    }

    fun setFollowed(followed: Boolean) {
        if (_slug.isBlank() || _slug == currentUserSlug.value || token.value.isNullOrBlank()) return
        if (_slug in followPending.value) return
        val tokenAtStart = token.value
        viewModelScope.launch {
            try {
                repository.setFollowed(_slug, followed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (tokenAtStart == token.value) _followError.value = true
            }
        }
    }

    fun clearFollowError() { _followError.value = false }
}

private fun ThirdUserDataModel.withFollowed(followed: Boolean) = copy(
    data = data.copy(
        followed = followed,
        followedCount = (data.followedCount +
            if (data.followed == followed) 0 else if (followed) 1 else -1).coerceAtLeast(0)
    )
)
