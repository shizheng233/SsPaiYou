package com.shihcheeng.sspaiyou.article

import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.shihcheeng.sspaiyou.modula.ImageModula
import com.shihcheeng.sspaiyou.utils.ImageCacheConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.CipherSuite
import okhttp3.Handshake
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.TlsVersion
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

@OptIn(ExperimentalCoilApi::class)
class ArticleImageCacheTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun thumbnailAndLargerImageReuseOriginalDiskFileWhenOffline() = runBlocking {
        val root = newCacheRoot()
        val cachedContext = object : ContextWrapper(context) {
            override fun getCacheDir(): File = root
        }
        val bytes = imageBytes()
        val offline = AtomicBoolean(false)
        val calls = AtomicInteger(0)
        val loader = ImageModula.imageLoader(client(bytes, calls, offline), cachedContext, 1024L * 1024)
        try {
            val url = "https://example.com/original.png"
            val request = ImageRequest.Builder(context).data(url).size(32)
                .memoryCachePolicy(CachePolicy.DISABLED).build()
            val thumbnail = loader.execute(request)
            assertTrue(thumbnail is SuccessResult)
            val cache = requireNotNull(loader.diskCache)
            assertEquals(1024 * 1024L, cache.maxSize)
            assertEquals(root.resolve(ImageCacheConfig.DIRECTORY_NAME), cache.directory.toFile())
            requireNotNull(cache.openSnapshot(url)).use { snapshot ->
                assertArrayEquals(bytes, snapshot.data.toFile().readBytes())
            }
            offline.set(true)
            val fullImage = loader.execute(request.newBuilder().size(800).build())
            assertTrue("Unexpected offline result: $fullImage", fullImage is SuccessResult)
            assertEquals(DataSource.DISK, (fullImage as SuccessResult).dataSource)
            assertEquals(1, calls.get())
        } finally {
            loader.shutdown()
            loader.diskCache?.clear()
            root.deleteRecursively()
        }
    }

    @Test
    fun configuredLimitEvictsLeastRecentlyReadImage() = runBlocking {
        val root = newCacheRoot()
        val cachedContext = object : ContextWrapper(context) {
            override fun getCacheDir(): File = root
        }
        val bytes = imageBytes()
        val limit = (bytes.size + 512L) * 2
        val loader = ImageModula.imageLoader(client(bytes), cachedContext, limit)
        try {
            suspend fun load(name: String): SuccessResult {
                val result = loader.execute(ImageRequest.Builder(context)
                    .data("https://example.com/$name.png").size(32)
                    .memoryCachePolicy(CachePolicy.DISABLED).build())
                assertTrue(result is SuccessResult)
                return result as SuccessResult
            }
            load("a")
            load("b")
            // Reading a from disk makes b the least recently used entry.
            assertEquals(DataSource.DISK, load("a").dataSource)
            load("c")
            val cache = requireNotNull(loader.diskCache)
            withTimeout(5_000) {
                while (cache.size > limit) delay(10)
            }
            assertTrue(cache.size <= limit)
            assertNull(cache.openSnapshot("https://example.com/b.png"))
            cache.openSnapshot("https://example.com/a.png").use { assertNotNull(it) }
            cache.openSnapshot("https://example.com/c.png").use { assertNotNull(it) }
        } finally {
            loader.shutdown()
            loader.diskCache?.clear()
            root.deleteRecursively()
        }
    }

    private fun newCacheRoot() = File.createTempFile("article_cache_", "", context.cacheDir)
        .apply { delete(); mkdirs() }

    private fun client(
        bytes: ByteArray,
        calls: AtomicInteger = AtomicInteger(),
        offline: AtomicBoolean = AtomicBoolean(false)
    ) = OkHttpClient.Builder().addInterceptor { chain ->
        calls.incrementAndGet()
        if (offline.get()) throw IOException("Offline")
        Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
            .code(200).message("OK")
            // Coil rejects cached HTTPS responses without handshake metadata.
            .handshake(Handshake.get(
                TlsVersion.TLS_1_3, CipherSuite.TLS_AES_128_GCM_SHA256,
                emptyList(), emptyList()
            ))
            .sentRequestAtMillis(System.currentTimeMillis())
            .receivedResponseAtMillis(System.currentTimeMillis())
            .header("Cache-Control", "public, max-age=86400")
            .body(bytes.toResponseBody("image/png".toMediaType())).build()
    }.build()

    private fun imageBytes(): ByteArray {
        val random = Random(1)
        val pixels = IntArray(64 * 64) { 0xff000000.toInt() or random.nextInt(0x1000000) }
        val bitmap = Bitmap.createBitmap(pixels, 64, 64, Bitmap.Config.ARGB_8888)
        return try {
            ByteArrayOutputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.toByteArray()
            }
        } finally {
            bitmap.recycle()
        }
    }
}
