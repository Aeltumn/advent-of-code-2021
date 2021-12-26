import java.io.File

/**
 * Day 6 - Lanternfish
 */
fun main() {
    val input = File("src/main/resources/day6").readLines()
    val fish = input[0].split(",").map { it.toInt() }

    println("P1 Total fish ${simulate(80, fish).values.sum()}")
    println("P2 Total fish ${simulate(256, fish).values.sum()}")
}

private fun simulate(days: Int, fish: List<Int>): Map<Int, Long> {
    var ocean = (0..8).associateWith { fish.count { timer -> it == timer }.toLong() }

    for (day in 0 until days) {
        val newOcean = mutableMapOf<Int, Long>()

        for ((key, value) in ocean) {
            if (key == 0) {
                // If the timer is at 0 go back to 6 and add a new entry at 8
                newOcean[6] = newOcean.getOrDefault(6, 0) + value
                newOcean[8] = newOcean.getOrDefault(8, 0) + value
            } else {
                // Otherwise we decrement the value by one
                newOcean[key - 1] = newOcean.getOrDefault(key - 1, 0) + value
            }
        }

        ocean = newOcean
    }

    return ocean
}