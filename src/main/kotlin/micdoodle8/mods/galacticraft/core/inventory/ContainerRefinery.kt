package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tile.TileEntityRefinery
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerRefinery constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityRefinery,
    player: EntityPlayer
) : Container() {
    init {
        // Electric Input Slot
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 38, 51, IItemElectric::class.java))

        // To be smelted
        this.addSlotToContainer(Slot(tileEntity, 1, 7, 7))

        // Smelting result
        this.addSlotToContainer(Slot(tileEntity, 2, 153, 7))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(
                    Slot(
                        par1InventoryPlayer,
                        var4 + var3 * 9 + 9,
                        8 + var4 * 18,
                        104 + var3 * 18 - 18
                    )
                )
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 144))
            ++var3
        }

        tileEntity.openInventory(player)
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
        this.tileEntity.closeInventory(entityplayer)
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
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.getHasStack()) {
            val var4: ItemStack = slot.getStack()
            var2 = var4.copy()

            if (par1 < 3) {
                if (!this.mergeItemStack(var4, 3, 39, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 2) {
                    slot.onSlotChange(var4, var2)
                }
            } else {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.mergeItemStack(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (FluidUtil.isOilContainerAny(var4)) {
                        if (!this.mergeItemStack(var4, 1, 2, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (FluidUtil.isPartialContainer(var4, GCItems.fuelCanister)) {
                        if (!this.mergeItemStack(var4, 2, 3, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 < 30) {
                        if (!this.mergeItemStack(var4, 30, 39, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(var4, 3, 30, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
