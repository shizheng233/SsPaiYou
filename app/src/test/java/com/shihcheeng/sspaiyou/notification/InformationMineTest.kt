package com.shihcheeng.sspaiyou.notification

import com.shihcheeng.sspaiyou.api.data.home.main.HomeMainDataModel
import com.shihcheeng.sspaiyou.api.data.home.second.SecondHomeDataModel
import com.shihcheeng.sspaiyou.api.data.home.sub.HomeSubDataModel
import com.shihcheeng.sspaiyou.api.data.home.top.HeaderBannerDataModel
import com.shihcheeng.sspaiyou.api.data.notification.InformationMine
import com.shihcheeng.sspaiyou.api.data.notification.InformationMineResponse
import com.shihcheeng.sspaiyou.api.service.HomeService
import com.shihcheeng.sspaiyou.repository.HomeRepository
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class InformationMineTest {
    @Test
    fun sampleDecodesAndAllCountsContributeToBell() {
        val sample = javaClass.getResource("/notification/information_mine.json")!!.readText()
        val response = Json.decodeFromString<InformationMineResponse>(sample)
        val data = requireNotNull(response.data)
        assertEquals(0, response.error)
        assertEquals(2, data.commentCount)
        assertEquals(1, data.onlineCount)
        assertEquals("<a href=\"https://sspai.com/u/arieszhou\">墨明棋妙</a> 回应了你的评论", data.lastMessageOnline)
        assertFalse(data.isSeriesAuthor)
        assertTrue(data.hasNotifications)
        assertFalse(InformationMine(isSeriesAuthor = true, lastMessageOnline = "old").hasNotifications)
        listOf(
            InformationMine(commentCount = 1), InformationMine(communityCommentCount = 1),
            InformationMine(communitySubscribeCount = 1), InformationMine(messageCount = 1),
            InformationMine(onlineCount = 1), InformationMine(seriesCommentCount = 1),
            InformationMine(systemCount = 1)
        ).forEach { assertTrue(it.hasNotifications) }
    }

    @Test
    fun repositoryUsesDefaultParametersAndRejectsErrorsOrMissingData() = runBlocking {
        val service = FakeHomeService()
        val repository = HomeRepository(service)
        val data = InformationMine(onlineCount = 1)
        service.response = InformationMineResponse(data, 0)
        assertEquals(data, repository.getInformation())
        assertEquals(listOf(false, true, true, true, true, true), service.parameters)

        service.response = InformationMineResponse(data, 403, "denied")
        val error = assertThrows(IllegalStateException::class.java) { runBlocking { repository.getInformation() } }
        assertEquals("denied", error.message)
        service.response = InformationMineResponse(error = 503)
        val noMessage = assertThrows(IllegalStateException::class.java) { runBlocking { repository.getInformation() } }
        assertTrue(noMessage.message!!.contains("503"))
        service.response = InformationMineResponse(error = 0)
        assertThrows(IllegalStateException::class.java) { runBlocking { repository.getInformation() } }
        Unit
    }

    private class FakeHomeService : HomeService {
        var response = InformationMineResponse(error = 0)
        var parameters = emptyList<Boolean>()

        override suspend fun getInformation(
            lastMessageFlag: Boolean, community: Boolean, hasBullet: Boolean,
            hasArticleBodyExtendStatus: Boolean, hasArticleCommentStatus: Boolean,
            hasArticleCommentReplyStatus: Boolean
        ): InformationMineResponse {
            parameters = listOf(lastMessageFlag, community, hasBullet, hasArticleBodyExtendStatus,
                hasArticleCommentStatus, hasArticleCommentReplyStatus)
            return response
        }

        override suspend fun getHomeSub(limit: Int, offset: Int, type: String): HomeSubDataModel = error("unused")
        override suspend fun getHomeSecond(view: String): SecondHomeDataModel = error("unused")
        override suspend fun getHome(limit: Int, offset: Int, createdAt: Long): HomeMainDataModel = error("unused")
        override suspend fun getHomeMain(limit: Int, offset: Int, type: String): HeaderBannerDataModel = error("unused")
    }
}
