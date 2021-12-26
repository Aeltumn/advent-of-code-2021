import java.io.File

/**
 * Day 12 - Passage Pathing
 */
fun main() {
    val input = File("src/main/resources/day12").readLines()
    val nodes = mutableMapOf<String, Node>()
    val startNode = nodes.computeIfAbsent("start") { Node(it) }
    val endNode = nodes.computeIfAbsent("end") { Node(it) }

    for (line in input) {
        // Find the nodes referenced
        val from = nodes.computeIfAbsent(line.split("-")[0]) { Node(it) }
        val to = nodes.computeIfAbsent(line.split("-")[1]) { Node(it) }

        // Set up connections (bi-directional)
        from.connections += to
        to.connections += from
    }

    // Try every possible path through the system
    var paths = mutableSetOf<List<Node>>()

    /** Iterates on the given [history] with all possible follow-up paths. */
    fun iteratePath(history: List<Node>, canDoubleTake: Boolean) {
        val current = history.lastOrNull() ?: throw IllegalArgumentException("Can't iterate on empty list")

        for (target in current.connections) {
            // Ignore the start node
            if (target == startNode) continue
            val newPath = history + target

            // If this is the end node we finish the path here
            if (target == endNode) {
                paths += newPath
                continue
            }

            // If the id is lowercase (small cave) we don't go there if we've already been there
            // unless we can do a double take in which we case we can try it once
            if (target.id.lowercase() == target.id) {
                if (target in history) {
                    // Allow a single double take
                    if (!canDoubleTake) continue
                    else {
                        // Consume the one double take we have and continue iterating
                        iteratePath(newPath, false)
                        continue
                    }
                }
            }

            // Try to go into this node
            iteratePath(newPath, canDoubleTake)
        }
    }

    fun puzzle1() {
        paths = mutableSetOf()
        iteratePath(listOf(startNode), false)
        println("P1 ${paths.size}")
    }

    fun puzzle2() {
        paths = mutableSetOf()
        iteratePath(listOf(startNode), true)
        println("P2 ${paths.size}")
    }

    puzzle1()
    puzzle2()
}