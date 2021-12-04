import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Paths

/** A point on a board. X is right. Y is up. */
data class Point(val x: Int, val y: Int)

/** The bingo boards. */
data class Board(val index: Int, val fields: Map<Point, Int>, val marked: List<Point> = listOf()) {

    /** The sum of all unmarked values on this board. */
    val score: Int
        get() = fields.filterKeys { it !in marked }.values.sum()

    /** Whether this board is complete. */
    val complete: Boolean
        get() {
            rows@for (row in 0..4) {
                for (x in 0..4) {
                    // If this point is not marked, discard the row
                    if (Point(x, row) !in marked) continue@rows
                }

                // Every point in the row is marked, bingo!
                return true
            }
            columns@for (column in 0..4) {
                for (y in 0..4) {
                    // If this point is not marked, discard the column
                    if (Point(column, y) !in marked) continue@columns
                }

                // Every point in the column is marked, bingo!
                return true
            }
            return false
        }

    constructor(index: Int, reader: BufferedReader) : this(index, reader.readBingoBoard())

    /** Returns this board with the given number marked off. */
    fun mark(number: Int): Board = copy(marked = marked + fields.filterValues { it == number }.keys)
}

/** Reads a bingo board from a buffered reader. */
private fun BufferedReader.readBingoBoard(): Map<Point, Int> {
    val board = mutableMapOf<Point, Int>()
    for (y in 4 downTo 0) {
        val line = readLine()
        val numbers = line.split(" ").filter { it.isNotBlank() }
        require(numbers.size == 5) { "Line had more than five numbers, $line had ${numbers.size}" }
        for (x in 0..4) {
            board[Point(x, y)] = numbers[x].toInt()
        }
    }
    return board
}

/**
 * Day 4 - Giant Squid
 */
fun main() {
    val inputs = mutableListOf<Int>()
    val boards = mutableListOf<Board>()

    BufferedReader(FileReader(Paths.get("day4").toFile())).use {
        // The first line has inputs
        val inputLine = it.readLine()
        for (num in inputLine.split(",")) {
            inputs += num.toInt()
        }

        // After that we read all boards
        var index = 0
        while (it.ready()) {
            // Read empty line
            it.readLine()

            // Read board
            boards += Board(index++, it)
        }
    }

    puzzle1(inputs, boards)
    puzzle2(inputs, boards)
}

fun puzzle1(inputs: List<Int>, boards: List<Board>) {
    val remainingInputs = inputs.toMutableList()

    var boardsInPlay = boards
    var winningBoard: Board? = null
    var lastDrawnNumber = 0

    // Draw numbers and update the boards
    while (winningBoard == null && remainingInputs.isNotEmpty()) {
        lastDrawnNumber = remainingInputs.removeFirst()
        boardsInPlay = boardsInPlay.map { it.mark(lastDrawnNumber) }

        // If any board won, make it the winning board
        for (board in boardsInPlay) {
            if (board.complete) {
                require(winningBoard == null) { "Two boards won at once!" }
                winningBoard = board
            }
        }
    }

    // Announce the winning board
    println("P1 Board ${winningBoard?.index} won. Score: ${(winningBoard?.score ?: 0)} * $lastDrawnNumber = ${(winningBoard?.score ?: 0) * lastDrawnNumber}")
}

fun puzzle2(inputs: List<Int>, boards: List<Board>) {
    val remainingInputs = inputs.toMutableList()

    var boardsInPlay = boards
    var winningBoard: Board? = null
    var lastDrawnNumber = 0

    // Draw numbers and update the boards
    while (remainingInputs.isNotEmpty()) {
        lastDrawnNumber = remainingInputs.removeFirst()
        boardsInPlay = boardsInPlay.map { it.mark(lastDrawnNumber) }

        // If there's only one board left and it's complete, that's the winner
        if (boardsInPlay.size == 1 && boardsInPlay[0].complete) {
            winningBoard = boardsInPlay[0]
            break
        }

        // Remove all complete boards for the next number, we want the last complete one
        boardsInPlay = boardsInPlay.filter { !it.complete }
    }

    // Announce the winning board
    println("P2 Board ${winningBoard?.index} won last. Score: ${(winningBoard?.score ?: 0)} * $lastDrawnNumber = ${(winningBoard?.score ?: 0) * lastDrawnNumber}")
}