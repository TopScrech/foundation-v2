package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class SlotRocketBenchResult constructor(
    private val thePlayer: EntityPlayer,
    private val craftMatrix: IInventory,
    par3IInventory: IInventory,
    par4: Int,
    par5: Int,
    par6: Int
) : Slot(par3IInventory, par4, par5, par6) {
    override fun isItemValid(par1ItemStack: ItemStack): Boolean {
        return false
    }


    override fun onTake(par1EntityPlayer: EntityPlayer, stack: ItemStack): ItemStack {
        for (var2: Int in 0..<this.craftMatrix.getSizeInventory()) {
            val var3: ItemStack = this.craftMatrix.getStackInSlot(var2)

            if (!var3.isEmpty()) {
                this.craftMatrix.decrStackSize(var2, 1)

                if (var3.getItem().hasContainerItem(var3)) {
                    val var4: ItemStack = ItemStack(var3.getItem().getContainerItem())

                    if (!this.thePlayer.inventory.addItemStackToInventory(var4)) {
                        if (this.craftMatrix.getStackInSlot(var2).isEmpty()) {
                            this.craftMatrix.setInventorySlotContents(var2, var4)
                        } else {
                            this.thePlayer.entityDropItem(var4, 0.0f)
                        }
                    }
                }
            }
        }

        return stack
    }
}
