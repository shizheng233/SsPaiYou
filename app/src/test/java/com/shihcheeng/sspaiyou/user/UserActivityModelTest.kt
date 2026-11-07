package com.shihcheeng.sspaiyou.user

import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityCommentAndReply
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityPrimeArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityTopicComment
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserActivityModelTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mixedPageKeepsCommunityCommentsRepliesReactionsAndArticleTargets() {
        val source = requireNotNull(javaClass.getResource("/user_activity.json"))
            .readText(Charsets.UTF_8)
        val response = json.decodeFromString<UserActivityDataModel>(source)
        val activities = requireNotNull(response.data)
        assertEquals(0, response.error)
        assertEquals(77, response.total)
        assertEquals(10, activities.size)

        val replies = activities.filterIsInstance<DataActivityCommentAndReply>()
        assertEquals(listOf(3104771, 3103758, 3103757), replies.map { it.id })
        assertEquals("回复了评论", replies.first().action)
        assertEquals("dbmr8p5b34t8f3colo80", replies.first().data.commentReply.idHash)
        assertTrue(replies.first().data.commentReply.body.contains("Windowed apps"))
        assertTrue(replies.first().data.comment.body.contains("iPad mini 7"))
        assertEquals("", replies.first().data.commentReplyTo.body)
        assertEquals("1l7lm3mxlt2n", replies.first().data.topic.idHash)
        assertNull(replies.first().data.comment.author)
        assertNull(replies.first().data.commentReply.author)

        val comments = activities.filterIsInstance<DataActivityTopicComment>()
        assertEquals(listOf(3103756, 3103755, 3069348), comments.map { it.id })
        val reaction = comments.first()
        assertEquals("回应了评论", reaction.action)
        assertEquals("dbkq6sdb34t9oboh129g", reaction.data.comment.idHash)
        assertTrue(reaction.data.comment.body.contains("严肃下载"))
        assertEquals("小红花", reaction.data.status.title)
        assertTrue(reaction.data.status.likeOn)
        assertEquals("https://cdn-static.sspai.com/ui/emoji/5_xiaohonghua.png", reaction.data.status.banner)
        assertNull(reaction.data.comment.author)
        assertNull(reaction.data.topic.author)
        val comment = comments.last()
        assertEquals("评论了主题", comment.action)
        assertTrue(comment.data.comment.body.contains("AI配音"))
        assertEquals("AI短剧让我心理+生理恶心🤢", comment.data.topic.title)
        assertEquals("", comment.data.status.title)

        val topic = activities.filterIsInstance<DataActivityPrimeArticle>().single()
        assertEquals(3103263, topic.id)
        assertEquals("iOS｜做了一个复古游戏机风格的扫雷游戏💣", topic.data.topic.title)
        val articles = activities.filterIsInstance<DataActivityArticle>()
        assertEquals(listOf(118458, 112121, 111417), articles.map { it.data.id })
        assertTrue(articles.all { it.data.title.isNotBlank() && it.data.banner.isNotBlank() })
    }

    @Test
    fun existingReplyReactionRetainsParentCommentAndReplyTarget() {
        val activity = json.decodeFromString<IBaseData>(
            """
            {"id":42,"key":"community_status_topic_comment_reply","action":"回应了回复",
            "data":{
              "comment":{"body":"<p>原评论</p>","author":null},
              "comment_reply":{"body":"<p>评论回复</p>","author":null},
              "comment_reply_to":{"body":"<p>被回复的内容</p>","id_hash":"reply-target"},
              "status":{"title":"小红花","like_on":true,"users":null},
              "topic":{"id_hash":"topic-id","title":"所属主题","author":null}
            }}
            """.trimIndent()
        ) as DataActivityCommentAndReply

        assertEquals("<p>原评论</p>", activity.data.comment.body)
        assertEquals("<p>评论回复</p>", activity.data.commentReply.body)
        assertEquals("<p>被回复的内容</p>", activity.data.commentReplyTo.body)
        assertEquals("reply-target", activity.data.commentReplyTo.idHash)
        assertEquals("小红花", activity.data.status.title)
        assertEquals("所属主题", activity.data.topic.title)
    }
}
