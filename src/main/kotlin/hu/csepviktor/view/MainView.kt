package hu.csepviktor.view

import hu.csepviktor.controller.StatisticsController
import hu.csepviktor.GameOptions
import hu.csepviktor.GameOptionsModel
import javafx.geometry.Pos
import tornadofx.*

class MainView : View("Star Battle") {

    private val gameOptions: GameOptionsModel = GameOptionsModel(GameOptions())

    private val gameView: GameView by inject()

    private val statisticsController: StatisticsController by inject()

    private val exitAction = {
        statisticsController.writeStatistics()
    }

    init {
        val statistics = statisticsController.readStatistics()

        setInScope(gameOptions)
        setInScope(statistics)

        primaryStage.setOnCloseRequest { exitAction() }

    }

    override val root = vbox(20, Pos.CENTER) {
        label("Star Battle") {
            id = "mainTitle"
        }
        vbox(10, Pos.CENTER) {
            label("Select Grid")
            hbox(10, Pos.CENTER) {
                togglegroup {
                    selectedToggleProperty().addListener { _, old, new ->
                        if (new == null)
                            old.isSelected = true
                    }
                    selectedValueProperty<Int>()
                        .onChange { gameOptions.item.gridSize = it ?: 7 }

                    togglebutton("8x8", value = 8)
                    togglebutton("9x9", value = 9)
                    togglebutton("10x10", value = 10)
                    togglebutton("11x11", value = 11)
                }
            }
        }
        button("Start Game") {
            action {
                replaceWith(gameView, ViewTransition.Metro(0.5.seconds))
            }
        }
        button("Stats") {
            action {
                replaceWith<StatsView>(ViewTransition.Metro(0.5.seconds).reversed())
            }
        }
        button("Quit") {
            action {
                exitAction()
                primaryStage.close()
            }
        }
    }
}
