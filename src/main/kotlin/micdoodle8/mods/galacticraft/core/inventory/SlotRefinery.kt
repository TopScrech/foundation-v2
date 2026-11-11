package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class SlotRefinery constructor(par1iInventory: IInventory, par2: Int, par3: Int, par4: Int) :
    Slot(par1iInventory, par2, par3, par4) {
    override fun mayPlace(stack: ItemStack): Boolean {
        return FluidUtil.isOilContainerAny(stack)
    }
}
