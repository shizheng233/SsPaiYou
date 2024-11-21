package com.shihcheeng.sspaiyou.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.ui.screen.Router.Detail.proceedToDetail
import com.shihcheeng.sspaiyou.ui.screen.Router.Login.proceedTo
import com.shihcheeng.sspaiyou.ui.screen.Router.UserInfo.proceedToUserInfo
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailScreen
import com.shihcheeng.sspaiyou.ui.screen.login.WebLoginScreen
import com.shihcheeng.sspaiyou.ui.screen.main.MainScreen
import com.shihcheeng.sspaiyou.ui.screen.user.UserScreen
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun Router(
    navHostController: NavHostController = rememberNavController(),
    slider: Int = rememberSlideDistance()
) {

    NavHost(
        navController = navHostController,
        startDestination = Router.Main.name,
        enterTransition = {
            materialSharedAxisXIn(forward = true, slideDistance = slider)
        },
        popEnterTransition = {
            materialSharedAxisXIn(forward = false, slideDistance = slider)
        },
        popExitTransition = {
            materialSharedAxisXOut(forward = false, slideDistance = slider)
        },
        exitTransition = {
            materialSharedAxisXOut(forward = true, slideDistance = slider)
        }
    ) {
        composable(Router.Main.name) {
            MainScreen(
                onClick = {
                    navHostController.proceedToDetail(it)
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
        composable(Router.Detail.name) {
            DetailScreen(
                onNavBack = navHostController::popBackStack,
                onAvatarClick = {
                    navHostController.proceedToUserInfo(it)
                }
            )
        }
        composable(Router.Login.name) {
            WebLoginScreen(onNavUp = navHostController::popBackStack)
        }
        composable(Router.Favourite.name) {

        }
        composable(Router.Setting.name) {

        }
        composable(Router.UserInfo.name) {
            UserScreen(onNavUp = navHostController::popBackStack)
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