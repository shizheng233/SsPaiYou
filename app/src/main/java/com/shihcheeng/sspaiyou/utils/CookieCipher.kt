package com.shihcheeng.sspaiyou.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** The key stays in Android Keystore; only the IV, ciphertext and authentication tag are stored. */
class CookieCipher(private val keyAlias: String = KEY_ALIAS) {
    private val keyStore by lazy {
        KeyStore.getInstance(KEYSTORE).apply { load(null) }
    }

    fun encrypt(name: String, value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        // Let the provider generate a fresh, randomized IV for every encryption.
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        cipher.updateAAD(associatedData(name))
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return PREFIX + Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
    }

    fun decrypt(name: String, value: String): String {
        if (!value.startsWith(PREFIX)) {
            throw GeneralSecurityException("Unsupported cookie ciphertext version")
        }
        val bytes = Base64.decode(value.removePrefix(PREFIX), Base64.NO_WRAP)
        if (bytes.size < IV_BYTES + TAG_BITS / 8) {
            throw GeneralSecurityException("Invalid cookie ciphertext")
        }
        // Reading must never generate a replacement key for existing ciphertext.
        val key = synchronized(keyLock) {
            keyStore.getKey(keyAlias, null) as? SecretKey
        } ?: throw GeneralSecurityException("Cookie encryption key is missing")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(TAG_BITS, bytes.copyOfRange(0, IV_BYTES))
        )
        cipher.updateAAD(associatedData(name))
        return cipher.doFinal(bytes, IV_BYTES, bytes.size - IV_BYTES).toString(Charsets.UTF_8)
    }

    fun deleteKey() = synchronized(keyLock) {
        keyStore.deleteEntry(keyAlias)
    }

    private fun getOrCreateKey(): SecretKey = synchronized(keyLock) {
        (keyStore.getKey(keyAlias, null) as? SecretKey) ?: KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE
        ).apply {
            init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    // Bind the ciphertext to both its format and preference key to prevent cookie swapping.
    private fun associatedData(name: String) =
        "TOKEN_MANAGER:$PREFIX$name".toByteArray(Charsets.UTF_8)

    private companion object {
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "com.shihcheeng.sspaiyou.cookies.aes_gcm.v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val PREFIX = "v1:"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
        val keyLock = Any()
    }
}
