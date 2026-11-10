package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerOxygenCompressor constructor(
    par1InventoryPlayer: InventoryPlayer,
    compressor: TileEntityOxygenCompressor,
    player: EntityPlayer
) : Container() {
    private val tileEntity: TileBaseElectricBlock

    init {
        this.tileEntity = compressor
        this.addSlotToContainer(Slot(compressor, 0, 133, 71))
        this.addSlotToContainer(SlotSpecific(compressor, 1, 47, 27, IItemElectric::class.java))
        this.addSlotToContainer(SlotSpecific(compressor, 2, 17, 27, IItemOxygenSupply::class.java))

        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(
                    Slot(
                        par1InventoryPlayer,
                        var4 + var3 * 9 + 9,
                        8 + var4 * 18,
                        20 + 84 + var3 * 18
                    )
                )
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 20 + 142))
            ++var3
        }

        compressor.openInventory(player)
    }

    override fun canInteractWith(var1: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(var1)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val stack: ItemStack = slot.getStack()
            var2 = stack.copy()
            var movedToMachineSlot: Boolean = false

            if (par1 < 3) {
                if (!this.mergeItemStack(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(stack.getItem())) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else if (stack.getItem() is IItemOxygenSupply) {
                    if (!this.mergeItemStack(stack, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else if (stack.getItem() is ItemOxygenTank && stack.getItemDamage() > 0) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else {
                    if (par1 < b - 9) {
                        if (!this.mergeItemStack(stack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(stack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stack.getCount() == 0) {
                // Needed where tile has inventoryStackLimit of 1
                if (movedToMachineSlot && var2.getCount() > 1) {
                    val remainder: ItemStack = var2.copy()
                    remainder.shrink(1)
                    slot.putStack(remainder)
                } else {
                    slot.putStack(ItemStack.EMPTY)
                }
            } else {
                slot.onSlotChanged()
            }

            if (stack.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, stack)
        }

        return var2
    }
}
