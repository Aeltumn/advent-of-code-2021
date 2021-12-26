import java.io.File
import kotlin.math.abs

/**
 * Day 7 - The Treachery of Whales
 */
fun main() {
    File("src/main/resources/day7").readLines()[0].split(",").map { it.toInt() }.also { crabs ->
        val min = crabs.minOrNull() ?: return
        val max = crabs.maxOrNull() ?: return

        println("P1 Optimal fuel usage: ${(min..max).minOfOrNull { target -> crabs.sumOf { abs(it - target) } }}")
        println(
            "P2 Optimal fuel usage: ${
                (min..max).minOfOrNull { target ->
                    crabs.sumOf {
                        val distance = abs(it - target)
                        distance * (distance + 1) / 2
                    }
                }
            }"
        )
    }
}