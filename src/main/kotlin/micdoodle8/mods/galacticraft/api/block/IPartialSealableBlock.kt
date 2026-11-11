package micdoodle8.mods.galacticraft.api.block

import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

interface IPartialSealableBlock {
    fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean
}
