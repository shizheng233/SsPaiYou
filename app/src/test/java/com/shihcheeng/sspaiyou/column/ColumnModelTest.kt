package com.shihcheeng.sspaiyou.column

import com.shihcheeng.sspaiyou.api.data.column.ColumnArticleDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnSingleDataModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ColumnModelTest {
    @Test
    fun detailDecodesChiefEditorFollowersAndStatistics() {
        val result = Json.decodeFromString<ColumnSingleDataModel>(
            """
            {"data":{"id":245,"title":"科研利器",
            "banner":"2021/04/09/c534cedbbc15b2d1b090179295908cf3.jpg",
            "intro":"分享科研、教学日常中使用的工具与技术",
            "chief_editor":{"avatar":"article/editor.jpeg","nickname":"玉树芝兰","slug":"a5xddvxl"},
            "article_count":54,"article_audit_count":1,"author_count":2,"editor_count":0,
            "follow_count":4305,"followed":false,"accept_article":false,"manage_permission":false,
            "follow_users":[{"id":0,"id_hash":"dbg3ecdb34t9occ9ii30","slug":"irjncjhh",
            "avatar":"2026/10/27/avatar/follower","nickname":"lj0060","bio":"","role":"user",
            "followed":false,"follow_count":2,"fans_count":0,"article_count":0,
            "user_flags":[],"user_badges":[],"user_plan_flags":[],"user_id_verify_flags":[],
            "intro":"","url":"","recommend_to_home_flag":{"id":0,"key":"","updated_at":0}}]},
            "error":0}
            """.trimIndent()
        )
        val column = requireNotNull(result.data)
        assertEquals(0, result.error)
        assertEquals(245, column.id)
        assertEquals("科研利器", column.title)
        assertEquals("a5xddvxl", column.chiefEditor.slug)
        assertEquals("玉树芝兰", column.chiefEditor.nickname)
        assertEquals(54, column.articleCount)
        assertEquals(1, column.articleAuditCount)
        assertEquals(2, column.authorCount)
        assertEquals(4305, column.followCount)
        assertFalse(column.followed)
        assertFalse(column.acceptArticle)
        assertFalse(column.managePermission)
        val follower = column.followUsers.single()
        assertEquals("irjncjhh", follower.slug)
        assertEquals("dbg3ecdb34t9occ9ii30", follower.idHash)
        assertEquals(2, follower.followCount)
        assertEquals(0L, follower.recommendToHomeFlag.updatedAt)
    }

    @Test
    fun articlesDecodeColumnObjectsBadgesAndNullableBodyExtensions() {
        val result = Json.decodeFromString<ColumnArticleDataModel>(
            """
            {"error":0,"msg":"","data":[{"id":99595,
            "title":"App+1 | 用 Remio 自动化、智能化管理知识",
            "banner":"2025/05/25/d06815f0db00ae4c7e071ac35b5216bc.png",
            "summary":"一款自动化累积资料和本地资料检索的工具。",
            "comment_count":15,"like_count":50,"free":true,"released_time":1748143658,
            "created_time":1748143559,"modify_time":1766897082,"recommend_to_home_at":1748424716,
            "author":{"slug":"a5xddvxl","nickname":"玉树芝兰",
            "user_flags":[{"id":19,"key":"writer","name":"少数派作者","updated_at":1768720750}],
            "user_badges":[{"id":17,"key":"member_2022","color":"#CFC8C8"}],
            "user_plan_flags":[{"id":14,"key":"matrix_plan_member"}],"user_id_verify_flags":[]},
            "corner":{"id":6,"name":"Matrix精选"},
            "special_columns":[{"id":245,"title":"科研利器","banner":"column.jpg"}],
            "body_extend_titles":[],"body_extend":{"id":0,"title":"","body":"",
            "comment_on":false,"comment_show_on":false,"status_on":false,"status_show_on":false,
            "comment_count":0,"comments":null,"statuses":null},"statuses":[]}],"total":54}
            """.trimIndent()
        )
        val article = result.data.single()
        assertEquals(54, result.total)
        assertEquals(99595, article.id)
        assertTrue(article.free)
        assertEquals(1748143658L, article.releasedTime)
        assertEquals(1766897082L, article.modifyTime)
        assertEquals(1748424716L, article.recommendToHomeAt)
        assertEquals(15, article.commentCount)
        assertEquals(50, article.likeCount)
        assertEquals("a5xddvxl", article.author.slug)
        assertEquals("writer", article.author.userFlags.single().key)
        assertEquals(1768720750L, article.author.userFlags.single().updatedAt)
        assertEquals("member_2022", article.author.userBadges.single().key)
        assertEquals("matrix_plan_member", article.author.userPlanFlags.single().key)
        assertEquals(6, article.corner.id)
        assertEquals(245, article.specialColumns.single().id)
        assertEquals("科研利器", article.specialColumns.single().title)
        assertNull(article.bodyExtend.comments)
        assertNull(article.bodyExtend.statuses)
        assertTrue(article.statuses.isEmpty())
    }

    @Test
    fun envelopesRequireErrorButAllowMissingData() {
        assertNull(Json.decodeFromString<ColumnSingleDataModel>("""{"error":404}""").data)
        assertTrue(Json.decodeFromString<ColumnArticleDataModel>("""{"error":403}""").data.isEmpty())
        assertNull(Json.decodeFromString<ColumnArticleDataModel>("""{"error":0,"data":[]}""").total)
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<ColumnSingleDataModel>("""{"data":{}}""")
        }
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<ColumnArticleDataModel>("""{"data":[]}""")
        }
    }
}
