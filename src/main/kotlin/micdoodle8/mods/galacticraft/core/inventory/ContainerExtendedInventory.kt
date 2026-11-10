package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerExtendedInventory constructor(
    thePlayer: EntityPlayer,
    @JvmField var extendedInventory: InventoryExtended?
) : Container() {
    @JvmField
    var inventoryPlayer: InventoryPlayer?

    init {
        this.inventoryPlayer = thePlayer.inventory

        var i: Int
        var j: Int

        i = 0
        while (i < 3) {
            j = 0
            while (j < 9) {
                this.addSlotToContainer(Slot(thePlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
                ++j
            }
            ++i
        }

        i = 0
        while (i < 9) {
            this.addSlotToContainer(Slot(thePlayer.inventory, i, 8 + i * 18, 142))
            ++i
        }

        i = 0
        while (i < 4) {
            this.addSlotToContainer(SlotArmorGC(thePlayer, thePlayer.inventory, 39 - i, 61, 8 + i * 18, i))
            ++i
        }

        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 0, 125, 26))
        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 1, 125, 44))
        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 2, 116, 62))
        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 3, 134, 62))
        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 4, 143, 26))
        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 5, 107, 26))

        i = 0
        while (i < 4) {
            this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 6 + i, 79, 8 + i * 18))
            ++i
        }

        this.addSlotToContainer(SlotExtendedInventory((extendedInventory)!!, 10, 125, 8))
    }

    override fun canInteractWith(var1: EntityPlayer): Boolean {
        return true
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.getHasStack()) {
            val stack: ItemStack = slot.getStack()
            var2 = stack.copy()

            if (par1 >= 36) {
                if (!this.mergeItemStack(stack, 0, 36, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                var flag: Boolean = false
                for (j: Int in 36..39) {
                    if ((this.inventorySlots.get(j) as SlotArmorGC).isItemValid(stack)) {
                        if (!this.mergeOneItem(stack, j, j + 1, false)) {
                            return ItemStack.EMPTY
                        }
                        flag = true
                        break
                    }
                }

                if (!flag) {
                    if (stack.getItem() is ItemOxygenTank || stack.getItem() is ItemCanisterOxygenInfinite) {
                        if (!this.mergeOneItem(stack, 42, 44, false)) {
                            return ItemStack.EMPTY
                        }
                        flag = true
                    } else {
                        for (j: Int in 40..50) {
                            if ((this.inventorySlots.get(j) as SlotExtendedInventory).isItemValid(stack)) {
                                if (!this.mergeOneItem(stack, j, j + 1, false)) {
                                    return ItemStack.EMPTY
                                }
                                flag = true
                                break
                            }
                        }
                    }
                }

                if (!flag) {
                    if (par1 < 27) {
                        if (!this.mergeItemStack(stack, 27, 36, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(stack, 0, 27, false)) {
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

    protected open fun mergeOneItem(par1ItemStack: ItemStack, par2: Int, par3: Int, par4: Boolean): Boolean {
        var flag1: Boolean = false
        if (par1ItemStack.getCount() > 0) {
            var slot: Slot
            var slotStack: ItemStack

            for (k: Int in par2..<par3) {
                slot = this.inventorySlots.get(k)
                slotStack = slot.getStack()

                if (slotStack.isEmpty()) {
                    val stackOneItem: ItemStack = par1ItemStack.copy()
                    stackOneItem.setCount(1)
                    par1ItemStack.shrink(1)
                    slot.putStack(stackOneItem)
                    slot.onSlotChanged()
                    flag1 = true
                    break
                }
            }
        }

        return flag1
    }
}
