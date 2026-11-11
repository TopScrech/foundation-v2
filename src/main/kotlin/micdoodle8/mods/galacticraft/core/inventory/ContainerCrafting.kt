package micdoodle8.mods.galacticraft.core.inventory

import foundation.item.legacyItemValue

import java.util.LinkedList

import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.Container as IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.core.NonNullList
import java.util.*

open class ContainerCrafting constructor(playerInventory: InventoryPlayer, tile: IInventory) : Container() {
    @JvmField
    var tileEntity: TileEntityCrafting

    @JvmField
    var craftMatrix: PersistantInventoryCrafting

    @JvmField
    var craftResult: IInventory = InventoryCraftResult()
    private val memory: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(9, ItemStack.EMPTY)

    init {
        this.tileEntity = tile as TileEntityCrafting
        this.craftMatrix = tileEntity.craftMatrix
        this.craftMatrix.craftingEventHandler = this
        this.addSlot(
            SlotCraftingMemory(
                playerInventory.player,
                this.craftMatrix,
                this.craftResult,
                0,
                124,
                35,
                this.tileEntity
            )
        )

        for (i: Int in 0..2) {
            for (j: Int in 0..2) {
                this.addSlot(Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18))
            }
        }

        for (k: Int in 0..2) {
            for (i1: Int in 0..8) {
                this.addSlot(Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18))
            }
        }

        for (l: Int in 0..8) {
            this.addSlot(Slot(playerInventory, l, 8 + l * 18, 142))
        }

        this.slotsChanged(this.craftMatrix)
    }

    override fun getInventory(): NonNullList<ItemStack> {
        val list: NonNullList<ItemStack> = NonNullList.create<ItemStack>()

        for (i: Int in this.inventorySlots.indices) {
            list.add(this.inventorySlots.get(i).getStack())
        }


        //Override this method to trick vanilla networking into carrying our memory at end of its packets 
        for (i: Int in 0..8) list.add(this.tileEntity.memory.get(i))

        return list
    }

    override fun setAll(list: MutableList<ItemStack?>) {
        for (i: Int in list.indices) {
            if (i < 46) this.getSlot(i).set(list.get(i))
            else if (i < 55)  //Read memory clientside from the end of the vanilla packet, see getInventory() 
                this.tileEntity.memory.set(i - 46, list.get(i))
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    override fun slotsChanged(inventoryIn: IInventory) {
        this.craftResult.setItem(
            0,
            CraftingManager.findMatchingResult(this.craftMatrix, this.tileEntity.getWorld())
        )
    }

    /**
     * Called when the container is closed.
     */
    override fun removed(playerIn: EntityPlayer) {
        super.removed(playerIn)
        if (!playerIn.level().isRemote) {
            craftMatrix.craftingEventHandler = null
        }
    }

    override fun stillValid(playerIn: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(playerIn)
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    override fun quickMoveStack(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(index)

        if (slot != null && slot.hasItem() && !slot.getItem().isEmpty()) {
            val itemstack1: ItemStack = slot.getItem()
            itemstack = itemstack1.copy()

            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY
                }

                slot.onQuickCraft(itemstack1, itemstack)
            } else if (index < 10) {
                if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                    return ItemStack.EMPTY
                }
            } else if (this.matchesCrafting(itemstack1)) {
                if (!this.mergeToCrafting(itemstack1, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index >= 10 && index < 37) {
                if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index >= 37 && index < 46) {
                if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    private fun mergeToCrafting(stack: ItemStack, b: Boolean): Boolean {
        if (stack.isEmpty()) {
            return false
        }
        val acceptSlots: MutableList<Slot> = LinkedList<Slot>()
        val acceptQuantity: MutableList<Int> = LinkedList<Int>()
        var minQuantity: Int = 64
        var acceptTotal: Int = 0
        for (i: Int in 1..9) {
            val slot: Slot? = this.inventorySlots.get(i)

            if (slot != null) {
                var target: ItemStack = slot.getItem()
                val target2: ItemStack = this.memory.get(i - 1)
                if (target2.isEmpty()) continue
                if (target.isEmpty()) {
                    target = target2.copy()
                }
                if (matchingStacks(stack, target)) {
                    acceptSlots.add(slot)
                    val availSpace: Int = target.getMaxStackSize() - target.getCount()
                    acceptQuantity.add(availSpace)
                    acceptTotal += availSpace
                    if (availSpace < minQuantity) minQuantity = availSpace
                }
            }
        }


        //First fill any empty slots
        for (slot: Slot in acceptSlots) {
            val target: ItemStack = slot.getItem()
            if (target.isEmpty()) {
                val target2: ItemStack = this.memory.get(slot.index - 1)
                this.craftMatrix.setInventorySlotContents(slot.index - 1, target2.copy())
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

    private fun matchesCrafting(itemstack1: ItemStack): Boolean {
        if (this.tileEntity.overrideMemory(itemstack1, this.memory)) {
            return true
        }

        for (i: Int in 0..8) {
            if (matchingStacks(itemstack1, this.tileEntity.getMemory(i)) && (this.craftMatrix.getStackInSlot(i)
                    .isEmpty() || this.craftMatrix.getStackInSlot(i).getCount() < itemstack1.getMaxStackSize())
            ) {
                for (j: Int in 0..8) {
                    this.memory.set(j, this.tileEntity.getMemory(j))
                }
                return true
            }
        }
        return false
    }

    private fun matchingStacks(stack: ItemStack, target: ItemStack): Boolean {
        return !target.isEmpty() && target.getItem() === stack.getItem() && (!stack.getHasSubtypes() || stack.legacyItemValue == target.legacyItemValue) && RecipeUtil.areItemStackTagsEqual(
            stack,
            target
        ) && (!target.isStackable() || target.getCount() < target.getMaxStackSize())
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    override fun canTakeItemForPickAll(stack: ItemStack, p_94530_2_: Slot): Boolean {
        return p_94530_2_.container !== this.craftResult && super.canTakeItemForPickAll(stack, p_94530_2_)
    }
}
