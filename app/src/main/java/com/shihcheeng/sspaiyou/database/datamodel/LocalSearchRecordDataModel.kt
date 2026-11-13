package com.shihcheeng.sspaiyou.database.datamodel

import androidx.room.Entity
import androidx.room.Index

/**
 * 本地搜索历史记录。
 *
 * 关键词本身作为主键，同一个关键词再次搜索时只更新时间，不会产生重复记录。
 */
@Entity(
    tableName = "local_search_record",
    indices = [Index(value = ["time"])]
)
data class LocalSearchRecordDataModel(
    @androidx.room.PrimaryKey
    val text: String,
    val time: Long
)
