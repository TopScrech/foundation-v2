package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

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
                    this.addSlot(Slot(this.spaceshipInv, var5 + var4 * 9, 8 + var5 * 18, 50 + var4 * 18))
                    ++var5
                }
                ++var4
            }
        }

        var4 = 0
        while (var4 < 3) {
            var5 = 0
            while (var5 < 9) {
                this.addSlot(
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
            this.addSlot(Slot(this.lowerChestInventory, var4, 8 + var4 * 18, 107 + 14 + type * 36))
            ++var4
        }
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.spaceshipInv.stillValid(par1EntityPlayer)
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or
     * you will crash when someone does that.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val var4: Slot? = this.inventorySlots.get(par2)
        val b: Int = this.inventorySlots.size - 36

        if (var4 != null && var4.hasItem()) {
            val var5: ItemStack = var4.getItem()
            var3 = var5.copy()

            if (par2 < b) {
                if (!this.moveItemStackTo(var5, b, b + 36, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(var5, 0, b, false)) {
                return ItemStack.EMPTY
            }

            if (var5.getCount() == 0) {
                var4.set(ItemStack.EMPTY)
            } else {
                var4.setChanged()
            }
        }

        return var3
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    override fun removed(par1EntityPlayer: EntityPlayer) {
        super.removed(par1EntityPlayer)
        this.lowerChestInventory.closeInventory(par1EntityPlayer)
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    open fun getLowerChestInventory(): IInventory? {
        return this.lowerChestInventory
    }
}
