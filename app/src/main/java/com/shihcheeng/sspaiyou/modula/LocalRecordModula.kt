package com.shihcheeng.sspaiyou.modula

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.shihcheeng.sspaiyou.database.base.LocalHistoryRecordBase
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

}