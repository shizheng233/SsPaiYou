package com.shihcheeng.sspaiyou.modula

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.shihcheeng.sspaiyou.database.base.LocalHistoryRecordBase
import com.shihcheeng.sspaiyou.database.base.LocalSearchKeywordRecordBase
import com.shihcheeng.sspaiyou.database.base.NotificationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalRecordModula {

    @Provides
    @Singleton
    fun provideNotificationDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder<NotificationDatabase>(context = context, name = "notifications")
            .setDriver(AndroidSQLiteDriver()).build()

    @Provides
    fun provideNotificationDao(database: NotificationDatabase) = database.notificationDao()

    @Provides
    @Singleton
    fun provideLocalHistoryRecordRepository(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder<LocalHistoryRecordBase>(
        context = context,
        name = "local_history_recrod"
    ).setDriver(AndroidSQLiteDriver())
        .build()

    @Provides
    @Singleton
    fun provideHistoryDAO(
        localHistoryRecordBase: LocalHistoryRecordBase
    ) = localHistoryRecordBase.historyDao()

    // ---- 关键字历史记录 ---
    @Provides
    @Singleton
    fun provideLocalKeywordRecord(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder<LocalSearchKeywordRecordBase>(
        context = context,
        name = "local_keyword_record"
    ).setDriver(AndroidSQLiteDriver())
        .build()

    @Provides
    @Singleton
    fun provideKeywordHistoryDAO(
        localSearchKeywordRecordBase: LocalSearchKeywordRecordBase
    ) = localSearchKeywordRecordBase.keywordDAO()

}
