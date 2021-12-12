import java.io.File
import java.util.Stack

private enum class SyntaxType {
    PARENTHESES,
    BRACKETS,
    BRACES,
    CHEVRONS
}

/**
 * Day 10 - Syntax Scoring
 */
fun main() {
    val input = File("day10").readLines()
    var syntaxErrorScore = 0
    val completionScores = mutableListOf<Long>()

    lines@for (line in input) {
        val history = Stack<SyntaxType>()

        // Filter out corrupted lines
        for (character in line) {
            when(character) {
                '(' -> history.push(SyntaxType.PARENTHESES)
                '[' -> history.push(SyntaxType.BRACKETS)
                '{' -> history.push(SyntaxType.BRACES)
                '<' -> history.push(SyntaxType.CHEVRONS)
                ')' -> {
                    val popped = history.pop()
                    if (popped != SyntaxType.PARENTHESES) {
                        syntaxErrorScore += 3
                        continue@lines
                    }
                }
                ']' -> {
                    val popped = history.pop()
                    if (popped != SyntaxType.BRACKETS) {
                        syntaxErrorScore += 57
                        continue@lines
                    }
                }
                '}' -> {
                    val popped = history.pop()
                    if (popped != SyntaxType.BRACES) {
                        syntaxErrorScore += 1197
                        continue@lines
                    }
                }
                '>' -> {
                    val popped = history.pop()
                    if (popped != SyntaxType.CHEVRONS) {
                        syntaxErrorScore += 25137
                        continue@lines
                    }
                }
            }
        }

        // Complete incomplete lines
        var amount = 0L
        while (history.isNotEmpty()) {
            amount *= 5
            when(history.pop()) {
                SyntaxType.PARENTHESES -> amount += 1
                SyntaxType.BRACKETS -> amount += 2
                SyntaxType.BRACES -> amount += 3
                SyntaxType.CHEVRONS -> amount += 4
            }
        }
        completionScores += amount
    }

    println("P1 $syntaxErrorScore")
    val sortedCompletionScores = completionScores.sorted()
    println("P2 ${sortedCompletionScores[sortedCompletionScores.size / 2]}")
}