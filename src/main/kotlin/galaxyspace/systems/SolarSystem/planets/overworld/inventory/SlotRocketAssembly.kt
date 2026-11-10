package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.inventory.SlotRocketAssembly
import galaxyspace.core.GSItems
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

open class SlotRocketAssembly constructor(
    par2IInventory: IInventory,
    private val index: Int,
    par4: Int,
    par5: Int
) : Slot(par2IInventory, index, par4, par5) {
    override fun isItemValid(par1ItemStack: ItemStack): Boolean {
        when (this.index) {
            2 -> return par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 0 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 5 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 10 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 15 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 20

            3, 4 -> return par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 1 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 6 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 11 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 16 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 21

            5 -> return par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 2 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 7 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 12 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 17 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 22

            6, 7 -> return par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 3 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 8 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 13 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 18 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 23

            8, 9 -> return par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 4 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 9 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 14 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 19 || par1ItemStack.getItem() === GSItems.ROCKET_PARTS && par1ItemStack.getItemDamage() == 24

            10 -> return par1ItemStack.getItem() === GSItems.ROCKET_MODULES && par1ItemStack.getItemDamage() == 2 || par1ItemStack.getItem() === GSItems.ROCKET_MODULES && par1ItemStack.getItemDamage() == 8 || par1ItemStack.getItem() === GSItems.ROCKET_MODULES && par1ItemStack.getItemDamage() == 9 || par1ItemStack.getItem() === GSItems.ROCKET_MODULES && par1ItemStack.getItemDamage() == 10

            11 -> return par1ItemStack.getItem() === Item.getItemFromBlock(Blocks.CHEST)
            12 -> return par1ItemStack.getItem() === Item.getItemFromBlock(Blocks.CHEST)
            13 -> return par1ItemStack.getItem() === Item.getItemFromBlock(Blocks.CHEST)
        }

        return false
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    override fun getSlotStackLimit(): Int {
        return 1
    }
}
