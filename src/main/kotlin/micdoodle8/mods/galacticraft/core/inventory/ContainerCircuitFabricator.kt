package micdoodle8.mods.galacticraft.core.inventory

import foundation.item.legacyItemValue

import java.util.ArrayList
import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tile.TileEntityCircuitFabricator
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.Slot
import net.minecraft.inventory.SlotFurnaceOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

open class ContainerCircuitFabricator constructor(
    playerInv: InventoryPlayer,
    private val tileEntity: TileEntityCircuitFabricator
) : Container() {
    init {
        // Energy slot
        this.addSlot(SlotSpecific(tileEntity, 0, 6, 69, IItemElectric::class.java))

        // Diamond
        var slotContentsList: ArrayList<ItemStack?> = CircuitFabricatorRecipes.slotValidItems.get(0)
        this.addSlot(SlotSpecific(tileEntity, 1, 15, 17, *slotContentsList.toTypedArray<ItemStack?>()))

        // Silicon
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(1)
        this.addSlot(SlotSpecific(tileEntity, 2, 74, 46, *slotContentsList.toTypedArray<ItemStack?>()))
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(2)
        this.addSlot(SlotSpecific(tileEntity, 3, 74, 64, *slotContentsList.toTypedArray<ItemStack?>()))

        // Redstone
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(3)
        this.addSlot(SlotSpecific(tileEntity, 4, 122, 46, *slotContentsList.toTypedArray<ItemStack?>()))

        // Optional
        slotContentsList = CircuitFabricatorRecipes.slotValidItems.get(4)
        this.addSlot(SlotSpecific(tileEntity, 5, 145, 20, *slotContentsList.toTypedArray<ItemStack?>()))

        // Smelting result
        this.addSlot(SlotFurnaceOutput(playerInv.player, tileEntity, 6, 152, 86))

        var slot: Int

        slot = 0
        while (slot < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(playerInv, var4 + slot * 9 + 9, 8 + var4 * 18, 110 + slot * 18))
            }
            ++slot
        }

        slot = 0
        while (slot < 9) {
            this.addSlot(Slot(playerInv, slot, 8 + slot * 18, 168))
            ++slot
        }
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun slotsChanged(par1IInventory: IInventory) {
        super.slotsChanged(par1IInventory)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 < b - 36) {
                if (!this.moveItemStackTo(var4, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 6) {
                    slot.onQuickCraft(var4, var2)
                }
            } else {
                val i: Item = var4.getItem()
                if (EnergyUtil.isElectricItem(i)) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (i === Items.DIAMOND) {
                    if (!this.moveItemStackTo(var4, 1, 2, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (this.isSilicon(var4)) {
                    if (!this.mergeEven(var4, 2, 4)) {
                        return ItemStack.EMPTY
                    }
                } else if (i === Items.REDSTONE) {
                    if (!this.moveItemStackTo(var4, 4, 5, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (i === Items.REPEATER || i === ItemStack(Blocks.REDSTONE_TORCH).getItem() || i === Items.DYE && i.getDamage(
                        var4
                    ) == 4
                ) {
                    if (!this.moveItemStackTo(var4, 5, 6, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (par1 < b - 9) {
                    if (!this.moveItemStackTo(var4, b - 9, b, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.moveItemStackTo(var4, b - 36, b - 9, false)) {
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

    private fun mergeEven(stack: ItemStack, a: Int, b: Int): Boolean {
        val acceptSlots: MutableList<Slot> = LinkedList<Slot>()
        val acceptQuantity: MutableList<Int> = LinkedList<Int>()
        var minQuantity: Int = 64
        var acceptTotal: Int = 0
        for (i: Int in a..<b) {
            val slot: Slot? = this.inventorySlots.get(i)

            if (slot != null) {
                val target: ItemStack = slot.getItem()
                if (matchingStacks(stack, target)) {
                    acceptSlots.add(slot)
                    val availSpace: Int = stack.getMaxStackSize() - target.getCount()
                    acceptQuantity.add(availSpace)
                    acceptTotal += availSpace
                    if (availSpace < minQuantity) minQuantity = availSpace
                }
            }
        }

        for (slot: Slot in acceptSlots) {
            var target: ItemStack = slot.getItem()
            if (target.isEmpty()) {
                target = stack.copy()
                target.setCount(1)
                slot.set(target)
                stack.shrink(1)
                if (stack.isEmpty()) {
                    return false
                }
            }
        }


        //The stack more than exceeds what the crafting inventory requires
        if (stack.getCount() >= acceptTotal) {
            if (acceptTotal == 0) return false

            for (slot: Slot in acceptSlots) {
                val target: ItemStack = slot.getItem()
                stack.shrink(target.getMaxStackSize() - target.getCount())
                target.setCount(target.getMaxStackSize())
                slot.setChanged()
            }
            return true
        }

        var uneven: Int = 0
        for (q: Int in acceptQuantity) {
            uneven += q - minQuantity
        }


        //Use the whole stack to try to even up the neediest slots
        if (stack.getCount() <= uneven) {
            do {
                var neediest: Slot? = null
                var smallestStack: Int = 64
                for (slot: Slot in acceptSlots) {
                    val target: ItemStack = slot.getItem()
                    if (target.getCount() < smallestStack) {
                        smallestStack = target.getCount()
                        neediest = slot
                    }
                }
                neediest!!.getItem().grow(1)
                stack.shrink(1)
            } while (!stack.isEmpty())
            for (slot: Slot in acceptSlots) {
                slot.setChanged()
            }
            return true
        }

        //Use some of the stack to even things up
        if (uneven > 0) {
            val targetSize: Int = stack.getMaxStackSize() - minQuantity
            for (slot: Slot in acceptSlots) {
                val target: ItemStack = slot.getItem()
                stack.shrink(targetSize - target.getCount())
                acceptTotal -= targetSize - target.getCount()
                target.setCount(targetSize)
                slot.setChanged()
            }
        }


        //Spread the remaining stack over all slots evenly
        val average: Int = stack.getCount() / acceptSlots.size
        var modulus: Int = stack.getCount() - average * acceptSlots.size
        for (slot: Slot? in acceptSlots) {
            if (slot != null) {
                val target: ItemStack = slot.getItem()
                var transfer: Int = average
                if (modulus > 0) {
                    transfer++
                    modulus--
                }
                if (transfer > stack.getCount()) transfer = stack.getCount()
                stack.shrink(transfer)
                target.grow(transfer)
                if (target.getCount() > target.getMaxStackSize()) {
                    GCLog.info("Shift clicking - slot " + slot.index + " wanted more than it could accept:" + target.getCount())
                    stack.grow(target.getCount() - target.getMaxStackSize())
                    target.setCount(target.getMaxStackSize())
                }
                slot.setChanged()
                if (stack.isEmpty()) break
            }
        }

        return true
    }

    private fun isSilicon(test: ItemStack): Boolean {
        for (stack in CircuitFabricatorRecipes.slotValidItems.get(1)) {
            if ((stack)!!.isItemEqual(test)) return true
        }
        return false
    }

    private fun matchingStacks(stack: ItemStack, target: ItemStack): Boolean {
        return target.isEmpty() || target.getItem() === stack.getItem() && (!stack.getHasSubtypes() || stack.legacyItemValue == target.legacyItemValue) && ItemStack.areItemStackTagsEqual(
            stack,
            target
        ) && (target.isStackable() && target.getCount() < target.getMaxStackSize())
    }
}
