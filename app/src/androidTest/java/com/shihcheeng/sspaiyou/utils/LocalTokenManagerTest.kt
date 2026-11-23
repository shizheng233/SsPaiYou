package com.shihcheeng.sspaiyou.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.webkit.CookieManager
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.security.KeyStore
import java.util.UUID

class LocalTokenManagerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesName = "cookie-encryption-test-${UUID.randomUUID()}"
    private val keyAlias = "cookie-encryption-test-${UUID.randomUUID()}"
    private lateinit var preferences: SharedPreferences
    private lateinit var cipher: CookieCipher
    private lateinit var manager: LocalTokenManager

    @Before
    fun setUp() {
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        cipher = CookieCipher(keyAlias)
        manager = newManager()
    }

    @After
    fun tearDown() {
        context.deleteSharedPreferences(preferencesName)
        cipher.deleteKey()
    }

    @Test
    fun tokenIsEncryptedOnDiskAndDecryptsWithANewManager() = runBlocking {
        manager.saveToken(TOKEN)

        assertNotEquals(TOKEN, storedToken())
        assertNoPlaintextOnDisk(TOKEN)
        assertEquals(TOKEN, newManager().getToken())
        assertEquals("Bearer $TOKEN", manager.transformToAuthorization())
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertNull(keyStore.getKey(keyAlias, null).encoded)
    }

    @Test
    fun repeatedWritesUseFreshIvsAndOnlyAnAccountChangeClearsSlug() = runBlocking {
        manager.saveToken(TOKEN)
        manager.saveUserSlug("first-user")
        val first = storedToken()

        manager.saveToken(TOKEN)
        assertNotEquals(first, storedToken())
        assertEquals("first-user", manager.getSlug())

        manager.saveToken("new-session")
        assertNull(manager.getSlug())
        assertEquals("new-session", manager.getToken())
    }

    @Test
    fun allLegacyCookiesMigrateTogetherAndPreserveAccountMetadata() {
        preferences.edit()
            .putString(Constants.SSPAI_AWT_JSON, TOKEN)
            .putString("another_cookie", "legacy-cookie=value;中文")
            .putString("LOCAL_USER_NAME", "existing-user")
            .commit()

        assertEquals(TOKEN, manager.getToken())
        assertEquals("legacy-cookie=value;中文", manager.getCookie("another_cookie"))
        assertEquals("existing-user", manager.getSlug())
        assertNoPlaintextOnDisk(TOKEN, "legacy-cookie=value;中文")
        assertEquals(TOKEN, newManager().getToken())
    }

    @Test
    fun arbitraryCookiesAlsoUseAuthenticatedEncryption() {
        manager.cookieSaver("session_cookie", "cookie=value=中文")

        assertNotEquals("cookie=value=中文", preferences.getString("session_cookie", null))
        assertEquals("cookie=value=中文", newManager().getCookie("session_cookie"))
        assertNoPlaintextOnDisk("cookie=value=中文")
    }

    @Test
    fun modifiedAuthenticationTagDiscardsTokenAndSlugAndAllowsLoginAgain() = runBlocking {
        manager.saveToken(TOKEN)
        manager.saveUserSlug("existing-user")
        val bytes = Base64.decode(storedToken().removePrefix("v1:"), Base64.NO_WRAP)
        bytes[bytes.lastIndex] = (bytes.last().toInt() xor 1).toByte()
        preferences.edit().putString(
            Constants.SSPAI_AWT_JSON,
            "v1:" + Base64.encodeToString(bytes, Base64.NO_WRAP)
        ).commit()

        val reopened = newManager()
        assertNull(reopened.getToken())
        assertNull(reopened.getSlug())
        assertNull(reopened.transformToAuthorization())
        assertFalse(preferences.contains(Constants.SSPAI_AWT_JSON))

        reopened.saveToken("new-session")
        assertEquals("new-session", reopened.getToken())
    }

    @Test
    fun ciphertextCannotBeMovedToADifferentCookieName() = runBlocking {
        manager.saveToken(TOKEN)
        preferences.edit().putString("another_cookie", storedToken()).commit()

        assertNull(manager.getCookie("another_cookie"))
        assertEquals(TOKEN, manager.getToken())
    }

    @Test
    fun malformedOrPlaintextValuesNeverBecomeCredentialsAfterMigration() = runBlocking {
        for (invalid in listOf("plaintext-session", "v1:!", "v1:AA==", "v2:AAAA")) {
            manager.saveToken(TOKEN)
            preferences.edit().putString(Constants.SSPAI_AWT_JSON, invalid).commit()

            assertNull(newManager().getToken())
            assertFalse(preferences.contains(Constants.SSPAI_AWT_JSON))
        }
    }

    @Test
    fun missingKeyDoesNotGetRecreatedOnReadAndNewLoginCreatesANewKey() = runBlocking {
        manager.saveToken(TOKEN)
        manager.saveUserSlug("existing-user")
        cipher.deleteKey()

        val reopened = newManager()
        assertNull(reopened.getToken())
        assertNull(reopened.getSlug())
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertFalse(keyStore.containsAlias(keyAlias))

        reopened.saveToken("new-session")
        assertEquals("new-session", newManager().getToken())
    }

    @Test
    fun tokenFlowEmitsDecryptedValuesAndNeverEmitsStoredCiphertext() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        try {
            val tokens = manager.tokenFlow(scope)
            assertNull(tokens.value)
            manager.saveToken(TOKEN)
            assertEquals(TOKEN, withTimeout(5_000) { tokens.first { it == TOKEN } })
            manager.saveToken(" ")
            assertEquals(" ", withTimeout(5_000) { tokens.first { it == " " } })
            assertNull(manager.transformToAuthorization())
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun webSessionCookieIsScopedToSspaiAndClearedOnLogout() = runBlocking {
        val cookies = CookieManager.getInstance()
        val sessionCookie = "${Constants.SSPAI_AWT_JSON}=$TOKEN"

        try {
            manager.saveToken(TOKEN)
            manager.restoreWebSessionCookie()

            assertTrue(cookies.getCookie(Constants.MAIN).orEmpty().contains(sessionCookie))
            assertFalse(cookies.getCookie("http://sspai.com/").orEmpty().contains(sessionCookie))
            assertFalse(cookies.getCookie("https://www.sspai.com/").orEmpty().contains(sessionCookie))
            assertFalse(cookies.getCookie("https://sspai.com.example.com/").orEmpty().contains(sessionCookie))
        } finally {
            manager.logout()
        }

        assertFalse(cookies.getCookie(Constants.MAIN).orEmpty().contains(sessionCookie))
    }

    @Test
    fun logoutClearsAllCookiesAndUserIdentityAndAllowsLoginAgain() = runBlocking {
        manager.saveToken(TOKEN)
        manager.saveUserSlug("existing-user")
        manager.cookieSaver("another_cookie", "session-cookie")

        manager.logout()
        manager.logout()

        val reopened = newManager()
        assertNull(reopened.getToken())
        assertNull(reopened.getSlug())
        assertNull(reopened.getCookie("another_cookie"))
        assertNull(reopened.transformToAuthorization())
        assertEquals(setOf("COOKIE_STORAGE_VERSION"), preferences.all.keys)
        val xml = File(context.applicationInfo.dataDir, "shared_prefs/$preferencesName.xml").readText()
        assertFalse(xml.contains("v1:"))
        assertFalse(xml.contains("existing-user"))

        reopened.saveToken("new-session")
        assertTrue(reopened.saveUserSlug("new-user"))
        assertEquals("new-session", reopened.getToken())
        assertEquals("new-user", reopened.getSlug())
    }

    @Test
    fun logoutRefreshesExistingTokenAndSlugSubscribers() = runBlocking<Unit> {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        try {
            val tokens = manager.tokenFlow(scope)
            val slugs = manager.slugFlow(scope)
            manager.saveToken(TOKEN)
            manager.saveUserSlug("existing-user")
            withTimeout(5_000) {
                tokens.first { it == TOKEN }
                slugs.first { it == "existing-user" }
            }

            manager.logout()

            withTimeout(5_000) {
                tokens.first { it == null }
                slugs.first { it == null }
            }
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun logoutDuringListenerRegistrationCannotLeaveStaleInitialState() = runBlocking {
        for (observeToken in listOf(true, false)) {
            manager.saveToken(TOKEN)
            manager.saveUserSlug("existing-user")
            val registered = CompletableDeferred<Unit>()
            val loggingOutPreferences = object : SharedPreferences by preferences {
                override fun registerOnSharedPreferenceChangeListener(
                    listener: SharedPreferences.OnSharedPreferenceChangeListener
                ) {
                    runBlocking { manager.logout() }
                    preferences.registerOnSharedPreferenceChangeListener(listener)
                    registered.complete(Unit)
                }
            }
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            try {
                val reader = LocalTokenManager(loggingOutPreferences, cipher)
                val state = if (observeToken) reader.tokenFlow(scope) else reader.slugFlow(scope)
                withTimeout(5_000) {
                    registered.await()
                    state.first { it == null }
                }
            } finally {
                scope.cancel()
            }
        }
    }

    @Test
    fun staleUserInfoCannotRestoreSlugAfterLogoutOrReplaceAnotherAccount() = runBlocking {
        manager.saveToken(TOKEN)
        manager.logout()

        assertFalse(manager.saveUserSlug("old-user", expectedToken = TOKEN))
        assertFalse(manager.saveUserSlug("old-user"))
        assertNull(manager.getSlug())

        manager.saveToken("new-session")
        assertTrue(manager.saveUserSlug("new-user", expectedToken = "new-session"))
        assertFalse(manager.saveUserSlug("old-user", expectedToken = TOKEN))
        assertEquals("new-user", manager.getSlug())
    }

    @Test
    fun concurrentLogoutAndUserInfoWritesNeverRestoreLoggedOutIdentity() = runBlocking {
        repeat(10) {
            manager.saveToken(TOKEN)
            manager.saveUserSlug("existing-user")
            val start = CompletableDeferred<Unit>()
            val writes = List(4) {
                async(Dispatchers.Default) {
                    start.await()
                    manager.saveUserSlug("late-user", expectedToken = TOKEN)
                }
            }
            val logout = async(Dispatchers.Default) {
                start.await()
                manager.logout()
            }
            start.complete(Unit)
            writes.awaitAll()
            logout.await()

            assertNull(manager.getToken())
            assertNull(manager.getSlug())
        }
    }

    private fun newManager() = LocalTokenManager(preferences, CookieCipher(keyAlias))

    private fun storedToken() = requireNotNull(preferences.getString(Constants.SSPAI_AWT_JSON, null))

    private fun assertNoPlaintextOnDisk(vararg values: String) {
        val xml = File(context.applicationInfo.dataDir, "shared_prefs/$preferencesName.xml").readText()
        values.forEach { assertFalse(xml.contains(it)) }
        assertTrue(xml.contains("v1:"))
    }

    private companion object {
        const val TOKEN = "header.payload.signature-test-session"
    }
}
