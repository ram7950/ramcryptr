package com.rambo.ramcryptr

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object CryptoKeyManager {

    private const val ITERATIONS = 100000
    private const val KEY_LENGTH = 256

    fun generateJoinSecret(): String {

        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    fun deriveAesKey(joinSecret: String): String {

        val salt = "RAMCRYPTR_TNET".toByteArray()

        val spec = PBEKeySpec(
            joinSecret.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        val factory = SecretKeyFactory.getInstance(
            "PBKDF2WithHmacSHA256"
        )

        val key = factory.generateSecret(spec).encoded

        return Base64.encodeToString(
            key,
            Base64.NO_WRAP
        )
    }
}
