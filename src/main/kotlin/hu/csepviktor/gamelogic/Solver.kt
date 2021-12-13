package hu.csepviktor.gamelogic

class Solver(private val size: Int) {
    fun isUnambiguous(grid: Matrix<Cell>): Boolean {
        var numberOfSolutions = 0

        // Returns true if the algorithm should be stopped
        fun backtrack(grid: Matrix<Cell>): Boolean {
            // Brute force backtrack, start with the smallest region to limit possibilities
            val nextPositions = getSmallestRegionEmptyCells(grid)

            if (nextPositions.isEmpty()) {
                if (grid.count { it.marking == CellType.STAR } == 2 * size) {
                    numberOfSolutions++
                    if (numberOfSolutions > 1)
                        return true
                }
                return false
            }

            for (position in nextPositions) {
                val newGrid = grid.clone()
                setStar(newGrid, position)

                return backtrack(newGrid)
            }

            return false
        }

        backtrack(grid)
        return numberOfSolutions == 1
    }


    private fun setStar(grid: Matrix<Cell>, position: Position) {
        grid[position].marking = CellType.STAR

        position.getNeighbourPositions().forEach {
            grid[it].marking = CellType.EXCLUDE
        }

        if (grid.getColumn(position.column).count { it.marking == CellType.STAR } == 2)
            grid.getColumn(position.column).forEach {
                if (it.marking == CellType.EMPTY) it.marking = CellType.EXCLUDE
            }

        if (grid.getRow(position.row).count { it.marking == CellType.STAR } == 2)
            grid.getRow(position.row).forEach {
                if (it.marking == CellType.EMPTY) it.marking = CellType.EXCLUDE
            }

        val regionCells = grid.filter { it.region == grid[position].region }
        if (regionCells.count { it.marking == CellType.STAR } == 2)
            regionCells.forEach {
                if (it.marking == CellType.EMPTY) it.marking = CellType.EXCLUDE
            }
    }

    private fun getSmallestRegionEmptyCells(grid: Matrix<Cell>): List<Position> {
        val smallestRegion = grid
            .filter { it.marking == CellType.EMPTY }
            .groupingBy { it.region!! }
            .eachCount()
            .minByOrNull { it.value }?.key ?: return emptyList()

        val positionList = mutableListOf<Position>()
        grid.forEachDoubleIndexed { row, col, cell ->
            if (cell.marking == CellType.EMPTY && cell.region == smallestRegion)
                positionList.add(row cross col)
        }

        return positionList
    }

    fun isSolved(grid: Matrix<Cell>): Boolean {
        // Check every region, count the regions with 2 stars
        if (grid.filter { it.marking == CellType.STAR }
                .groupBy { it.region }
                .count { it.value.size == 2 } != size)
            return false

        // Check columns and rows
        (0 until size).forEach { i ->
            if (grid.getColumn(i).count { it.marking == CellType.STAR } != 2)
                return false

            if (grid.getRow(i).count { it.marking == CellType.STAR } != 2)
                return false
        }

        // Check adjacency
        grid.forEachDoubleIndexed { row, col, cell ->
            if (cell.marking == CellType.STAR) {
                (row cross col).getNeighbourPositions().forEach {
                    if (grid[it].marking == CellType.STAR)
                        return false
                }
            }
        }

        return true
    }

    // Direct and diagonal neighbours
    private fun Position.getNeighbourPositions(): List<Position> {
        val positions = mutableListOf<Position>()
        if (row != 0) {
            positions.add(row - 1 cross column)
            if (column != 0)
                positions.add(row - 1 cross column - 1)
            if (column != size - 1)
                positions.add(row - 1 cross column + 1)
        }

        if (row != size - 1) {
            positions.add(row + 1 cross column)
            if (column != 0)
                positions.add(row + 1 cross column - 1)
            if (column != size - 1)
                positions.add(row + 1 cross column + 1)
        }

        if (column != 0)
            positions.add(row cross column - 1)

        if (column != size - 1)
            positions.add(row cross column + 1)

        return positions
    }

    private fun isValid(grid: Matrix<Cell>, newPos: Position): Boolean {
        // Is region full
        if (grid.count { it.marking == CellType.STAR && it.region == grid[newPos].region } == 2)
            return false

        // Is column full
        if (grid.getColumn(newPos.column).count { it.marking == CellType.STAR } == 2)
            return false

        // Is row full
        if (grid.getRow(newPos.row).count { it.marking == CellType.STAR } == 2)
            return false

        // Is neighbour to other star
        grid.forEachDoubleIndexed { row, col, cell ->
            if (cell.marking == CellType.STAR && newPos.isNeighbouring(row cross col))
                return false
        }

        return true
    }
}