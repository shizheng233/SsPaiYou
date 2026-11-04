package com.shihcheeng.sspaiyou.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.ui.screen.Router.Detail.proceedToDetail
import com.shihcheeng.sspaiyou.ui.screen.Router.Login.proceedTo
import com.shihcheeng.sspaiyou.ui.screen.Router.UserInfo.proceedToUserInfo
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailScreen
import com.shihcheeng.sspaiyou.ui.screen.favourite.FavouritemScreen
import com.shihcheeng.sspaiyou.ui.screen.login.WebLoginScreen
import com.shihcheeng.sspaiyou.ui.screen.main.MainScreen
import com.shihcheeng.sspaiyou.ui.screen.setting.SettingScreen
import com.shihcheeng.sspaiyou.ui.screen.user.UserScreen
import com.shihcheeng.sspaiyou.ui.theme.MaterialMotions
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun Router(
    navHostController: NavHostController = rememberNavController(),
    slider: Int = rememberSlideDistance(),
    toDetail: (String) -> Unit
) {
    val slideDistance = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -slider else slider

    NavHost(
        navController = navHostController,
        startDestination = Router.Main.name,
        enterTransition = {
            MaterialMotions.screenEnter(forward = true, slideDistance = slideDistance)
        },
        popEnterTransition = {
            MaterialMotions.screenEnter(forward = false, slideDistance = slideDistance)
        },
        popExitTransition = {
            MaterialMotions.screenExit(forward = false, slideDistance = slideDistance)
        },
        exitTransition = {
            MaterialMotions.screenExit(forward = true, slideDistance = slideDistance)
        },
        predictivePopEnterTransition = {
            MaterialMotions.predictivePopEnter()
        },
        predictivePopExitTransition = { swipeEdge ->
            MaterialMotions.predictivePopExit(swipeEdge = swipeEdge, slideDistance = slider)
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
                }
            ) {
                navHostController proceedTo Router.Login
            }
        }
        composable(Router.Login.name) {
            WebLoginScreen(onNavUp = navHostController::popBackStack)
        }
        composable(Router.Favourite.name) {
            FavouritemScreen()
        }
        composable(Router.Setting.name) {
            SettingScreen()
        }
        composable(Router.UserInfo.name) {
            UserScreen(
                onNavUp = navHostController::popBackStack,
                onArticleClick = {
                    navHostController.proceedToDetail(it.toString())
                }
            )
        }
        composable(Router.Detail.name) {
            DetailScreen(
                onAvatarClick = {
                    navHostController.proceedToUserInfo(it)
                }
            ) {
                navHostController.popBackStack()
            }
        }
    }

}


sealed interface Router {

    val name: String

    /**
     * 跳转到目的。
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
            navigate(name.replace("{id}", id))
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

}
