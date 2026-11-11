package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerGasGenerator constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityGasGenerator
) : Container() {
    init {
        //this.addSlot(new SlotSpecific(tileEntity, 0, 7, 7, new ItemStack(GCItems.bucketFuel)));
        this.addSlot(Slot(tileEntity, 0, 16, 14))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 102 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 160))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 != 0) {
                if (!this.moveItemStackTo(var4, 0, 1, false)) {
                    return ItemStack.EMPTY
                } else if (par1 >= 28) {
                    if (!this.moveItemStackTo(var4, 1, 28, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(var4, 28, 37, false)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(var4, 1, 37, false)) {
                return ItemStack.EMPTY
            }

            if (var4.getCount() == 0) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
