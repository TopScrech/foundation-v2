package micdoodle8.mods.galacticraft.api.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.min

/**
 * Some helper functions for electric items.
 *
 * @author Calclavia
 */
object ElectricItemHelper {
    /**
     * Recharges an electric item.
     *
     * @param joules - The joules being provided to the electric item
     * @return The total amount of joules provided by the provider.
     */
    @JvmStatic
    fun chargeItem(itemStack: ItemStack?, joules: Float): Float {
        if (itemStack != null) {
            if (itemStack.getItem() is IItemElectric) {
                return (itemStack.getItem() as IItemElectric).recharge(
                    itemStack,
                    min((itemStack.getItem() as IItemElectric).getTransfer(itemStack), joules),
                    true
                )
            }
        }

        return 0f
    }

    /**
     * Decharges an electric item.
     *
     * @param joules - The joules being withdrawn from the electric item
     * @return The total amount of joules the provider received.
     */
    @JvmStatic
    fun dischargeItem(itemStack: ItemStack?, joules: Float): Float {
        if (itemStack != null) {
            if (itemStack.getItem() is IItemElectric) {
                return (itemStack.getItem() as IItemElectric).discharge(
                    itemStack,
                    min((itemStack.getItem() as IItemElectric).getMaxElectricityStored(itemStack), joules),
                    true
                )
            }
        }

        return 0f
    }

    /**
     * Returns an uncharged version of the electric item. Use this if you want
     * the crafting recipe to use a charged version of the electric item instead
     * of an empty version of the electric item
     *
     * @return An electrical ItemStack with a specific charge.
     */
    @JvmStatic
    fun getWithCharge(itemStack: ItemStack?, joules: Float): ItemStack? {
        if (itemStack != null) {
            if (itemStack.getItem() is IItemElectric) {
                (itemStack.getItem() as IItemElectric).setElectricity(itemStack, joules)
                return itemStack
            }
        }

        return itemStack
    }

    @JvmStatic
    fun getWithCharge(item: Item, joules: Float): ItemStack? {
        return getWithCharge(ItemStack(item), joules)
    }

    @JvmStatic
    fun getCloneWithCharge(itemStack: ItemStack, joules: Float): ItemStack? {
        return getWithCharge(itemStack.copy(), joules)
    }

    @JvmStatic
    fun getUncharged(itemStack: ItemStack?): ItemStack? {
        return getWithCharge(itemStack, 0f)
    }

    @JvmStatic
    fun getUncharged(item: Item): ItemStack? {
        return getUncharged(ItemStack(item))
    }
}
