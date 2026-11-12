package com.shihcheeng.sspaiyou.database.datamodel

import androidx.room.Entity
import androidx.room.Index

/**
 * 本地阅读历史记录。
 *
 * 这张表只保存历史列表和阅读进度所需的文章快照，不保存文章正文。
 * 同一篇文章可以被多个账号分别记录，因此使用 ownerKey + postId 作为联合主键。
 */
@Entity(
    tableName = "reading_records",
    primaryKeys = ["ownerKey", "postId"],
    indices = [
        Index(value = ["ownerKey", "lastReadAtEpochMillis"])
    ]
)
data class LocalRecordDataModel(
    /**
     * 记录所属者的本地标识，例如登录用户 slug；未登录时可使用固定的 anonymous。
     * 这是账号标识，不是文章 slug。
     */
    val ownerKey: String,

    /** 服务端文章 ID，必须大于 0。 */
    val postId: Int,

    /**
     * 重新打开文章时使用的详情路由类型，例如 id 或 member_slug。
     * 具体取值应由调用方统一定义，避免仅凭 postId 误用详情接口。
     */
    val detailRoute: String,

    /**
     * 文章的 slug，不是用户 slug。
     * 普通文章和会员文章都可能有 slug；会员文章重新打开时通常需要它。
     */
    val slug: String? = null,

    /** 保存到历史列表中的文章标题快照。 */
    val title: String,

    /** 保存到历史列表中的文章封面 URL 快照。 */
    val coverUrl: String? = null,

    /** 文章作者的服务端用户 ID。 */
    val authorId: Int? = null,

    /** 文章作者昵称快照。 */
    val authorName: String? = null,

    /** 文章作者的用户 slug，不是文章 slug。 */
    val authorSlug: String? = null,

    /** 文章作者头像 URL 快照。 */
    val authorAvatarUrl: String? = null,

    /**
     * 当前阅读进度，范围为 0f..1f。
     * 0 表示未开始，1 表示已读完，主要用于列表展示。
     */
    val readProgress: Float = 0f,

    /**
     * 恢复阅读时对应的 LazyColumn 项目索引。
     * 与 resumeItemOffsetPx 一起使用，不能单独视为百分比进度。
     */
    val resumeItemIndex: Int? = null,

    /** 恢复阅读时对应项目的像素偏移量。 */
    val resumeItemOffsetPx: Int? = null,

    /**
     * 最近一次阅读时间，Unix epoch 毫秒。
     * 用于历史记录排序，不是文章发布时间。
     */
    val lastReadAtEpochMillis: Long,

    /** 阅读完成时间，Unix epoch 毫秒；未读完时为 null。 */
    val completedAtEpochMillis: Long? = null
) {
    companion object {
        const val DETAIL_ROUTE_ID = "id"
        const val DETAIL_ROUTE_MEMBER_SLUG = "member_slug"
        const val OWNER_ANONYMOUS = "anonymous"
    }
}
