package com.shihcheeng.sspaiyou.modula

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageLoadClient

@Qualifier
@Retention
annotation class NormalClient

@Module
@InstallIn(SingletonComponent::class)
object ImageModula {

    private val header = mapOf(
        "Accept" to "image/jxl,image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8",
        "Referer" to "https://sspai.com/",
        "Sec-Fetch-Dest" to "image",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
    ).toHeaders()

    @ImageLoadClient
    @Provides
    @Singleton
    fun provideImageOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val newBuilder = it.request().newBuilder()
                newBuilder.headers(header)
                it.proceed(newBuilder.build())
            }
            .build()
    }

    fun imageLoader(okHttpClient: OkHttpClient, context: Context) =
        ImageLoader.Builder(context)
            .crossfade(true)
            .okHttpClient(okHttpClient)
            .build()

}