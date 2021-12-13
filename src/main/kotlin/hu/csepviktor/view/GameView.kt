package hu.csepviktor.view

import hu.csepviktor.TimeStringConverter
import hu.csepviktor.controller.GameController
import hu.csepviktor.GameOptionsModel
import hu.csepviktor.GameViewModel
import hu.csepviktor.view.Styles.Companion.variableText
import javafx.geometry.Pos
import javafx.scene.canvas.Canvas
import tornadofx.*

class GameView : View("Star Battle") {

    private val gameOptions: GameOptionsModel by inject()
    private val controller: GameController by inject()
    private lateinit var canvas: Canvas

    private var gameViewModel = GameViewModel()

    override val root = hbox(30, Pos.CENTER) {
        canvas {
            widthProperty().bind(this@hbox.heightProperty())
            heightProperty().bind(this@hbox.heightProperty())
            canvas = this
            setOnMouseClicked { e ->
                controller.onCanvasClicked(e)
            }
            setOnMouseDragged { e ->
                controller.onCanvasDragged(e)
            }
        }
        vbox(10, Pos.CENTER) {
            text {
                addClass(variableText)
                bind(gameViewModel.message)
            }
            button("Change Colors") {
                action {
                    controller.changeColors()
                }
            }
            button("Clear") {
                disableProperty().bind(gameViewModel.disableGameButtons)
                action {
                    controller.clear()
                }
            }
            label("Elapsed Time")
            text {
                addClass(variableText)
                bind(gameViewModel.elapsedTime, converter = TimeStringConverter())
            }
            button("Show Solution") {
                disableProperty().bind(gameViewModel.disableGameButtons)
                action {
                    controller.showSolution()
                }
            }
            button("Exit Game") {
                action {
                    replaceWith<MainView>(ViewTransition.Metro(0.5.seconds).reversed())
                }
            }
        }
    }

    init {
        setInScope(gameViewModel)
    }

    override fun onDock() {
        controller.startGame(gameOptions.item, canvas)
    }

    override fun onUndock() {
        controller.exitGame()
    }
}

