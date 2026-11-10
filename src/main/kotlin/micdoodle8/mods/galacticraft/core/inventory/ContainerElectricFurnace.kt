package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes

open class ContainerElectricFurnace constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityElectricFurnace
) : Container() {
    init {
        // Electric Input Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 8, 49, IItemElectric::class.java))

        // To be smelted
        this.addSlotToContainer(Slot(tileEntity, 1, 56, 25))

        // Smelting result
        this.addSlotToContainer(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 2, 109, 25))
        if (tileEntity.tierGC == 2) {
            this.addSlotToContainer(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 3, 127, 25))
        }
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142))
            ++var3
        }

        tileEntity.playersUsing.add(par1InventoryPlayer.player)
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
        this.tileEntity.playersUsing.remove(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)
        val off: Int = if (this.tileEntity.tierGC == 2) 1 else 0

        if (var3 != null && var3.getHasStack()) {
            val var4: ItemStack = var3.getStack()
            var2 = var4.copy()

            if (par1 >= 2 && par1 <= 2 + off) {
                if (!this.mergeItemStack(var4, 3 + off, 39 + off, true)) {
                    return ItemStack.EMPTY
                }

                var3.onSlotChange(var4, var2)
            } else if (par1 != 1 && par1 != 0) {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!FurnaceRecipes.instance().getSmeltingResult(var4).isEmpty()) {
                    if (!this.mergeItemStack(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 >= 3 + off && par1 < 30 + off) {
                    if (!this.mergeItemStack(var4, 30 + off, 39 + off, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 >= 30 + off && par1 < 39 + off && !this.mergeItemStack(
                        var4,
                        3 + off,
                        30 + off,
                        false
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(var4, 3 + off, 39 + off, false)) {
                return ItemStack.EMPTY
            }

            if (var4.getCount() == 0) {
                var3.putStack(ItemStack.EMPTY)
            } else {
                var3.onSlotChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
