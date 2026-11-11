package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IPaintable
import micdoodle8.mods.galacticraft.core.tile.TileEntityPainter
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack

open class ContainerPainter constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityPainter
) : Container() {
    init {
        // To be painted
        this.addSlot(Slot(tileEntity, 0, 40, 25))

        //TODO: slots which can only accept one item

        // For dyes and other colour giving items
        this.addSlot(Slot(tileEntity, 1, 122, 25))

        var i: Int
        i = 0
        while (i < 3) {
            for (j: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 104 + i * 18))
            }
            ++i
        }
        i = 0
        while (i < 9) {
            this.addSlot(Slot(par1InventoryPlayer, i, 8 + i * 18, 162))
            ++i
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

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, index: Int): ItemStack {
        var stackOrig: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(index)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val stack: ItemStack = slot.getItem()
            stackOrig = stack.copy()

            if (index < 2) {
                if (!this.moveItemStackTo(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
                slot.onQuickCraft(stack, stackOrig)
            } else if (index != 1 && index != 0) {
                val item: Item = stack.getItem()
                if (item is IPaintable || (item is ItemBlock && item.getBlock() is IPaintable)) {
                    if (!this.mergeOneItem(stack, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (index < b - 9) {
                    if (!this.moveItemStackTo(stack, b - 9, b, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(stack, b - 36, b - 9, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (stack.getCount() == stackOrig.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, stack)
        }

        return stackOrig
    }

    protected open fun mergeOneItem(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (par1ItemStack.getCount() > 0) {
            var slot: Slot
            var slotStack: ItemStack

            for (k: Int in par2..<par3) {
                slot = this.inventorySlots.get(k) as Slot
                slotStack = slot.getItem()

                if (slotStack.isEmpty()) {
                    val stackOneItem: ItemStack = par1ItemStack.copy()
                    stackOneItem.setCount(1)
                    par1ItemStack.shrink(1)
                    slot.set(stackOneItem)
                    slot.setChanged()
                    flag1 = true
                    break
                }
            }
        }

        return flag1
    }
}
