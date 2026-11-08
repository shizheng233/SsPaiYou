package com.shihcheeng.sspaiyou.article

import androidx.paging.PagingSource
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.paging.ArticleCommentPaging
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException

class ArticleCommentTest {
    @Test
    fun sampleDecodesCommentsUsersAndNestedMetadata() {
        val response = Json.decodeFromString<ArticleCommentDataModel>(sampleJson())
        assertEquals(0, response.error)
        assertEquals(2, response.total)
        assertEquals(listOf(433748, 433738), response.data.map { it.id })
        val first = response.data.first()
        assertEquals("在用m4的mba，期待m7的macmini\n", first.comment)
        assertEquals(1793953905L, first.createdAt)
        assertEquals("xoj8fg97", first.user.slug)
        assertEquals("Lelucermaire", first.user.nickname)
        assertTrue(first.user.avatar.startsWith("https://cdnfile.sspai.com/"))
        assertFalse(first.user.deleted)
        assertTrue(first.commentFiles.isEmpty())
        assertTrue(first.reply.isEmpty())
        assertFalse(first.article.allowComment)
        assertEquals("", first.article.author.slug)
        assertFalse(first.series.isAuthor)
        assertEquals(3, first.status)
        assertTrue(first.replyShow)
        assertEquals(0, first.articleBodyExtendId)
        assertEquals(1, response.data.last().likesCount)
        assertEquals("q5o9uhg4", response.data.last().user.slug)
    }

    @Test
    fun retrofitUsesV2EndpointAndAllQueryParametersWithV1BaseUrl() = runBlocking {
        var request: Request? = null
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            request = chain.request()
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(sampleJson().toResponseBody(mediaType))
                .build()
        }.build()
        try {
            val service = Retrofit.Builder()
                .baseUrl(SSPaiUrl.BASE_URL)
                .client(client)
                .addConverterFactory(Json.asConverterFactory(mediaType))
                .build()
                .create(ArticleService::class.java)
            val result = service.getArticleComments(20, 40, 1793954000L, 123456)
            val capturedRequest = requireNotNull(request)
            val url = capturedRequest.url
            assertEquals("GET", capturedRequest.method)
            assertEquals("https", url.scheme)
            assertEquals("sspai.com", url.host)
            assertEquals("/api/v2/comment/search/page/get", url.encodedPath)
            assertEquals("20", url.queryParameter("limit"))
            assertEquals("40", url.queryParameter("offset"))
            assertEquals("1793954000", url.queryParameter("created_at"))
            assertEquals("123456", url.queryParameter("article_id"))
            assertEquals(2, result.data.size)
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }

    @Test
    fun shortPageContinuesUntilTotalWithFixedTimestampAndArticleId() = runBlocking {
        val service = FakeArticleService()
        val before = System.currentTimeMillis() / 1000
        val paging = ArticleCommentPaging(service, 123456)
        service.response = ArticleCommentDataModel(data = listOf(ArticleComment(id = 1), ArticleComment(id = 2)), total = 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)
        service.response = ArticleCommentDataModel(data = listOf(ArticleComment(id = 3)), total = 3)
        val last = paging.load(PagingSource.LoadParams.Append(2, 20, false)) as PagingSource.LoadResult.Page
        assertNull(last.nextKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 20 && it.articleId == 123456 })
        assertEquals(service.requests[0].createdAt, service.requests[1].createdAt)
        assertTrue(service.requests[0].createdAt in before..(System.currentTimeMillis() / 1000))
    }

    @Test
    fun emptyPageEndsEvenWhenTotalClaimsMore() = runBlocking {
        val service = FakeArticleService()
        service.response = ArticleCommentDataModel(total = 100)
        val page = ArticleCommentPaging(service, 123456)
            .load(PagingSource.LoadParams.Append(20, 20, false)) as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
    }

    @Test
    fun businessAndNetworkErrorsAreRetryableButCancellationPropagates() = runBlocking {
        val service = FakeArticleService()
        val paging = ArticleCommentPaging(service, 123456)
        service.response = ArticleCommentDataModel(error = 403, msg = "评论暂不可用")
        val businessError = paging.load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Error
        assertEquals("评论暂不可用", businessError.throwable.message)
        service.response = ArticleCommentDataModel(error = 500)
        val fallbackError = paging.load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Error
        assertTrue(fallbackError.throwable.message!!.contains("500"))
        val networkError = IOException("offline")
        service.failure = networkError
        val network = paging.load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Error
        assertSame(networkError, network.throwable)
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 20, false)) }
        })
    }

    private fun sampleJson(): String = requireNotNull(javaClass.getResource("/article_comment.json"))
        .readText(Charsets.UTF_8)

    private class FakeArticleService : ArticleService {
        var response = ArticleCommentDataModel()
        var failure: Exception? = null
        val requests = mutableListOf<CommentRequest>()

        override suspend fun getArticleDetail(id: String, view: String, supportWebp: Boolean): ArticleDataModel =
            error("Unexpected article detail request")

        override suspend fun getMemberArticleDetail(slug: String, view: String, supportWebp: Boolean): ArticleDataModel =
            error("Unexpected member article request")

        override suspend fun favouriteOrNot(request: ArticleFavouriteRequest): ArticleFavouriteResponse =
            error("Unexpected favourite request")

        override suspend fun likeItOrNot(request: ArticleLikeRequest): ArticleLikeResponse =
            error("Unexpected like request")

        override suspend fun getArticleComments(limit: Int, offset: Int, createdAt: Long, articleId: Int): ArticleCommentDataModel {
            requests += CommentRequest(limit, offset, createdAt, articleId)
            failure?.let { throw it }
            return response
        }
    }

    private data class CommentRequest(val limit: Int, val offset: Int, val createdAt: Long, val articleId: Int)
}
