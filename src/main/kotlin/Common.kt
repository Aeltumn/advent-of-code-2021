
/** A point on a board. X is right. Y is up. */
public data class Point(val x: Int, val y: Int)

/** A node on a graph. */
public class Node(
    val id: String,
    val connections: MutableSet<Node> = mutableSetOf(),
) {

    override fun toString(): String = id
}