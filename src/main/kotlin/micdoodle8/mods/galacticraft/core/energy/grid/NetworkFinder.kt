package micdoodle8.mods.galacticraft.core.energy.grid

import java.util.HashSet
import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.*

open class NetworkFinder
constructor(@JvmField var worldObj: World, @JvmField var start: BlockVec3, private val toIgnore: BlockVec3?) {
    private val theDim = 0

    private val iterated: MutableSet<BlockVec3?> = HashSet<BlockVec3?>()

    @JvmField
    var found: MutableList<IConductor?> = LinkedList<IConductor?>()

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

                val tileEntity: TileEntity? = worldObj.getBlockEntity(BlockPos(obj!!.x, obj.y, obj.z))

                if (tileEntity is IConductor) {
                    found.add(tileEntity as IConductor)
                    loopAll(obj.x, obj.y, obj.z, dir xor 1)
                }
            }
        }
    }

    open fun exploreNetwork(): MutableList<IConductor?> {
        if (start.getTileEntity(worldObj) is IConductor) {
            iterated.add(start)
            iterated.add(toIgnore)
            found.add(start.getTileEntity(worldObj) as IConductor?)
            loopAll(start.x, start.y, start.z, 6)
        }

        return found
    }
}
