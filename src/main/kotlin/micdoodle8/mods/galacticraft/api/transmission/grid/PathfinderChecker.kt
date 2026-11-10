package micdoodle8.mods.galacticraft.api.transmission.grid

import java.util.Arrays
import java.util.HashSet

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import java.util.*

/**
 * Check if a conductor connects with another.
 *
 * @author Calclavia
 */
open class PathfinderChecker constructor(
    world: World,
    targetConnector: INetworkConnection?,
    networkType: NetworkType?,
    vararg ignoreConnector: INetworkConnection?
) : Pathfinder(object : IPathCallBack {
    override fun getConnectedNodes(finder: Pathfinder?, currentNode: BlockVec3?): MutableSet<BlockVec3?> {
        val neighbors: MutableSet<BlockVec3?> = HashSet<BlockVec3?>()

        for (i: Int in 0..5) {
            val direction: EnumFacing = EnumFacing.byIndex(i)
            val position: BlockVec3 = (currentNode)!!.clone().modifyPositionFromSide(direction)
            val connectedBlock: TileEntity? = position.getTileEntity(world)

            if (connectedBlock is ITransmitter && !Arrays.asList<INetworkConnection?>(*ignoreConnector)
                    .contains(connectedBlock)
            ) {
                if ((connectedBlock as ITransmitter).canConnect(direction.getOpposite(), networkType)) {
                    neighbors.add(position)
                }
            }
        }

        return neighbors
    }

    override fun onSearch(finder: Pathfinder?, node: BlockVec3?): Boolean {
        if ((node)!!.getTileEntity(world) === targetConnector) {
            ((finder)!!.results)!!.add(node)
            return true
        }

        return false
    }
})
