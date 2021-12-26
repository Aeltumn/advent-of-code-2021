import java.io.File
import kotlin.math.max
import kotlin.math.min

private data class Cube(val x: LongRange, val y: LongRange, val z: LongRange, val state: Boolean) {

    val area: Long = ((x.last - x.first + 1) * (y.last - y.first + 1) * (z.last - z.first + 1))

    fun fullyOverlap(other: Cube): Boolean =
        x == other.x && y == other.y && z == other.z

    fun intersection(other: Cube): Cube? {
        if (fullyOverlap(other)) return this

        val lowX = max(x.first, other.x.first)
        val highX = min(x.last, other.x.last)
        val lowY = max(y.first, other.y.first)
        val highY = min(y.last, other.y.last)
        val lowZ = max(z.first, other.z.first)
        val highZ = min(z.last, other.z.last)

        return if (lowX > highX || lowY > highY || lowZ > highZ) null
        else Cube(lowX..highX, lowY..highY, lowZ..highZ, state)
    }
}

/**
 * Recursively counts the amount of enabled cubes from the given input list.
 */
private fun count(cubes: List<Cube>): Long {
    var total = 0L
    val counted = mutableSetOf<Cube>()
    for (cube in cubes) {
        // Find every intersection within this cube
        val intersections = mutableListOf<Cube>()
        for (other in counted) {
            // Intersection passes down on/off state of other
            val intersect = other.intersection(cube)
            if (intersect != null) {
                intersections += intersect
            }
        }

        // Remove the cubes enabled within the intersection regions so we don't count double
        if (intersections.isNotEmpty()) {
            total -= count(intersections)
        }

        // Add this cube to the total only if it's on, if it's off removing everything in the
        // intersections has already reduced the total with the area that was here
        if (cube.state) {
            total += cube.area
        }

        // We've added this cube to the total so future cubes can subtract
        // intersections with this one from the total
        counted += cube
    }

    require(total >= 0) { "Total can't be negative" }
    return total
}

/**
 * Day 22 - Reactor Reboot
 */
fun main() {
    val input = File("day22").readLines()
    val cubes = mutableListOf<Cube>()

    for (line in input) {
        val data = line.split(" ")
        val coordinates = data[1].split(",")
        val xData = coordinates[0].substring(2).split("..")
        val yData = coordinates[1].substring(2).split("..")
        val zData = coordinates[2].substring(2).split("..")

        // Read out data
        val on = data[0] == "on"
        val minX = xData[0].toLong()
        val maxX = xData[1].toLong()
        val minY = yData[0].toLong()
        val maxY = yData[1].toLong()
        val minZ = zData[0].toLong()
        val maxZ = zData[1].toLong()
        require(minX <= maxX && minY <= maxY && minZ <= maxZ) { "Invalid input" }
        cubes += Cube(minX..maxX, minY..maxY, minZ..maxZ, on)
    }

    println("P2 ${count(cubes)}")
}

private fun puzzle1(cubes: List<Cube>) {
    val reverseCubes = cubes.reversed()
    var total = 0L
    for (x in -50..50) {
        for (y in -50..50) {
            for (z in -50..50) {
                // If we find a cube that this point is in, we increase the total
                for (cube in reverseCubes) {
                    if (x in cube.x && y in cube.y && z in cube.z) {
                        if (cube.state) {
                            total++
                        }
                        break
                    }
                }
            }
        }
    }
    println("P1 $total")
}