package com.shihcheeng.sspaiyou.ui.screen.bullet.single

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.repository.BulletRepository
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailContent
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import com.shihcheeng.sspaiyou.utils.NetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class BulletSingleContent(
    val topic: BulletData,
    val blocks: List<DetailContent>
)

@HiltViewModel
class BulletSingleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BulletRepository,
    private val parser: DetailMarkDownParser
) : ViewModel() {
    private val createdAt = savedStateHandle.get<String>("createdAt")?.toLongOrNull()
        ?.takeIf { it > 0L }
    private val _data = MutableStateFlow<NetState<BulletSingleContent>>(NetState.Loading)
    val data = _data.asStateFlow()
    val comments = (createdAt?.let { repository.comments(it) }
        ?: flowOf(PagingData.empty())).cachedIn(viewModelScope)
    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _data.value = NetState.Loading
            try {
                val topic =
                    repository.getBullet(requireNotNull(createdAt) { "Invalid discussion timestamp" })
                val blocks = withContext(Dispatchers.Default) { parser.parse(topic.body) }
                _data.value = NetState.Success(BulletSingleContent(topic, blocks))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _data.value = NetState.Error(e)
            }
        }
    }
}
