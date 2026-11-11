package micdoodle8.mods.galacticraft.api.tile

import java.util.HashSet

import micdoodle8.mods.galacticraft.api.entity.IDockable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess

interface IFuelDock {
    fun getConnectedTiles(): HashSet<ILandingPadAttachable?>?

    fun isBlockAttachable(world: IBlockAccess?, pos: BlockPos?): Boolean

    fun getDockedEntity(): IDockable?

    fun dockEntity(entity: IDockable?)
}
