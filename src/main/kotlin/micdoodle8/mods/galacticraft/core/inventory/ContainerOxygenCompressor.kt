package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerOxygenCompressor constructor(
    par1InventoryPlayer: InventoryPlayer,
    compressor: TileEntityOxygenCompressor,
    player: EntityPlayer
) : Container() {
    private val tileEntity: TileBaseElectricBlock

    init {
        this.tileEntity = compressor
        this.addSlot(Slot(compressor, 0, 133, 71))
        this.addSlot(SlotSpecific(compressor, 1, 47, 27, IItemElectric::class.java))
        this.addSlot(SlotSpecific(compressor, 2, 17, 27, IItemOxygenSupply::class.java))

        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(
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
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 20 + 142))
            ++var3
        }

        compressor.openInventory(player)
    }

    override fun stillValid(var1: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(var1)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val stack: ItemStack = slot.getItem()
            var2 = stack.copy()
            var movedToMachineSlot: Boolean = false

            if (par1 < 3) {
                if (!this.moveItemStackTo(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(stack.getItem())) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else if (stack.getItem() is IItemOxygenSupply) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else if (stack.getItem() is ItemOxygenTank && ItemOxygenTank.used(stack) > 0) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                    movedToMachineSlot = true
                } else {
                    if (par1 < b - 9) {
                        if (!this.moveItemStackTo(stack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(stack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stack.getCount() == 0) {
                // Needed where tile has inventoryStackLimit of 1
                if (movedToMachineSlot && var2.getCount() > 1) {
                    val remainder: ItemStack = var2.copy()
                    remainder.shrink(1)
                    slot.set(remainder)
                } else {
                    slot.set(ItemStack.EMPTY)
                }
            } else {
                slot.setChanged()
            }

            if (stack.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, stack)
        }

        return var2
    }
}
