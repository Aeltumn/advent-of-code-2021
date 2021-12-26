import java.io.File

/** The direction over which we can fold the paper. */
private enum class FoldDirection { X, Y }

/**
 * Day 13 - Transparent Origami
 */
fun main() {
    var areReadingFolds = false
    val inputPoints = mutableSetOf<Point>()
    val folds = mutableListOf<Pair<FoldDirection, Int>>()

    for (line in File("day13").readLines()) {
        // When we encounter a blank line we start reading folds
        if (line.isBlank()) {
            areReadingFolds = true
            continue
        }

        if (areReadingFolds) {
            // Read out folds
            val name = line.substringAfter("fold along ")
            val values = name.split("=")
            folds += FoldDirection.valueOf(values[0].uppercase()) to values[1].toInt()
        } else {
            // Read out dots
            val values = line.split(",")
            inputPoints += Point(values[0].toInt(), values[1].toInt())
        }
    }

    // Start folding the map
    var points = inputPoints.toList()
    var firstResult = 0
    for ((direction, fold) in folds) {
        points = points.map {
            when (direction) {
                FoldDirection.X -> {
                    if (it.x < fold) {
                        it
                    } else {
                        Point(2 * fold - it.x, it.y)
                    }
                }
                FoldDirection.Y -> {
                    if (it.y < fold) {
                        it
                    } else {
                        Point(it.x, 2 * fold - it.y)
                    }
                }
            }
        }.distinct()

        if (firstResult == 0) {
            firstResult = points.size
        }
    }
    println("P1 $firstResult")

    // Put the points in the map
    val width = (points.maxOfOrNull { it.x } ?: 0) + 1
    val height = (points.maxOfOrNull { it.y } ?: 0) + 1
    val map = Array(height) { Array(width) { false } }
    for (point in points) {
        map[point.y][point.x] = true
    }
    println("P2 \n${map.joinToString("\n") { row -> row.joinToString("") { if (it) "#" else " " } }}")
}