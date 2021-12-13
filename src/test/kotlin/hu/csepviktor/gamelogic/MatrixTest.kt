package hu.csepviktor.gamelogic

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class MatrixTest {

    /*
    0, 1, 2
    3, 4, 5
    6, 7, 8
    9, 10, 11,
    12, 13, 14
     */
    private val matrix: Matrix<Int> = Matrix(5, 3) { it }

    @Test
    fun getRow() {
        assertEquals(listOf(12, 13, 14), matrix.getRow(4))
    }

    @Test
    fun getColumn() {
        assertEquals(listOf(1, 4, 7, 10, 13), matrix.getColumn(1))
    }

    @Test
    fun get() {
        assertEquals(14, matrix[14])
    }

    @Test
    fun testGet() {
        assertEquals(7, matrix[2, 1])
    }

    @Test
    fun testGet1() {
        assertEquals(listOf(2, 5, 8), matrix[0..2, 2])
    }

    @Test
    fun testGet2() {
        assertEquals(listOf(9, 10), matrix[3, 0..1])
    }

    @Test
    fun testGet3() {
        assertEquals(listOf(6, 7, 9, 10), matrix[2..3, 0..1])
    }
}