package com.shihcheeng.sspaiyou.user

import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class UserFavouriteModelTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun samplePageDecodesAllTenFavouriteArticlesAndTotal() {
        val response = sample()
        assertEquals(0, response.error)
        assertEquals("", response.msg)
        assertEquals(30, response.total)
        assertEquals(10, response.data.size)
        assertEquals(
            listOf(86611, 85708, 65567, 69006, 52190, 74978, 68370, 71936, 72761, 73294),
            response.data.map { it.id }
        )

        val first = response.data.first()
        assertEquals("iPhone快捷指令自动记账，懒人无痛记账神器", first.title)
        assertEquals("2024/02/23/f87dd94ff0a34ff52e41beb55103eeea.jpg", first.banner)
        assertTrue(first.summary.contains("iPhone快捷指令记账"))
        assertEquals(32, first.commentCount)
        assertEquals(91, first.likeCount)
        assertEquals(0, first.viewCount)
        assertTrue(first.free)
        assertTrue(first.isMatrix)
        assertFalse(first.isRecommendToHome)
        assertEquals(1708671747L, first.releasedTime)
        assertEquals(1708918510L, first.createdTime)
        assertEquals(1756040969L, first.modifyTime)
        assertEquals("e1w3j9wv", first.author.slug)
        assertEquals("李坤雨", first.author.nickname)
        assertEquals("2024/02/23/avatar/ce67f52e54703d61dd2172a58d4e596b", first.author.avatar)

        val last = response.data.last()
        assertEquals("别被 root 挡在门外：Shizuku 让 Android 玩机更简单", last.title)
        assertEquals("LuZhNan", last.author.slug)
        assertEquals(6, last.corner.id)
        assertEquals("Matrix精选", last.corner.name)
        assertEquals(1652856047L, last.recommendToHomeAt)
    }

    @Test
    fun samplePreservesAuthorFlagsBadgesPlansAndNullableBodyExtensionLists() {
        val articles = sample().data
        val writer = articles.first().author.userFlags.single()
        assertEquals(19, writer.id)
        assertEquals("writer", writer.key)
        assertEquals("少数派作者", writer.name)
        assertEquals(1768720750L, writer.updatedAt)
        assertFalse(writer.allowAddUser)
        assertEquals(listOf("id_auth_personal", "writer"), articles[2].author.userFlags.map { it.key })

        val staff = articles[5].author
        assertEquals("role_admin", staff.userFlags.single().key)
        assertEquals("#4A4A4A", staff.userFlags.single().color)
        val badge = staff.userBadges.single()
        assertEquals("member_2022", badge.key)
        assertEquals("https://sspai.com/prime", badge.url)
        assertEquals("#CFC8C8", badge.color)
        val plan = staff.userPlanFlags.single()
        assertEquals("matrix_plan_member", plan.key)
        assertEquals("Matrix 共创计划成员，立即加入>", plan.description)
        assertEquals(11, articles[7].corner.id)
        assertEquals("Tron计划", articles[7].corner.name)

        articles.forEach { article ->
            assertTrue(article.author.userIdVerifyFlags.isEmpty())
            assertTrue(article.bodyExtendTitles.isEmpty())
            assertNull(article.bodyExtend.comments)
            assertNull(article.bodyExtend.statuses)
            assertFalse(article.bodyExtend.commentOn)
            assertFalse(article.bodyExtend.statusOn)
            assertTrue(article.statuses.isEmpty())
            assertTrue(article.specialColumns.isEmpty())
            assertTrue(article.tags.isEmpty())
        }
    }

    @Test
    fun articleAndBadgeTimestampsRetainValuesBeyondIntRange() {
        val response = json.decodeFromString<UserFavouriteDataModel>(
            """
            {"error":0,"data":[{"id":42,"released_time":2200000000,
            "created_time":2200000001,"modify_time":2200000002,"recommend_to_home_at":2200000003,
            "article_limit_free_stime":2200000004,"article_limit_free_etime":2200000005,
            "author":{"user_flags":[{"created_at":2200000006,"updated_at":2200000007}]},
            "body_extend":{"comments":null,"statuses":null}}]}
            """.trimIndent()
        )
        val article = response.data.single()
        assertEquals(2200000000L, article.releasedTime)
        assertEquals(2200000001L, article.createdTime)
        assertEquals(2200000002L, article.modifyTime)
        assertEquals(2200000003L, article.recommendToHomeAt)
        assertEquals(2200000004L, article.articleLimitFreeStime)
        assertEquals(2200000005L, article.articleLimitFreeEtime)
        assertEquals(2200000006L, article.author.userFlags.single().createdAt)
        assertEquals(2200000007L, article.author.userFlags.single().updatedAt)
    }

    @Test
    fun envelopeRequiresErrorButDefaultsToAnEmptyPageAndOptionalTotal() {
        val empty = json.decodeFromString<UserFavouriteDataModel>("""{"error":0}""")
        assertTrue(empty.data.isEmpty())
        assertEquals("", empty.msg)
        assertNull(empty.total)
        val explicitEmpty = json.decodeFromString<UserFavouriteDataModel>(
            """{"error":0,"data":[],"total":null}"""
        )
        assertEquals(empty, explicitEmpty)
        val failure = json.decodeFromString<UserFavouriteDataModel>("""{"error":403,"msg":"私密收藏"}""")
        assertTrue(failure.data.isEmpty())
        assertEquals(403, failure.error)
        assertEquals("私密收藏", failure.msg)
        assertThrows(SerializationException::class.java) {
            json.decodeFromString<UserFavouriteDataModel>("""{"data":[]}""")
        }
    }

    private fun sample(): UserFavouriteDataModel = json.decodeFromString(
        requireNotNull(javaClass.getResource("/user_favourite.json")).readText(Charsets.UTF_8)
    )
}
