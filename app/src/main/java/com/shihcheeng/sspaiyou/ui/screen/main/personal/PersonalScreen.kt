package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.utils.extra.untilToDay

@Composable
fun PersonalScreen(
    viewModel: PersonalViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onFavourite: () -> Unit,
    onSetting: () -> Unit,
    onMine: (String) -> Unit
) {
    val token by viewModel.token.collectAsStateWithLifecycle()

    if (token == null) {
        NoPersonalScreen(onLoginClick)
    } else {
        val info by viewModel.info.collectAsStateWithLifecycle()
        TriStateScreen(
            triState = info,
            onError = {
                Error(error = it.localizedMessage) {
                    viewModel.load()
                }
            }
        ) { model ->
            PersonalScreenContent(
                username = model.data.nickname,
                followingCount = model.data.followingCount,
                beingDays = model.data.createdAt.toLong().untilToDay(),
                imageUrl = model.data.avatar,
                onSetting = onSetting,
                onFavourite = onFavourite,
                onMine = { onMine(model.data.slug) }
            )
        }
    }
}