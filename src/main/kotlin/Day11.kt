import java.io.File
import java.util.Stack

/**
 * Day 11 - Dumbo Octopus
 */
fun main() {
    val inputLines = File("day11").readLines()
    val width = inputLines.maxOfOrNull { it.length } ?: 0
    val height = inputLines.size
    val area = width * height
    val map = Array(height) { Array<Int?>(width) { null } }

    for (index in inputLines.indices) {
        val chars = inputLines[index].toCharArray()
        for (char in chars.indices) {
            map[index][char] = chars[char].toString().toInt()
        }
    }

    var flashes = 0
    var lastIteration = 0
    val attempts = 300

    /** Iterate on the input. */
    fun iterate(input: Array<Array<Int?>>, times: Int = 0) {
        // Only iterate attempt times
        if (times >= attempts) return

        val mapped = Array(height) { Array<Int?>(width) { null } }
        val flashed = mutableSetOf<Pair<Int, Int>>()

        /** Tests an octopus at x, y. */
        fun test(x: Int, y: Int) {
            if ((mapped[y][x] ?: 0) > 9 && (x to y) !in flashed) {
                flashes++
                flashed += x to y

                // increment all neighbouring octopuses
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if ((x + dx) !in 0 until width || (y + dy) !in 0 until height) continue
                        mapped[y + dy][x + dx] = (mapped[y + dy][x + dx] ?: 0) + 1

                        // 2. octopuses with an energy level over 9 flash
                        test(x + dx, y + dy)
                    }
                }
            }
        }

        // 1. the energy level of each octopus increases by 1
        for (x in 0 until width) {
            for (y in 0 until height) {
                mapped[y][x] = (input[y][x] ?: 0) + 1
            }
        }

        // 2. octopuses with an energy level over 9 flash
        // (we do this after finishing step 1 so all inputs are loaded in)
        for (x in 0 until width) {
            for (y in 0 until height) {
                test(x, y)
            }
        }

        // 3. all octopuses that flashed are set to 0
        for ((x, y) in flashed) {
            mapped[y][x] = 0
        }

        // If all octopuses flashed we're done
        if (flashed.size == area) {
            lastIteration = times
            return
        }

        iterate(mapped, times + 1)
    }

    // Start iteration
    iterate(map)
    println("P1 $flashes")
    println("P2 ${lastIteration + 1}")
}