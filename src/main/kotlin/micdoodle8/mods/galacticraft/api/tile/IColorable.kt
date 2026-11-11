package micdoodle8.mods.galacticraft.api.tile

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing

/**
 * Used for colored pipes to set/get colors
 */
interface IColorable {
    /**
     * Called when the color is changed
     */
    fun onColorUpdate()

    /**
     * Gets the color of this tile from the Block State
     *
     * @return the color of the tile, equivalent to the dye colors in vanilla
     * minecraft
     */
    fun getColor(state: IBlockState?): Byte

    /**
     * Called when a tile adjacent to this one has it's color changed
     *
     * @param direction the direction (relative to this tile) that was updated.
     */
    fun onAdjacentColorChanged(direction: EnumFacing?)
}
