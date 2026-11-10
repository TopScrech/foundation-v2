package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType

import micdoodle8.mods.galacticraft.api.entity.IRocketType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerRocketInventory constructor(
    private val lowerChestInventory: IInventory,
    private val spaceshipInv: IInventory,
    private val rocketType: IRocketType.EnumRocketType,
    player: EntityPlayer
) : Container() {
    init {
        spaceshipInv.openInventory(player)

        when (rocketType.getInventorySpace() - 2) {
            0 -> this.addSlotsNoInventory()
            18 -> this.addSlotsWithInventory(rocketType.getInventorySpace())
            36 -> this.addSlotsWithInventory(rocketType.getInventorySpace())
            54 -> this.addSlotsWithInventory(rocketType.getInventorySpace())
        }
    }

    private fun addSlotsNoInventory() {
        var var4: Int
        var var5: Int

        var4 = 0
        while (var4 < 3) {
            var5 = 0
            while (var5 < 9) {
                this.addSlotToContainer(
                    Slot(
                        this.lowerChestInventory,
                        var5 + (var4 + 1) * 9,
                        8 + var5 * 18,
                        84 + var4 * 18 - 34
                    )
                )
                ++var5
            }
            ++var4
        }

        var4 = 0
        while (var4 < 9) {
            this.addSlotToContainer(Slot(this.lowerChestInventory, var4, 8 + var4 * 18, 142 - 34))
            ++var4
        }
    }

    private fun addSlotsWithInventory(slotCount: Int) {
        var var4: Int
        var var5: Int
        val lastRow: Int = slotCount / 9
        val ySize: Int = 145 + (this.rocketType.getInventorySpace() - 2) * 2

        var4 = 0
        while (var4 < lastRow) {
            var5 = 0
            while (var5 < 9) {
                this.addSlotToContainer(Slot(this.spaceshipInv, var5 + var4 * 9, 8 + var5 * 18, 50 + var4 * 18))
                ++var5
            }
            ++var4
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
                        ySize - 82 + var4 * 18
                    )
                )
                ++var5
            }
            ++var4
        }

        var4 = 0
        while (var4 < 9) {
            this.addSlotToContainer(Slot(this.lowerChestInventory, var4, 8 + var4 * 18, ySize - 24))
            ++var4
        }
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.spaceshipInv.isUsableByPlayer(par1EntityPlayer)
    }

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
