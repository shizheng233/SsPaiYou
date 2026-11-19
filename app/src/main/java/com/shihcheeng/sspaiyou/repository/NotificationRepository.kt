package com.shihcheeng.sspaiyou.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.shihcheeng.sspaiyou.api.data.notification.CommentNotification
import com.shihcheeng.sspaiyou.api.data.notification.InformationMine
import com.shihcheeng.sspaiyou.api.data.notification.OnlineNotification
import com.shihcheeng.sspaiyou.api.service.NotificationService
import com.shihcheeng.sspaiyou.database.dao.NotificationDao
import com.shihcheeng.sspaiyou.database.dao.StoredNotification
import com.shihcheeng.sspaiyou.database.datamodel.NotificationEntity
import com.shihcheeng.sspaiyou.database.datamodel.NotificationReadState
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

private fun digest(value: String): String = MessageDigest.getInstance("SHA-256")
    .digest(value.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

private fun onlineId(item: OnlineNotification): String = digest(
    Json.encodeToString(
        listOf(
            item.type.toString(),
            item.createdAt.toString(),
            item.user?.id.toString(),
            item.articleId.toString(),
            item.commentId.toString(),
            item.commentReplyId.toString(),
            item.communityTopicIdHash,
            item.topicCommentIdHash,
            item.topicCommentReplyIdHash,
            item.threadIdHash,
            item.threadCommentIdHash,
            item.threadCommentReplyIdHash,
            item.bulletCreatedAt.toString(),
            item.articleBodyExtendId.toString(),
            item.title,
            item.comment
        )
    )
)

@Singleton
class NotificationRepository @Inject constructor(
    private val service: NotificationService,
    private val dao: NotificationDao,
    private val tokenManager: LocalTokenManager,
    private val homeRepository: HomeRepository
) {
    private val pageLoader = NotificationPageLoader(service, dao, tokenManager)
    private fun account(token: String) = digest(token)

    fun unreadCount(token: String): Flow<Int> = dao.observeUnreadCount(account(token))

    suspend fun saveSummary(token: String, information: InformationMine) {
        if (tokenManager.getToken() != token) return
        fun total(vararg counts: Int) = counts.sumOf { it.coerceAtLeast(0).toLong() }
            .coerceAtMost((Int.MAX_VALUE / 3).toLong()).toInt()

        val account = account(token)
        dao.saveSummary(
            listOf(
                NotificationReadState(
                    account,
                    "summary",
                    "online",
                    unreadCount = total(information.onlineCount)
                ),
                NotificationReadState(
                    account,
                    "summary",
                    "comment",
                    unreadCount = total(
                        information.commentCount,
                        information.communityCommentCount,
                        information.seriesCommentCount
                    )
                ),
                NotificationReadState(
                    account,
                    "summary",
                    "other",
                    unreadCount = total(
                        information.communitySubscribeCount,
                        information.messageCount,
                        information.systemCount
                    )
                )
            )
        )
    }

    suspend fun markOnlineRead(token: String, item: OnlineNotification) =
        dao.markRead(account(token), "online", onlineId(item))

    suspend fun markCommentRead(token: String, item: CommentNotification) =
        dao.markRead(account(token), "comment", item.id.toString())

    /** Refresh each feed independently, preserving its cache if its request fails. */
    suspend fun sync(token: String) = coroutineScope {
        suspend fun attempt(block: suspend () -> Unit) {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // The local snapshot remains available for the next refresh.
            }
        }
        launch { attempt { saveSummary(token, homeRepository.getInformation()) } }
        listOf("online", "comment").forEach { kind ->
            launch {
                attempt {
                    pageLoader.load(
                        token,
                        account(token),
                        kind,
                        0,
                        4,
                        System.currentTimeMillis() / 1000,
                        refresh = true
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun stored(token: String, kind: String): Flow<PagingData<StoredNotification>> {
        val account = account(token)
        return Pager(
            config = PagingConfig(pageSize = 4, initialLoadSize = 4, enablePlaceholders = false),
            remoteMediator = NotificationMediator(token, account, kind, pageLoader),
            pagingSourceFactory = { dao.pagingSource(account, kind) }
        ).flow
    }

    fun online(token: String): Flow<PagingData<OnlineNotification>> =
        stored(token, "online").map { page ->
            page.map {
                Json.decodeFromString<OnlineNotification>(it.payload).copy(isRead = it.isRead)
            }
        }

    fun comments(token: String): Flow<PagingData<CommentNotification>> =
        stored(token, "comment").map { page ->
            page.map {
                Json.decodeFromString<CommentNotification>(it.payload).copy(isRead = it.isRead)
            }
        }
}

private data class LoadedNotificationPage(
    val size: Int,
    val total: Int?,
    val offset: Int,
    val snapshot: Long,
    val generation: Long
)

private class NotificationPageLoader(
    private val service: NotificationService,
    private val dao: NotificationDao,
    private val tokenManager: LocalTokenManager
) {
    private val mutex = Mutex()
    private val generations = mutableMapOf<Pair<String, String>, Long>()

    suspend fun load(
        token: String,
        account: String,
        kind: String,
        pageOffset: Int,
        limit: Int,
        snapshot: Long,
        refresh: Boolean,
        expectedGeneration: Long? = null
    ): LoadedNotificationPage =
        mutex.withLock {
            val key = account to kind
            val generation = generations[key] ?: 0L
            // A home refresh may have replaced the rows while a mediator was alive.
            val doRefresh =
                refresh || (expectedGeneration != null && expectedGeneration != generation)
            val offset = if (doRefresh) 0 else pageOffset
            val createdAt =
                if (doRefresh && !refresh) System.currentTimeMillis() / 1000 else snapshot
            val messages: List<NotificationEntity>
            val states: List<NotificationReadState>
            val total: Int?
            if (kind == "online") {
                val response =
                    service.getOnlinePageGet(offset = offset, limit = limit, createdAt = createdAt)
                check(response.error == 0) { response.msg.ifBlank { "Notification API error: ${response.error}" } }
                val items = checkNotNull(response.data) { "Notification response has no data" }
                messages = items.mapIndexed { index, item ->
                    NotificationEntity(
                        account,
                        kind,
                        onlineId(item),
                        offset + index,
                        Json.encodeToString(item)
                    )
                }
                states = items.map {
                    NotificationReadState(
                        account,
                        kind,
                        onlineId(it),
                        isRead = it.isRead
                    )
                }
                total = response.total
            } else {
                val response =
                    service.getInfoCommentGet(offset = offset, limit = limit, createdAt = createdAt)
                check(response.error == 0) { response.msg.ifBlank { "Notification API error: ${response.error}" } }
                val items = checkNotNull(response.data) { "Notification response has no data" }
                messages = items.mapIndexed { index, item ->
                    NotificationEntity(
                        account = account,
                        kind = kind,
                        messageId = item.id.toString(),
                        position = offset + index,
                        payload = Json.encodeToString(item)
                    )
                }
                states = items.map {
                    NotificationReadState(
                        account = account,
                        kind = kind,
                        messageId = it.id.toString(),
                        isRead = it.isRead
                    )
                }
                total = response.total
            }
            if (tokenManager.getToken() != token) throw CancellationException("Account changed")
            dao.savePage(account, kind, doRefresh, messages, states)
            val resultGeneration = if (doRefresh) generation + 1 else generation
            generations[key] = resultGeneration
            LoadedNotificationPage(messages.size, total, offset, createdAt, resultGeneration)
        }
}

@OptIn(ExperimentalPagingApi::class)
private class NotificationMediator(
    private val token: String,
    private val account: String,
    private val kind: String,
    private val pageLoader: NotificationPageLoader
) : RemoteMediator<Int, StoredNotification>() {
    private var offset = 0
    private var createdAt = 0L
    private var generation = 0L

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoredNotification>
    ): MediatorResult {
        if (loadType == LoadType.PREPEND) return MediatorResult.Success(endOfPaginationReached = true)
        return try {
            val refresh = loadType == LoadType.REFRESH
            val pageOffset = if (refresh) 0 else offset
            val snapshot = if (refresh) System.currentTimeMillis() / 1000 else createdAt
            val page = pageLoader.load(
                token = token,
                account = account,
                kind = kind,
                pageOffset = pageOffset,
                limit = state.config.pageSize,
                snapshot = snapshot,
                refresh = refresh,
                expectedGeneration = generation
            )
            createdAt = page.snapshot
            generation = page.generation
            offset = page.offset + page.size
            MediatorResult.Success(endOfPaginationReached = page.size == 0 || (page.total != null && offset >= page.total))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
