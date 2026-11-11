package com.shihcheeng.sspaiyou.ui.screen.innerbrowser

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.ui.screen.Router
import com.shihcheeng.sspaiyou.ui.screen.Router.UrlWebBrowserScreen.proceedToInnerBrowser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.Closeable
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WebUrlBrowserScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun browserRoutePreservesTheEntireUrlWithoutDoubleDecoding() {
        lateinit var navController: NavHostController
        compose.setContent {
            navController = rememberNavController()
            NavHost(navController, startDestination = "start") {
                composable("start") { Text("start") }
                composable(Router.UrlWebBrowserScreen.name) { Text("browser") }
            }
        }
        val urls = listOf(
            "https://sspai.com/post/123?source=reader&name=中文 空格#评论",
            "https://example.com/a%2Fb?q=a+b%20c&next=https%3A%2F%2Fsspai.com%2F#part%2F1",
            "https://example.com/?literal=%252F&symbols=%25%23%3F%26%3D",
            "http://example.com:8080/path/with/trailing/slash/"
        )
        for (url in urls) {
            compose.runOnIdle { navController.proceedToInnerBrowser(url) }
            compose.runOnIdle {
                assertEquals(url, navController.currentBackStackEntry?.arguments?.getString("url_browser"))
                assertTrue(navController.popBackStack())
            }
        }
    }

    @Test
    fun titleWaitsForLoadThenTracksJavascriptWithoutRecreatingWebViewAndBackUsesHistory() {
        PageServer().use { server ->
            lateinit var activity: Activity
            val visible = mutableStateOf(true)
            var exits = 0
            compose.setContent {
                activity = checkNotNull(LocalActivity.current)
                MaterialTheme {
                    if (visible.value) {
                        WebUrlBrowserScreen(server.url("/first")) {
                            exits++
                            visible.value = false
                        }
                    }
                }
            }
            assertTrue("The page must start loading its subresource", server.imageRequested.await(10, TimeUnit.SECONDS))
            compose.onNodeWithText("正在加载网页…").assertIsDisplayed()
            compose.onNodeWithText("第一页").assertDoesNotExist()
            val originalWebView = compose.runOnIdle { webViews(activity.window.decorView).single() }

            server.finishImage.countDown()
            waitForTitle("第一页")
            compose.runOnIdle {
                assertSame(originalWebView, webViews(activity.window.decorView).single())
                originalWebView.evaluateJavascript("document.title = '动态标题'", null)
            }
            waitForTitle("动态标题")
            compose.runOnIdle {
                assertSame(originalWebView, webViews(activity.window.decorView).single())
                originalWebView.loadUrl(server.url("/second"))
            }
            waitForTitle("第二页")
            compose.runOnIdle { assertTrue("The second page must have back history", originalWebView.canGoBack()) }
            compose.onNodeWithContentDescription("返回上一层级").performClick()
            waitForTitle("第一页")
            compose.runOnIdle {
                assertEquals(0, exits)
                assertSame(originalWebView, webViews(activity.window.decorView).single())
                (activity as ComponentActivity).onBackPressedDispatcher.onBackPressed()
            }
            compose.runOnIdle {
                assertEquals(1, exits)
                assertNull(originalWebView.parent)
                assertTrue(webViews(activity.window.decorView).isEmpty())
            }
        }
    }

    @Test
    fun newUrlReleasesOldWebViewAndUntitledPageDisplaysAddress() {
        PageServer().use { server ->
            server.finishImage.countDown()
            lateinit var activity: Activity
            val url = mutableStateOf(server.url("/second"))
            val visible = mutableStateOf(true)
            compose.setContent {
                activity = checkNotNull(LocalActivity.current)
                MaterialTheme {
                    if (visible.value) WebUrlBrowserScreen(url.value) { visible.value = false }
                }
            }
            waitForTitle("第二页")
            val originalWebView = compose.runOnIdle { webViews(activity.window.decorView).single() }
            compose.runOnIdle { url.value = server.url("/untitled") }
            // Android WebView supplies an address without the scheme when <title> is absent.
            waitForTitle(server.url("/untitled").removePrefix("http://"))
            compose.runOnIdle {
                assertNotSame(originalWebView, webViews(activity.window.decorView).single())
                assertNull(originalWebView.parent)
                visible.value = false
            }
            compose.runOnIdle { assertTrue(webViews(activity.window.decorView).isEmpty()) }
        }
    }

    private fun waitForTitle(title: String) {
        try {
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
            }
        } catch (error: ComposeTimeoutException) {
            throw AssertionError("Expected title: $title\n${compose.onRoot().printToString()}", error)
        }
        compose.onNodeWithText(title).assertIsDisplayed()
    }

    private fun webViews(view: View): List<WebView> = when (view) {
        is WebView -> listOf(view)
        is ViewGroup -> (0 until view.childCount).flatMap { webViews(view.getChildAt(it)) }
        else -> emptyList()
    }

    /** Loopback HTTP fixture: a blocked image makes onReceivedTitle precede onPageFinished. */
    private class PageServer : Closeable {
        val imageRequested = CountDownLatch(1)
        val finishImage = CountDownLatch(1)
        private val socket = ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))
        private val executor = Executors.newCachedThreadPool()

        init {
            executor.execute {
                while (!socket.isClosed) {
                    val client = try {
                        socket.accept()
                    } catch (_: IOException) {
                        break
                    }
                    executor.execute {
                        client.use {
                            val reader = it.getInputStream().bufferedReader()
                            val path = reader.readLine()?.split(' ')?.getOrNull(1)
                            while (!reader.readLine().isNullOrEmpty()) { /* Skip headers. */ }
                            val body = when (path) {
                                "/first" -> "<html><head><title>第一页</title></head><body><img src='/pending.svg'></body></html>"
                                "/second" -> "<html><head><title>第二页</title></head><body>second</body></html>"
                                "/pending.svg" -> {
                                    imageRequested.countDown()
                                    finishImage.await(15, TimeUnit.SECONDS)
                                    "<svg xmlns='http://www.w3.org/2000/svg' width='1' height='1'></svg>"
                                }
                                else -> "<html><body>untitled</body></html>"
                            }.toByteArray(Charsets.UTF_8)
                            val type = if (path == "/pending.svg") "image/svg+xml" else "text/html; charset=utf-8"
                            val headers = "HTTP/1.1 200 OK\r\nContent-Type: $type\r\nContent-Length: ${body.size}\r\nCache-Control: no-store\r\nConnection: close\r\n\r\n"
                            it.getOutputStream().apply {
                                write(headers.toByteArray(Charsets.UTF_8))
                                write(body)
                                flush()
                            }
                        }
                    }
                }
            }
        }

        fun url(path: String) = "http://127.0.0.1:${socket.localPort}$path"

        override fun close() {
            finishImage.countDown()
            socket.close()
            executor.shutdownNow()
        }
    }
}
