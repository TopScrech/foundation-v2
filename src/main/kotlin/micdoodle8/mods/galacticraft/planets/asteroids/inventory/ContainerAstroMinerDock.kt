package micdoodle8.mods.galacticraft.planets.asteroids.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerAstroMinerDock constructor(par1InventoryPlayer: InventoryPlayer, tile: IInventory) :
    Container() {
    private val tileEntity: TileEntityMinerBase

    init {
        this.tileEntity = tile as TileEntityMinerBase
        this.addSlotToContainer(SlotSpecific(tile, 0, 230, 108, IItemElectric::class.java))

        var i: Int
        var j: Int

        i = 0
        while (i < 6) {
            j = 0
            while (j < 12) {
                this.addSlotToContainer(Slot(tile, j + i * 12 + 1, 8 + j * 18, 18 + i * 18))
                ++j
            }
            ++i
        }

        // Player inv:
        i = 0
        while (i < 3) {
            j = 0
            while (j < 9) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 139 + i * 18))
                ++j
            }
            ++i
        }

        i = 0
        while (i < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, i, 8 + i * 18, 58 + 139))
            ++i
        }
    }

    override fun canInteractWith(var1: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(var1)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par2)
        val b: Int = TileEntityMinerBase.HOLDSIZE + 1

        if (slot != null && slot.getHasStack()) {
            val var5: ItemStack = slot.getStack()
            var3 = var5.copy()

            if (par2 < b) {
                if (!this.mergeItemStack(var5, b, b + 36, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(var5.getItem())) {
                    if (!this.mergeItemStack(var5, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par2 < b + 27) {
                    if (!this.mergeItemStack(var5, 1, b, false) && !this.mergeItemStack(var5, b + 27, b + 36, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.mergeItemStack(var5, 1, b, false) && !this.mergeItemStack(var5, b, b + 27, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (var5.isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (var5.getCount() == var3.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var5)
        }

        return var3
    }
}
