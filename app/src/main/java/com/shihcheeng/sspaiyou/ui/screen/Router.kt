package com.shihcheeng.sspaiyou.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.ui.screen.Router.Bullet.proceedToBullet
import com.shihcheeng.sspaiyou.ui.screen.Router.Column.proceedToColumn
import com.shihcheeng.sspaiyou.ui.screen.Router.Detail.proceedToDetail
import com.shihcheeng.sspaiyou.ui.screen.Router.HistoryScreen.proceedToHistory
import com.shihcheeng.sspaiyou.ui.screen.Router.Login.proceedTo
import com.shihcheeng.sspaiyou.ui.screen.Router.TagsScreen.proceedToTagsDetail
import com.shihcheeng.sspaiyou.ui.screen.Router.UrlWebBrowserScreen.proceedToInnerBrowser
import com.shihcheeng.sspaiyou.ui.screen.Router.UserInfo.proceedToUserInfo
import com.shihcheeng.sspaiyou.ui.screen.about.AboutScreen
import com.shihcheeng.sspaiyou.ui.screen.bullet.single.BulletSingleScreen
import com.shihcheeng.sspaiyou.ui.screen.column.ColumnScreen
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailScreen
import com.shihcheeng.sspaiyou.ui.screen.favourite.FavouritemScreen
import com.shihcheeng.sspaiyou.ui.screen.history.HistoryScreen
import com.shihcheeng.sspaiyou.ui.screen.innerbrowser.WebUrlBrowserScreen
import com.shihcheeng.sspaiyou.ui.screen.login.WebLoginScreen
import com.shihcheeng.sspaiyou.ui.screen.main.MainScreen
import com.shihcheeng.sspaiyou.ui.screen.setting.SSPaiSettingScreen
import com.shihcheeng.sspaiyou.ui.screen.tags.TagsScreen
import com.shihcheeng.sspaiyou.ui.screen.user.UserScreen
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut

@Composable
fun Router(
    navHostController: NavHostController = rememberNavController()
) {
    val slideDistance = with(LocalDensity.current) { 48.dp.roundToPx() }

    NavHost(
        navController = navHostController,
        startDestination = Router.Main.name,
        enterTransition = {
            materialSharedAxisXIn(forward = true, slideDistance = slideDistance)
        },
        popEnterTransition = {
            materialSharedAxisXIn(forward = false, slideDistance = slideDistance)
        },
        popExitTransition = {
            materialSharedAxisXOut(forward = false, slideDistance = slideDistance)
        },
        exitTransition = {
            materialSharedAxisXOut(forward = true, slideDistance = slideDistance)
        }
    ) {
        composable(Router.Main.name) {
            MainScreen(
                onClick = { x ->
                    navHostController.proceedToDetail(x)
                },
                onSetting = {
                    navHostController proceedTo Router.Setting
                },
                onFavourite = {
                    navHostController proceedTo Router.Favourite
                },
                onMineClick = {
                    navHostController.proceedToUserInfo(it)
                },
                onBulletClick = {
                    navHostController.proceedToBullet(it)
                },
                onColumnClick = {
                    navHostController.proceedToColumn(it)
                },
                onTagClick = {
                    navHostController.proceedToTagsDetail(it)
                },
                onHistory = {
                    navHostController.proceedToHistory(it)
                }
            ) {
                navHostController proceedTo Router.Login
            }
        }
        composable(Router.Login.name) {
            WebLoginScreen(onNavUp = navHostController::popBackStack)
        }
        composable(Router.Favourite.name) {
            FavouritemScreen(
                onNavUp = navHostController::popBackStack,
                onArticleClick = { navHostController.proceedToDetail(it) },
                onUserClick = { navHostController.proceedToUserInfo(it) }
            )
        }
        composable(Router.Setting.name) {
            SSPaiSettingScreen(
                onBack = navHostController::popBackStack,
                onSeeLibsClick = {
                    navHostController proceedTo Router.OpensourceLibs
                }
            )
        }
        composable(Router.UserInfo.name) {
            UserScreen(
                onNavUp = navHostController::popBackStack,
                onArticleClick = { navHostController.proceedToDetail(it) },
                onUserClick = { navHostController.proceedToUserInfo(it) }
            )
        }
        composable(Router.Detail.name) {
            DetailScreen(
                onAvatarClick = {
                    navHostController.proceedToUserInfo(it)
                },
                onLinkClick = {
                    navHostController.proceedToInnerBrowser(it)
                },
                onTagClick = { navHostController.proceedToTagsDetail(it) }
            ) {
                navHostController.popBackStack()
            }
        }
        composable(Router.Bullet.name) {
            BulletSingleScreen(
                onUserClick = {
                    navHostController.proceedToUserInfo(it)
                },
                onNavUp = navHostController::popBackStack
            )
        }
        composable(Router.Column.name) {
            ColumnScreen(
                onArticleClick = { navHostController.proceedToDetail(it) },
                onUserClick = { navHostController.proceedToUserInfo(it) },
                onNavUp = navHostController::popBackStack
            )
        }
        composable(Router.TagsScreen.name) { backStackEntry ->
            TagsScreen(
                tagName = backStackEntry.arguments?.getString("tag_name").orEmpty(),
                onArticleClick = { navHostController.proceedToDetail(it) },
                onNavUp = navHostController::popBackStack
            )
        }
        composable(Router.OpensourceLibs.name) {
            AboutScreen(onBack = navHostController::popBackStack)
        }
        composable(Router.UrlWebBrowserScreen.name) { backStackEntry ->
            // Navigation 已解码路由参数，保留网页 URL 自身原有的百分号编码。
            WebUrlBrowserScreen(
                url = requireNotNull(backStackEntry.arguments?.getString("url_browser")),
                onNavUp = { navHostController.popBackStack() }
            )
        }
        composable(Router.HistoryScreen.name) {
            HistoryScreen(
                onBacK = navHostController::popBackStack,
                gotoItem = { record ->
                    val detailId = when (record.detailRoute) {
                        com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel.DETAIL_ROUTE_MEMBER_SLUG -> record.slug
                        else -> record.postId.toString()
                    }
                    detailId?.let { navHostController.proceedToDetail(it) }
                }
            )
        }
    }
}


