package com.shihcheeng.sspaiyou.api.data.user.activity.datadtos

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object DataInteraction : JsonContentPolymorphicSerializer<IBaseData>(IBaseData::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<IBaseData> {
        val key = element.jsonObject["key"]
            ?.jsonPrimitive
            ?.content?.takeIf { x -> x.isNotEmpty() }
            ?: error("致命错误：在中间处理时找不到KEY，以至于无法判断；执行强制退出。")
        return when (key) {
            "community_status_topic" -> DataActivityPrimeArticle.serializer()
            "follow_user" -> DataActivityAuthors.serializer()
            "follow_special_column" -> DataActivityColumns.serializer()
            "comment_article" -> DataActivityComment.serializer()
            "community_release_topic" -> DataActivityPrimeArticle.serializer()
            "community_reply_topic_comment",
            "community_status_topic_comment_reply" -> DataActivityCommentAndReply.serializer()
            "community_comment_topic",
            "community_status_topic_comment" -> DataActivityTopicComment.serializer()
            "release_article" -> DataActivityArticle.serializer()
            else -> DataActivityArticle.serializer()
        }
    }
}
