package com.shihcheeng.sspaiyou.ui.screen.setting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shihcheeng.sspaiyou.BuildConfig
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.icons.ArchiveBoxOutline
import com.shihcheeng.sspaiyou.utils.icons.BookletFill
import com.shihcheeng.sspaiyou.utils.icons.Browser
import com.shihcheeng.sspaiyou.utils.icons.CodepenFill
import com.shihcheeng.sspaiyou.utils.icons.ErrorWarningLine
import com.shihcheeng.sspaiyou.utils.icons.FontSizeIcon
import com.shihcheeng.sspaiyou.utils.icons.Output
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
    viewModel: SettingViewModel = hiltViewModel(),
    repository: SettingsRepository = LocalComposeSetting.current,
    onBack: () -> Unit,
    onSeeLibsClick: () -> Unit,
) {
    val context = LocalResources.current
    var logoutDialog by remember { mutableStateOf(false) }
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
                toggle(
                    key = SettingData.urlIsOpenInner,
                    title = context.getString(R.string.use_inner_browser_title),
                    description = context.getString(R.string.use_inner_browser_summary),
                    icon = SSPaiPack.Browser
                )
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
            group(title = context.getString(R.string.cache_text)) {
                slider(
                    key = SettingData.imageCacheSize,
                    title = context.getString(R.string.size_of_image_cache_title),
                    description = context.getString(R.string.size_of_image_cache_title_summary),
                    valueRange = 64f..1024f,
                    steps = 14,
                    formatValue = { "${it.roundToInt()} MiB" },
                    icon = SSPaiPack.ArchiveBoxOutline
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
            group(context.getString(R.string.about_user)) {
                action(
                    title = context.getString(R.string.log_out_text),
                    description = context.getString(R.string.log_out_summary),
                    id = KEY_ACTION_OF_CLEAR_USER_LOGIN,
                    icon = SSPaiPack.Output,
                    isDestructive = true
                )
            }
        },
        repository = repository,
        onBack = onBack,
        onAction = { x ->
            when (x.id) {
                KEY_ACTION_OF_SEE_LIBS -> onSeeLibsClick()
                KEY_ACTION_OF_CLEAR_USER_LOGIN -> {
                    logoutDialog = true
                }
            }
        }
    )

    if (logoutDialog) {
        AlertDialog(
            onDismissRequest = {
                logoutDialog = false
            },
            icon = {
                Icon(
                    imageVector = SSPaiPack.ErrorWarningLine,
                    contentDescription = stringResource(R.string.warning)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.waring_text)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.log_out_text)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logOut()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.log_out_now),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        logoutDialog = false
                    }
                ) {
                    Text(text = stringResource(dev.expressive.settings.R.string.es_cancel))
                }
            }
        )
    }
}

private const val KEY_ACTION_OF_SEE_LIBS = "KEY_ACTION_OF_SEE_LIBS"
private const val KEY_ACTION_OF_CLEAR_USER_LOGIN = "KEY_ACTION_OF_CLEAR_USER_LOGIN"