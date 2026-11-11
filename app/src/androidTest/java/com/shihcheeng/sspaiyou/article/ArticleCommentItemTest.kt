package com.shihcheeng.sspaiyou.article

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentFile
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentReply
import com.shihcheeng.sspaiyou.ui.screen.detail.ArticleCommentItem
import com.shihcheeng.sspaiyou.ui.screen.detail.ArticleCommentsSheet
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArticleCommentItemTest {
    @get:Rule
    val compose = createComposeRule()

    private val openedUrls = mutableListOf<String>()
    private val openedUsers = mutableListOf<String>()
    private val openedImages = mutableListOf<String>()
    private val uriHandler = object : UriHandler {
        override fun openUri(uri: String) { openedUrls += uri }
    }

    @Test
    fun htmlLinksRenderWithoutTagsAndOpenTheirDestination() {
        showItem(sample().copy(
            comment = "扫雷攻略：<a href=\"https://minesweeper.cn/\">网页版扫雷</a><p style=\"margin: 0;height: 20px;\"></p>第二段内容",
            commentFiles = listOf(ArticleCommentFile(6699, "https://example.com/comment.png")),
            reply = emptyList()
        ))
        awaitText("网页版扫雷")
        compose.onNodeWithText("第二段内容", substring = true).assertIsDisplayed()
        compose.onNodeWithText("<a ", substring = true).assertDoesNotExist()
        clickLink("网页版扫雷", "https://minesweeper.cn/")
        assertEquals(listOf("https://minesweeper.cn/"), openedUrls)
        compose.onNodeWithContentDescription("评论图片 1").performClick()
        assertEquals(listOf("https://example.com/comment.png"), openedImages)
    }

    @Test
    fun replyUsesUserShowsAuthorBadgeAndPreservesNewlines() {
        showItem(sample())
        awaitText("Dark mode")
        compose.onNodeWithText("hAPPydOg").assertIsDisplayed()
        compose.onNodeWithText("bakamio").assertIsDisplayed()
        compose.onNodeWithText("作者").assertIsDisplayed()
        compose.onNodeWithText("1 条回复").assertIsDisplayed()
        val layout = layoutFor("Dark mode")
        assertTrue(layout.layoutInput.text.text.contains("\n国区的话"))
        assertFalse(layout.hasVisualOverflow)
        compose.onNodeWithContentDescription("查看 bakamio 的主页").performClick()
        assertEquals(listOf("vst7qe4v"), openedUsers)
    }

    @Test
    fun repliesAlsoSupportMarkdownLinksAndMetadata() {
        val parent = sample()
        showItem(parent.copy(reply = listOf(parent.reply.single().copy(
            comment = "参考 **指南**：[打开教程](https://example.com/tutorial)",
            likesCount = 3,
            liked = true
        ))))
        awaitText("打开教程")
        compose.onNodeWithText("3 赞").assertIsDisplayed()
        clickLink("打开教程", "https://example.com/tutorial")
        assertEquals(listOf("https://example.com/tutorial"), openedUrls)
    }

    @Test
    fun replyVisibilityAndHiddenFlagsApplyToThreadContent() {
        val parent = sample()
        val item = mutableStateOf(parent.copy(replyShow = false))
        compose.setContent {
            MaterialTheme { ArticleCommentItem(item.value, onAvatarClick = {}) }
        }
        compose.onNodeWithText("bakamio").assertDoesNotExist()
        compose.onNodeWithText("1 条回复").assertDoesNotExist()
        val hiddenVariants = listOf<(ArticleCommentReply) -> ArticleCommentReply>(
            { it.copy(blocked = true) },
            { it.copy(hide = true) },
            { it.copy(authorBlocked = true) }
        )
        hiddenVariants.forEach { hide ->
            compose.runOnIdle { item.value = parent.copy(reply = listOf(hide(parent.reply.single()))) }
            compose.onNodeWithText("这条评论暂不可见").assertIsDisplayed()
            compose.onNodeWithText("Dark mode", substring = true).assertDoesNotExist()
            compose.onNodeWithContentDescription("评论图片 1").assertDoesNotExist()
        }
    }

    @Test
    fun commentsSheetIncludesRepliesFromPagedParentAndNavigatesToReplyUser() {
        val pages = flowOf(PagingData.from(listOf(sample())))
        compose.setContent {
            MaterialTheme {
                ArticleCommentsSheet(
                    comments = pages.collectAsLazyPagingItems(),
                    commentCount = 1,
                    onDismiss = {},
                    onAvatarClick = { openedUsers += it },
                    onImageClick = { openedImages += it }
                )
            }
        }
        awaitText("Dark mode")
        compose.onNodeWithText("评论 · 1").assertIsDisplayed()
        compose.onNodeWithText("bakamio").assertIsDisplayed()
        compose.onNodeWithContentDescription("查看 bakamio 的主页").performClick()
        assertEquals(listOf("vst7qe4v"), openedUsers)
        compose.onNodeWithContentDescription("评论图片 1").performClick()
        assertEquals(listOf("https://example.com/reply.png"), openedImages)
    }

    private fun showItem(comment: ArticleComment) {
        compose.setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                MaterialTheme {
                    ArticleCommentItem(
                        comment = comment,
                        onAvatarClick = { openedUsers += it },
                        modifier = Modifier.width(360.dp),
                        onImageClick = { openedImages += it }
                    )
                }
            }
        }
    }

    private fun awaitText(text: String) {
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun layoutFor(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text, substring = true).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(layouts)
        }
        return layouts.single()
    }

    private fun clickLink(text: String, url: String) {
        val layout = layoutFor(text)
        val annotated = layout.layoutInput.text
        val link = annotated.getLinkAnnotations(0, annotated.length).single()
        assertEquals(url, (link.item as LinkAnnotation.Url).url)
        val position = layout.getBoundingBox(link.start).center
        compose.onNodeWithText(text, substring = true).performTouchInput { click(position) }
    }

    // Relevant fields from comment_has_reply.json; the JVM tests decode the complete fixture.
    private fun sample(): ArticleComment = Json.decodeFromString("""
        {
          "id": 433819,
          "comment": "UI绘制的还挺不错，每套主题有对应的夜间模式吗？可惜国区还无法下载",
          "created_at": 1794203669,
          "user": {"slug": "ub29du0l", "nickname": "hAPPydOg"},
          "reply_show": true,
          "reply": [{
            "id": 396692,
            "comment": "Dark mode我没调好，所以把它去掉了。之后我调满意了再给它加回去😆\n国区的话，因为游戏要有版号，应该是这样的，所以我觉得暂时应该没办法上架…",
            "created_at": 1794292474,
            "comment_files": [{"id": 6700, "file_path": "https://example.com/reply.png"}],
            "author": {"slug": "", "nickname": ""},
            "user": {"slug": "vst7qe4v", "nickname": "bakamio"},
            "is_author": true
          }]
        }
    """.trimIndent())
}
