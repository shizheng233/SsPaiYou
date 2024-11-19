package com.shihcheeng.sspaiyou.utils

import android.content.Context
import androidx.core.content.edit
import com.shihcheeng.sspaiyou.utils.extra.flowKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val tokenPreferences =
        context.getSharedPreferences("TOKEN_MANAGER", Context.MODE_PRIVATE)

    suspend fun saveToken(string: String) = withContext(Dispatchers.IO) {
        tokenPreferences.edit {
            putString(Constants.SSPAI_AWT_JSON, string)
        }
    }

    fun cookieSaver(key: String, string: String) {
        tokenPreferences.edit(commit = true) {
            putString(key, string)
        }
    }

    fun getToken(): String? {
        return tokenPreferences.getString(Constants.SSPAI_AWT_JSON, null)
    }

    fun transformToAuthorization(): String? {
        return if (getToken() != null) {
            "Bearer ${getToken()}"
        } else null
    }


    fun tokenFlow(
        scoped: CoroutineScope
    ) = tokenPreferences.flowKey()
        .filter { it == Constants.SSPAI_AWT_JSON || it == null }
        .onStart {
            emit("ignition")
        }.map { getToken() }
        .conflate()
        .stateIn(scoped, SharingStarted.Eagerly, getToken())

}