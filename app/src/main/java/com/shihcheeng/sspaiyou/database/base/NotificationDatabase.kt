package com.shihcheeng.sspaiyou.database.base

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shihcheeng.sspaiyou.database.dao.NotificationDao
import com.shihcheeng.sspaiyou.database.datamodel.NotificationEntity
import com.shihcheeng.sspaiyou.database.datamodel.NotificationReadState

@Database(entities = [NotificationEntity::class, NotificationReadState::class], version = 1, exportSchema = false)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
