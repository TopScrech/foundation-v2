package micdoodle8.mods.galacticraft.api.item

import net.minecraft.item.ItemStack

interface IItemElectric {
    /**
     * Adds energy to an item. Returns the quantity of energy that was accepted.
     * This should always return 0 if the item cannot be externally charged.
     *
     * @param itemStack  ItemStack to be charged.
     * @param energy     Maximum amount of energy to be sent into the item.
     * @param doRecharge If false, the charge will only be simulated.
     * @return Amount of energy that was accepted by the item.
     */
    fun recharge(itemStack: ItemStack?, energy: Float, doRecharge: Boolean): Float

    /**
     * Removes energy from an item. Returns the quantity of energy that was
     * removed. This should always return 0 if the item cannot be externally
     * discharged.
     *
     * @param itemStack   ItemStack to be discharged.
     * @param energy      Maximum amount of energy to be removed from the item.
     * @param doDischarge If false, the discharge will only be simulated.
     * @return Amount of energy that was removed from the item.
     */
    fun discharge(itemStack: ItemStack?, energy: Float, doDischarge: Boolean): Float

    /**
     * Get the amount of energy currently stored in the item.
     */
    fun getElectricityStored(theItem: ItemStack?): Float

    /**
     * Get the max amount of energy that can be stored in the item.
     */
    fun getMaxElectricityStored(theItem: ItemStack?): Float

    /**
     * Sets the amount of energy in the ItemStack.
     *
     * @param itemStack - the ItemStack.
     * @param joules    - Amount of electrical energy.
     */
    fun setElectricity(itemStack: ItemStack?, joules: Float)

    /**
     * @return the energy request this ItemStack demands.
     */
    fun getTransfer(itemStack: ItemStack?): Float

    /**
     * @return The energy tier this item runs on (must be 1 or 2)
     */
    fun getTierGC(itemStack: ItemStack?): Int
}
