package micdoodle8.mods.galacticraft.api.transmission.grid

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

/** Searches connected, loaded conductors without pulling additional chunks into memory */
open class PathfinderChecker(
    world: Level,
    targetConnector: INetworkConnection?,
    networkType: NetworkType?,
    vararg ignoreConnector: INetworkConnection?
) : Pathfinder(object : IPathCallBack {
    private val ignored = ignoreConnector.toSet()

    override fun getConnectedNodes(finder: Pathfinder?, currentNode: BlockVec3?): MutableSet<BlockVec3?> {
        if (currentNode == null) return HashSet()
        val neighbors = LinkedHashSet<BlockVec3?>()
        for (direction in Direction.values()) {
            val position = currentNode.clone().modifyPositionFromSide(direction)
            val blockEntity = world.chunkSource.getChunkNow(position.x shr 4, position.z shr 4)
                ?.getBlockEntity(position.toBlockPos())
            if (blockEntity is ITransmitter && blockEntity !in ignored && blockEntity.canConnect(direction.opposite, networkType)) {
                neighbors.add(position)
            }
        }
        return neighbors
    }

    override fun onSearch(finder: Pathfinder?, node: BlockVec3?): Boolean {
        if (node == null || targetConnector == null) return false
        val blockEntity = world.chunkSource.getChunkNow(node.x shr 4, node.z shr 4)?.getBlockEntity(node.toBlockPos())
        if (blockEntity !== targetConnector) return false
        finder?.results?.add(node)
        return true
    }
})
