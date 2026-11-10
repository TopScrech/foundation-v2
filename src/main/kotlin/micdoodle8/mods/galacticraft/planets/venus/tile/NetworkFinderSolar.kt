package micdoodle8.mods.galacticraft.planets.venus.tile

import java.util.HashSet
import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

open class NetworkFinderSolar
constructor(@JvmField var worldObj: World, @JvmField var start: BlockVec3, private val toIgnore: BlockVec3?) {
    private val iterated: MutableSet<BlockVec3?> = HashSet<BlockVec3?>()

    @JvmField
    var found: MutableList<ITransmitter?> = LinkedList<ITransmitter?>()

    private fun loopAll(x: Int, y: Int, z: Int, dirIn: Int) {
        var obj: BlockVec3? = null
        for (dir: Int in 0..5) {
            if (dir == dirIn) {
                continue
            }
            when (dir) {
                0 -> obj = BlockVec3(x, y - 1, z)
                1 -> obj = BlockVec3(x, y + 1, z)
                2 -> obj = BlockVec3(x, y, z - 1)
                3 -> obj = BlockVec3(x, y, z + 1)
                4 -> obj = BlockVec3(x - 1, y, z)
                5 -> obj = BlockVec3(x + 1, y, z)
            }

            if (!iterated.contains(obj)) {
                iterated.add(obj)

                val tileEntity: TileEntity? = worldObj.getTileEntity(BlockPos(obj!!.x, obj.y, obj.z))

                if (tileEntity is ITransmitter && (tileEntity as ITransmitter).getNetworkType() == NetworkType.SOLAR_MODULE) {
                    found.add(tileEntity as ITransmitter)
                    loopAll(obj.x, obj.y, obj.z, dir xor 1)
                }
            }
        }
    }

    open fun exploreNetwork(): MutableList<ITransmitter?> {
        val tile: TileEntity? = start.getTileEntity(worldObj)
        if (tile is ITransmitter && (tile as ITransmitter).getNetworkType() == NetworkType.SOLAR_MODULE) {
            iterated.add(start)
            iterated.add(toIgnore)
            found.add(tile as ITransmitter)
            loopAll(start.x, start.y, start.z, 6)
        }

        return found
    }
}
