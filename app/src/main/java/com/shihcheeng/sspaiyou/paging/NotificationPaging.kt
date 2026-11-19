package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.notification.CommentNotification
import com.shihcheeng.sspaiyou.api.data.notification.OnlineNotification
import com.shihcheeng.sspaiyou.api.service.NotificationService
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging

class OnlineNotificationPaging(private val service: NotificationService) : BaseMatrixPaging<OnlineNotification>() {
    override suspend fun loadPage(limit: Int, offset: Int, createdAt: Long): PageData<OnlineNotification> {
        val response = service.getOnlinePageGet(offset = offset, limit = limit, createdAt = createdAt)
        check(response.error == 0) {
            response.msg.ifBlank { "Online notifications API error: ${response.error}" }
        }
        return PageData(checkNotNull(response.data) { "Online notifications response has no data" }, response.total)
    }
}

class CommentNotificationPaging(private val service: NotificationService) : BaseMatrixPaging<CommentNotification>() {
    override suspend fun loadPage(limit: Int, offset: Int, createdAt: Long): PageData<CommentNotification> {
        val response = service.getInfoCommentGet(offset = offset, limit = limit, createdAt = createdAt)
        check(response.error == 0) {
            response.msg.ifBlank { "Comment notifications API error: ${response.error}" }
        }
        return PageData(checkNotNull(response.data) { "Comment notifications response has no data" }, response.total)
    }
}
