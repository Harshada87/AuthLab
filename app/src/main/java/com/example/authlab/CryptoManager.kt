package com.example.authlab

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val KEY_NAME = "authlab_biometric_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"

private const val TRANSFORMATION =
            KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_CBC + "/" +
            KeyProperties.ENCRYPTION_PADDING_PKCS7

// Creates the key once and reuses it after. The key is locked behind biometric auth.
private fun getOrCreateSecretKet(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    val existing = keyStore.getKey(KEY_NAME, null) as? SecretKey
    if (existing != null) return existing

    val spec = KeyGenParameterSpec.Builder(
        KEY_NAME,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )

        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(true)  // the rule: no key use without auth
        .setUserAuthenticationValidityDurationSeconds(-1) // -1 = authenticate for EVERY use
        .build()

    val generator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        ANDROID_KEYSTORE
    )
    generator.init(spec)
    return generator.generateKey()
}

//Hands back a cipher wired to that locked key, ready for the fingerprint to unlock.
fun getCryptoCipher() : Cipher {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKet())
    return cipher
}

fun encryptWith(cipher: Cipher, plainText: String): EncryptedData{
    val bytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
    return EncryptedData(ciphertext = bytes, iv = cipher.iv)
}

fun getDecryptCipher(iv: ByteArray): Cipher {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKet(), IvParameterSpec(iv))
    return cipher
}

fun decryptWith(cipher: Cipher, data: EncryptedData): String {
    val plainBytes = cipher.doFinal(data.ciphertext)
    return String(plainBytes, Charsets.UTF_8)
}
