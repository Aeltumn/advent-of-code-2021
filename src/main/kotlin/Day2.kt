import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Paths

enum class Command {
    FORWARD,
    DOWN,
    UP
}

/**
 * Day 2 - Dive!
 */
fun main() {
    val commands = mutableListOf<Pair<Command, Int>>()
    BufferedReader(FileReader(Paths.get("src/main/resources/day2").toFile())).use {
        while (it.ready()) {
            val line = it.readLine()
            val split = line.split(" ")
            val cmd = Command.valueOf(split[0].uppercase())
            val amount = split[1].toInt()
            commands += cmd to amount
        }
    }

    puzzle1(commands)
    puzzle2(commands)
}

private fun puzzle1(commands: List<Pair<Command, Int>>) {
    var horizon = 0
    var depth = 0
    for ((cmd, amount) in commands) {
        when (cmd) {
            Command.FORWARD -> horizon += amount
            Command.UP -> depth -= amount
            Command.DOWN -> depth += amount
        }
    }

    println("P1 Horizontal Position: $horizon, Depth: $depth")
    println("P1 Multiplied: ${horizon * depth}")
}

private fun puzzle2(commands: List<Pair<Command, Int>>) {
    var horizon = 0
    var depth = 0
    var aim = 0
    for ((cmd, amount) in commands) {
        when (cmd) {
            Command.FORWARD -> {
                horizon += amount
                depth += aim * amount
            }
            Command.UP -> {
                aim -= amount
            }
            Command.DOWN -> {
                aim += amount
            }
        }
    }

    println("P2 Horizontal Position: $horizon, Depth: $depth")
    println("P2 Multiplied: ${horizon * depth}")
}