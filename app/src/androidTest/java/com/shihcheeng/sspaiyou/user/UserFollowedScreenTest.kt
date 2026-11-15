package com.shihcheeng.sspaiyou.user

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.screen.Router
import com.shihcheeng.sspaiyou.ui.screen.Router.UserInfo.proceedToUserInfo
import com.shihcheeng.sspaiyou.ui.screen.userfollowed.UserFollowedList
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class UserFollowedScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun listShowsNamesAndBiosInBothThemesAndNavigatesWithTheExactSlug() {
        val specialSlug = "作者/with space?x=1"
        val users = flowOf(PagingData.from(
            data = listOf(
                ColumnUser(slug = "first", nickname = "利兹与青鸟"),
                ColumnUser(slug = specialSlug, nickname = "车不能快", bio = "分享键盘与生活中的小乐趣"),
                ColumnUser(slug = "last", nickname = "SamWanng", bio = "集合啦！")
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(true),
                append = LoadState.NotLoading(true)
            )
        ))
        val dark = mutableStateOf(false)
        lateinit var navController: NavHostController
        compose.setContent {
            MaterialTheme(colorScheme = if (dark.value) darkColorScheme() else lightColorScheme()) {
                navController = rememberNavController()
                NavHost(navController, startDestination = "followed") {
                    composable("followed") {
                        Surface(Modifier.fillMaxSize()) {
                            UserFollowedList(
                                users = users.collectAsLazyPagingItems(),
                                onUserClick = { navController.proceedToUserInfo(it) },
                                modifier = Modifier.fillMaxSize().testTag("followed_list")
                            )
                        }
                    }
                    composable(Router.UserInfo.name) { entry ->
                        Text("用户详情：${entry.arguments?.getString("slug")}")
                    }
                }
            }
        }
        compose.onNodeWithText("利兹与青鸟").assertIsDisplayed()
        compose.onNodeWithText("无签名").assertIsDisplayed()
        compose.onNodeWithText("分享键盘与生活中的小乐趣").assertIsDisplayed()
        capture("user-followed-light.png")
        compose.runOnIdle { dark.value = true }
        compose.onNodeWithText("SamWanng").assertIsDisplayed()
        capture("user-followed-dark.png")
        compose.onNodeWithText("车不能快").performClick()
        compose.onNodeWithText("用户详情：$specialSlug").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(specialSlug, navController.currentBackStackEntry?.arguments?.getString("slug"))
        }
    }

    private fun capture(name: String) {
        val bitmap = compose.onNodeWithTag("followed_list").captureToImage().asAndroidBitmap()
        val directory = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)
        File(directory, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
