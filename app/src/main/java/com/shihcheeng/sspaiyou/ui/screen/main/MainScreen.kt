package com.shihcheeng.sspaiyou.ui.screen.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.Pager
import com.shihcheeng.sspaiyou.ui.screen.main.home.HomeScreen
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixScreen
import com.shihcheeng.sspaiyou.ui.screen.main.personal.PersonalScreen
import com.shihcheeng.sspaiyou.utils.LocalComposeTokenManager

@Composable
fun MainScreen(
    onClick: (String) -> Unit,
    onFavourite: () -> Unit,
    onSetting: () -> Unit,
    onMineClick: (String) -> Unit,
    onBulletClick: (Long) -> Unit,
    onColumnClick: (Int) -> Unit,
    onTagClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {

    val navItems = MainScreenItem.listInCompose()
    val (select, onSelect) = rememberSaveable { mutableIntStateOf(0) }
    val tokenManager = LocalComposeTokenManager.current
    val scope = rememberCoroutineScope()
    val token by remember(tokenManager, scope) {
        tokenManager.tokenFlow(scope)
    }.collectAsStateWithLifecycle()
    val isLogin = token != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(navItems[select].name))
                },
                actions = {
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
                    onMine = onMineClick
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
