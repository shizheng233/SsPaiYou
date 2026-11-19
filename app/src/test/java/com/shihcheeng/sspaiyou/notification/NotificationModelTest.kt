package com.shihcheeng.sspaiyou.notification

import com.shihcheeng.sspaiyou.api.data.notification.CommentNotification
import com.shihcheeng.sspaiyou.api.data.notification.NotificationPage
import com.shihcheeng.sspaiyou.api.data.notification.OnlineNotification
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationModelTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun fixture(name: String) =
        requireNotNull(javaClass.getResource("/notification/$name.json")).readText(Charsets.UTF_8)

    @Test
    fun onlinePageDecodesWithoutActivityDiscriminator() {
        val page = json.decodeFromString<NotificationPage<OnlineNotification>>(fixture("online_get_mine"))
        assertEquals(0, page.error)
        assertEquals(9, page.total)
        assertEquals(4, page.data?.size)
        val first = requireNotNull(page.data).first()
        assertEquals(13, first.type)
        assertEquals(118844, first.articleId)
        assertEquals(433960, first.commentId)
        assertTrue(first.comment.isNotBlank())
        assertTrue(first.isRead)
        assertNotNull(first.user)
        assertTrue(first.user?.slug?.isNotBlank() == true)
    }

    @Test
    fun commentPagePreservesReplyAndReadState() {
        val page = json.decodeFromString<NotificationPage<CommentNotification>>(fixture("comment_get_mine"))
        assertEquals(0, page.error)
        assertEquals(10, page.total)
        assertEquals(4, page.data?.size)
        val first = requireNotNull(page.data).first()
        assertEquals(4713049, first.id)
        assertTrue(first.isRead)
        assertTrue(first.isDeleted)
        assertFalse(first.articleBelongToMember)
        assertEquals(118844, first.data.articleId)
        assertEquals(397136, first.data.commentReplyId)
        assertTrue(first.data.commentReply.isNotBlank())
        assertTrue(first.data.nickname.isNotBlank())
    }
}
