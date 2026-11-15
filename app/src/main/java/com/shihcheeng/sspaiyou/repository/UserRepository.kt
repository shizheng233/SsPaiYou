package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.paging.ActivityPaging
import com.shihcheeng.sspaiyou.paging.UserFavouritePaging
import com.shihcheeng.sspaiyou.paging.UserFollowedPaging
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository internal constructor(
    private val service: UserService,
    private val readToken: () -> String? = { null }
) {
    @Inject
    constructor(service: UserService, tokenManager: LocalTokenManager) :
        this(service, tokenManager::getToken)

    private val _followChanges = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 16)
    val followChanges = _followChanges.asSharedFlow()
    private val _followPending = MutableStateFlow<Set<String>>(emptySet())
    val followPending = _followPending.asStateFlow()
    private val followedListVersion = MutableStateFlow(0)
    private val followLock = Any()

    suspend fun setFollowed(slug: String, followed: Boolean) {
        require(slug.isNotBlank()) { "Invalid user slug" }
        synchronized(followLock) {
            if (slug in _followPending.value) return
            _followPending.value = _followPending.value + slug
        }
        try {
            val token = readToken()?.takeIf { it.isNotBlank() } ?: return
            val response = service.followUser(FollowUserRequest(slug, if (followed) 1 else 2))
            if (token != readToken()) return
            check(response.error == 0) {
                response.msg.ifBlank { "Follow user API error: ${response.error}" }
            }
            followedListVersion.update { it + 1 }
            _followChanges.emit(slug to followed)
        } finally {
            synchronized(followLock) {
                _followPending.value = _followPending.value - slug
            }
        }
    }

    suspend fun getUserInfo() = withContext(Dispatchers.Default) {
        service.getUserInfo()
    }

    suspend fun getUserInfo(slug: String) = withContext(Dispatchers.Default) {
        require(slug.isNotBlank()) { "Invalid user slug" }
        service.getUserInfo(slug).also {
            check(it.error == 0) { "User info API error: ${it.error}" }
            check(it.data.slug.isNotBlank()) { "User data is missing" }
        }
    }

    fun getData(slug: String) = Pager(
        config = PagingConfig(
            pageSize = Constants.NORMAL_LIMIT,
            initialLoadSize = Constants.NORMAL_LIMIT,
            enablePlaceholders = false
        )
    ) {
        ActivityPaging(service, slug)
    }.flow

    fun getFavourites(slug: String) = Pager(
        config = PagingConfig(
            pageSize = Constants.NORMAL_LIMIT,
            initialLoadSize = Constants.NORMAL_LIMIT,
            enablePlaceholders = false
        )
    ) {
        UserFavouritePaging(service, slug)
    }.flow

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFollowedUsers(slug: String) = followedListVersion.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = Constants.NORMAL_LIMIT,
                initialLoadSize = Constants.NORMAL_LIMIT,
                enablePlaceholders = false
            )
        ) {
            UserFollowedPaging(service, slug)
        }.flow
    }
}
