package micdoodle8.mods.galacticraft.api.block

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IPartialSealableBlock {
    fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean
}
