package com.shihcheeng.sspaiyou.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyPermanentlyInvalidatedException
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
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.ProviderException
import java.security.UnrecoverableKeyException
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("UseKtx")
@Singleton
class LocalTokenManager internal constructor(
    private val tokenPreferences: SharedPreferences,
    private val cookieCipher: CookieCipher
) {
    @Inject
    constructor(@ApplicationContext context: Context) : this(
        tokenPreferences = context.getSharedPreferences("TOKEN_MANAGER", Context.MODE_PRIVATE),
        cookieCipher = CookieCipher()
    )

    suspend fun saveToken(string: String) = withContext(Dispatchers.IO) {
        cookieSaver(Constants.SSPAI_AWT_JSON, string)
    }

    suspend fun saveUserSlug(slug: String) = withContext(Dispatchers.IO) {
        synchronized(storageLock) {
            tokenPreferences.edit {
                putString(LOCAL_REAL_USER_NAME, slug)
            }
        }
    }

    /** Synchronous for existing callers; invoke writes from an IO dispatcher. */
    fun cookieSaver(key: String, string: String) = synchronized(storageLock) {
        require(key != LOCAL_REAL_USER_NAME && key != COOKIE_STORAGE_VERSION) {
            "This preference key is reserved for token metadata"
        }
        val previous = getCookieLocked(key)
        val encrypted = try {
            cookieCipher.encrypt(key, string)
        } catch (e: GeneralSecurityException) {
            if (!e.isInvalidatedKey()) throw e
            // A permanently unusable key can be replaced when saving a new login.
            cookieCipher.deleteKey()
            cookieCipher.encrypt(key, string)
        }
        val editor = tokenPreferences.edit().putString(key, encrypted)
        if (key == Constants.SSPAI_AWT_JSON && previous != string) {
            editor.remove(LOCAL_REAL_USER_NAME)
        }
        // A successful login must mean the ciphertext has actually reached disk.
        check(editor.commit()) { "Unable to persist encrypted cookie" }
    }

    fun getCookie(key: String): String? = synchronized(storageLock) {
        require(key != LOCAL_REAL_USER_NAME && key != COOKIE_STORAGE_VERSION) {
            "This preference key is reserved for token metadata"
        }
        getCookieLocked(key)
    }

    fun getToken(): String? = getCookie(Constants.SSPAI_AWT_JSON)

    fun transformToAuthorization(): String? = getToken()
        ?.takeIf { it.isNotBlank() }
        ?.let { "Bearer $it" }

    private fun getCookieLocked(key: String): String? {
        return try {
            migrateCookiesLocked()
            tokenPreferences.getString(key, null)?.let { cookieCipher.decrypt(key, it) }
        } catch (e: Exception) {
            when (e) {
                is GeneralSecurityException, is IllegalArgumentException, is IOException,
                is ProviderException, is ClassCastException -> {
                    // Never interpret failed ciphertext as a legacy plaintext cookie.
                    val editor = tokenPreferences.edit()
                    if (tokenPreferences.all[COOKIE_STORAGE_VERSION] != ENCRYPTED_VERSION) {
                        editor.clear().putInt(COOKIE_STORAGE_VERSION, ENCRYPTED_VERSION)
                    } else {
                        editor.remove(key)
                        if (key == Constants.SSPAI_AWT_JSON) editor.remove(LOCAL_REAL_USER_NAME)
                    }
                    check(editor.commit()) { "Unable to discard unreadable cookies" }
                    if (e.isInvalidatedKey()) cookieCipher.deleteKey()
                    null
                }

                else -> throw e
            }
        }
    }


    private fun migrateCookiesLocked() {
        val preferences = tokenPreferences.all
        if (preferences[COOKIE_STORAGE_VERSION] == ENCRYPTED_VERSION) return
        if (preferences.containsKey(COOKIE_STORAGE_VERSION)) {
            throw GeneralSecurityException("Unsupported cookie storage version")
        }
        // Encrypt everything before opening the editor, then atomically replace the old values.
        val encryptedCookies = preferences
            .filterKeys { it != LOCAL_REAL_USER_NAME }
            .mapValues { (name, value) ->
                val cookie = value as? String
                    ?: throw GeneralSecurityException("Invalid legacy cookie")
                cookieCipher.encrypt(name, cookie)
            }
        val editor = tokenPreferences.edit()
        encryptedCookies.forEach { (name, value) -> editor.putString(name, value) }
        editor.putInt(COOKIE_STORAGE_VERSION, ENCRYPTED_VERSION)
        check(editor.commit()) { "Unable to migrate plaintext cookies" }
    }

    private fun Exception.isInvalidatedKey() =
        this is KeyPermanentlyInvalidatedException || this is UnrecoverableKeyException

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
        private const val COOKIE_STORAGE_VERSION = "COOKIE_STORAGE_VERSION"
        private const val ENCRYPTED_VERSION = 1
        private val storageLock = Any()
    }

}
