package com.shihcheeng.sspaiyou.tags

import android.net.Uri
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.api.service.TagsService
import com.shihcheeng.sspaiyou.ui.screen.Router
import com.shihcheeng.sspaiyou.ui.screen.Router.TagsScreen.proceedToTagsDetail
import com.shihcheeng.sspaiyou.ui.screen.main.home.BannerType
import com.shihcheeng.sspaiyou.ui.screen.main.home.detectiveUrlTypeOp
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
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TagNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun encodedBannerTagReachesNavigationAndApiAsOriginalText() = runBlocking {
        lateinit var navController: NavHostController
        compose.setContent {
            navController = rememberNavController()
            NavHost(navController, startDestination = "start") {
                composable("start") {}
                composable(Router.TagsScreen.name) {}
            }
        }
        val requests = mutableListOf<Request>()
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests += chain.request()
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("""{"error":0,"data":[]}""".toResponseBody(mediaType))
                .build()
        }.build()
        try {
            val service = Retrofit.Builder()
                .baseUrl("https://example.com/")
                .client(client)
                .addConverterFactory(Json.asConverterFactory(mediaType))
                .build().create(TagsService::class.java)
            val names = listOf("Sonos 众测", "C++ 技巧", "100% 体验", "路径/标签", "字面%20标签")
            names.forEach { name ->
                var argument = ""
                compose.runOnIdle {
                    "https://sspai.com/tag/${Uri.encode(name)}?from=home#articles"
                        .detectiveUrlTypeOp { type, value ->
                            assertEquals(BannerType.TAG, type)
                            navController.proceedToTagsDetail(value)
                        }
                    val entry = requireNotNull(navController.currentBackStackEntry)
                    argument = entry.savedStateHandle.get<String>("tag_name").orEmpty()
                    assertEquals(name, entry.arguments?.getString("tag_name"))
                }
                assertEquals(name, argument)
                service.getArticleTagsList(limit = 10, offset = 0, tagName = argument)
                assertEquals(name, requests.last().url.queryParameter("tag"))
                if (name == "Sonos 众测") {
                    assertFalse(requests.last().url.encodedQuery.orEmpty().contains("%25"))
                }
            }
            // Detail-page tags already are plain text, including literal percent sequences.
            compose.runOnIdle {
                names.forEach { name ->
                    navController.proceedToTagsDetail(name)
                    assertEquals(name, navController.currentBackStackEntry?.arguments?.getString("tag_name"))
                }
            }
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }
}
