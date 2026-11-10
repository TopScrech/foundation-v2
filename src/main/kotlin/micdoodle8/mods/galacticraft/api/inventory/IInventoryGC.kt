package micdoodle8.mods.galacticraft.api.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory

/**
 * An interface for other mods to access the Galacticraft
 * extended inventory slots.
 *
 * (All normal IInventory methods will work)
 */
interface IInventoryGC : IInventory {
    /**
     * Drop only the Galacticraft items from the player's inventory.
     * @param player
     */
    fun dropExtendedItems(player: EntityPlayer?)

    /**
     * Make the implementing inventory a copy of the specified extended inventory.
     * @param par1InventoryPlayer  The inventory to copy
     */
    fun copyInventory(par1InventoryPlayer: IInventoryGC?)
}
