package com.rambo.ramcryptr

object MatrixPayloadExtractor {

    fun extractBits(
        matrix: String
    ): String {

        val rows =
            matrix.trim()
                .split("\n")

        val gridSize =
            rows.size

        val bits =
            StringBuilder()

        for (row in 0 until gridSize) {

            for (col in 0 until gridSize) {

                val isFinder =
                    (
                        (row < 4 && col < 4) ||
                        (row < 4 && col > 13) ||
                        (row > 13 && col < 4)
                    )

                val isReserved =
                    (
                        row == 8 ||
                        col == 8
                    )

                if (
                    isFinder ||
                    isReserved
                ) {
                    continue
                }

                val ch =
                    rows[row][col]

                when (ch) {

                    '▓' -> bits.append("1")

                    '░' -> bits.append("0")
                }
            }
        }

        return bits.toString()
    }
}
