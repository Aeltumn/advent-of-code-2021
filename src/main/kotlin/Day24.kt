import java.io.File

/** Turns the ALU into kotlin code. */
private fun writeALUInputToKotlin(inputLines: List<List<String>>) {
    // Start reading in the input
    var inputCount = 0
    for (args in inputLines) {
        if (args[0] == "inp") {
            println("${args[1]} = inputVariables[$inputCount]")
            inputCount++
            continue
        }

        when (args[0]) {
            "add" -> println("${args[1]} += ${args[2]}")
            "mul" -> println("${args[1]} *= ${args[2]}")
            "div" -> println("${args[1]} /= ${args[2]}")
            "mod" -> println("${args[1]} %= ${args[2]}")
            "eql" -> println("${args[1]} = if(${args[1]} == ${args[2]}) 1 else 0")
        }
    }
}

/** Verifies the input by running it through the original ALU and through the reverse engineered one. */
private fun verify(inputLines: List<List<String>>, input: Long): Boolean =
    testByReadingALU(inputLines, input.toString().toCharArray().map { it.digitToInt() }.toMutableList()) &&
            testUsingReverseEngineeredALU(input.toString())

/** Run the ALU with the given input variables. */
private fun testByReadingALU(inputLines: List<List<String>>, inputVariables: MutableList<Int>): Boolean {
    // Set up initial variables
    val variables = mutableMapOf<String, Int>()
    variables["x"] = 0
    variables["y"] = 0
    variables["z"] = 0
    variables["w"] = 0

    // Start reading in the input
    for (args in inputLines) {
        if (args.isEmpty()) continue
        if (args[0] == "inp") {
            variables[args[1]] = inputVariables.removeFirst()
            continue
        }

        // Load out the value as either a variable or a number
        val variable = variables[args[1]] ?: throw IllegalStateException("Did not find ${args[1]} in $variables")
        val value = if (args.size >= 3) {
            if (args[2] in variables) {
                variables[args[2]]!!
            } else {
                args[2].toInt()
            }
        } else 0

        when (args[0]) {
            "add" -> variables[args[1]] = variable + value
            "mul" -> variables[args[1]] = variable * value
            "div" -> variables[args[1]] = variable / value
            "mod" -> variables[args[1]] = variable % value
            "eql" -> variables[args[1]] = if (variable == value) 1 else 0
        }
    }

    return variables["z"] == 0
}

/**
 * Runs the ALU based on interpreted code.
 */
private fun testUsingReverseEngineeredALU(input: String): Boolean {
    // I've rewritten the MONAD into expressions using the helpful reverse engineering efforts of various redditors,
    // namely u/etotheipi1 and u/i_have_no_biscuits.

    // These values only work for my input. We compare char codes as it's faster, this only works
    // because the char codes of the digits are relative to each other just as to the digits are to themselves.
    return (input[4].code - 7 == input[5].code) &&
            (input[3].code + 1 == input[6].code) &&
            (input[7].code + 2 == input[8].code) &&
            (input[9].code + 5 == input[10].code) &&
            (input[2].code - 4 == input[11].code) &&
            (input[1].code - 8 == input[12].code) &&
            (input[0].code + 7 == input[13].code)
}

/**
 * Day 24 - Arithmetic Logic Unit
 */
fun main() {
    // Input
    val inputLines = File("day24").readLines().map {
        if (it.isBlank()) emptyList() else it.split(" ")
    }

    // Even with the reverse engineered MONAD it's slow to loop through everything, so I just manually
    // figured out the largest and smallest inputs and only used the code to verify correctness.

    // Puzzle 1 - Largest valid
    println("P1 29989297949519 = ${verify(inputLines, 29989297949519)}")

    // Puzzle 2 - Smallest valid
    println("P2 19518121316118 = ${verify(inputLines, 19518121316118)}")
}