package com.shihcheeng.sspaiyou.utils.extra

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


fun <T> mutableNetStateOf(started: NetState<T> = NetState.Loading) =
    MutableStateFlow(started)

fun <T> Flow<T>.observe(owner: LifecycleOwner, collector: FlowCollector<T>) {
    val start = if (this is StateFlow) CoroutineStart.UNDISPATCHED else CoroutineStart.DEFAULT
    owner.lifecycleScope.launch(start = start) {
        collect(collector)
    }
}