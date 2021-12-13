package hu.csepviktor.controller

import hu.csepviktor.GameOptions
import hu.csepviktor.GameViewModel
import hu.csepviktor.StatisticsModel
import hu.csepviktor.gamelogic.GameLoop
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import tornadofx.*

class GameController : Controller() {
    private val gameViewModel: GameViewModel by inject()
    private val statisticsModel: StatisticsModel by inject()

    private val gameLoop = GameLoop(gameViewModel, statisticsModel)

    private lateinit var canvas: Canvas
    private var gridSize: Int = 1

    fun startGame(gameOptions: GameOptions, canvas: Canvas) {
        gameViewModel.reset()
        this.canvas = canvas
        this.gridSize = gameOptions.gridSize
        // Set game drawer and start the game with timer
        gameLoop.start(canvas, gameOptions)
    }

    fun exitGame() {
        gameLoop.stop()
        gameViewModel.reset()
    }


    fun showSolution() {
        gameLoop.giveUp()
    }

    fun changeColors() {
        gameLoop.changeColors()
    }

    fun clear() {
        gameLoop.clear()
    }

    fun onCanvasClicked(e: MouseEvent?) {
        if (e == null)
            return

        gameLoop.clicked((e.y / canvas.height * gridSize).toInt(), (e.x / canvas.width * gridSize).toInt())
    }

    fun onCanvasDragged(e: MouseEvent?) {
        if (e == null)
            return

        if (e.x in 0.0..canvas.width && e.y in 0.0..canvas.height)
            gameLoop.dragged((e.y / canvas.height * gridSize).toInt(), (e.x / canvas.width * gridSize).toInt())
    }
}

