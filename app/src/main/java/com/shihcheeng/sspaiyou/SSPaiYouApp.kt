package com.shihcheeng.sspaiyou

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.shihcheeng.sspaiyou.modula.ImageLoadClient
import com.shihcheeng.sspaiyou.modula.ImageModula
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class SSPaiYouApp : Application(), ImageLoaderFactory {

    @Inject
    @ImageLoadClient
    lateinit var okHttpClient: OkHttpClient

    override fun newImageLoader(): ImageLoader {
        return ImageModula.imageLoader(okHttpClient, applicationContext)
    }
}