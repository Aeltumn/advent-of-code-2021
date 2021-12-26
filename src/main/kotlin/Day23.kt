import java.io.File

/**
 * Day 23 - Amphipod
 */
fun main() {
    val input = File("src/main/resources/day23").readLines()

    // Puzzle 1 - 2 / 20000
    // Puzzle 2 - 4 / 50000
    val podLength = 4
    val upperLimit = 50000

    val startPods = Array(4) { Array<Char?>(podLength) { null } }
    val hallwaySize = 11
    val hallwayEntrances = mapOf(
        0 to 2,
        1 to 4,
        2 to 6,
        3 to 8
    )
    val costs = mapOf(
        'A' to 1,
        'B' to 10,
        'C' to 100,
        'D' to 1000
    )
    val destinations = mapOf(
        'A' to 0,
        'B' to 1,
        'C' to 2,
        'D' to 3
    )

    // Fill up the pods from the input
    val topRow = input[2]
    val extraRow1 = "  #D#C#B#A#"
    val extraRow2 = "  #D#B#A#C#"
    val bottomRow = input[3]
    for (index in startPods.indices) {
        val textIndex = index * 2 + 3
        if (podLength == 4) {
            startPods[index] =
                arrayOf(topRow[textIndex], extraRow1[textIndex], extraRow2[textIndex], bottomRow[textIndex])
        } else {
            startPods[index] = arrayOf(topRow[textIndex], bottomRow[textIndex])
        }
    }

    var bestScore = Integer.MAX_VALUE

    /**
     * Simulates all possible steps from the current configuration.
     */
    fun step(pods: Array<Array<Char?>>, hallway: Array<Char?> = Array(hallwaySize) { null }, energy: Int = 0) {
        // Don't bother if we're worse than the best score already
        if (energy >= upperLimit) return
        if (energy >= bestScore) return

        // Check if we're done
        var done = true
        for (depth in 0 until podLength) {
            if (!(pods[0][depth] == 'A' && pods[1][depth] == 'B' && pods[2][depth] == 'C' && pods[3][depth] == 'D')) {
                done = false
            }
        }

        // Update the best score if this is a result
        if (done) {
            bestScore = bestScore.coerceAtMost(energy)
            return
        }

        // To create all steps we look for each amphipod in a pod that can move
        for (index in pods.indices) {
            var creature: Char? = null
            var originIndex = -1

            for (i in 0 until podLength) {
                if (pods[index][i] != null) {
                    creature = pods[index][i]!!
                    originIndex = i
                    break
                }
            }

            // Ignore empty pods
            if (creature == null) continue

            // An amphipod will never leave the pod if its own species is in the back of
            // this pod and it's the destination.
            if ((creature == pods[index][podLength - 1]) && (destinations[creature]!! == index)) continue

            // Determine how long it takes to leave
            val leaveDistance = originIndex + 1

            // Figure out every place the creature can move to
            val entrance = hallwayEntrances[index] ?: continue
            if (hallway[entrance] != null) continue
            val possibleDestinations = mutableMapOf(entrance to leaveDistance)

            // Move to the left in the hallway
            var distance = leaveDistance
            for (i in (entrance - 1) downTo 0) {
                if (hallway[i] == null) {
                    distance++
                    possibleDestinations += i to distance
                } else {
                    // If we can't move here, we can't go past here
                    break
                }
            }

            // Move to the right in the hallway
            distance = leaveDistance
            for (i in (entrance + 1) until hallwaySize) {
                if (hallway[i] == null) {
                    distance++
                    possibleDestinations += i to distance
                } else {
                    // If we can't move here, we can't go past here
                    break
                }
            }

            run {
                // Test if they can go to their destination pod
                val destination = destinations[creature]!!
                val destinationPod = pods[destination]
                val finalEntrance = hallwayEntrances[destination]!!

                // If we can't reach the pod, there's no chance
                if (finalEntrance !in possibleDestinations) return@run

                // See if we can enter the pod
                if (destinationPod[0] != null) return@run
                for (i in 0 until podLength) {
                    // If there isn't either a free space or a friend in this pod, we can't go
                    if (destinationPod[i] != null && destinationPod[i] != creature) return@run
                }

                // Find the target where we'll sit
                var targetIndex = -1
                for (i in (podLength - 1) downTo 0) {
                    if (destinationPod[i] == null) {
                        targetIndex = i
                        break
                    }
                }
                if (targetIndex == -1) return@run

                // Copy the board state so we can modify it freely
                val podsCopy = Array(4) { index -> Array(podLength) { pods[index][it] } }

                // Remove creature from its start
                podsCopy[index][originIndex] = null

                // Place them at the target
                podsCopy[destination][targetIndex] = creature
                val cost = possibleDestinations[finalEntrance]!! + targetIndex + 1
                step(podsCopy, hallway, energy + cost * costs[creature]!!)
            }

            // Remove spots in front hallways (they never go there)
            possibleDestinations.keys.removeAll(hallwayEntrances.values.toSet())

            // Start a new path for every possible destination
            for ((target, cost) in possibleDestinations) {
                // Copy the board state so we can modify it freely
                val hallwayCopy = Array(hallwaySize) { hallway[it] }
                val podsCopy = Array(4) { ind -> Array(podLength) { pods[ind][it] } }

                // Remove creature from its start
                podsCopy[index][originIndex] = null

                // Put the creature in the hallway
                hallwayCopy[target] = creature

                step(podsCopy, hallwayCopy, energy + cost * costs[creature]!!)
            }
        }

        // Second we look at all amphipods in the hallway to see if they can leave
        outer@ for (index in hallway.indices) {
            val creature = hallway[index] ?: continue

            // Try and move into the destination
            val destination = destinations[creature]!!
            val destinationPod = pods[destination]
            val entrance = hallwayEntrances[destination]!!

            // See if we can move there
            var distance = 0
            if (entrance < index) {
                for (i in (index - 1) downTo entrance) {
                    // If this space is occupied we can't get here
                    if (hallway[i] != null) continue@outer
                    distance++
                }
            } else {
                for (i in (index + 1) until (entrance + 1)) {
                    // If this space is occupied we can't get here
                    if (hallway[i] != null) continue@outer
                    distance++
                }
            }

            // See if we can enter the pod
            if (destinationPod[0] != null) continue
            for (i in 0 until podLength) {
                // If there isn't either a free space or a friend in this pod, we can't go
                if (destinationPod[i] != null && destinationPod[i] != creature) continue@outer
            }

            // Find the target where we'll sit
            var targetIndex = -1
            for (i in (podLength - 1) downTo 0) {
                if ((destinationPod[i]) == null) {
                    targetIndex = i
                    break
                }
            }
            if (targetIndex == -1) continue

            // Copy the board state so we can modify it freely
            val hallwayCopy = Array(hallwaySize) { hallway[it] }
            val podsCopy = Array(4) { ind -> Array(podLength) { pods[ind][it] } }

            // Remove creature from its start
            hallwayCopy[index] = null

            // Place them at the target
            podsCopy[destination][targetIndex] = creature
            val cost = distance + targetIndex + 1
            step(podsCopy, hallwayCopy, energy + cost * costs[creature]!!)
        }
    }

    step(startPods)
    println(bestScore)
}