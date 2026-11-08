package com.shihcheeng.sspaiyou.article

import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException

class ArticleFavouriteTest {
    @Test
    fun retrofitPostsJsonToExistingEndpointAndDecodesSuccess() {
        val requests = mutableListOf<Request>()
        withService(onRequest = { requests += it }) { service ->
            for (flag in listOf(1, 2)) {
                assertEquals(0, service.favouriteOrNot(ArticleFavouriteRequest(115074, flag)).error)
            }
        }
        requests.forEachIndexed { index, request ->
            assertEquals("POST", request.method)
            assertEquals("${SSPaiUrl.BASE_URL}${SSPaiUrl.FAVOURITE_ON_OFF}", request.url.toString())
            val body = requireNotNull(request.body)
            assertEquals("application", body.contentType()?.type)
            assertEquals("json", body.contentType()?.subtype)
            val json = Buffer().also { body.writeTo(it) }.readUtf8()
            assertEquals(
                Json.parseToJsonElement("""{"target_id":115074,"flag":${index + 1}}"""),
                Json.parseToJsonElement(json)
            )
        }
        assertEquals(2, requests.size)
    }

    @Test
    fun repositoryMapsFavouriteAndUnfavouriteToFlags() {
        val bodies = mutableListOf<ArticleFavouriteRequest>()
        withService(onRequest = { request ->
            val json = Buffer().also { requireNotNull(request.body).writeTo(it) }.readUtf8()
            bodies += Json.decodeFromString<ArticleFavouriteRequest>(json)
        }) { service ->
            val repository = DetailRepository(service)
            repository.setFavourite(115074, true)
            repository.setFavourite(115074, false)
        }
        assertEquals(listOf(ArticleFavouriteRequest(115074, 1), ArticleFavouriteRequest(115074, 2)), bodies)
    }

    @Test
    fun repositoryRejectsBusinessError() {
        withService(response = """{"error":403}""") { service ->
            val error = assertThrows(IllegalStateException::class.java) {
                runBlocking { DetailRepository(service).setFavourite(115074, true) }
            }
            assertTrue(error.message.orEmpty().contains("403"))
        }
    }

    @Test
    fun responseWithoutErrorCannotBeMistakenForSuccess() {
        withService(response = "{}") { service ->
            assertThrows(SerializationException::class.java) {
                runBlocking { DetailRepository(service).setFavourite(115074, true) }
            }
        }
    }

    @Test
    fun repositoryPropagatesHttpFailure() {
        withService(code = 401) { service ->
            val error = assertThrows(HttpException::class.java) {
                runBlocking { DetailRepository(service).setFavourite(115074, true) }
            }
            assertEquals(401, error.code())
        }
    }

    @Test
    fun repositoryPropagatesNetworkFailure() {
        withService(onRequest = { throw IOException("offline") }) { service ->
            val error = assertThrows(IOException::class.java) {
                runBlocking { DetailRepository(service).setFavourite(115074, true) }
            }
            assertEquals("offline", error.message)
        }
    }

    @Test
    fun invalidIdIsRejectedBeforeSendingRequest() {
        var requests = 0
        withService(onRequest = { requests++ }) { service ->
            for (id in listOf(0, -1)) {
                assertThrows(IllegalArgumentException::class.java) {
                    runBlocking { DetailRepository(service).setFavourite(id, true) }
                }
            }
        }
        assertEquals(0, requests)
    }

    private fun withService(
        response: String = """{"error":0}""",
        code: Int = 200,
        onRequest: (Request) -> Unit = {},
        block: suspend (ArticleService) -> Unit
    ) = runBlocking {
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            onRequest(chain.request())
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("test response")
                .body(response.toResponseBody(mediaType))
                .build()
        }.build()
        try {
            val service = Retrofit.Builder()
                .baseUrl(SSPaiUrl.BASE_URL)
                .client(client)
                .addConverterFactory(Json.asConverterFactory(mediaType))
                .build()
                .create(ArticleService::class.java)
            block(service)
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }
}
