package micdoodle8.mods.galacticraft.api.transmission.grid

import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import java.util.ArrayDeque

/** Depth-first network traversal without consuming the JVM stack for every cable */
open class Pathfinder(@JvmField var callBackCheck: IPathCallBack) {
    @JvmField var closedSet: MutableSet<BlockVec3?> = HashSet()
    @JvmField var results: MutableSet<BlockVec3?> = HashSet()

    /** Returns true when the callback reports a completed search */
    open fun findNodes(currentNode: BlockVec3?): Boolean {
        if (currentNode == null) return false
        val pending = ArrayDeque<Iterator<BlockVec3?>>()
        var current: BlockVec3? = currentNode
        while (true) {
            if (current != null && closedSet.add(current)) {
                if (callBackCheck.onSearch(this, current)) return true
                pending.addLast(callBackCheck.getConnectedNodes(this, current)?.iterator() ?: emptySet<BlockVec3?>().iterator())
            }
            while (pending.isNotEmpty() && !pending.last().hasNext()) pending.removeLast()
            if (pending.isEmpty()) return false
            current = pending.last().next()
        }
    }

    open fun init(startNode: BlockVec3?): Pathfinder {
        findNodes(startNode)
        return this
    }

    open fun reset(): Pathfinder {
        closedSet.clear()
        results.clear()
        return this
    }
}
