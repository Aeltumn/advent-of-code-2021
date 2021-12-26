/** A point on a board. X is right. Y is up. */
data class Point(val x: Int, val y: Int) {

    operator fun plus(other: Point): Point =
        Point(x + other.x, y + other.y)
}

/** A node on a graph. */
class Node(
    val id: String,
    val connections: MutableSet<Node> = mutableSetOf(),
) {

    override fun toString(): String = id
}