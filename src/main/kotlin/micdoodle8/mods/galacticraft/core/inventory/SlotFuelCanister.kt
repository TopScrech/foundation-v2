package micdoodle8.mods.galacticraft.core.inventory

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.items.ItemFuelCanister
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class SlotFuelCanister constructor(par1iInventory: IInventory, par2: Int, par3: Int, par4: Int) :
    Slot(par1iInventory, par2, par3, par4) {
    override fun mayPlace(par1ItemStack: ItemStack): Boolean {
        return par1ItemStack.getItem() is ItemFuelCanister && par1ItemStack.legacyItemValue > 0
    }
}
