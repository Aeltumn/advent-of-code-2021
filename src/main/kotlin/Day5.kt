import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.sign

/**
 * Day 5 - Hydrothermal Venture
 */
fun main() {
    val input = File("day5").readLines()

    puzzle1(input)
    puzzle2(input)
}

private fun createBoard(input: List<String>, includeDiagonal: Boolean): Array<Array<AtomicInteger>> {
    val boardSize = 1000
    val board = Array(boardSize) { Array(boardSize) { AtomicInteger() } }

    for (line in input) {
        val assignment = line.split(" -> ")
        val from = assignment[0].split(",")
        val to = assignment[1].split(",")

        val x1 = from[0].toInt()
        val y1 = from[1].toInt()
        val x2 = to[0].toInt()
        val y2 = to[1].toInt()

        if (!includeDiagonal) {
            // Only consider horizontal and vertical lines if we're not including diagonals
            if (!(x1 == x2 || y1 == y2)) continue
        }

        // Write the lines onto the board
        var x = x1
        var y = y1
        while (true) {
            board[y][x].incrementAndGet()

            // If both values are at the end, break the loop
            if (x == x2 && y == y2) break

            // Increment the values on each axis until we've reached the end
            if (x != x2) x += (x2 - x).sign
            if (y != y2) y += (y2 - y).sign
        }
    }
    return board
}

private fun puzzle1(input: List<String>) {
    val board = createBoard(input, false)
    println("P1 Points with >= 2: ${board.sumOf { row -> row.count { it.get() >= 2 } }}")
}

private fun puzzle2(input: List<String>) {
    val board = createBoard(input, true)
    println("P2 Points with >= 2: ${board.sumOf { row -> row.count { it.get() >= 2 } }}")
}