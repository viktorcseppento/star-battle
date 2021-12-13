package hu.csepviktor.gamelogic


/*
Grid generation has 2 steps:
   - Star placement
   - Splitting to regions
*/

data class GenerateResult(val grid: Matrix<Cell>, val regions: List<Region>)

class Generator(private val size: Int) {

    fun generateStars(): Result<GenerateResult> {
        var grid: Matrix<Cell>
        val regions: List<Region>

        mainLoop@ while (true) {
            while (true) {
                val generateRowsResult = generateRows()
                if (generateRowsResult.isSuccess) {
                    grid = generateRowsResult.getOrThrow()
                    break
                }
            }

            // If can't split to regions in so many trials redo the stars
            for (i in 0 until 2000) {
                val splitRegionResult = splitToRegions(grid)
                if (splitRegionResult.isSuccess) {
                    regions = splitRegionResult.getOrThrow()
                    break@mainLoop
                }
            }
        }

        return Result.success(GenerateResult(grid, regions))
    }

    // Place random 2 stars every row without conflict
    private fun generateRows(): Result<Matrix<Cell>> {
        val grid = Matrix(size, size) { Cell() }
        val globalAvailable = (0 until size).toMutableSet()
        var restrictedFromPrev = listOf<Int>()
        val partialColumns = mutableSetOf<Int>()
        for (row in 0 until size) {
            // Remove first 6 elements after second row (remove first row's restrictions)
            if (row > 1) restrictedFromPrev = restrictedFromPrev.subList(6, restrictedFromPrev.size)
            var available = globalAvailable subtract restrictedFromPrev.toSet()

            if (available.isEmpty())
                return Result.failure(Exception())

            val firstCol = available.random()
            grid[row, firstCol].isStar = true

            // If already in partial then not available for upcoming rows
            if (!partialColumns.add(firstCol)) globalAvailable.remove(firstCol)

            restrictedFromPrev = restrictedFromPrev + ((firstCol - 1)..(firstCol + 1))
            available = globalAvailable subtract restrictedFromPrev.toSet()

            // If it can only be placed next to the other
            if (available.isEmpty()) {
                return Result.failure(Exception())
            }
            val secondCol = available.random()
            grid[row, secondCol].isStar = true
            restrictedFromPrev = restrictedFromPrev + ((secondCol - 1)..(secondCol + 1))
            if (!partialColumns.add(secondCol)) globalAvailable.remove(secondCol)
        }

        return Result.success(grid)
    }

    private fun splitToRegions(grid: Matrix<Cell>): Result<List<Region>> {
        val remainingStars = mutableListOf<Position>()
        grid.forEachDoubleIndexed { row, col, cell ->
            if (cell.isStar) remainingStars.add(row cross col)
        }

        val regions = mutableListOf<Region>()
        val excludePositions = mutableListOf<Position>()
        excludePositions.addAll(remainingStars)
        while (remainingStars.isNotEmpty()) {
            val start = remainingStars.random()
            remainingStars.remove(start)
            excludePositions.remove(start)
            val finish = start.closest(remainingStars)
            remainingStars.remove(finish)
            excludePositions.remove(finish)
            val pathResult = randomOpenPath(start, finish, excludePositions)
            val newRegion = Region(pathResult.getOrElse { return Result.failure(Exception()) })
            regions.add(newRegion)
            excludePositions.addAll(newRegion.positions)
        }

        regions.forEachIndexed { i, region ->
            region.positions.forEach { pos ->
                grid[pos].region = i
            }
        }

        // Check cells with no region
        while (grid.any { it.region == null }) {
            grid.forEachDoubleIndexed { row, col, cell ->
                if (cell.region == null) {
                    val adjacentsWithRegion =
                        grid.getAdjacents(row, col).filter { it.region != null }
                    if (adjacentsWithRegion.isNotEmpty())
                        cell.region = adjacentsWithRegion.random().region
                }
            }
        }

        return Result.success(regions)
    }

    // Random path from start to finish excluding positions from a list
    private fun randomOpenPath(
        start: Position,
        finish: Position,
        exclude: List<Position>,
    ): Result<List<Position>> {
        val positions = mutableListOf<Position>()
        val maxTrials = 20 * size

        positions.add(start)
        var currentPos = start
        for (i in 0 until maxTrials) {
            val nextPos = currentPos.randomAdjacent()
            if (nextPos in exclude) continue
            if (nextPos !in positions) positions.add(nextPos)
            if (nextPos == finish) return Result.success(positions.toList())
            currentPos = nextPos
        }

        return Result.failure(Exception())
    }

    private fun Position.randomAdjacent(): Position {
        val availablePositions = mutableListOf<Position>()
        if (row != 0) availablePositions.add(row - 1 cross column)
        if (row != size - 1) availablePositions.add(row + 1 cross column)

        if (column != 0) availablePositions.add(row cross column - 1)
        if (column != size - 1) availablePositions.add(row cross column + 1)
        return availablePositions.random()
    }

    private fun <T> Matrix<T>.getAdjacents(row: Int, column: Int): List<T> {
        val adjacentList = mutableListOf<T>()
        if (row != 0) adjacentList.add(this[row - 1, column])
        if (row != height - 1) adjacentList.add(this[row + 1, column])

        if (column != 0) adjacentList.add(this[row, column - 1])
        if (column != size - 1) adjacentList.add(this[row, column + 1])
        return adjacentList
    }
}
