package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerOxygenDistributor constructor(
    par1InventoryPlayer: InventoryPlayer,
    distributor: TileEntityOxygenDistributor
) : Container() {
    private val tileEntity: TileBaseElectricBlock

    init {
        this.tileEntity = distributor
        this.addSlotToContainer(SlotSpecific(distributor, 0, 47, 27, IItemElectric::class.java))
        this.addSlotToContainer(SlotSpecific(distributor, 1, 17, 27, IItemOxygenSupply::class.java))

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
                        46 + 58 + var6 * 18
                    )
                )
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 46 + 116))
            ++var6
        }
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

            if (par1 < 2) {
                if (!this.mergeItemStack(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(stack.getItem())) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (stack.getItem() is IItemOxygenSupply) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
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
                slot.putStack(ItemStack.EMPTY)
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
