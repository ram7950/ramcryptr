package com.rambo.ramcryptr

import android.util.Base64
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object TextCrypto {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128

    // 🔐 Key derivation (stable + safe)
    private fun deriveKey(
        master: String,
        index: Int,
        channelSeed: String
    ): SecretKeySpec {

        val digest =
            MessageDigest.getInstance("SHA-256")

        val fullKey =
            digest.digest(
                (
                    master +
                    index +
                    channelSeed
                ).toByteArray(
                    Charset.forName("UTF-8")
                )
            )

        val keyBytes =
            fullKey.copyOf(32)

        return SecretKeySpec(
            keyBytes,
            "AES"
        )
    }

    // 🔐 Encrypt
    fun encrypt(plainText: String, master: String): String {
        try {
            val index = DateUtils.getDayIndex()
            val code = Base62.encode(index)

            
            val key = deriveKey(
                master,
                index,
                RamApp.instance?.let {
                    ChannelCryptoBridge
                        .getActiveSeed(it)
                } ?: "GLOBAL_DEFAULT"
            )
    

            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))

            val encrypted = cipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))

            val combined = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)

            val base64 = Base64.encodeToString(combined, Base64.NO_WRAP)

            return "AES256::$code::$base64"

        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }

    // 🔓 Decrypt
    fun decrypt(message: String, master: String): String {
        try {
            val parts = message.split("::", limit = 3)
            if (parts.size != 3 || parts[0] != "AES256") {
                throw IllegalArgumentException("Invalid format")
            }

            val code = parts[1]
            val data = parts[2]

            val index = Base62.decode(code)
            
            val key = deriveKey(
                master,
                index,
                RamApp.instance?.let {
                    ChannelCryptoBridge
                        .getActiveSeed(it)
                } ?: "GLOBAL_DEFAULT"
            )
    

            val decoded = Base64.decode(data, Base64.NO_WRAP)

            if (decoded.size <= IV_SIZE) {
                throw IllegalArgumentException("Corrupted data")
            }

            val iv = decoded.copyOfRange(0, IV_SIZE)
            val encrypted = decoded.copyOfRange(IV_SIZE, decoded.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))

            val decrypted = cipher.doFinal(encrypted)

            return String(decrypted, Charset.forName("UTF-8"))

        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }
}
