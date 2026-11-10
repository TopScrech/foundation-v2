package micdoodle8.mods.galacticraft.api.entity

import net.minecraft.util.math.BlockPos

/**
 * An entity which requires a hook into landing pad events should implement this interface
 */
interface ILandable : IDockable {
    /**
     * Called when the entity lands on a dock
     *
     * @param pos coordinates of the dock
     */
    fun landEntity(pos: BlockPos?)
}
