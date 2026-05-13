package com.rambo.ramcryptr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

object MatrixBitmapGenerator {

    fun generate(
        matrix: String,
        cellSize: Int = 48
    ): Bitmap {

        val rows =
            matrix.trim()
                .split("\n")

        val gridSize =
            rows.size

        val bitmap =
            Bitmap.createBitmap(
                gridSize * cellSize,
                gridSize * cellSize,
                Bitmap.Config.ARGB_8888
            )

        val canvas =
            Canvas(bitmap)

        canvas.drawColor(
            Color.BLACK
        )

        val paint =
            Paint()

        rows.forEachIndexed { rowIndex, row ->

            row.forEachIndexed { colIndex, ch ->

                paint.color =
                    when (ch) {

                        '▓' -> Color.GREEN

                        '▒' -> Color.DKGRAY

                        else -> Color.BLACK
                    }

                canvas.drawRect(
                    (
                        colIndex * cellSize
                    ).toFloat(),

                    (
                        rowIndex * cellSize
                    ).toFloat(),

                    (
                        (colIndex + 1) * cellSize
                    ).toFloat(),

                    (
                        (rowIndex + 1) * cellSize
                    ).toFloat(),

                    paint
                )
            }
        }

        return bitmap
    }
}
