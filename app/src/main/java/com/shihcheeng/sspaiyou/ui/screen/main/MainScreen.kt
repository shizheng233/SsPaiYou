package com.shihcheeng.sspaiyou.ui.screen.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.Pager
import com.shihcheeng.sspaiyou.ui.screen.main.home.HomeScreen
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixScreen
import com.shihcheeng.sspaiyou.ui.screen.main.personal.PersonalScreen

@Composable
fun MainScreen(
    onClick: (String) -> Unit,
    onFavourite: () -> Unit,
    onSetting: () -> Unit,
    onMineClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {

    val navItems = MainScreenItem.listInCompose()
    val (select, onSelect) = rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(navItems[select].name))
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
                0 -> HomeScreen(onClick = onClick)
                1 -> MatrixScreen(onClick = onClick)
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