package com.rambo.ramcryptr.encoding

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES256Encoder {

    private const val MARKER = "AES256::"

    private fun makeKey(
        password:String
    ):ByteArray{

        return MessageDigest
            .getInstance("SHA-256")
            .digest(
                password.toByteArray()
            )
    }

    fun encryptText(
        plainText:String,
        key:String
    ):String{

        val iv=ByteArray(16)

        SecureRandom()
            .nextBytes(iv)

        val cipher=
            Cipher.getInstance(
                "AES/CBC/PKCS5Padding"
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(
                makeKey(key),
                "AES"
            ),
            IvParameterSpec(iv)
        )

        val encrypted=
            cipher.doFinal(
                plainText.toByteArray()
            )

        val combined=iv+encrypted

        return MARKER+
            Base64.encodeToString(
                combined,
                Base64.NO_WRAP
            )
    }

    fun decryptText(
        encodedText:String,
        key:String
    ):String{

        if(
            !encodedText.startsWith(
                MARKER
            )
        ){
            throw Exception(
                "Invalid encrypted data"
            )
        }

        val raw=
            Base64.decode(
                encodedText.removePrefix(
                    MARKER
                ),
                Base64.NO_WRAP
            )

        val iv=
            raw.copyOfRange(
                0,
                16
            )

        val cipherBytes=
            raw.copyOfRange(
                16,
                raw.size
            )

        val cipher=
            Cipher.getInstance(
                "AES/CBC/PKCS5Padding"
            )

        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(
                makeKey(key),
                "AES"
            ),
            IvParameterSpec(iv)
        )

        val decrypted=
            cipher.doFinal(
                cipherBytes
            )

        return String(
            decrypted
        )
    }

}
