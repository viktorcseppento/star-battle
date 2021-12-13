package hu.csepviktor.view

import hu.csepviktor.ScoreRow
import hu.csepviktor.StatisticsModel
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import tornadofx.*

class StatsView : View("Star Battle") {
    private val statisticsModel: StatisticsModel by inject()

    private val model = object : ViewModel() {
        var totalGames: IntegerProperty = SimpleIntegerProperty()
        var completedGames: IntegerProperty = SimpleIntegerProperty()
        var scoreList: ObservableList<ScoreRow> = FXCollections.observableList(mutableListOf())
    }

    override fun onDock() {
        model.totalGames.set(statisticsModel.item.totalGames)
        model.completedGames.set(statisticsModel.item.completedGames)
        model.scoreList.clear()
        model.scoreList.addAll(statisticsModel.item.scoreList)
    }

    override val root = vbox(10, Pos.CENTER) {
        hbox(100, Pos.TOP_CENTER) {
            vbox(0, Pos.TOP_CENTER) {
                label("Total Games")
                text {
                    addClass(Styles.variableText)
                    bind(model.totalGames)
                }
            }
            vbox(0, Pos.TOP_CENTER) {
                label("Completed Games")
                text {
                    addClass(Styles.variableText)
                    bind(model.completedGames)
                }
            }
        }
        tableview(model.scoreList) {

            maxWidthProperty().bind(this@vbox.widthProperty() * 0.5)
            readonlyColumn("Date", ScoreRow::date) {
                prefWidthProperty().bind(this@tableview.widthProperty() * 0.5)
            }
            readonlyColumn("Size", ScoreRow::sizeString) {
                prefWidthProperty().bind(this@tableview.widthProperty() * 0.2)
                alignment = Pos.CENTER
            }
            readonlyColumn("Time", ScoreRow::timeString) {
                prefWidthProperty().bind(this@tableview.widthProperty() * 0.2)
            }
        }
        button("Back") {
            action {
                replaceWith<MainView>(ViewTransition.Metro(0.5.seconds))
            }
        }
    }
}