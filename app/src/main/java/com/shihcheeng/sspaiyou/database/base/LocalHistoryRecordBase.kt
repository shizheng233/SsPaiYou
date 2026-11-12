package com.shihcheeng.sspaiyou.database.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shihcheeng.sspaiyou.database.dao.HistoryDAO
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel

@Database(entities = [LocalRecordDataModel::class], version = 1)
abstract class LocalHistoryRecordBase : RoomDatabase() {
    abstract fun historyDao(): HistoryDAO
}