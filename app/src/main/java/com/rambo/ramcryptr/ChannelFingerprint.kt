package com.rambo.ramcryptr

import java.security.MessageDigest

object ChannelFingerprint {

    fun generate(
        seed: String
    ): String {

        return try {

            val hash =
                MessageDigest
                    .getInstance("SHA-256")
                    .digest(
                        seed.toByteArray()
                    )

            hash.joinToString("") {
                "%02X".format(it)
            }
            .take(4)

        } catch (_: Exception) {

            "0000"
        }
    }
}
