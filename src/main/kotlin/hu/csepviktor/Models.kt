package hu.csepviktor

import tornadofx.*

// Expandable e.g. with difficulty
@kotlinx.serialization.Serializable
data class GameOptions(var gridSize: Int = 0)

@kotlinx.serialization.Serializable
data class ScoreRow(val gameOptions: GameOptions, val elapsedTime: Long, val date: String) {
    val timeString = TimeStringConverter().toString(elapsedTime)
    val sizeString = "${gameOptions.gridSize}x${gameOptions.gridSize}"
}

@kotlinx.serialization.Serializable
data class Statistics(
    var totalGames: Int = 0,
    var completedGames: Int = 0,
    var scoreList: MutableList<ScoreRow> = mutableListOf(),
)

class GameOptionsModel(gameOptions: GameOptions) : ItemViewModel<GameOptions>(gameOptions)

class GameViewModel : ViewModel() {
    val disableGameButtons = booleanProperty(false)

    // Nanoseconds
    val elapsedTime = longProperty(0)

    val message = stringProperty("")

    fun reset() {
        message.set("Double click\nto place star")
        disableGameButtons.set(false)
        elapsedTime.set(0)
    }
}

class StatisticsModel(statistics: Statistics) : ItemViewModel<Statistics>(statistics)