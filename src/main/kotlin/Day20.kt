import java.io.File

/**
 * Day 20 - Trench Map
 */
fun main() {
    val input = File("day20").readLines()
    val enhancementData = input[0]

    // In the example the 0 enhancement is . but in the real data it's a #, which means the entire infinite picture
    // becomes filled after the first iteration. Of course we can't simulate this, but if the full 1 enhancement
    // happens to be a . (which is it for me, I think it's like this for everyone) this just makes everything blink.
    // So if we do it twice it ends up having the entire image off, and it allows us to simulate normally. The
    // image still can't grow by more than 1 to each side, but we need to see every unknown pixel as 1 instead of
    // 0 on every other iteration.
    val blink = if (enhancementData[0] == '#') {
        require(enhancementData.last() == '.') { "Can't simulate an entire field that doesn't blink" }
        true
    } else {
        false
    }

    var width = input[2].length
    var height = input.size - 2
    var image = Array(height) { Array(width) { false } }

    // Build the original image
    for (y in 0 until height) {
        val line = input[y + 2]
        for (x in line.indices) {
            if (line[x] == '#') {
                image[y][x] = true
            }
        }
    }

    // Iterate on the image
    val iterations = 50 // set this to 2 for puzzle 1
    var outside = "0"
    repeat(iterations) {
        width += 2
        height += 2
        val newImage = Array(height) { Array(width) { false } }
        for (y in newImage.indices) {
            for (x in newImage[0].indices) {
                var str = ""
                // Take the 3x3 from the original image that is used for this new image,
                // the new image is 2 wider so we shift it 1 over here
                for (dy in -2..0) {
                    for (dx in -2..0) {
                        if (x + dx !in 0 until width - 2 || y + dy !in 0 until height - 2) {
                            // If the original image did not have this pixel, it's a 0
                            str += outside
                        } else {
                            // If the original image did have this pixel, read it out
                            str += if (image[y + dy][x + dx]) "1" else "0"
                        }
                    }
                }

                val value = str.toInt(2)
                newImage[y][x] = enhancementData[value] == '#'
            }
        }
        // If we're blinking we change the entire outside every other iteration
        if (blink) {
            outside = if (outside == "0") {
                "1"
            } else {
                "0"
            }
        }
        image = newImage
    }

    println("P2 ${image.sumOf { row -> row.count { it } }}")
}