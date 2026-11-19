package com.shihcheeng.sspaiyou.ui.screen.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.modula.NotificationEntryPoint
import com.shihcheeng.sspaiyou.ui.compoents.Pager
import com.shihcheeng.sspaiyou.ui.screen.main.home.HomeScreen
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixScreen
import com.shihcheeng.sspaiyou.ui.screen.main.personal.PersonalScreen
import com.shihcheeng.sspaiyou.utils.LocalComposeTokenManager
import com.shihcheeng.sspaiyou.utils.icons.BellRingingFilled
import com.shihcheeng.sspaiyou.utils.icons.BellRingingOutline
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack
import com.shihcheeng.sspaiyou.utils.icons.SearchFilled
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.flowOf

@Composable
fun MainScreen(
    onClick: (String) -> Unit,
    onFavourite: () -> Unit,
    onSetting: () -> Unit,
    onMineClick: (String) -> Unit,
    onBulletClick: (Long) -> Unit,
    onColumnClick: (Int) -> Unit,
    onTagClick: (String) -> Unit,
    onHistory: (String) -> Unit,
    onSearch: () -> Unit,
    onFollowed: (String) -> Unit,
    onTouchNotify: () -> Unit,
    onLoginClick: () -> Unit
) {

    val context = LocalContext.current.applicationContext
    val notifications = remember(context) {
        EntryPointAccessors.fromApplication(context, NotificationEntryPoint::class.java)
            .notificationRepository()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val navItems = MainScreenItem.listInCompose()
    val (select, onSelect) = rememberSaveable { mutableIntStateOf(0) }
    val tokenManager = LocalComposeTokenManager.current
    val scope = rememberCoroutineScope()
    val token by remember(tokenManager, scope) {
        tokenManager.tokenFlow(scope)
    }.collectAsStateWithLifecycle()
    val isLogin = !token.isNullOrBlank()
    val userSlug by remember(tokenManager, scope) {
        tokenManager.slugFlow(scope)
    }.collectAsStateWithLifecycle()
    val currentToken = token?.takeIf(String::isNotBlank)
    val unreadCount = key(currentToken) {
        val unreadFlow = remember(notifications, currentToken) {
            currentToken?.let(notifications::unreadCount) ?: flowOf(0)
        }
        val count by unreadFlow.collectAsStateWithLifecycle(initialValue = 0)
        count
    }
    val haveNotify = unreadCount > 0
    LaunchedEffect(currentToken, notifications, lifecycleOwner) {
        if (currentToken != null) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                notifications.sync(currentToken)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(navItems[select].name))
                },
                actions = {
                    IconButton(
                        onClick = onSearch
                    ) {
                        Icon(
                            imageVector = SSPaiPack.SearchFilled,
                            contentDescription = stringResource(R.string.search_articles)
                        )
                    }
                    if (isLogin) {
                        IconButton(
                            onClick = onTouchNotify
                        ) {
                            BadgedBox(badge = { if (haveNotify) Badge() }) {
                                Icon(
                                    imageVector = if (haveNotify) {
                                        SSPaiPack.BellRingingFilled
                                    } else {
                                        SSPaiPack.BellRingingOutline
                                    },
                                    contentDescription = stringResource(
                                        if (haveNotify) {
                                            R.string.have_notify
                                        } else {
                                            R.string.all_notify_clear
                                        }
                                    )
                                )
                            }
                        }
                    }
                    if (navItems[select].name == R.string.personal && !isLogin) {
                        IconButton(
                            onClick = { onHistory("anonymous") }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.coolicons__history_duocolor_sharp__1),
                                contentDescription = stringResource(R.string.history)
                            )
                        }
                    }
                    if (navItems[select].name != R.string.personal || !isLogin) {
                        IconButton(
                            onClick = onSetting
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_settings_24),
                                contentDescription = stringResource(R.string.setting)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth()
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == select,
                        onClick = {
                            onSelect(index)
                        },
                        label = {
                            Text(text = stringResource(item.name))
                        },
                        icon = {
                            Icon(
                                painter = if (index == select) {
                                    painterResource(item.iconAlt)
                                } else {
                                    painterResource(item.icon)
                                },
                                contentDescription = stringResource(item.name)
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        Pager(
            index = select,
            modifier = Modifier.padding(padding)
        ) { selectedIndex ->
            when (selectedIndex) {
                0 -> HomeScreen(
                    onClick = onClick,
                    onTagClick = onTagClick
                )

                1 -> MatrixScreen(
                    onClick = onClick,
                    onBulletClick = onBulletClick,
                    onColumnClick = onColumnClick
                )

                2 -> PersonalScreen(
                    onLoginClick = onLoginClick,
                    onFavourite = onFavourite,
                    onSetting = onSetting,
                    onMine = onMineClick,
                    onHistory = { userSlug?.let { x -> onHistory(x) } },
                    onFollowed = onFollowed
                )
            }
        }
    }
}

data class MainScreenItem(
    @StringRes
    val name: Int,
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val iconAlt: Int
) {

    companion object {
        @Composable
        fun listInCompose() = remember {
            listOf(
                MainScreenItem(
                    name = R.string.home,
                    icon = R.drawable.outline_home_24,
                    iconAlt = R.drawable.baseline_home_24
                ),
                MainScreenItem(
                    name = R.string.matrix,
                    icon = R.drawable.ph_matrix_logo,
                    iconAlt = R.drawable.ph_matrix_logo_fill
                ),
                MainScreenItem(
                    name = R.string.personal,
                    icon = R.drawable.baseline_person_outline_24,
                    iconAlt = R.drawable.baseline_person_24
                )
            )
        }
    }

}
