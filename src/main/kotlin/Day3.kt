import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Paths

/**
 * Day 3 - Binary Diagnostic
 */
fun main() {
    val measurements = mutableListOf<String>()
    BufferedReader(FileReader(Paths.get("src/main/resources/day3").toFile())).use {
        while (it.ready()) {
            measurements += it.readLine()
        }
    }

    puzzle1(measurements)
    puzzle2(measurements)
}

private fun puzzle1(measurements: List<String>) {
    val zeroes = mutableMapOf<Int, Int>()
    val ones = mutableMapOf<Int, Int>()

    for (measurement in measurements) {
        for (i in measurement.indices) {
            // Get the inverse number as 0 in zeroes is the 0th bit from the right
            val num = measurement[measurement.length - i - 1]
            if (num == '0') {
                zeroes[i] = (zeroes[i] ?: 0) + 1
            } else if (num == '1') {
                ones[i] = (ones[i] ?: 0) + 1
            }
        }
    }

    var gammaRate = 0
    for (i in measurements[0].indices) {
        // Ignore bits where there are fewer ones
        if (ones.getOrDefault(i, 0) < zeroes.getOrDefault(i, 0)) continue

        // Add this bit to the gamma rate
        gammaRate += 1 shl (i)
    }
    var epsilonRate = 0
    for (i in measurements[0].indices) {
        // Ignore bits where there are more ones
        if (ones.getOrDefault(i, 0) > zeroes.getOrDefault(i, 0)) continue

        // Add this bit to the epsilon rate
        epsilonRate += 1 shl (i)
    }

    println("P1 Gamma Rate: $gammaRate, Epsilon Rate: $epsilonRate")
    println("P1 Power Consumption: ${gammaRate * epsilonRate}")
}

private fun filter(bit: Int, measurements: List<String>, compare: (Int, Int) -> Boolean): List<String> {
    // Tally the zeroes and ones
    var zeroes = 0
    var ones = 0
    for (x in measurements) {
        if (bit >= x.length) continue

        val num = x[bit]
        if (num == '0') {
            zeroes++
        } else if (num == '1') {
            ones++
        }
    }

    // Determine if we should keep ones and filter based on that
    val keepIfOne = compare(zeroes, ones)
    val result = measurements.filter {
        if (bit >= it.length) {
            false
        } else {
            val value = it[bit]
            val bool = value == '1'
            bool == keepIfOne
        }
    }

    // Determine the result, possibly calling filter again
    if (result.size <= 1) return result
    return filter(bit + 1, result, compare)
}

private fun puzzle2(measurements: List<String>) {
    // For oxygen, we keep a 1 if there are more or equal ones than zeroes
    val oxygen = filter(0, measurements) { zeroes, ones -> ones >= zeroes }[0].toInt(2)

    // For co2, we keep a 1 only if there were more zeroes
    val co2 = filter(0, measurements) { zeroes, ones -> ones < zeroes }[0].toInt(2)

    println("P2 Oxygen: $oxygen, CO2: $co2")
    println("P2 Life Support Rating: ${oxygen * co2}")
}