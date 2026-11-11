package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.items.ItemParaChute
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class SlotTankRefill constructor(par3IInventory: IInventory, par4: Int, par5: Int, par6: Int) :
    Slot(par3IInventory, par4, par5, par6) {
    override fun mayPlace(par1ItemStack: ItemStack): Boolean {
        if (this.slotNumber == 49) {
            return par1ItemStack.getItem() is ItemParaChute
        }

        return OxygenUtil.isItemValidForPlayerTankInv(this.slotNumber - 45, par1ItemStack)
    }

    override fun getMaxStackSize(): Int {
        return 1
    }
}
