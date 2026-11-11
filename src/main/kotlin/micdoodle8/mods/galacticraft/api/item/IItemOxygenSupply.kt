package micdoodle8.mods.galacticraft.api.item

import net.minecraft.world.item.ItemStack

interface IItemOxygenSupply {
    /*
     * Returns the amount of gas that this oxygen item is able to supply
     */
    fun discharge(itemStack: ItemStack?, amount: Int): Int

    fun getOxygenStored(theItem: ItemStack?): Int
}
