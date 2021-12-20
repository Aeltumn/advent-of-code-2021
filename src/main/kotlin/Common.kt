
/** A point on a board. X is right. Y is up. */
public data class Point(val x: Int, val y: Int) {

    public operator fun plus(other: Point): Point =
        Point(x + other.x, y + other.y)
}

/** A node on a graph. */
public class Node(
    val id: String,
    val connections: MutableSet<Node> = mutableSetOf(),
) {

    override fun toString(): String = id
}