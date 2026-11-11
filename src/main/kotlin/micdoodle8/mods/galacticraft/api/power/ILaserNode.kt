package micdoodle8.mods.galacticraft.api.power

import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity

interface ILaserNode : IEnergyHandlerGC {
    fun getInputPoint(): Vector3?

    fun getOutputPoint(offset: Boolean): Vector3?

    fun getTarget(): ILaserNode?

    fun getTile(): TileEntity?

    fun canConnectTo(node: ILaserNode?): Boolean

    fun getColor(): Vector3?

    fun addNode(node: ILaserNode?)

    fun removeNode(node: ILaserNode?)

    fun compareTo(otherNode: ILaserNode?, origin: BlockVec3?): Int
}
