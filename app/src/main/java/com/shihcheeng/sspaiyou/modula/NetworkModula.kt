package com.shihcheeng.sspaiyou.modula

import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModula {

    private fun headerOf(
        refer: String,
    ) = mapOf(
        "Host" to "sspai.com",
        "Referer" to refer,
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
    ).toHeaders()

    private fun detectiveHeaderUrl(url: String): Headers {
        return when (url.removePrefix("/api/v1/")) {
            SSPaiUrl.HOME_SUB_URL -> headerOf(Constants.MAIN)
            SSPaiUrl.HOME_MAIN_URL -> headerOf(Constants.MAIN)
            SSPaiUrl.MATRIX_URL -> headerOf(Constants.MATRIX)
            SSPaiUrl.MATRIX_URL_HOT -> headerOf(Constants.MATRIX)
            SSPaiUrl.MATRIX_URL_LOOK -> headerOf(Constants.MATRIX)
            SSPaiUrl.COLUMN_URL -> headerOf(Constants.COLUMN)
            else -> headerOf(Constants.MAIN)
        }
    }

    @Provides
    @Singleton
    @NormalClient
    fun provideOkHttp(
        tokenManager: LocalTokenManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val newBuilder = it.request().newBuilder()
                val map = detectiveHeaderUrl(it.request().url.encodedPath).toMap().toMutableMap()
                val token = tokenManager.transformToAuthorization()
                if (token != null) {
                    map["Authorization"] = token
                }
                newBuilder.headers(map.toHeaders())
                it.proceed(newBuilder.build())
            }
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                redactHeader("Authorization")
                redactHeader("Cookie")
                redactHeader("Set-Cookie")
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @NormalClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(SSPaiUrl.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .build()

}

