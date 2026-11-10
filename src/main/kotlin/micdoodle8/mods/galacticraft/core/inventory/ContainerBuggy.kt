package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerBuggy constructor(
    private val lowerChestInventory: IInventory,
    private val spaceshipInv: IInventory,
    type: Int,
    player: EntityPlayer
) : Container() {
    init {
        spaceshipInv.openInventory(player)

        var var4: Int
        var var5: Int

        if (type != 0) {
            var4 = 0
            while (var4 < type * 2) {
                var5 = 0
                while (var5 < 9) {
                    this.addSlotToContainer(Slot(this.spaceshipInv, var5 + var4 * 9, 8 + var5 * 18, 50 + var4 * 18))
                    ++var5
                }
                ++var4
            }
        }

        var4 = 0
        while (var4 < 3) {
            var5 = 0
            while (var5 < 9) {
                this.addSlotToContainer(
                    Slot(
                        this.lowerChestInventory,
                        var5 + var4 * 9 + 9,
                        8 + var5 * 18,
                        49 + var4 * 18 + 14 + type * 36
                    )
                )
                ++var5
            }
            ++var4
        }

        var4 = 0
        while (var4 < 9) {
            this.addSlotToContainer(Slot(this.lowerChestInventory, var4, 8 + var4 * 18, 107 + 14 + type * 36))
            ++var4
        }
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.spaceshipInv.isUsableByPlayer(par1EntityPlayer)
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that.
     */
    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val var4: Slot? = this.inventorySlots.get(par2)
        val b: Int = this.inventorySlots.size - 36

        if (var4 != null && var4.getHasStack()) {
            val var5: ItemStack = var4.getStack()
            var3 = var5.copy()

            if (par2 < b) {
                if (!this.mergeItemStack(var5, b, b + 36, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(var5, 0, b, false)) {
                return ItemStack.EMPTY
            }

            if (var5.getCount() == 0) {
                var4.putStack(ItemStack.EMPTY)
            } else {
                var4.onSlotChanged()
            }
        }

        return var3
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    override fun onContainerClosed(par1EntityPlayer: EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer)
        this.lowerChestInventory.closeInventory(par1EntityPlayer)
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    open fun getLowerChestInventory(): IInventory? {
        return this.lowerChestInventory
    }
}
