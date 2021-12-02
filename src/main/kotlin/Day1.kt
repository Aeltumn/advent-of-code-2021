import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Paths

/**
 * Day 1 - Sonar Sweep
 */
fun main() {
    val measurements = mutableListOf<Int>()
    BufferedReader(FileReader(Paths.get("day1").toFile())).use {
        while (it.ready()) {
            measurements += it.readLine().toInt()
        }
    }

    puzzle1(measurements)
    puzzle2(measurements)
}

fun puzzle1(measurements: List<Int>) {
    var increments = 0
    for (index in 1 until measurements.size) {
        val previous = measurements[index - 1]
        val current = measurements[index]

        if (current > previous) {
            increments++
        }
    }
    println("P1 Increments: $increments")
}

fun puzzle2(measurements: List<Int>) {
    var increments = 0
    for (index in 1 until measurements.size - 2) {
        val previous = measurements[index - 1] + measurements[index] + measurements[index + 1]
        val current = measurements[index] + measurements[index + 1] + measurements[index + 2]

        if (current > previous) {
            increments++
        }
    }
    println("P2 Increments: $increments")
}