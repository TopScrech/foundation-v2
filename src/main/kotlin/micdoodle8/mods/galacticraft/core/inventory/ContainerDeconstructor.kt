package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerDeconstructor constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityDeconstructor
) : Container() {
    init {
        // Battery Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 55, 75, IItemElectric::class.java))

        // Input slot
        this.addSlot(Slot(tileEntity, 1, 26, 36))

        var count: Int = 2
        for (x: Int in 0..2) {
            for (y: Int in 0..2) {
                this.addSlot(Slot(tileEntity, count++, 112 + y * 18, 18 + x * 18))
            }
        }

        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 117 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 175))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.hasItem()) {
            val var4: ItemStack = var3.getItem()
            var2 = var4.copy()

            if (par1 <= 10) {
                if (!this.moveItemStackTo(var4, 11, 47, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 < 38) {
                    if (!this.moveItemStackTo(var4, 1, 2, false) && !this.moveItemStackTo(var4, 38, 47, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(var4, 1, 2, false) && !this.moveItemStackTo(var4, 11, 38, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var4.getCount() == 0) {
                var3.set(ItemStack.EMPTY)
            } else {
                var3.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
