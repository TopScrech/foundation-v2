package micdoodle8.mods.galacticraft.api.block

import net.minecraft.world.level.block.state.BlockState as IBlockState

/**
 * Extend this block to allow planting of saplings on it. Requires
 */
interface IPlantableBlock {
    /**
     * @return amount of water blocks required for sapling to be growable. 4 is
     * default.
     */
    fun requiredLiquidBlocksNearby(): Int

    /**
     * @return is sapling can be placed on the provided metadata value, return true
     */
    fun isPlantable(state: IBlockState?): Boolean
}
