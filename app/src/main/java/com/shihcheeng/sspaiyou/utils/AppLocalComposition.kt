package com.shihcheeng.sspaiyou.utils

import androidx.compose.runtime.compositionLocalOf
import dev.expressive.settings.DataStoreSettingsRepository

val LocalComposeTokenManager = compositionLocalOf<LocalTokenManager> {
    error("致命错误：未能置入本地Token管理器")
}
val LocalComposeSetting =
    compositionLocalOf<DataStoreSettingsRepository> { error("致命错误：未能注入设置中间库") }