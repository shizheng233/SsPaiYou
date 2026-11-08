package com.shihcheeng.sspaiyou.modula

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.expressive.settings.DataStoreSettingsRepository
import dev.expressive.settings.expressiveSettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingRepositoryModula {

    @Singleton
    @Provides
    fun provideGlobalAppSettingRepository(
        @ApplicationContext context: Context
    ) = DataStoreSettingsRepository(context.expressiveSettingsDataStore)

}