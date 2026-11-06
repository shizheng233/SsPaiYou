package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.api.service.BulletService
import com.shihcheeng.sspaiyou.paging.BulletCommentPaging
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BulletRepository @Inject constructor(
    private val service: BulletService
) {
    suspend fun getBullet(createdAt: Long): BulletData {
        val response = service.getBulletInfo(createdAt = createdAt)
        check(response.error == 0) { "Bullet API error: ${response.error}" }
        return checkNotNull(response.data) { "Bullet API returned no data" }
    }

    fun comments(createdAt: Long, sortWay: String = "new"): Flow<PagingData<BulletComment>> =
        Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false)
        ) {
            BulletCommentPaging(service, createdAt, sortWay)
        }.flow
}
