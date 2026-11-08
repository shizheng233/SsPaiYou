package com.shihcheeng.sspaiyou.ui.screen.setting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalResources
import com.shihcheeng.sspaiyou.BuildConfig
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.icons.BookletFill
import com.shihcheeng.sspaiyou.utils.icons.CodepenFill
import com.shihcheeng.sspaiyou.utils.icons.FontSizeIcon
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData
import com.shihcheeng.sspaiyou.utils.settingdata.SettingKeys
import dev.expressive.settings.SettingsOption
import dev.expressive.settings.SettingsRepository
import dev.expressive.settings.SettingsScreen
import dev.expressive.settings.settingsSpec
import kotlin.math.roundToInt

@Composable
fun SSPaiSettingScreen(
    repository: SettingsRepository = LocalComposeSetting.current,
    onBack: () -> Unit,
    onSeeLibsClick: () -> Unit,
) {
    val context = LocalResources.current
    SettingsScreen(
        spec = settingsSpec(title = context.getString(R.string.setting)) {
            group(title = context.getString(R.string.display)) {
                choice(
                    key = SettingData.themeMode,
                    title = context.getString(R.string.theme_mode),
                    description = context.getString(R.string.select_system_theme),
                    icon = Icons.Outlined.Star,
                    options = SettingKeys.ThemeMode.entries.map { mode ->
                        SettingsOption(
                            value = mode,
                            label = context.getString(mode.label)
                        )
                    },
                )
                toggle(
                    key = SettingData.dynamicColor,
                    title = context.getString(R.string.dynamic_color),
                    description = context.getString(R.string.use_wallpaper_colors_on_android_12_and_later),
                    icon = Icons.Outlined.Settings,
                )
            }
            group(title = context.getString(R.string.reader)) {
                slider(
                    key = SettingData.readerScale,
                    title = context.getString(R.string.font_scale),
                    valueRange = 1f..1.3f,
                    icon = SSPaiPack.FontSizeIcon,
                    formatValue = {
                        "${(it * 100).roundToInt()}%"
                    },
                    steps = 5
                )
            }
            group(title = context.getString(R.string.about_app)) {
                action(
                    title = context.getString(R.string.version),
                    trailingText = BuildConfig.VERSION_NAME + "(${BuildConfig.VERSION_CODE})",
                    description = when (BuildConfig.BUILD_TYPE) {
                        "release" -> context.getString(R.string.release_verison_text)
                        "debug" -> context.getString(R.string.debug_version_text)
                        else -> context.getString(R.string.unknown_type_version_text)
                    },
                    icon = SSPaiPack.CodepenFill
                )
                action(
                    title = context.getString(R.string.opensource_libs),
                    description = context.getString(R.string.opensource_libs_summary),
                    id = KEY_ACTION_OF_SEE_LIBS,
                    icon = SSPaiPack.BookletFill
                )
            }
        },
        repository = repository,
        onBack = onBack,
        onAction = { x ->
            when (x.id) {
                KEY_ACTION_OF_SEE_LIBS -> onSeeLibsClick()
            }
        }
    )
}

private const val KEY_ACTION_OF_SEE_LIBS = "KEY_ACTION_OF_SEE_LIBS"