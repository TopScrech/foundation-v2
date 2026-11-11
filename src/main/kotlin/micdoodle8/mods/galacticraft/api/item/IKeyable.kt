package micdoodle8.mods.galacticraft.api.item

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing

/**
 * Implement into tile entities that do something upon being activated by a key
 */
interface IKeyable {
    /**
     * @return -1 for any tier, or return tier required for key activated to
     * pass
     */
    fun getTierOfKeyRequired(): Int

    /**
     * called when key of correct tier is clicked
     *
     * @param key  the key itemstack
     * @param face the block face clicked
     * @return true if something was done, false if not
     */
    fun onValidKeyActivated(player: EntityPlayer?, key: ItemStack?, face: EnumFacing?): Boolean

    /**
     * called when player is not holding correct tier of key, or any key at all
     *
     * @param player
     * @return true if something was done, false if not
     */
    fun onActivatedWithoutKey(player: EntityPlayer?, face: EnumFacing?): Boolean

    fun canBreak(): Boolean
}
