package com.shihcheeng.sspaiyou.activities.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.repository.HistoryRepository
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
    private val localTokenManager: LocalTokenManager,
    private val historyRepository: HistoryRepository
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

    private val _sendMessageStatus = MutableStateFlow<CommentSendStatus>(CommentSendStatus.Empty)
    val sendMessageStatus = _sendMessageStatus.asStateFlow()

    private val _contentBlocks = MutableStateFlow<List<DetailContent>>(emptyList())
    val contentBlocks = _contentBlocks.asStateFlow()

    private val _bodyExtendsBlocks = MutableStateFlow<List<DetailBodyExtends>>(emptyList())
    val bodyExtendsBlocks = _bodyExtendsBlocks.asStateFlow()

    private val commentArticleId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = commentArticleId.filterNotNull()
        .flatMapLatest { articleId -> repository.comments(articleId = articleId) }
        .cachedIn(viewModelScope)

    fun blockComments(
        articleId: Int,
        articleBodyExtendId: Int,
    ) = repository.comments(
        articleId = articleId,
        articleBodyExtendId = articleBodyExtendId,
    )

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
                                likeCount = (current.articleCount.likeCount + countChange).coerceAtLeast(
                                    0
                                )
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

    suspend fun savedReadingPosition(articleId: Int): LocalRecordDataModel? {
        val ownerKey = currentHistoryOwnerKey() ?: return null
        return try {
            historyRepository.getHistoryByPostId(ownerKey, articleId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            null
        }
    }

    suspend fun saveReadingProgress(
        article: ArticleData,
        progress: Float,
        itemIndex: Int,
        itemOffsetPx: Int
    ) {
        if (article.id <= 0) return
        val ownerKey = currentHistoryOwnerKey() ?: return
        try {
            val existing = historyRepository.getHistoryByPostId(ownerKey, article.id)
            val memberSlug = id?.takeIf { it.toIntOrNull() == null && it.isNotBlank() }
            val preserveMemberRoute = memberSlug == null &&
                    existing?.detailRoute == LocalRecordDataModel.DETAIL_ROUTE_MEMBER_SLUG &&
                    !existing.slug.isNullOrBlank()
            val normalizedProgress = progress.coerceIn(0f, 1f)
            val now = System.currentTimeMillis()
            val coverUrl = article.banner.takeIf { it.isNotBlank() }
                ?: article.promoteImage.takeIf { it.isNotBlank() }
                ?: existing?.coverUrl

            historyRepository.upsertHistory(
                LocalRecordDataModel(
                    ownerKey = ownerKey,
                    postId = article.id,
                    detailRoute = when {
                        memberSlug != null -> LocalRecordDataModel.DETAIL_ROUTE_MEMBER_SLUG
                        preserveMemberRoute -> LocalRecordDataModel.DETAIL_ROUTE_MEMBER_SLUG
                        else -> LocalRecordDataModel.DETAIL_ROUTE_ID
                    },
                    slug = memberSlug ?: existing?.slug?.takeIf { preserveMemberRoute },
                    title = article.title,
                    coverUrl = coverUrl,
                    authorId = article.author.id.takeIf { it > 0 } ?: existing?.authorId,
                    authorName = article.author.nickname.takeIf { it.isNotBlank() }
                        ?: existing?.authorName,
                    authorSlug = article.author.slug.takeIf { it.isNotBlank() }
                        ?: existing?.authorSlug,
                    authorAvatarUrl = article.author.avatar.takeIf { it.isNotBlank() }
                        ?: existing?.authorAvatarUrl,
                    readProgress = normalizedProgress,
                    resumeItemIndex = itemIndex.coerceAtLeast(0),
                    resumeItemOffsetPx = itemOffsetPx.coerceAtLeast(0),
                    lastReadAtEpochMillis = now,
                    completedAtEpochMillis = if (normalizedProgress >= 0.99f) {
                        existing?.completedAtEpochMillis ?: now
                    } else {
                        null
                    }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackOnlyDebug()
        }
    }

    private fun currentHistoryOwnerKey(): String? {
        if (localTokenManager.getToken().isNullOrBlank()) {
            return LocalRecordDataModel.OWNER_ANONYMOUS
        }
        return localTokenManager.getSlug()?.takeIf { it.isNotBlank() }
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


    fun sendComment(
        articleID: Int,
        comment: String
    ) {
        if (_sendMessageStatus.value == CommentSendStatus.Sending || comment.isBlank()) return

        // Set before launching so rapid clicks cannot enqueue duplicate comments.
        _sendMessageStatus.value = CommentSendStatus.Sending
        viewModelScope.launch {
            try {
                require(articleID > 0) { "文章ID必须大于0" }
                repository.postComment(articleID, comment)
                _sendMessageStatus.value = CommentSendStatus.Success
            } catch (e: CancellationException) {
                _sendMessageStatus.value = CommentSendStatus.Empty
                throw e
            } catch (e: Exception) {
                e.printStackOnlyDebug()
                _sendMessageStatus.value = CommentSendStatus.Error(e.localizedMessage)
            }
        }
    }

    fun clearCommentSendStatus() {
        if (_sendMessageStatus.value != CommentSendStatus.Sending) {
            _sendMessageStatus.value = CommentSendStatus.Empty
        }
    }

    sealed interface CommentSendStatus {
        data object Empty : CommentSendStatus
        data object Sending : CommentSendStatus
        data object Success : CommentSendStatus
        data class Error(val message: String?) : CommentSendStatus
    }

}
