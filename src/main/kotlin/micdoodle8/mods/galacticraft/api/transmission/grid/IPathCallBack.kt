package micdoodle8.mods.galacticraft.api.transmission.grid

import micdoodle8.mods.galacticraft.api.vector.BlockVec3

interface IPathCallBack {
    /**
     * @param finder      - The Pathfinder object.
     * @param currentNode - The node being iterated through.
     * @return A set of nodes connected to the currentNode. Essentially one
     * should return a set of neighboring nodes.
     */
    fun getConnectedNodes(finder: Pathfinder?, currentNode: BlockVec3?): MutableSet<BlockVec3?>?

    /**
     * Called when looping through nodes.
     *
     * @param finder - The Pathfinder.
     * @param node   - The node being searched.
     * @return True to stop the path finding operation.
     */
    fun onSearch(finder: Pathfinder?, node: BlockVec3?): Boolean
}
