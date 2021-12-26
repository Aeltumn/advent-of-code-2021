import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

/**
 * Day 17 - Trick Shot
 */
fun main() {
    val input = File("day17").readLines()[0].substringAfter("target area: ").split(", ")
    val targetXs = input[0].substringAfter("x=").split("..")
    val targetYs = input[1].substringAfter("y=").split("..")

    // Determine the target area
    val minX = targetXs[0].toInt()
    val maxX = targetXs[1].toInt()
    val minY = targetYs[0].toInt()
    val maxY = targetYs[1].toInt()

    // Determine the shot strength
    var x = 0
    var y = 0
    var xVelocity = 0
    var yVelocity = 0
    var highestY = 0

    /**
     * Runs one step of the simulation. Returns true if we've reached the target area.
     */
    fun runStep(): Boolean {
        // Move the probe
        x += xVelocity
        y += yVelocity

        // Update highest y
        highestY = highestY.coerceAtLeast(y)

        // Drag reduces the x velocity by 1 and gravity reduces y velocity by 1
        xVelocity = (xVelocity - 1).coerceAtLeast(0)
        yVelocity -= 1

        // Return whether we've reached the goal
        return x in minX..maxX && y in minY..maxY
    }

    /**
     * Simulates the trajectory with the given x and y velocity. Returns whether it hit the target.
     */
    fun runSimulation(velocityX: Int, velocityY: Int): Boolean {
        x = 0
        y = 0
        highestY = 0
        xVelocity = velocityX
        yVelocity = velocityY

        // Keep running steps until we're either hopeless or we're done
        while (!runStep()) {
            // If we've hopelessly missed we stop
            if (xVelocity.sign != (if (x < minX) 1 else -1) && y < minY) {
                return false
            }
        }
        return true
    }

    // Test out different velocities
    // This is far from optimal of course, but the code is so fast that it really doesn't matter.
    var record = 0
    var valid = 0

    val bound = max(max(abs(minX * 2), abs(minY * 2)), max(abs(maxX * 2), abs(maxY * 2)))
    for (vx in -bound..bound) {
        for (vy in -bound..bound) {
            if (runSimulation(vx, vy)) {
                record = record.coerceAtLeast(highestY)
                valid++
            }
        }
    }

    println("P1 $record")
    println("P2 $valid")
}