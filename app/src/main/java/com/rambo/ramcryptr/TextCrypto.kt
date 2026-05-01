package com.rambo.ramcryptr

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object TextCrypto {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    fun deriveKey(master: String, index: Int): SecretKeySpec {
        val md = MessageDigest.getInstance("SHA-256")
        val keyBytes = md.digest((master + index).toByteArray())
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(text: String, master: String): String {
        val index = DateUtils.getDayIndex()
        val code = Base62.encode(index)

        val key = deriveKey(master, index)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))

        val encrypted = cipher.doFinal(text.toByteArray())

        val combined = iv + encrypted
        val data = Base64.encodeToString(combined, Base64.NO_WRAP)

        return "AES256::$code::$data"
    }

    fun decrypt(message: String, master: String): String {
        val parts = message.split("::")
        if (parts.size != 3) throw Exception("Invalid format")

        val code = parts[1]
        val data = parts[2]

        val index = Base62.decode(code)
        val key = deriveKey(master, index)

        val decoded = Base64.decode(data, Base64.NO_WRAP)

        val iv = decoded.copyOfRange(0, 12)
        val encrypted = decoded.copyOfRange(12, decoded.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

        val decrypted = cipher.doFinal(encrypted)

        return String(decrypted)
    }
}
