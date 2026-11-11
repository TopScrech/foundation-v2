package micdoodle8.mods.galacticraft.api.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World

interface ITerraformableBlock {
    /**
     * Determines if the block as this position is terraformable. This will only
     * be called for blocks inside the terraformer bubble. It is recommended
     * that you test whether the block can see the surface before returning
     * true.
     *
     * @param world World that the block is a part of
     * @param pos The position of the block
     * @return True if the block can be terraformed, false if not.
     */
    fun isTerraformable(world: World?, pos: BlockPos?): Boolean
}
