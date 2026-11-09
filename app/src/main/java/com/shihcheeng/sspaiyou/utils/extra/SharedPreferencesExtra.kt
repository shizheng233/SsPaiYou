package com.shihcheeng.sspaiyou.utils.extra

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun SharedPreferences.flowKey(): Flow<String?> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        trySend(key)
    }
    registerOnSharedPreferenceChangeListener(listener)
    // Read the initial state only after registration so concurrent changes cannot be missed.
    trySend(null)
    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}
