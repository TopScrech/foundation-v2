package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class SlotExtendedInventory constructor(par2IInventory: IInventory, par3: Int, par4: Int, par5: Int) :
    Slot(par2IInventory, par3, par4, par5) {
    override fun getSlotStackLimit(): Int {
        return 1
    }

    override fun isItemValid(itemstack: ItemStack): Boolean {
        val gearID: Int = GalacticraftRegistry.findMatchingGearID(itemstack, getTypeFromSlot())

        if (gearID >= 0) {
            return true
        }

        return false
    }

    private fun getTypeFromSlot(): EnumExtendedInventorySlot? {
        when (this.getSlotIndex()) {
            0 -> return EnumExtendedInventorySlot.MASK
            1 -> return EnumExtendedInventorySlot.GEAR
            2 -> return EnumExtendedInventorySlot.LEFT_TANK
            3 -> return EnumExtendedInventorySlot.RIGHT_TANK
            4 -> return EnumExtendedInventorySlot.PARACHUTE
            5 -> return EnumExtendedInventorySlot.FREQUENCY_MODULE
            6 -> return EnumExtendedInventorySlot.THERMAL_HELMET
            7 -> return EnumExtendedInventorySlot.THERMAL_CHESTPLATE
            8 -> return EnumExtendedInventorySlot.THERMAL_LEGGINGS
            9 -> return EnumExtendedInventorySlot.THERMAL_BOOTS
            10 -> return EnumExtendedInventorySlot.SHIELD_CONTROLLER
        }

        return null
    }
}
