import java.io.File

private val NEIGHBORS = listOf(
    Point(1, 0),
    Point(-1, 0),
    Point(0, 1),
    Point(0, -1)
)

/**
 * Day 15 - Chiton
 */
fun main() {
    val input = File("src/main/resources/day15").readLines()
    val cellWidth = input.maxOfOrNull { it.length } ?: 0
    val cellHeight = input.size

    val width = cellWidth * 5
    val height = cellHeight * 5

    // Build the risk map
    val risk = Array(height) { Array(width) { 0 } }
    for (y in 0 until height) {
        for (x in 0 until width) {
            val taxicab = (y / cellHeight) + (x / cellWidth)
            risk[y][x] = (input[y % cellHeight][x % cellWidth].digitToInt() + taxicab - 1) % 9 + 1
        }
    }

    val map = Array(height) { Array<Pair<Int, List<Point>>>(width) { Integer.MAX_VALUE to emptyList() } }
    val updated = mutableSetOf<Point>()

    /**
     * Updates the weights of all points. Returns false if no points were updated.
     */
    fun updateAllPoints(): Boolean {
        // Update the entire board, store if something was changed
        var changed = false

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Ignore points that can't have changed
                val point = Point(x, y)
                if (point in updated) continue

                // Get the risk level of this point
                val history = map[y][x]

                // Update each neighbor
                for (relative in NEIGHBORS) {
                    val newPoint = point + relative

                    // Ignore neighbors outside the field
                    if (newPoint.x < 0 || newPoint.y < 0 || newPoint.x >= width || newPoint.y >= height) continue

                    // Check if we can get here with less risk than the current best path
                    val current = map[newPoint.y][newPoint.x]
                    val pointRisk = risk[newPoint.y][newPoint.x]
                    if (history.first + pointRisk < current.first) {
                        // Update the data of this point
                        map[newPoint.y][newPoint.x] = history.first + pointRisk to history.second + newPoint

                        // Mark down that a point has changed
                        updated -= newPoint
                        changed = true
                    }
                }

                // Mark that we've gone through this point so we don't do it again unless necessary
                updated += point
            }
        }

        return changed
    }

    // Start iteration on the top corner and set the initial risk of the top corner to 0
    map[0][0] = 0 to emptyList()

    // Keep updating all points until they're all minimal
    while (updateAllPoints());

    // Get the risk for getting to the bottom corner
    println("P2 ${map[height - 1][width - 1].first}")
}