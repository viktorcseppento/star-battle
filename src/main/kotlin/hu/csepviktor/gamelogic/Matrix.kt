package hu.csepviktor.gamelogic

class Matrix<T>(val height: Int, val width: Int, private val data: Array<T>) : Iterable<T> {
    companion object {
        inline operator fun <reified T> invoke(height: Int, width: Int, init: (Int) -> T): Matrix<T> {
            val data = Array(height * width) { init(it) }
            return Matrix(height, width, data)
        }

        inline operator fun <reified T> invoke(height: Int, width: Int, defaultValue: T): Matrix<T> {
            val data = Array(height * width) { defaultValue }
            return Matrix(height, width, data)
        }
    }

    private val columnCheck = { column: Int ->
        require(column in 0 until width)
    }

    private val rowCheck: (Int) -> Unit = { row: Int ->
        require(row in 0 until height)
    }

    fun getRow(row: Int): List<T> {
        rowCheck(row)
        return data.slice(row * width until (row + 1) * width)
    }

    fun getColumn(column: Int): List<T> {
        columnCheck(column)
        return data.slice((0 until height).map { row -> row * width + column })
    }

    operator fun get(index: Int): T {
        require(index in 0 until height * width)
        return data[index]
    }

    operator fun get(indices: IntRange): List<T> {
        require(indices.first in 0 until height * width)
        require(indices.last in 0 until height * width)
        return data.slice(indices)
    }

    operator fun set(index: Int, value: T) {
        require(index in 0 until height * width)
        data[index] = value
    }

    operator fun get(row: Int, column: Int): T {
        rowCheck(row)
        columnCheck(column)
        return data[row * width + column]
    }

    operator fun get(rows: IntRange, column: Int): List<T> {
        columnCheck(column)
        rowCheck(rows.first)
        rowCheck(rows.last)
        return getColumn(column).slice(rows)
    }

    operator fun get(row: Int, columns: IntRange): List<T> {
        rowCheck(row)
        columnCheck(columns.first)
        columnCheck(columns.last)
        return getRow(row).slice(columns)
    }

    operator fun get(rows: IntRange, columns: IntRange): List<T> {
        rowCheck(rows.first)
        rowCheck(rows.last)
        columnCheck(columns.first)
        columnCheck(columns.last)
        return mutableListOf<T>().apply {
            rows.forEach { addAll(get(it, columns)) }
        }
    }

    inline fun asciiDraw(stringSelector: (T) -> String) {
        (0 until height).forEach { row ->
            (0 until width).forEach { col ->
                print(stringSelector(get(row, col)))
            }
            println()
        }
    }

    override fun iterator(): Iterator<T> {
        return data.iterator()
    }

}

inline fun <T> Matrix<T>.forEachDoubleIndexed(action: (row: Int, col: Int, T) -> Unit) {
    (0 until height).forEach { row ->
        (0 until width).forEach { col ->
            action(row, col, this[row, col])
        }
    }
}