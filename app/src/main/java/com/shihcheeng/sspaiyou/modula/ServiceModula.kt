package com.shihcheeng.sspaiyou.modula

import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.api.service.BulletService
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.api.service.HomeService
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.api.service.NotificationService
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.api.service.TagsService
import com.shihcheeng.sspaiyou.api.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModula {

    @Singleton
    @Provides
    fun provideNotificationService(retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)

    @Singleton
    @Provides
    fun provideBulletService(
        retrofit: Retrofit
    ): BulletService = retrofit.create(BulletService::class.java)

    @Singleton
    @Provides
    fun provideHomeService(
        retrofit: Retrofit
    ): HomeService = retrofit.create(HomeService::class.java)

    @Singleton
    @Provides
    fun provideMatrixService(
        retrofit: Retrofit
    ): MatrixService = retrofit.create(MatrixService::class.java)

    @Singleton
    @Provides
    fun provideArticleService(
        retrofit: Retrofit
    ): ArticleService = retrofit.create(ArticleService::class.java)

    @Singleton
    @Provides
    fun provideColumnsService(
        retrofit: Retrofit
    ): ColumnService = retrofit.create(ColumnService::class.java)

    @Singleton
    @Provides
    fun provideTagsService(
        retrofit: Retrofit
    ): TagsService = retrofit.create(TagsService::class.java)

    @Singleton
    @Provides
    fun provideSearchService(
        retrofit: Retrofit
    ): SearchService = retrofit.create(SearchService::class.java)

    @Singleton
    @Provides
    fun provideUserInfoService(
        retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)
}
