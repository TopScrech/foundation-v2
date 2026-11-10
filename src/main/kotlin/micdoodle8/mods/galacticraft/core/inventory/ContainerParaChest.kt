package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerParaChest constructor(
    par1IInventory: IInventory,
    private val parachestInventory: IInventory,
    player: EntityPlayer
) : Container() {
    @JvmField
    var numRows: Int

    init {
        this.numRows = (parachestInventory.getSizeInventory() - 3) / 9
        parachestInventory.openInventory(player)
        val i: Int = (this.numRows - 4) * 18 + 19
        var j: Int
        var k: Int

        j = 0
        while (j < this.numRows) {
            k = 0
            while (k < 9) {
                this.addSlotToContainer(Slot(parachestInventory, k + j * 9, 8 + k * 18, 18 + j * 18))
                ++k
            }
            ++j
        }

        this.addSlotToContainer(
            Slot(
                parachestInventory,
                parachestInventory.getSizeInventory() - 3,
                125 + 0 * 18,
                (if (this.numRows == 0) 24 else 26) + this.numRows * 18
            )
        )
        this.addSlotToContainer(
            Slot(
                parachestInventory,
                parachestInventory.getSizeInventory() - 2,
                125 + 1 * 18,
                (if (this.numRows == 0) 24 else 26) + this.numRows * 18
            )
        )
        this.addSlotToContainer(
            Slot(
                parachestInventory,
                parachestInventory.getSizeInventory() - 1,
                75,
                (if (this.numRows == 0) 24 else 26) + this.numRows * 18
            )
        )

        j = 0
        while (j < 3) {
            k = 0
            while (k < 9) {
                this.addSlotToContainer(
                    Slot(
                        par1IInventory,
                        k + j * 9 + 9,
                        8 + k * 18,
                        (if (this.numRows == 0) 116 else 118) + j * 18 + i
                    )
                )
                ++k
            }
            ++j
        }

        j = 0
        while (j < 9) {
            this.addSlotToContainer(Slot(par1IInventory, j, 8 + j * 18, (if (this.numRows == 0) 174 else 176) + i))
            ++j
        }
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.parachestInventory.isUsableByPlayer(par1EntityPlayer)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par2)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val itemstack1: ItemStack = slot.getStack()
            itemstack = itemstack1.copy()

            if (par2 < this.parachestInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.parachestInventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }
        }

        return itemstack
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    override fun onContainerClosed(par1EntityPlayer: EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer)
        this.parachestInventory.closeInventory(par1EntityPlayer)
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    open fun getparachestInventory(): IInventory? {
        return this.parachestInventory
    }
}
