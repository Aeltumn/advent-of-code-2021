import java.io.File

/**
 * Day 25 - Sea Cucumber
 */
fun main() {
    val inputLines = File("src/main/resources/day25").readLines()
    val width = inputLines.maxOfOrNull { it.length } ?: 0
    val height = inputLines.size
    val map = Array(height) { Array<Char?>(width) { null } }

    for (index in inputLines.indices) {
        val chars = inputLines[index].toCharArray()
        for (char in chars.indices) {
            val found = chars[char]
            map[index][char] = if (found == '.') null else found
        }
    }

    var noMovement: Int? = null
    val maxAttempts = 1_000

    /** Iterate on the input. */
    fun iterate(input: Array<Array<Char?>>, times: Int = 0) {
        // Only iterate attempt times
        if (times >= maxAttempts) return

        var moved = false
        val afterRightMove = Array(height) { Array<Char?>(width) { null } }

        // Try to move to the right
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (input[y][x] == '>' && input[y][(x + 1) % width] == null) {
                    // If the space is free we move forward
                    afterRightMove[y][(x + 1) % width] = '>'
                    moved = true
                    continue
                }

                // Copy anything else that's not null
                if (input[y][x] != null) {
                    afterRightMove[y][x] = input[y][x]
                }
            }
        }

        // Store the state of the map after right movement
        val afterDownMove = Array(height) { Array<Char?>(width) { null } }

        // Try to move down
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (afterRightMove[y][x] == 'v' && afterRightMove[(y + 1) % height][x] == null) {
                    // If the space is free we move forward
                    afterDownMove[(y + 1) % height][x] = 'v'
                    moved = true
                    continue
                }


                // Copy anything else that's not null
                if (afterRightMove[y][x] != null) {
                    afterDownMove[y][x] = afterRightMove[y][x]
                }
            }
        }

        // Continue iterating
        if (noMovement == null && !moved) {
            noMovement = times + 1
            return
        }
        iterate(afterDownMove, times + 1)
    }

    // Start iteration
    iterate(map)
    println("P1 $noMovement")
}