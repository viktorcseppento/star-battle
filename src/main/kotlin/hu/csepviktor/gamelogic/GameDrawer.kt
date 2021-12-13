package hu.csepviktor.gamelogic

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.random.Random

class GameDrawer(canvas: Canvas, private val map: PuzzleMap) {
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D
    private val width = canvas.width
    private val height = canvas.height
    private val cellWidth = (width - 2 * canvasMargin) / map.size
    private val cellHeight = (height - 2 * canvasMargin) / map.size

    private var colors: List<Color> = randomColors()

    companion object {
        const val alpha = 0.5
        const val boldStroke = 5.0
        const val normalStroke = 2.0
        const val canvasMargin = boldStroke / 2
    }

    fun changeColors() {
        colors = randomColors()
    }

    private fun randomColors(): List<Color> {
        val colors = mutableListOf<Color>()
        repeat(map.size) {
            colors.add(Color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble(), alpha))
        }
        return colors
    }


    fun draw() {
        clear()
        // Draw cells
        for (i in 0 until map.size) {
            for (j in 0 until map.size) {
                // Draw region colors
                // No null check, region is surely generated
                graphicsContext.drawRect(
                    colors[map[i, j].region!!],
                    j * cellWidth + canvasMargin,
                    i * cellHeight + canvasMargin,
                    cellWidth,
                    cellHeight)

                // Draw cell content
                graphicsContext.drawShape(cellWidth / 25, map[i, j].marking,
                    j * cellWidth + canvasMargin,
                    i * cellHeight + canvasMargin,
                    cellWidth,
                    cellHeight)
            }
        }

        // Draw borders
        // Left border
        graphicsContext.drawStroke(boldStroke, canvasMargin, 0.0, canvasMargin, height)
        // Top Border
        graphicsContext.drawStroke(boldStroke, 0.0, canvasMargin, width, canvasMargin)

        for (i in 0 until map.size) {
            for (j in 0 until map.size) {
                // Right borders
                var lineWidth =
                    if (j == map.size - 1 || map[i, j].region != map[i, j + 1].region)
                        boldStroke
                    else
                        normalStroke

                graphicsContext.drawStroke(lineWidth,
                    (j + 1) * cellWidth + canvasMargin, i * cellHeight + canvasMargin,
                    (j + 1) * cellWidth + canvasMargin, (i + 1) * cellHeight)

                // Bottom borders
                lineWidth =
                    if (i == map.size - 1 || map[i, j].region != map[i + 1, j].region)
                        boldStroke
                    else
                        normalStroke

                graphicsContext.drawStroke(lineWidth,
                    j * cellWidth + canvasMargin, (i + 1) * cellHeight + canvasMargin,
                    (j + 1) * cellWidth, (i + 1) * cellHeight + canvasMargin)
            }
        }
    }

    fun clear() {
        graphicsContext.clear()
    }


    private fun GraphicsContext.clear() {
        clearRect(0.0, 0.0, width, height)
    }

}

private fun GraphicsContext.drawStroke(strokeWidth: Double, x1: Double, y1: Double, x2: Double, y2: Double) {
    stroke = Color.BLACK
    lineWidth = strokeWidth
    strokeLine(x1, y1, x2, y2)
}

private fun GraphicsContext.drawRect(color: Color, x: Double, y: Double, width: Double, height: Double) {
    fill = color
    fillRect(x, y, width, height)
}

private fun GraphicsContext.drawShape(
    lineWidth: Double, type: CellType, x: Double,
    y: Double,
    width: Double,
    height: Double,
) {
    when (type) {
        CellType.STAR -> {
            drawStroke(lineWidth,
                x + width / 3,
                y + height / 2,
                x + 2 * width / 3,
                y + height / 2)

            drawStroke(lineWidth,
                x + width / 2,
                y + height / 3,
                x + width / 2,
                y + 2 * height / 3)

            drawStroke(lineWidth,
                x + width / 3,
                y + height / 3,
                x + 2 * width / 3,
                y + 2 * height / 3)

            drawStroke(lineWidth,
                x + 2 * width / 3,
                y + height / 3,
                x + width / 3,
                y + 2 * height / 3)
        }
        CellType.EXCLUDE -> {
            drawStroke(lineWidth,
                x + width / 3,
                y + height / 2,
                x + 2 * width / 3,
                y + height / 2)
        }
        else -> return
    }
}
