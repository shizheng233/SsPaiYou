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
    private val markDownParser: DetailMarkDownParser
) : ViewModel() {

    val id = savedStateHandle.get<String>("id")

    private val _data = MutableStateFlow<NetState<ArticleData>>(NetState.Loading)
    val data = _data.asStateFlow()

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
