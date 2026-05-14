package com.rambo.ramcryptr

import android.graphics.Bitmap
import android.graphics.Color

object MatrixBitmapDecoder {

    fun decode(
        bitmap: Bitmap,
        gridSize: Int = 64
    ): String {

        val cellSize =
            bitmap.width / gridSize

        val builder =
            StringBuilder()

        for (row in 0 until gridSize) {

            for (col in 0 until gridSize) {

                val centerX =
                    col * cellSize +
                    cellSize / 2

                val centerY =
                    row * cellSize +
                    cellSize / 2

                var totalRed = 0
                var totalGreen = 0
                var totalBlue = 0
                var samples = 0

                for (offsetY in -2..2) {

                    for (offsetX in -2..2) {

                        val sampleX =
                            (centerX + offsetX)
                                .coerceIn(
                                    0,
                                    bitmap.width - 1
                                )

                        val sampleY =
                            (centerY + offsetY)
                                .coerceIn(
                                    0,
                                    bitmap.height - 1
                                )

                        val pixel =
                            bitmap.getPixel(
                                sampleX,
                                sampleY
                            )

                        totalRed +=
                            Color.red(pixel)

                        totalGreen +=
                            Color.green(pixel)

                        totalBlue +=
                            Color.blue(pixel)

                        samples++
                    }
                }

                val avgRed =
                    totalRed / samples

                val avgGreen =
                    totalGreen / samples

                val avgBlue =
                    totalBlue / samples

                val ch =
                    when {

                        (
                            avgGreen > 160 &&
                            avgRed < 140
                        ) -> '▓'

                        (
                            kotlin.math.abs(
                                avgRed - avgGreen
                            ) < 25 &&
                            kotlin.math.abs(
                                avgGreen - avgBlue
                            ) < 25
                        ) -> '▒'

                        else -> '░'
                    }

                builder.append(ch)
            }

            builder.append("\n")
        }

        return builder.toString()
    }

    private fun isGreen(
        color: Int
    ): Boolean {

        return (
            Color.green(color) > 180 &&
            Color.red(color) < 100
        )
    }

    private fun isGray(
        color: Int
    ): Boolean {

        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        return (
            kotlin.math.abs(r - g) < 20 &&
            kotlin.math.abs(g - b) < 20 &&
            r in 60..180
        )
    }
}
