package com.shihcheeng.sspaiyou.article

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailArticleTable
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailContent
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypo
import com.shihcheeng.sspaiyou.ui.theme.toMarkDownColors
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailArticleTableTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun htmlTableWrapsLongCellsAndScrollsHorizontallyInsideArticleList() {
        val longText = "这是一段很长的单元格说明，需要完整换行显示，不能只显示第一行或者省略后面的文字。"
        val blocks = DetailMarkDownParser().parse(
            """<figure class="table"><table><thead><tr><th>按键</th><th>快捷键</th><th>动作</th><th>实际用途</th></tr></thead>
                <tbody><tr><td>$longText</td><td><code>Enter</code></td><td>确认</td><td>执行当前任务</td></tr></tbody>
            </table></figure>"""
        ).filterIsInstance<DetailContent.Markdown>()
        compose.setContent {
            MaterialTheme {
                val components = markdownComponents(table = {
                    DetailArticleTable(content = it.content, node = it.node, style = it.typography.table)
                })
                LazyColumn(Modifier.width(320.dp)) {
                    items(blocks) {
                        Markdown(
                            content = it.text,
                            colors = MaterialTheme.colorScheme.toMarkDownColors(),
                            typography = sspaiReaderTypo(fontScale = 1.3f),
                            components = components
                        )
                    }
                }
            }
        }
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("按键", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("按键", substring = true).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(longText, substring = true).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(layouts)
        }
        assertTrue(layouts.single().lineCount > 1)
        assertFalse(layouts.single().hasVisualOverflow)

        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange))
            .performSemanticsAction(SemanticsActions.ScrollBy) { it(10_000f, 0f) }
        compose.onNodeWithText("实际用途", substring = true).assertIsDisplayed()
        compose.onNodeWithText("执行当前任务", substring = true).assertIsDisplayed()
    }
}
