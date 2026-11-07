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
            if (string != getToken()) {
                remove(LOCAL_REAL_USER_NAME)
            }
            putString(Constants.SSPAI_AWT_JSON, string)
        }
    }

    suspend fun saveUserSlug(slug: String) = withContext(Dispatchers.IO) {
        tokenPreferences.edit {
            putString(LOCAL_REAL_USER_NAME, slug)
        }
    }

    fun cookieSaver(key: String, string: String) {
        tokenPreferences.edit {
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

    fun getSlug(): String? = tokenPreferences.getString(LOCAL_REAL_USER_NAME, null)

    fun slugFlow(scoped: CoroutineScope) = tokenPreferences.flowKey()
        .filter { it == LOCAL_REAL_USER_NAME || it == null }
        .onStart { emit(LOCAL_REAL_USER_NAME) }
        .map { getSlug() }
        .conflate()
        .stateIn(scoped, SharingStarted.Eagerly, getSlug())


    fun tokenFlow(
        scoped: CoroutineScope
    ) = tokenPreferences.flowKey()
        .filter { it == Constants.SSPAI_AWT_JSON || it == null }
        .onStart {
            emit("ignition")
        }.map { getToken() }
        .conflate()
        .stateIn(scoped, SharingStarted.Eagerly, getToken())

    companion object {
        private const val LOCAL_REAL_USER_NAME = "LOCAL_USER_NAME"
    }

}
