package hu.csepviktor.gamelogic

enum class CellType {
    EMPTY, STAR, EXCLUDE
}

data class Cell(
    var marking: CellType = CellType.EMPTY,
    var region: Int? = null,
    var isStar: Boolean = false, // Used internally (generation, solution showing)
)