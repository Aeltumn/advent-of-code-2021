import java.io.File

/** Returns the string representation of this number in the given radix padded to be length long. */
private fun Int.toPaddedString(radix: Int, length: Int): String {
    val result = toString(radix)
    return "0".repeat(length - result.length) + result
}

/**
 * Day 16 - Packet Decoder
 */
fun main() {
    // The input represented in bits stored as chars, I could keep it as an actual int but I prefer
    // checking chars over dealing with bit manipulation in Kotlin.
    val input = File("day16").readLines()[0].lowercase()
        .flatMap { it.digitToInt(16).toPaddedString(2, 4).toCharArray().toList() }
    var head = 0
    var totalVersion = 0

    /**
     * Reads an int from the binary stream starting at head for [length].
     */
    fun readValue(length: Int): Int {
        var str = ""
        for (i in 0 until length) {
            str += input[i + head]
        }
        head += length
        return str.toInt(2)
    }

    /**
     * Reads a packet starting at  the given index.
     */
    fun readPacket(): Long {
        val version = readValue(3)
        totalVersion += version

        val type = readValue(3)
        if (type == 4) {
            // Literal value
            var literal = ""
            var readLastChunk = false
            while (!readLastChunk) {
                val next = readValue(5).toPaddedString(2, 5)

                // If this chunk starts with a 0 it ends here
                if (next[0] == '0') {
                    readLastChunk = true
                }

                // Add this chunk to the value (ignore first bit)
                literal += next.substring(1)
            }
            return literal.toLong(2)
        } else {
            // Operator
            val mode = readValue(1)
            val values = mutableListOf<Long>()

            if (mode == 0) {
                // 15 bits are the length of upcoming packets
                val length = readValue(15)
                val end = head + length
                while (head < end) {
                    values += readPacket()
                }
                require(head == end) { "Reading packets in operator did not land at the correct spot" }
            } else {
                // 11 bits are a number of packets
                val amount = readValue(11)
                for (i in 0 until amount) {
                    values += readPacket()
                }
            }

            var result = 0L
            when (type) {
                0 -> result = values.sum()
                1 -> {
                    result = 1
                    for (value in values) {
                        result *= value
                    }
                }
                2 -> result = values.minOrNull() ?: 0
                3 -> result = values.maxOrNull() ?: 0
                5 -> {
                    require(values.size == 2) { "Greater than only supports two subpackets" }
                    return if (values[0] > values[1]) 1 else 0
                }
                6 -> {
                    require(values.size == 2) { "Lesser than only supports two subpackets" }
                    return if (values[0] < values[1]) 1 else 0
                }
                7 -> {
                    require(values.size == 2) { "Equal to only supports two subpackets" }
                    return if (values[0] == values[1]) 1 else 0
                }
            }
            return result
        }
    }

    // Read out the packet
    val result = readPacket()
    println("P1 $totalVersion")
    println("P2 $result")
}