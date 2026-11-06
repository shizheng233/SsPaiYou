package com.shihcheeng.sspaiyou.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.shihcheeng.sspaiyou.ui.screen.Router.Bullet.proceedToBullet
import com.shihcheeng.sspaiyou.ui.screen.Router.Column.proceedToColumn
import com.shihcheeng.sspaiyou.ui.screen.Router.Detail.proceedToDetail
import com.shihcheeng.sspaiyou.ui.screen.Router.Login.proceedTo
import com.shihcheeng.sspaiyou.ui.screen.Router.UserInfo.proceedToUserInfo
import com.shihcheeng.sspaiyou.ui.screen.bullet.single.BulletSingleScreen
import com.shihcheeng.sspaiyou.ui.screen.column.ColumnScreen
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailScreen
import com.shihcheeng.sspaiyou.ui.screen.favourite.FavouritemScreen
import com.shihcheeng.sspaiyou.ui.screen.login.WebLoginScreen
import com.shihcheeng.sspaiyou.ui.screen.main.MainScreen
import com.shihcheeng.sspaiyou.ui.screen.setting.SettingScreen
import com.shihcheeng.sspaiyou.ui.screen.user.UserScreen
import com.shihcheeng.sspaiyou.ui.theme.MaterialMotions
import com.shihcheeng.sspaiyou.ui.theme.motionComposable

@Composable
fun Router(
    navHostController: NavHostController = rememberNavController(),
    toDetail: (String) -> Unit
) {
    val backGestureDistance = with(LocalDensity.current) { 48.dp.roundToPx() }

    NavHost(
        navController = navHostController,
        startDestination = Router.Main.name,
        enterTransition = {
            MaterialMotions.navigationEnter(forward = true)
        },
        popEnterTransition = {
            MaterialMotions.navigationEnter(forward = false)
        },
        popExitTransition = {
            MaterialMotions.navigationExit(forward = false)
        },
        exitTransition = {
            MaterialMotions.navigationExit(forward = true)
        },
        predictivePopEnterTransition = {
            MaterialMotions.predictivePopEnter()
        },
        predictivePopExitTransition = { swipeEdge ->
            MaterialMotions.predictivePopExit(
                swipeEdge = swipeEdge,
                slideDistance = backGestureDistance
            )
        }
    ) {
        motionComposable(Router.Main.name) {
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
                }
            ) {
                navHostController proceedTo Router.Login
            }
        }
        motionComposable(Router.Login.name) {
            WebLoginScreen(onNavUp = navHostController::popBackStack)
        }
        motionComposable(Router.Favourite.name) {
            FavouritemScreen()
        }
        motionComposable(Router.Setting.name) {
            SettingScreen()
        }
        motionComposable(Router.UserInfo.name) {
            UserScreen(
                onNavUp = navHostController::popBackStack,
                onArticleClick = {
                    navHostController.proceedToDetail(it.toString())
                }
            )
        }
        motionComposable(Router.Detail.name) {
            DetailScreen(
                onAvatarClick = {
                    navHostController.proceedToUserInfo(it)
                }
            ) {
                navHostController.popBackStack()
            }
        }
        motionComposable(Router.Bullet.name) {
            BulletSingleScreen(
                onUserClick = {
                    navHostController.proceedToUserInfo(it)
                },
                onNavUp = navHostController::popBackStack
            )
        }
        motionComposable(Router.Column.name) {
            ColumnScreen(
                onArticleClick = { navHostController.proceedToDetail(it) },
                onUserClick = { navHostController.proceedToUserInfo(it) },
                onNavUp = navHostController::popBackStack
            )
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

}