sealed interface Router {

    val name: String

    /**
     * 跳转到目的。
     *
     * @receiver [NavHostController]。
     * @param t 是Router
     */
    infix fun <T : Router> NavHostController.proceedTo(t: T) {
        navigate(t.name)
    }

    data object Main : Router {
        override val name: String
            get() = "MAIN"
    }

    data object Detail : Router {
        override val name: String
            get() = "DETAIL/{id}"

        fun NavHostController.proceedToDetail(id: String) {
            navigate(name.replace("{id}", Uri.encode(id)))
        }

    }

    data object Bullet : Router {
        override val name: String
            get() = "BULLET/{createdAt}"

        fun NavHostController.proceedToBullet(createdAt: Long) {
            navigate(name.replace("{createdAt}", createdAt.toString()))
        }
    }

    data object Column : Router {
        override val name: String
            get() = "COLUMN/{columnId}"

        fun NavHostController.proceedToColumn(columnID: Int) {
            navigate(name.replace("{columnId}", columnID.toString()))
        }

    }

    data object Login : Router {
        override val name: String
            get() = "LOGIN"
    }

    data object Setting : Router {
        override val name: String
            get() = "SETTING"
    }

    data object Favourite : Router {
        override val name: String
            get() = "FAVOURITE"
    }

    data object UserInfo : Router {

        override val name: String
            get() = "USER_INFO/{slug}"

        fun NavHostController.proceedToUserInfo(slug: String) {
            navigate(name.replace("{slug}", slug))
        }

    }

    data object OpensourceLibs : Router {
        override val name: String
            get() = "OPENSOURCE_LIBS"
    }

    data object TagsScreen : Router {
        override val name: String
            get() = "TAGS/{tag_name}"

        fun NavHostController.proceedToTagsDetail(tagName: String) {
            navigate(name.replace("{tag_name}", Uri.encode(tagName)))
        }
    }

    data object UrlWebBrowserScreen : Router {
        override val name: String
            get() = "WEB_BROWSER/{url_browser}"

        fun NavHostController.proceedToInnerBrowser(url: String) {
            navigate(name.replace("{url_browser}", Uri.encode(url)))
        }

    }

    data object HistoryScreen : Router {
        override val name: String
            get() = "HISTORY/{owner_key}"

        fun NavHostController.proceedToHistory(ownerKey: String) {
            navigate(name.replace("{owner_key}", Uri.encode(ownerKey)))
        }
    }

}
