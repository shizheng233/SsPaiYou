package com.shihcheeng.sspaiyou.bullet

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.bullet.BulletUser
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletCommentFile
import com.shihcheeng.sspaiyou.ui.screen.bullet.comment.BulletCommentItem
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BulletCommentItemTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun commentPreservesPlainTextAndNavigatesToAuthor() {
        var selectedSlug: String? = null
        val comment = BulletComment(
            idHash = "comment-1",
            author = BulletUser(slug = "test-author", nickname = "Test Author"),
            body = "**Plain text**\nSecond line",
            createdAt = 1793789698L
        )
        compose.setContent {
            MaterialTheme {
                Surface {
                    BulletCommentItem(comment = comment, onUserClick = { selectedSlug = it })
                }
            }
        }
        compose.onNodeWithText(comment.body).assertIsDisplayed()
        compose.onNodeWithText("Test Author").performClick()
        compose.runOnIdle { assertEquals("test-author", selectedSlug) }
    }

    @Test
    fun blockedCommentDoesNotComposeBodyOrAttachments() {
        val comment = BulletComment(
            idHash = "comment-2",
            author = BulletUser(nickname = "Blocked Author"),
            body = "Hidden content",
            blocked = true,
            files = listOf(BulletCommentFile(id = 1, filePath = "https://example.com/hidden.png"))
        )
        compose.setContent {
            MaterialTheme {
                Surface {
                    BulletCommentItem(comment = comment, onUserClick = {})
                }
            }
        }
        compose.onNodeWithText("Blocked Author").assertIsDisplayed()
        compose.onNodeWithText(comment.body).assertDoesNotExist()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.onNodeWithText(context.getString(R.string.bullet_comment_hidden)).assertIsDisplayed()
        compose.onNodeWithContentDescription(context.getString(R.string.bullet_comment_attachment, 1))
            .assertDoesNotExist()
    }
}
