package com.shihcheeng.sspaiyou.ui.screen.main.matrix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.bullet.BulletInnerScreen
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.column.ColumnScreen
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.hot.HotScreen
import com.shihcheeng.sspaiyou.ui.theme.materialPagerMotion
import kotlinx.coroutines.launch

@Composable
fun MatrixScreen(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onColumnClick: (Int) -> Unit,
    onBulletClick: (Long) -> Unit
) {
    val tabStrings = MatrixScreenItem.tabStringInCompose()
    val state = rememberPagerState(initialPage = 0, pageCount = tabStrings::size)
    val scoped = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        PrimaryTabRow(
            selectedTabIndex = state.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabStrings.forEachIndexed { i, t ->
                Tab(
                    selected = i == state.currentPage,
                    text = {
                        Text(text = stringResource(t))
                    },
                    onClick = {
                        scoped.launch {
                            state.animateScrollToPage(i)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = state
        ) { indexx ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .materialPagerMotion(state, indexx)
            ) {
                when (indexx) {
                    0 -> HotScreen(onClick = onClick)
                    1 -> ColumnScreen(onColumnClick = onColumnClick)
                    2 -> BulletInnerScreen(onBulletClick = onBulletClick)
                }
            }
        }
    }
}

object MatrixScreenItem {

    @Composable
    fun tabStringInCompose() = remember {
        listOf(
            R.string.community,
            R.string.column,
            R.string.onep
        )
    }

}
