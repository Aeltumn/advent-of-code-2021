import java.io.File

/**
 * Day 8 - Seven Segment Search
 */
fun main() {
    var puzzle1 = 0
    var puzzle2 = 0L
    File("day8").readLines().forEach { line ->
        val input = line.split(" | ")
        val problem = input[1].split(" ").map { str -> str.toCharArray().sorted() }

        // Include the problem in the patterns we get more to work with
        val patterns = input[0].split(" ").map { str -> str.toCharArray().sorted() } + problem
        val solutions = mutableMapOf<Int, List<Char>>()

        /** Filters out all used patterns and those of the wrong size. */
        fun withSize(size: Int): List<List<Char>> =
            patterns.filter { it !in solutions.values && it.size == size }

        // Puzzle 1 counts up the amount of 1's, 4's, 7's and 8's
        puzzle1 += problem.map { it.size }.count { it == 2 || it == 3 || it == 4 || it == 7 }

        // Puzzle 2 requires solving the puzzle
        // First we decide numbers 1, 4, 7 and 8 based on size
        solutions[1] = withSize(2).firstOrNull() ?: throw IllegalArgumentException("1 never showed up")
        solutions[4] = withSize(4).firstOrNull() ?: throw IllegalArgumentException("4 never showed up")
        solutions[7] = withSize(3).firstOrNull() ?: throw IllegalArgumentException("7 never showed up")
        solutions[8] = withSize(7).firstOrNull() ?: throw IllegalArgumentException("8 never showed up")

        // The 3 shares all segments that are in a 1. No other 5 length does.
        solutions[3] = withSize(5).firstOrNull { it.containsAll(solutions[1]!!) } ?: throw IllegalArgumentException("3 never showed up")

        // The 6 doesn't share all segments that are in a 1. No other 6 length does.
        solutions[6] = withSize(6).firstOrNull { !it.containsAll(solutions[1]!!) } ?: throw IllegalArgumentException("6 never showed up")

        // The 5 is fully contained in the 6, the 2 isn't.
        solutions[5] = withSize(5).firstOrNull { solutions[6]!!.containsAll(it) } ?: throw IllegalArgumentException("5 never showed up")
        solutions[2] = withSize(5).firstOrNull { !solutions[6]!!.containsAll(it) } ?: throw IllegalArgumentException("2 never showed up")

        // The 5 is fully contained in the 9, the 0 isn't.
        solutions[9] = withSize(6).firstOrNull { it.containsAll(solutions[5]!!) } ?: throw IllegalArgumentException("9 never showed up")
        solutions[0] = withSize(6).firstOrNull { !it.containsAll(solutions[5]!!) } ?: throw IllegalArgumentException("0 never showed up")

        // Determine the solution
        val solution = problem.map { inp -> solutions.filterValues { inp == it }.keys.firstOrNull() ?: throw IllegalArgumentException("No solution for $inp") }.joinToString("")
        puzzle2 += solution.toInt()
    }

    // Puzzle 1 - Count 1, 4, 7 and 8
    println("P1 $puzzle1")

    // Puzzle 2 - Output values
    println("P2 $puzzle2")
}