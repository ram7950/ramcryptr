package com.rambo.ramcryptr

object MatrixBitstream {

    fun stringToBits(
        text: String
    ): String {

        return buildString {

            text.toByteArray().forEach { byte ->

                append(
                    byte.toInt()
                        .and(0xFF)
                        .toString(2)
                        .padStart(8, '0')
                )
            }
        }
    }

    fun bitsToString(
        bits: String
    ): String {

        return try {

            val bytes =
                bits.chunked(8)
                    .map {

                        it.toInt(2)
                            .toByte()
                    }
                    .toByteArray()

            String(bytes)

        } catch (_: Exception) {

            ""
        }
    }
}
