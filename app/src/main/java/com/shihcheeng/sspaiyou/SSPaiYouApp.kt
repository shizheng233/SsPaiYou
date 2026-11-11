package com.shihcheeng.sspaiyou

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.material.color.DynamicColors
import com.shihcheeng.sspaiyou.modula.ImageLoadClient
import com.shihcheeng.sspaiyou.modula.ImageModula
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData
import dagger.hilt.android.HiltAndroidApp
import dev.expressive.settings.DataStoreSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltAndroidApp
class SSPaiYouApp : Application(), ImageLoaderFactory {

    @Inject
    @ImageLoadClient
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var settingsRepository: DataStoreSettingsRepository

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun newImageLoader(): ImageLoader {
        // Coil's factory is synchronous: wait for the saved setting before creating its cache.
        val cacheSizeMiB = runBlocking(Dispatchers.IO) {
            settingsRepository.get(SettingData.imageCacheSize)
        }
        return ImageModula.imageLoader(
            okHttpClient = okHttpClient,
            context = applicationContext,
            diskCacheMaxSizeBytes = cacheSizeMiB.roundToLong() * 1024L * 1024L
        )
    }
}
