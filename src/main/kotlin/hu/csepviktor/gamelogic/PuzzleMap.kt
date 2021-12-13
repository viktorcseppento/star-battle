package hu.csepviktor.gamelogic

import kotlin.math.abs

data class Position(val row: Int, val column: Int) {
    private fun distance(other: Position): Int {
        return abs(this.row - other.row) + abs(this.column - other.column)
    }

    fun closest(list: Iterable<Position>): Position {
        return list.minByOrNull { distance(it) }!!
    }

    // Directly or diagonally
    fun isNeighbouring(other: Position): Boolean {
        val distance = distance(other)
        if (distance > 2)
            return false

        if (distance == 1 && (row == other.row || column == other.column))
            return false

        return true
    }
}

// Creates a position from row and column
infix fun Int.cross(column: Int) = Position(this, column)

operator fun <T> Matrix<T>.get(pos: Position): T {
    return this[pos.row, pos.column]
}

data class Region(val positions: List<Position>)

class PuzzleMap(val size: Int) {
    private var grid: Matrix<Cell> = Matrix(size, size) { Cell() }
    private var regions: List<Region> = emptyList()
    private val generator: Generator = Generator(size)
    private val solver: Solver = Solver(size)

    // For test purposes
    private fun asciiDrawStars() {
        grid.asciiDraw { if (it.isStar) "* " else "- " }
        println()
    }

    // For test purposes
    private fun asciiDrawRegions() {
        grid.asciiDraw { "${it.region} " }
        println()
    }

    fun generateStars() {
        do {
            val (grid, regions) = generator.generateStars().getOrThrow()
            this.grid = grid
            this.regions = regions
        } while (!solver.isUnambiguous(grid))
        clear()
    }

    operator fun get(row: Int, column: Int): Cell {
        return grid[row, column]
    }

    fun isSolved(): Boolean {
        return solver.isSolved(grid)
    }

    fun clear() {
        grid.forEach { it.marking = CellType.EMPTY }
    }

    fun showSolution() {
        grid.forEach {
            it.marking = if (it.isStar) {
                CellType.STAR
            } else {
                CellType.EMPTY
            }
        }
    }
}

fun Matrix<Cell>.clone(): Matrix<Cell> {
    val matrix = Matrix<Cell>(height, width, Cell())
    this.forEachIndexed { i, cell -> matrix[i] = cell.copy() }
    return matrix
}