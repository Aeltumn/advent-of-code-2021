import java.io.File
import kotlin.math.abs

private data class Vector(val x: Int, val y: Int, val z: Int) {

    fun rotateX(): Vector = Vector(x, z, -y)
    fun rotateY(): Vector = Vector(z, y, -x)
    fun rotateZ(): Vector = Vector(y, -x, z)

    operator fun plus(other: Vector): Vector =
        Vector(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector): Vector =
        Vector(x - other.x, y - other.y, z - other.z)

    operator fun times(factor: Int): Vector =
        Vector(x * factor, y * factor, z * factor)
}

private data class Scanner(val index: Int, val positions: Set<Vector>)

private data class AlignedScanner(val index: Int, val positions: List<Vector>, val offset: Vector) {

    constructor(scanner: Scanner, function: (Vector) -> Vector) : this(
        scanner.index,
        scanner.positions.map(function),
        Vector(0, 0, 0)
    )

    constructor(scanner: AlignedScanner, offset: Vector) : this(
        scanner.index,
        scanner.positions.map { it + offset },
        offset
    )

    fun relativize(index: Int): List<Vector> = positions.map { it - positions[index] }
}

/**
 * Day 19 - Beacon Scanner
 */
fun main() {
    val input = File("src/main/resources/day19").readLines()
    var scannerIndex = -1
    var positions = mutableSetOf<Vector>()
    val scanners = mutableListOf<Scanner>()

    for (line in input) {
        if (line.isBlank()) continue
        if (line.startsWith("---")) {
            // Scanner definition
            if (scannerIndex >= 0) {
                scanners += Scanner(scannerIndex, positions)
            }
            scannerIndex = line.split(" ")[2].toInt()
            positions = mutableSetOf()
            continue
        }

        // Read out positions
        val data = line.split(",")
        positions += Vector(data[0].toInt(), data[1].toInt(), data[2].toInt())
    }

    // Save the last read scanner
    scanners += Scanner(scannerIndex, positions)

    // Scanner 0 is at the origin and the first correct scanner
    val firstScanner = AlignedScanner(scanners.find { it.index == 0 }!!) { it }
    scanners.removeIf { it.index == 0 }

    // Rotations are from https://github.com/Crazytieguy/advent-2021/blob/master/src/bin/day19/main.rs because
    // I'm bad at 3d math and 3d rotations are hard.
    val rotations: List<(Vector) -> Vector> = listOf(
        { it },
        { it.rotateZ() },
        { it.rotateZ().rotateZ() },
        { it.rotateZ().rotateZ().rotateZ() },
        { it.rotateX() },
        { it.rotateZ().rotateX() },
        { it.rotateZ().rotateZ().rotateX() },
        { it.rotateZ().rotateZ().rotateZ().rotateX() },
        { it.rotateX().rotateX() },
        { it.rotateZ().rotateX().rotateX() },
        { it.rotateZ().rotateZ().rotateX().rotateX() },
        { it.rotateZ().rotateZ().rotateZ().rotateX().rotateX() },
        { it.rotateX().rotateX().rotateX() },
        { it.rotateZ().rotateX().rotateX().rotateX() },
        { it.rotateZ().rotateZ().rotateX().rotateX().rotateX() },
        { it.rotateZ().rotateZ().rotateZ().rotateX().rotateX().rotateX() },
        { it.rotateY() },
        { it.rotateZ().rotateY() },
        { it.rotateZ().rotateZ().rotateY() },
        { it.rotateZ().rotateZ().rotateZ().rotateY() },
        { it.rotateY().rotateY().rotateY() },
        { it.rotateZ().rotateY().rotateY().rotateY() },
        { it.rotateZ().rotateZ().rotateY().rotateY().rotateY() },
        { it.rotateZ().rotateZ().rotateZ().rotateY().rotateY().rotateY() }
    )

    // Go through each beacon and find a pairing with the first
    val alignedScanners = mutableListOf<AlignedScanner>()
    alignedScanners += firstScanner

    /**
     * Finds a pairing between a given remaining scanner and any of the found aligned scanners.
     */
    fun findPair(index: Int) {
        val other = scanners[index]
        for (scanner in alignedScanners) {
            for (mapping in rotations) {
                val aligned = AlignedScanner(other, mapping)

                // For each combination of beacons the scanner and aligned
                for (i in scanner.positions.indices) {
                    for (j in aligned.positions.indices) {
                        // Stop once we're past the half-way point as we've tried every pair
                        if (j > i) break

                        // We relativize both on the aligned and scanned and then check if we can find 12 matches
                        val relativized = aligned.relativize(j)
                        val matches = scanner.relativize(i).count { it in relativized }
                        if (matches >= 12) {
                            // We've found a pair!
                            scanners -= other

                            // Determine the offset between the scanners
                            val offset = scanner.positions[i] - aligned.positions[j]
                            alignedScanners += AlignedScanner(aligned, offset)
                            return
                        }
                    }
                }
            }
        }
    }

    // Start searching
    var lastIndex = -1
    while (scanners.isNotEmpty()) {
        lastIndex++
        if (lastIndex >= scanners.size) {
            lastIndex = 0
        }
        findPair(lastIndex)
    }

    // Print how many beacons there truly are
    val realBeacons = alignedScanners.flatMap { it.positions }.distinct()
    println("P1 ${realBeacons.size}")

    // Find the largest manhattan distance
    var record = 0
    for (i in alignedScanners.indices) {
        for (j in alignedScanners.indices) {
            val diff = alignedScanners[j].offset - alignedScanners[i].offset
            val distance = abs(diff.x) + abs(diff.y) + abs(diff.z)
            record = record.coerceAtLeast(distance)
        }
    }
    println("P2 $record")
}