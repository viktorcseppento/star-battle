package hu.csepviktor.gamelogic

import hu.csepviktor.GameOptions
import hu.csepviktor.GameViewModel
import hu.csepviktor.ScoreRow
import hu.csepviktor.StatisticsModel
import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import java.text.SimpleDateFormat
import java.util.*

class GameLoop(private var gameViewModel: GameViewModel, private var statisticsModel: StatisticsModel) :
    AnimationTimer() {
    private var lastTime = 0L
    private var isSolved = false

    private lateinit var puzzleMap: PuzzleMap
    private lateinit var drawer: GameDrawer
    private lateinit var gameOptions: GameOptions

    // For mouse events
    private var dragInProgress = false
    private var dragCellType = CellType.EMPTY

    fun start(canvas: Canvas, gameOptions: GameOptions) {
        isSolved = false
        this.gameOptions = gameOptions
        puzzleMap = PuzzleMap(gameOptions.gridSize)
        puzzleMap.generateStars()
        drawer = GameDrawer(canvas, puzzleMap)

        statisticsModel.item.totalGames++
        lastTime = System.nanoTime()
        super.start()
    }

    override fun handle(now: Long) {
        val currentTime = System.nanoTime()
        if (!isSolved)
            gameViewModel.elapsedTime.value += currentTime - lastTime
        lastTime = currentTime

        drawer.draw()
    }


    fun giveUp() {
        puzzleMap.showSolution()
        isSolved = true
        gameViewModel.disableGameButtons.set(true)
        gameViewModel.message.set("Failed :(\n")
    }

    fun changeColors() {
        drawer.changeColors()
    }

    fun clear() {
        puzzleMap.clear()
    }

    override fun stop() {
        drawer.clear()
        super.stop()
    }

    private fun checkSolved() {
        if (puzzleMap.isSolved()) {
            isSolved = true
            gameViewModel.disableGameButtons.set(true)
            gameViewModel.message.set("Success :)\n")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            statisticsModel.item.scoreList.add(
                ScoreRow(gameOptions, gameViewModel.elapsedTime.value, dateFormat.format(Date())))
            statisticsModel.item.completedGames++
        }
    }

    // Called when drag ended or mouse clicked
    fun clicked(row: Int, col: Int) {
        if (dragInProgress) {
            dragInProgress = false
            return
        }
        if (!isSolved) puzzleMap[row, col].run {
            this.marking = when (this.marking) {
                CellType.EMPTY -> CellType.EXCLUDE
                CellType.EXCLUDE -> CellType.STAR
                CellType.STAR -> CellType.EMPTY
                else -> return
            }
            checkSolved()
        }
    }


    fun dragged(row: Int, col: Int) {
        if (!dragInProgress) {
            dragInProgress = true
            dragCellType = puzzleMap[row, col].marking
        }

        if (!isSolved) puzzleMap[row, col].marking =
            when (dragCellType) {
                CellType.EMPTY -> CellType.EXCLUDE
                CellType.EXCLUDE -> CellType.EMPTY
                else -> return
            }
    }
}