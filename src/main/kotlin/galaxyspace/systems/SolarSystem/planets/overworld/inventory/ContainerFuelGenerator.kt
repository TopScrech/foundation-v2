package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityFuelGenerator
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerFuelGenerator constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityFuelGenerator
) : Container() {
    init {
        //this.addSlotToContainer(new SlotSpecific(tileEntity, 0, 7, 7, new ItemStack(GCItems.bucketFuel)));
        this.addSlotToContainer(Slot(tileEntity, 0, 16, 14))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 102 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 160))
            ++var3
        }
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
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

            if (par1 != 0) {
                if (!this.mergeItemStack(var4, 0, 1, false)) {
                    return ItemStack.EMPTY
                } else if (par1 >= 28) {
                    if (!this.mergeItemStack(var4, 1, 28, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.mergeItemStack(var4, 28, 37, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.mergeItemStack(var4, 1, 37, false)) {
                return ItemStack.EMPTY
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
