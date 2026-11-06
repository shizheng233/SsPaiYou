package com.shihcheeng.sspaiyou.bullet

import com.shihcheeng.sspaiyou.api.data.bullet.BulletDataModel
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletCommentDataModel
import com.shihcheeng.sspaiyou.api.data.bullet.single.BulletSingleDataModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BulletModelTest {
    @Test
    fun discussionAndVoteUseTheSameDetailAndListModel() {
        val discussion = """{"title":"Discussion","type":1,"created_at":1792994679,"votes":[]}"""
        val vote = """
            {"title":"Poll","type":2,"created_at":1793254303,
             "vote_title":"Travel","vote_type":1,"vote_start_time":1793203200,
             "vote_end_time":1793894399,"authors":[{"slug":"author","nickname":"Author"}],
             "votes":[{"id":868,"type":1,"title":"Book early","banner":"",
             "banner_id":0,"status":3,"created_at":1793254303,"updated_at":1793256830,
             "vote_count":38,"is_voted":false}]}
        """.trimIndent()
        val normal = Json.decodeFromString<BulletSingleDataModel>("""{"data":$discussion,"error":0}""")
        val poll = Json.decodeFromString<BulletSingleDataModel>("""{"data":$vote,"error":0}""")
        val list = Json.decodeFromString<BulletDataModel>("""{"data":[$discussion,$vote],"error":0,"total":2}""")

        assertEquals(normal.data, list.data[0])
        assertEquals(poll.data, list.data[1])
        assertTrue(normal.data!!.votes.isEmpty())
        val option = poll.data!!.votes.single()
        assertEquals(868, option.id)
        assertEquals(38, option.voteCount)
        assertEquals(1793256830L, option.updatedAt)
        assertFalse(option.isVoted)
        assertEquals("author", poll.data.authors.single().slug)
    }

    @Test
    fun commentsDecodeAttachmentsMetadataAndNullPayloads() {
        val result = Json.decodeFromString<BulletCommentDataModel>(
            """
            {"data":[{"id_hash":"dblgu0k4o16ctnvdl3u0","body":"Travel tips",
            "status":3,"blocked":false,"top":true,"top_at":1793789690,
            "chosen":true,"chosen_at":1793789691,"views_count_web":2,"views_count_ios":3,
            "created_at":1793789698,"client_admin_id":4,"thread_flag":true,"threads":null,
            "author":{"slug":"p0aevobw","nickname":"Author","user_flags":[{"id":19,"key":"writer"}]},
            "comments_count":1,"likes_count":2,"hates_count":3,"like_status":0,
            "files":[{"id":1611513,"file_path":"https://cdnfile.sspai.com/image.png"}],
            "flags":[],"bullet_created_at":1793254303,"comments":null,"views_count":5}],
            "error":0,"total":17}
            """.trimIndent()
        )
        val comment = result.data.single()
        assertEquals(17, result.total)
        assertEquals("dblgu0k4o16ctnvdl3u0", comment.idHash)
        assertEquals("p0aevobw", comment.author.slug)
        assertEquals("writer", comment.author.userFlags.single().key)
        assertEquals(1611513, comment.files.single().id)
        assertEquals("https://cdnfile.sspai.com/image.png", comment.files.single().filePath)
        assertTrue(comment.top)
        assertTrue(comment.chosen)
        assertTrue(comment.threadFlag)
        assertEquals(1793789690L, comment.topAt)
        assertEquals(1793789691L, comment.chosenAt)
        assertEquals(2, comment.viewsCountWeb)
        assertEquals(3, comment.viewsCountIos)
        assertEquals(4, comment.clientAdminId)
        assertEquals(5, comment.viewsCount)
        assertNull(comment.comments)
        assertNull(comment.threads)
    }

    @Test
    fun envelopesRequireErrorButAllowMissingData() {
        assertNull(Json.decodeFromString<BulletSingleDataModel>("""{"error":404}""").data)
        assertTrue(Json.decodeFromString<BulletCommentDataModel>("""{"error":403}""").data.isEmpty())
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<BulletSingleDataModel>("""{"data":{}}""")
        }
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<BulletCommentDataModel>("""{"data":[]}""")
        }
    }
}
