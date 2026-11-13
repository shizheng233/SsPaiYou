package com.shihcheeng.sspaiyou.database.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shihcheeng.sspaiyou.database.dao.SearchKeywordDAO
import com.shihcheeng.sspaiyou.database.datamodel.LocalSearchRecordDataModel

@Database(entities = [LocalSearchRecordDataModel::class], version = 1)
abstract class LocalSearchKeywordRecordBase : RoomDatabase() {
    abstract fun keywordDAO(): SearchKeywordDAO
}