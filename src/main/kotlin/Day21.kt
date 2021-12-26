import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.min

/**
 * Day 21 - Dirac Dice
 */
fun main() {
    // The puzzle is so widly different in puzzle 2 here that I just copied the first version over.
    val input = File("day21").readLines()
    val player1Start = input[0].substringAfter("Player 1 starting position: ").toInt() - 1
    val player2Start = input[1].substringAfter("Player 2 starting position: ").toInt() - 1

    // Simulate the possible results from rolling three dice to minimise how many branches
    // we need to simulate. Store how often every outcome is, so we can multiply the wins
    // in that branch.
    val rolls = mutableMapOf<Int, Long>()
    for (d1 in 1..3) {
        for (d2 in 1..3) {
            for (d3 in 1..3) {
                val roll = d1 + d2 + d3
                rolls[roll] = (rolls[roll] ?: 0) + 1
            }
        }
    }

    val player1Wins = AtomicLong(0)
    val player2Wins = AtomicLong(0)

    /**
     * Plays one iteration of the game.
     */
    fun step(
        firstPlayer: Boolean,
        move: Int,
        player1: Int,
        player2: Int,
        times: Long = 1,
        score1: Int = 0,
        score2: Int = 0
    ) {
        val newPlayer1 = if (firstPlayer) (player1 + move) % 10 else player1
        val newPlayer2 = if (!firstPlayer) (player2 + move) % 10 else player2
        val newScore1 = if (firstPlayer) score1 + newPlayer1 + 1 else score1
        val newScore2 = if (!firstPlayer) score2 + newPlayer2 + 1 else score2

        if (newScore1 >= 21) {
            player1Wins.addAndGet(times)
            return
        }
        if (newScore2 >= 21) {
            player2Wins.addAndGet(times)
            return
        }

        // Start new universes
        for ((roll, tms) in rolls) {
            step(!firstPlayer, roll, newPlayer1, newPlayer2, times * tms, newScore1, newScore2)
        }
    }

    // Start the puzzle for each possible roll
    for ((roll, times) in rolls) {
        step(true, roll, player1Start, player2Start, times)
    }

    println("P2 ${max(player1Wins.get(), player2Wins.get())}")
}

private fun puzzle1() {
    val input = File("day21").readLines()
    var player1 = input[0].substringAfter("Player 1 starting position: ").toInt() - 1
    var player2 = input[1].substringAfter("Player 2 starting position: ").toInt() - 1

    var score1 = 0
    var score2 = 0

    var deterministicDice = 0
    var rolls = 0

    /**
     * Rolls the dice and returns the result.
     */
    fun rollDice(): Int {
        // Puzzle 1
        rolls++
        deterministicDice++
        if (deterministicDice > 100) {
            deterministicDice = 1
        }
        return deterministicDice
    }

    /**
     * Plays one step of the game. Returns true if a player has won.
     */
    fun step(): Boolean {
        // Player 1 rolls
        val move1 = rollDice() + rollDice() + rollDice()
        player1 = (player1 + move1) % 10
        score1 += player1 + 1

        if (score1 >= 1000) return true

        // Player 2 rolls
        val move2 = rollDice() + rollDice() + rollDice()
        player2 = (player2 + move2) % 10
        score2 += player2 + 1

        if (score2 >= 1000) return true
        return false
    }

    // Play until we have a winner
    while (!step());

    println("P1 ${min(score1, score2) * rolls}")
}