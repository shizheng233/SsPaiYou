package com.shihcheeng.sspaiyou.utils.extra

import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.flow.MutableStateFlow


fun <T> mutableNetStateOf(started: NetState<T> = NetState.Loading) =
    MutableStateFlow(started)