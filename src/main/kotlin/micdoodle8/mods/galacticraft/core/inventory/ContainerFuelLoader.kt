package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerFuelLoader constructor(
    par1InventoryPlayer: InventoryPlayer,
    fuelLoader: TileEntityFuelLoader
) : Container() {
    private val tileEntity: TileBaseElectricBlock

    init {
        this.tileEntity = fuelLoader
        this.addSlotToContainer(SlotSpecific(fuelLoader, 0, 51, 55, IItemElectric::class.java))
        this.addSlotToContainer(Slot(fuelLoader, 1, 7, 12))

        var var6: Int
        var var7: Int

        // Player inv:
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlotToContainer(
                    Slot(
                        par1InventoryPlayer,
                        var7 + var6 * 9 + 9,
                        8 + var7 * 18,
                        31 + 58 + var6 * 18
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 31 + 116))
            ++var6
        }
    }

    override fun canInteractWith(var1: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(var1)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par2: Int): ItemStack {
        var var3: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par2)

        if (slot != null && slot.getHasStack()) {
            val var5: ItemStack = slot.getStack()
            var3 = var5.copy()
            var movedToMachineSlot: Boolean = false

            if (par2 < 2) {
                if (!this.mergeItemStack(var5, 2, 38, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(var5.getItem())) {
                    if (!this.mergeItemStack(var5, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else {
                    if (FluidUtil.isFuelContainerAny(var5)) {
                        if (!this.mergeItemStack(var5, 1, 2, false)) {
                            return ItemStack.EMPTY
                        }
                        movedToMachineSlot = true
                    } else if (par2 < 29) {
                        if (!this.mergeItemStack(var5, 29, 38, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(var5, 2, 29, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var5.getCount() == 0) {
                // Needed where tile has inventoryStackLimit of 1
                if (movedToMachineSlot && var3.getCount() > 1) {
                    val remainder: ItemStack = var3.copy()
                    remainder.shrink(1)
                    slot.putStack(remainder)
                } else {
                    slot.putStack(ItemStack.EMPTY)
                }
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
