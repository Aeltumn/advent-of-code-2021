import java.io.File

/**
 * Day 9 - Smoke Basin
 */
fun main() {
    val input = File("src/main/resources/day9").readLines()
    val width = input.maxOfOrNull { it.length } ?: 0
    val height = input.size
    val map = Array(height) { Array<Int?>(width) { null } }

    for (index in input.indices) {
        val chars = input[index].toCharArray()
        for (char in chars.indices) {
            map[index][char] = chars[char].toString().toInt()
        }
    }

    /** Returns the value at the given index. */
    fun get(x: Int, y: Int): Int? = if (x in 0 until width && y in 0 until height) map[y][x] else null

    var puzzle1 = 0
    val basins = mutableListOf<Int>()
    for (x in 0..width) {
        for (y in 0..height) {
            val value = get(x, y) ?: continue
            val up = get(x, y + 1)
            if (up != null && up <= value) continue

            val down = get(x, y - 1)
            if (down != null && down <= value) continue

            val right = get(x + 1, y)
            if (right != null && right <= value) continue

            val left = get(x - 1, y)
            if (left != null && left <= value) continue

            puzzle1 += 1 + value

            // When we've found a basin center we explore around it
            val exploredPoints = mutableSetOf<Point>()

            /** Tries to explore a tile. */
            fun explore(x: Int, y: Int) {
                // 9's indicate the end of a basin
                val exploredValue = get(x, y) ?: return
                if (exploredValue == 9) return
                val point = Point(x, y)

                // Avoid exploring points twice
                if (point in exploredPoints) return
                exploredPoints += point

                // Explore adjacent points
                explore(x + 1, y)
                explore(x - 1, y)
                explore(x, y + 1)
                explore(x, y - 1)
            }

            explore(x, y)
            basins += exploredPoints.size
        }
    }

    println("P1 $puzzle1")
    val sortedBasins = basins.sortedDescending()
    println("P2 ${sortedBasins[0] * sortedBasins[1] * sortedBasins[2]}")
}