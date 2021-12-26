import java.io.File

/**
 * Represents a node in the tree.
 */
private class TreeNode(
    var parent: TreeNode? = null,
    var value: Int? = null,
    var left: TreeNode? = null,
    var right: TreeNode? = null,
) {

    val magnitude: Int
        get() = when {
            value != null -> value ?: 0
            else -> 3 * (left?.magnitude ?: 0) + 2 * (right?.magnitude ?: 0)
        }

    fun copy(): TreeNode {
        val newTree = TreeNode(null, value)
        newTree.left = left?.copy()?.also { it.parent = newTree }
        newTree.right = right?.copy()?.also { it.parent = newTree }
        return newTree
    }

    override fun toString(): String = when {
        value != null -> "$value"
        else -> "[$left, $right]"
    }

    operator fun plus(other: TreeNode): TreeNode {
        // Create new parent
        val newParent = TreeNode()
        newParent.left = this.copy()
        newParent.right = other.copy()

        // Update parent references
        newParent.left?.parent = newParent
        newParent.right?.parent = newParent
        return newParent.reduce()
    }

    /** Returns the node directly to the left of this one. */
    fun moveLeft(): TreeNode? = if (this == parent?.left) {
        parent?.moveLeft()
    } else {
        var target = parent?.left
        while (target != null && target.value == null) {
            target = target.right
        }
        target
    }

    /** Returns the node directly to the right of this one. */
    fun moveRight(): TreeNode? = if (this == parent?.right) {
        parent?.moveRight()
    } else {
        var target = parent?.right
        while (target != null && target.value == null) {
            target = target.left
        }
        target
    }

    /** Reduces a tree. */
    fun reduce(): TreeNode {
        var success: Boolean
        do {
            success = false

            /**
             * Tests the given node for explosion.
             */
            fun testExplosion(node: TreeNode, depth: Int = 0) {
                // If we've found something we end iterating immediately
                if (success) return

                // Ignore nodes with values
                if (node.value != null) return

                // If the depth is 4 or greater we explode this node
                if (depth >= 4) {
                    // Iterate deeper into the left as far as possible
                    if (node.left != null && node.left?.value == null) {
                        testExplosion(node.left!!, depth + 1)
                    } else {
                        // We're in the leftmost explodable node, explode it!
                        require(node.left?.value != null && node.right?.value != null) { "Can't explode non-literal pair" }
                        val leftTarget = node.moveLeft()
                        val rightTarget = node.moveRight()
                        val newNode = TreeNode(node.parent, 0)
                        leftTarget?.apply {
                            requireNotNull(value) { "Found left target $this with no value" }
                            value = value!! + (node.left?.value ?: 0)
                        }
                        rightTarget?.apply {
                            requireNotNull(value) { "Found right target $this with no value" }
                            value = value!! + (node.right?.value ?: 0)
                        }

                        if (node.parent?.left == node) {
                            node.parent?.left = newNode
                        } else {
                            node.parent?.right = newNode
                        }
                        success = true
                        return
                    }
                }

                // Test the left side and then the right
                if (node.left != null) {
                    testExplosion(node.left!!, depth + 1)
                }
                if (node.right != null) {
                    testExplosion(node.right!!, depth + 1)
                }
            }

            /**
             * Tests the given node for splitting.
             */
            fun testSplitting(node: TreeNode) {
                // If we've found something we end iterating immediately
                if (success) return

                // Split the node
                if (node.value != null && node.value!! >= 10) {
                    val newNode = TreeNode(node.parent)
                    newNode.left = TreeNode(newNode, node.value!! / 2)
                    newNode.right = TreeNode(newNode, node.value!! / 2 + node.value!! % 2)

                    if (node.parent?.left == node) {
                        node.parent?.left = newNode
                    } else {
                        node.parent?.right = newNode
                    }
                    success = true
                    return
                }

                // Test the left side and then the right
                if (node.left != null) {
                    testSplitting(node.left!!)
                }
                if (node.right != null) {
                    testSplitting(node.right!!)
                }
            }

            // Test for explosion first, splitting second
            testExplosion(this)
            testSplitting(this)
        } while (success)
        return this
    }
}

/**
 * Builds a new tree from the input string.
 */
private fun String.buildTree(): TreeNode {
    val leftStack = mutableListOf<Boolean>()
    var left = true
    var head = TreeNode()

    for (char in substring(1, length - 1).toCharArray()) {
        if (char.isDigit()) {
            // Literal
            if (left) {
                head.left = TreeNode(head, char.digitToInt())
            } else {
                head.right = TreeNode(head, char.digitToInt())
            }
        } else if (char == ',') {
            // Move to right
            require(left) { "Can't move to the right twice" }
            left = false
        } else if (char == '[') {
            // New level
            val new = TreeNode(head)
            if (left) {
                head.left = new
            } else {
                head.right = new
            }

            // Move into new level
            leftStack.add(left)
            left = true
            head = new
        } else if (char == ']') {
            // End level
            require(!left) { "Can't end level on the left" }
            head = head.parent!!
            left = leftStack.removeLast()
        }
    }
    return head
}

/**
 * Day 18 - Snailfish
 */
fun main() {
    val input = File("src/main/resources/day18").readLines().map { it.buildTree() }

    // Add up all values
    var value = input[0]
    for (i in 1 until input.size) {
        value += input[i]
    }

    println("P1 ${value.magnitude}")

    // Try out all combinations
    var record = 0
    for (a in input) {
        for (b in input) {
            // We can't use the same number twice
            if (a == b) continue

            val magni = (a + b).magnitude
            if (magni >= record) {
                record = magni
            }
        }
    }

    println("P2 $record")
}