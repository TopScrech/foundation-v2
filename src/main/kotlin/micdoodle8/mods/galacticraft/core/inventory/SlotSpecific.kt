package micdoodle8.mods.galacticraft.core.inventory

import java.util.ArrayList
import java.util.Arrays

import cofh.redstoneflux.api.IEnergyContainerItem
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*

/**
 * Creates a slot with a specific amount of items that matches the slot's
 * requirements. Allows easy shift right clicking management and slot blocking
 * in classes. In your container you can use this.getSlot(i).isItemValid to
 * justify the player's shift clicking actions to match the slot.
 *
 * @author Calclavia
 */
open class SlotSpecific : Slot {
    @JvmField
    var validItemStacks: Array<ItemStack> = (arrayOfNulls<ItemStack>(0) as Array<ItemStack>)

    @JvmField
    var validClasses: Array<Class<*>> = (arrayOfNulls<Class<*>>(0) as Array<Class<*>>)

    @JvmField
    var isInverted: Boolean = false

    @JvmField
    var isMetadataSensitive: Boolean = false

    constructor(par2IInventory: IInventory, par3: Int, par4: Int, par5: Int, vararg itemStacks: ItemStack?) : super(
        par2IInventory,
        par3,
        par4,
        par5
    ) {
        this.setItemStacks(*itemStacks)
    }

    constructor(
        par2IInventory: IInventory,
        par3: Int,
        par4: Int,
        par5: Int,
        vararg validClasses: Class<*>
    ) : super(par2IInventory, par3, par4, par5) {
        var validClasses: Array<out Class<*>> = validClasses
        if (validClasses != null && Arrays.asList<Class<*>?>(*validClasses).contains(IItemElectric::class.java)) {
            try {
                if (EnergyConfigHandler.isRFAPILoaded()) {
                    val existing: ArrayList<Class<*>?> = ArrayList<Class<*>?>(Arrays.asList<Class<*>?>(*validClasses))
                    existing.add(IEnergyContainerItem::class.java)
                    validClasses = ((existing.toTypedArray<Class<*>?>() as Array<out Class<*>>?))!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        this.setClasses(*validClasses)
    }

    open fun setMetadataSensitive(): SlotSpecific {
        this.isMetadataSensitive = true
        return this
    }

    open fun setItemStacks(vararg validItemStacks: ItemStack?): SlotSpecific {
        this.validItemStacks = (validItemStacks as Array<ItemStack>)
        return this
    }

    open fun setClasses(vararg validClasses: Class<*>): SlotSpecific {
        this.validClasses = (validClasses as Array<Class<*>>)
        return this
    }

    open fun toggleInverted(): SlotSpecific {
        this.isInverted = !this.isInverted
        return this
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * the armor slots.
     */
    override fun mayPlace(compareStack: ItemStack): Boolean {
        var returnValue: Boolean = false

        for (itemStack: ItemStack in this.validItemStacks) {
            if (compareStack.isItemEqual(itemStack) || !this.isMetadataSensitive && compareStack.getItem() === itemStack.getItem()) {
                returnValue = true
                break
            }
        }

        if (!returnValue) {
            for (clazz: Class<*> in this.validClasses) {
                if (clazz.isInstance(compareStack.getItem())) {
                    returnValue = true
                    break
                }
            }
        }

        if (this.isInverted) {
            return !returnValue
        }

        return returnValue
    }
}
