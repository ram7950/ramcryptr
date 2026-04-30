package com.rambo.ramcryptr

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object FileCryptoManager {

    private const val PREFIX = "RAMCRYPT_V2|"
    private const val KEY = "12345678901234567890123456789012"

    private fun makeKey(): ByteArray {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(KEY.toByteArray())
    }

    // ---------------- ENCRYPT ----------------

    fun encryptFile(
        input: File,
        output: File,
        ext: String,
        mime: String
    ) {

        val cleanExt = ext.trim().lowercase()

        val header =
            "${PREFIX}ext=${cleanExt}|mime=${mime}\n"

        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

        val cipher =
            Cipher.getInstance("AES/CBC/PKCS5Padding")

        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(makeKey(), "AES"),
            IvParameterSpec(iv)
        )

        FileOutputStream(output).use { fos ->

            fos.write(header.toByteArray())
            fos.write(iv)

            CipherOutputStream(fos, cipher).use { cos ->

                FileInputStream(input).use { fis ->
                    fis.copyTo(cos)
                }

            }
        }
    }

    // ---------------- DECRYPT ----------------

    fun decryptFile(
        input: File,
        output: File
    ): Pair<String, String> {

        FileInputStream(input).use { fis ->

            val headerBuilder = StringBuilder()

            while (true) {
                val ch = fis.read()
                if (ch == -1 || ch.toChar() == '\n') break
                headerBuilder.append(ch.toChar())
            }

            val header = headerBuilder.toString()

            if (!header.startsWith(PREFIX)) {
                throw Exception("Invalid encrypted file")
            }

            val parts = header.split("|")

            var ext = "dat"
            var mime = "*/*"

            for (p in parts) {

                val clean = p.trim()

                if (clean.startsWith("ext=")) {
                    ext = clean
                        .removePrefix("ext=")
                        .trim()
                        .lowercase()
                }

                if (clean.startsWith("mime=")) {
                    mime = clean
                        .removePrefix("mime=")
                        .trim()
                }
            }

            // FINAL EXT SAFETY
            ext = normalizeExt(ext)

            val iv = ByteArray(16)
            fis.read(iv)

            val cipher =
                Cipher.getInstance("AES/CBC/PKCS5Padding")

            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(makeKey(), "AES"),
                IvParameterSpec(iv)
            )

            CipherInputStream(fis, cipher).use { cis ->

                FileOutputStream(output).use { fos ->
                    cis.copyTo(fos)
                }

            }

            return Pair(ext, mime)
        }
    }

    // ---------------- EXT NORMALIZER ----------------

    private fun normalizeExt(extRaw: String): String {

        var ext = extRaw.trim().lowercase()

        // अगर mime जैसा आया (image/jpeg)
        if (ext.contains("/")) {
            val after = ext.substringAfter("/")
            if (after.isNotEmpty()) ext = after
        }

        return when (ext) {
            "jpeg" -> "jpg"
            "jpg", "png", "pdf", "mp4", "txt" -> ext
            else -> "dat"
        }
    }
}
