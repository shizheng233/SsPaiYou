package com.shihcheeng.sspaiyou.activities.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailBodyExtends
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailContent
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailRepository,
    private val markDownParser: DetailMarkDownParser,
    private val localTokenManager: LocalTokenManager
) : ViewModel() {

    val id = savedStateHandle.get<String>("id")

    private val _data = MutableStateFlow<NetState<ArticleData>>(NetState.Loading)
    val data = _data.asStateFlow()

    val token = localTokenManager.tokenFlow(viewModelScope)

    private val _isFavouriteUpdating = MutableStateFlow(false)
    val isFavouriteUpdating = _isFavouriteUpdating.asStateFlow()

    private val _favouriteError = MutableStateFlow(false)
    val favouriteError = _favouriteError.asStateFlow()

    private val _isLikeUpdating = MutableStateFlow(false)
    val isLikeUpdating = _isLikeUpdating.asStateFlow()

    private val _likeError = MutableStateFlow<LikeError?>(null)
    val likeError = _likeError.asStateFlow()

    private val _contentBlocks = MutableStateFlow<List<DetailContent>>(emptyList())
    val contentBlocks = _contentBlocks.asStateFlow()

    private val _bodyExtendsBlocks = MutableStateFlow<List<DetailBodyExtends>>(emptyList())
    val bodyExtendsBlocks = _bodyExtendsBlocks.asStateFlow()

    private val commentArticleId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = commentArticleId.filterNotNull()
        .flatMapLatest(repository::comments)
        .cachedIn(viewModelScope)

    init {
        load()
    }

    fun toggleFavourite() {
        val currentToken = localTokenManager.getToken()?.takeIf { it.isNotBlank() } ?: return
        val article = (_data.value as? NetState.Success)?.content ?: return
        if (article.id <= 0 || _isFavouriteUpdating.value) return

        // Set before launching so rapid clicks cannot enqueue duplicate requests.
        _isFavouriteUpdating.value = true
        _favouriteError.value = false
        viewModelScope.launch {
            try {
                val favourite = !article.favorited
                repository.setFavourite(article.id, favourite)
                val current = (_data.value as? NetState.Success)?.content
                if (localTokenManager.getToken() == currentToken && current?.id == article.id) {
                    _data.value = NetState.Success(current.copy(favorited = favourite))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackOnlyDebug()
                if (localTokenManager.getToken() == currentToken) {
                    _favouriteError.value = true
                }
            } finally {
                _isFavouriteUpdating.value = false
            }
        }
    }

    fun clearFavouriteError() {
        _favouriteError.value = false
    }

    fun toggleLike() {
        val currentToken = localTokenManager.getToken()?.takeIf { it.isNotBlank() }
        if (currentToken == null) {
            _likeError.value = LikeError.NOT_LOGGED_IN
            return
        }
        val article = (_data.value as? NetState.Success)?.content ?: return
        if (article.id <= 0 || _isLikeUpdating.value) return

        _isLikeUpdating.value = true
        _likeError.value = null
        viewModelScope.launch {
            try {
                val liked = !article.liked
                repository.setLike(article.id, liked)
                val current = (_data.value as? NetState.Success)?.content
                if (localTokenManager.getToken() == currentToken && current?.id == article.id) {
                    val countChange = if (current.liked == liked) 0 else if (liked) 1 else -1
                    _data.value = NetState.Success(
                        current.copy(
                            liked = liked,
                            articleCount = current.articleCount.copy(
                                likeCount = (current.articleCount.likeCount + countChange).coerceAtLeast(0)
                            )
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackOnlyDebug()
                if (localTokenManager.getToken() == currentToken) {
                    _likeError.value = LikeError.REQUEST_FAILED
                }
            } finally {
                _isLikeUpdating.value = false
            }
        }
    }

    fun clearLikeError() {
        _likeError.value = null
    }

    enum class LikeError {
        NOT_LOGGED_IN,
        REQUEST_FAILED
    }

    fun load() = viewModelScope.launch {
        _data.emit(NetState.Loading)
        try {
            id?.let {
                val dataI = repository.getArticleDetail(it)
                // HTML -> Markdown -> 内容块是 CPU 密集操作，放到后台线程，
                // 且先于 Success 发出，保证页面展示时正文一次性就绪
                val bodyBlocks = dataI.bodyExtends.map { x ->
                    x.hyperTransformToMD(markDownParser)
                }
                val blocks = withContext(Dispatchers.Default) {
                    markDownParser.parse(dataI.body.trimIndent())
                }
                _bodyExtendsBlocks.emit(bodyBlocks)
                _contentBlocks.emit(blocks)
                _data.emit(NetState.Success(dataI))
                // Member articles are opened by slug; comments still require the resolved numeric ID.
                commentArticleId.value = dataI.id.takeIf { articleId -> articleId > 0 }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            _data.emit(NetState.Error(e))
        }
    }

}
