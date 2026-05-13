package com.rambo.ramcryptr

object MatrixBitstream {

    fun stringToBits(
        text: String
    ): String {

        val payloadBits =
            buildString {

                text.toByteArray().forEach { byte ->

                    append(
                        byte.toInt()
                            .and(0xFF)
                            .toString(2)
                            .padStart(8, '0')
                    )
                }
            }

        val lengthHeader =
            payloadBits.length
                .toString(2)
                .padStart(16, '0')

        return (
            lengthHeader +
            payloadBits
        )
    }

    fun bitsToString(
        bits: String
    ): String {

        return try {

            if (bits.length < 16) {
                return ""
            }

            val payloadLength =
                bits.substring(0, 16)
                    .toInt(2)

            val payloadBits =
                bits.substring(
                    16,
                    16 + payloadLength
                )

            val bytes =
                payloadBits.chunked(8)
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
