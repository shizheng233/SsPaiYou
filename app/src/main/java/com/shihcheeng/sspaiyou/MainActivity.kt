package com.shihcheeng.sspaiyou

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shihcheeng.sspaiyou.ui.screen.Router
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.LocalComposeTokenManager
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.extra.TRANSPARENT
import com.shihcheeng.sspaiyou.utils.extra.observe
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData
import com.shihcheeng.sspaiyou.utils.settingdata.SettingKeys
import dagger.hilt.android.AndroidEntryPoint
import dev.expressive.settings.DataStoreSettingsRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: LocalTokenManager

    @Inject
    lateinit var settingsRepository: DataStoreSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.TRANSPARENT,
            navigationBarStyle = SystemBarStyle.TRANSPARENT
        )
        window.isNavigationBarContrastEnforced = false
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        settingsRepository.observe(SettingData.themeMode).observe(this) { type ->
            when (type) {
                SettingKeys.ThemeMode.System -> {
                    controller.isAppearanceLightStatusBars = !isDarkMode()
                }

                SettingKeys.ThemeMode.Light -> controller.isAppearanceLightStatusBars = true
                SettingKeys.ThemeMode.Dark -> controller.isAppearanceLightStatusBars = false
            }
        }
        setContent {
            CompositionLocalProvider(
                LocalComposeTokenManager provides tokenManager,
                LocalComposeSetting provides settingsRepository
            ) {
                val isDynamicColor by settingsRepository.observe(SettingData.dynamicColor)
                    .collectAsStateWithLifecycle(initialValue = true)
                val darkTheme by settingsRepository.observe(SettingData.themeMode)
                    .collectAsStateWithLifecycle(initialValue = SettingKeys.ThemeMode.System)
                AppTheme(
                    dynamicColor = isDynamicColor,
                    darkTheme = when (darkTheme) {
                        SettingKeys.ThemeMode.System -> isSystemInDarkTheme()
                        SettingKeys.ThemeMode.Light -> false
                        SettingKeys.ThemeMode.Dark -> true
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Router()
                    }
                }
            }
        }
    }

    fun isDarkMode(): Boolean {
        val currentNightMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }


}
