package com.rambo.ramcryptr

object MatrixRecoveryPipeline {

    fun recoverChannel(
        matrix: String
    ): Channel? {

        return try {

            val bits =
                MatrixPayloadExtractor
                    .extractBits(matrix)

            val payload =
                MatrixBitstream
                    .bitsToString(bits)

            MatrixPayloadCodec
                .decodeChannel(payload)

        } catch (_: Exception) {

            null
        }
    }
}
