package micdoodle8.mods.galacticraft.core.inventory

import foundation.item.legacyItemValue

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerOxygenSealer constructor(
    par1InventoryPlayer: InventoryPlayer,
    sealer: TileEntityOxygenSealer
) : Container() {
    private val tileEntity: TileBaseElectricBlock

    init {
        this.tileEntity = sealer
        this.addSlot(SlotSpecific(sealer, 0, 33, 27, IItemElectric::class.java))
        this.addSlot(SlotSpecific(sealer, 1, 10, 27, IItemOxygenSupply::class.java))
        this.addSlot(SlotSpecific(sealer, 2, 56, 27, FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 20)))

        var var6: Int
        var var7: Int

        // Player inv:
        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var7 + var6 * 9 + 9,
                        8 + var7 * 18,
                        46 + 78 + var6 * 18
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 46 + 136))
            ++var6
        }
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

            if (par1 <= 2) {
                if (!this.moveItemStackTo(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(stack.getItem())) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (stack.getItem() is IItemOxygenSupply) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (stack.getItem() === GCItems.basicItem && stack.legacyItemValue == 20) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) {
                        return ItemStack.EMPTY
                    }
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
                slot.set(ItemStack.EMPTY)
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
