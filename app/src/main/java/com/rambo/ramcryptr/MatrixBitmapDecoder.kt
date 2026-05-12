package com.rambo.ramcryptr

import android.graphics.Bitmap
import android.graphics.Color

object MatrixBitmapDecoder {

    fun decode(
        bitmap: Bitmap,
        gridSize: Int = 18
    ): String {

        val cellSize =
            bitmap.width / gridSize

        val builder =
            StringBuilder()

        for (row in 0 until gridSize) {

            for (col in 0 until gridSize) {

                val x =
                    col * cellSize +
                    cellSize / 2

                val y =
                    row * cellSize +
                    cellSize / 2

                val pixel =
                    bitmap.getPixel(x, y)

                val ch =
                    when {

                        isGreen(pixel) -> '▓'

                        isGray(pixel) -> '▒'

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
