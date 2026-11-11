package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.world.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace

open class ContainerIngotCompressor constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityIngotCompressor
) : Container() {
    init {
        tileEntity.compressingCraftMatrix.craftingEventHandler = this

        for (x: Int in 0..2) {
            for (y: Int in 0..2) {
                this.addSlot(Slot(tileEntity.compressingCraftMatrix, y + x * 3, 19 + y * 18, 18 + x * 18))
            }
        }

        // Coal slot
        this.addSlot(Slot(tileEntity, 0, 55, 75))

        // Smelting result
        this.addSlot(SlotFurnaceOutput(par1InventoryPlayer.player, tileEntity, 1, 138, 38))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 110 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 168))
            ++var3
        }

        tileEntity.playersUsing.add(par1InventoryPlayer.player)
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
        this.tileEntity.playersUsing.remove(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(par1EntityPlayer)
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        super.slotsChanged(par1IInventory)
        this.tileEntity.updateInput()
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

            if (par1 <= 10) {
                if (!this.moveItemStackTo(var4, 11, 47, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 1) {
                    slot.onQuickCraft(var4, var2)
                }
            } else {
                if (TileEntityFurnace.getItemBurnTime(var4) > 0) {
                    if (!this.moveItemStackTo(var4, 9, 10, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 < 38) {
                    if (!this.moveItemStackTo(var4, 0, 9, false) && !this.moveItemStackTo(var4, 38, 47, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(var4, 0, 9, false) && !this.moveItemStackTo(var4, 11, 38, false)) {
                    return ItemStack.EMPTY
                }
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

    //Can only split-drag into the crafting table slots
    override fun canDragIntoSlot(par1Slot: Slot): Boolean {
        return par1Slot.index < 9
    }
}
