package com.shihcheeng.sspaiyou.utils

import androidx.compose.runtime.compositionLocalOf

val LocalComposeTokenManager = compositionLocalOf<LocalTokenManager> { error("no local manager") }