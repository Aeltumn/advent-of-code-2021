import java.io.File

private typealias PolymerPair = Pair<Char, Char>

/**
 * Day 14 - Extended Polymerization
 */
fun main() {
    val lines = File("day14").readLines()
    var start = ""
    val functions = mutableMapOf<PolymerPair, List<PolymerPair>>()

    for (index in lines.indices) {
        val line = lines[index]

        // Ignore empty lines
        if (line.isEmpty()) continue

        // The first line is our starting state
        if (start.isBlank()) {
            start = line
            continue
        }

        // Read everything else as an instruction
        val values = line.split(" -> ")
        val input = values[0][0] to values[0][1]
        val result = values[1][0]
        require(input !in functions) { "A pair can't have two functions" }
        functions[input] = listOf(input.first to result, result to input.second)
    }


    println("P1 ${runPolymerization(10, start, functions)}")
    println("P2 ${runPolymerization(40, start, functions)}")
}

private fun runPolymerization(iterations: Int, start: String, functions: Map<PolymerPair, List<PolymerPair>>): Long {
    // Run the polymerization, storing how many of each pair are present
    val pairs = functions.entries.flatMap { it.value + it.key }.distinct()
    val startAmounts = (0..start.length - 2).map { start[it] to start[it + 1] }
    var state = pairs.associateWith { pair -> startAmounts.count { pair == it }.toLong() }

    repeat(iterations) {
        val newState = mutableMapOf<PolymerPair, Long>()
        for ((pair, amount) in state) {
            if (pair in functions) {
                // If the pair does change we change it into the results
                functions[pair]?.forEach { poly ->
                    newState[poly] = (newState[poly] ?: 0L) + amount
                }
            } else {
                // If this pair cannot change we keep the amount
                newState[pair] = (newState[pair] ?: 0L) + amount
            }
        }
        state = newState
    }

    val characters = pairs.flatMap { listOf(it.first, it.second) }.distinct()
    val amounts =
        characters.associateWith { char ->
            // Every letter will be in two pairs with the first and last being in one more
            val doubleTotal = state.filterKeys { it.first == char || it.second == char }.map { entry -> if (entry.key.first == char && entry.key.second == char) entry.value * 2 else entry.value }.sum()

            // Round up in a long-compatible way
            if (doubleTotal % 2 == 0L) {
                return@associateWith doubleTotal / 2
            } else {
                return@associateWith doubleTotal / 2 + 1
            }
        }

    val mostCommon = amounts.maxOfOrNull { it.value } ?: return 0
    val leastCommon = amounts.minOfOrNull { it.value } ?: return 0
    return mostCommon - leastCommon
}