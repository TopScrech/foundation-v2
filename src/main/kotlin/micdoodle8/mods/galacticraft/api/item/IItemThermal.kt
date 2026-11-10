package micdoodle8.mods.galacticraft.api.item

import net.minecraft.item.ItemStack

interface IItemThermal {
    /**
     * The thermal strength of this armor. Value for first tier thermal padding is 1.
     */
    fun getThermalStrength(): Int

    /**
     * Returns whether the provided itemstack is valid for the armor slot:
     *
     * @param stack
     * The item to test
     * @param armorSlot
     * 0 - helmet
     * 1 - chestplate
     * 2 - leggings
     * 3 - boots
     */
    fun isValidForSlot(stack: ItemStack?, armorSlot: Int): Boolean
}
