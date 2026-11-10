package micdoodle8.mods.galacticraft.api.transmission.grid

import java.util.HashSet

import micdoodle8.mods.galacticraft.api.vector.BlockVec3

/**
 * A class that allows flexible ai for different positions. Compared to
 * AStar ai, this version is faster but does not calculated the most
 * optimal path.
 *
 * @author Calclavia
 */
open class Pathfinder
constructor(
    /**
     * A ai call back interface used to call back on paths.
     */
    @JvmField var callBackCheck: IPathCallBack
) {
    /**
     * A list of nodes that the pathfinder already went through.
     */
    @JvmField
    var closedSet: MutableSet<BlockVec3?>? = null

    /**
     * The resulted path found by the pathfinder. Could be null if no path was
     * found.
     */
    @JvmField
    var results: MutableSet<BlockVec3?>? = null

    init {
        this.reset()
    }

    /**
     * @return True on success finding, false on failure.
     */
    open fun findNodes(currentNode: BlockVec3?): Boolean {
        this.closedSet!!.add(currentNode)

        if (this.callBackCheck.onSearch(this, currentNode)) {
            return false
        }

        for (node: BlockVec3? in (this.callBackCheck.getConnectedNodes(this, currentNode))!!) {
            if (!this.closedSet!!.contains(node)) {
                if (this.findNodes(node)) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Called to execute the ai operation.
     */
    open fun init(startNode: BlockVec3?): Pathfinder {
        this.findNodes(startNode)
        return this
    }

    open fun reset(): Pathfinder {
        this.closedSet = HashSet<BlockVec3?>()
        this.results = HashSet<BlockVec3?>()
        return this
    }
}
