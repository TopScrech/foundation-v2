package micdoodle8.mods.galacticraft.core.inventory

import java.util.LinkedList

import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCraftResult
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.util.NonNullList
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
        this.addSlotToContainer(
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
                this.addSlotToContainer(Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18))
            }
        }

        for (k: Int in 0..2) {
            for (i1: Int in 0..8) {
                this.addSlotToContainer(Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18))
            }
        }

        for (l: Int in 0..8) {
            this.addSlotToContainer(Slot(playerInventory, l, 8 + l * 18, 142))
        }

        this.onCraftMatrixChanged(this.craftMatrix)
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
            if (i < 46) this.getSlot(i).putStack(list.get(i))
            else if (i < 55)  //Read memory clientside from the end of the vanilla packet, see getInventory() 
                this.tileEntity.memory.set(i - 46, list.get(i))
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    override fun onCraftMatrixChanged(inventoryIn: IInventory) {
        this.craftResult.setInventorySlotContents(
            0,
            CraftingManager.findMatchingResult(this.craftMatrix, this.tileEntity.getWorld())
        )
    }

    /**
     * Called when the container is closed.
     */
    override fun onContainerClosed(playerIn: EntityPlayer) {
        super.onContainerClosed(playerIn)
        if (!playerIn.world.isRemote) {
            craftMatrix.craftingEventHandler = null
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(playerIn)
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(index)

        if (slot != null && slot.getHasStack() && !slot.getStack().isEmpty()) {
            val itemstack1: ItemStack = slot.getStack()
            itemstack = itemstack1.copy()

            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY
                }

                slot.onSlotChange(itemstack1, itemstack)
            } else if (index < 10) {
                if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                    return ItemStack.EMPTY
                }
            } else if (this.matchesCrafting(itemstack1)) {
                if (!this.mergeToCrafting(itemstack1, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return ItemStack.EMPTY
                }
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
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
                var target: ItemStack = slot.getStack()
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
            val target: ItemStack = slot.getStack()
            if (target.isEmpty()) {
                val target2: ItemStack = this.memory.get(slot.slotNumber - 1)
                this.craftMatrix.setInventorySlotContents(slot.slotNumber - 1, target2.copy())
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
                val target: ItemStack = slot.getStack()
                stack.shrink(target.getMaxStackSize() - target.getCount())
                target.setCount(target.getMaxStackSize())
                slot.onSlotChanged()
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
                    val target: ItemStack = slot.getStack()
                    if (target.getCount() < smallestStack) {
                        smallestStack = target.getCount()
                        neediest = slot
                    }
                }
                neediest!!.getStack().grow(1)
                stack.shrink(1)
            } while (!stack.isEmpty())
            for (slot: Slot in acceptSlots) {
                slot.onSlotChanged()
            }
            return true
        }

        //Use some of the stack to even things up
        if (uneven > 0) {
            val targetSize: Int = stack.getMaxStackSize() - minQuantity
            for (slot: Slot in acceptSlots) {
                val target: ItemStack = slot.getStack()
                stack.shrink(targetSize - target.getCount())
                acceptTotal -= targetSize - target.getCount()
                target.setCount(targetSize)
                slot.onSlotChanged()
            }
        }


        //Spread the remaining stack over all slots evenly
        val average: Int = stack.getCount() / acceptSlots.size
        var modulus: Int = stack.getCount() - average * acceptSlots.size
        for (slot: Slot? in acceptSlots) {
            if (slot != null) {
                val target: ItemStack = slot.getStack()
                var transfer: Int = average
                if (modulus > 0) {
                    transfer++
                    modulus--
                }
                if (transfer > stack.getCount()) transfer = stack.getCount()
                stack.shrink(transfer)
                target.grow(transfer)
                if (target.getCount() > target.getMaxStackSize()) {
                    GCLog.info("Shift clicking - slot " + slot.slotNumber + " wanted more than it could accept:" + target.getCount())
                    stack.grow(target.getCount() - target.getMaxStackSize())
                    target.setCount(target.getMaxStackSize())
                }
                slot.onSlotChanged()
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
        return !target.isEmpty() && target.getItem() === stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == target.getMetadata()) && RecipeUtil.areItemStackTagsEqual(
            stack,
            target
        ) && (!target.isStackable() || target.getCount() < target.getMaxStackSize())
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    override fun canMergeSlot(stack: ItemStack, p_94530_2_: Slot): Boolean {
        return p_94530_2_.inventory !== this.craftResult && super.canMergeSlot(stack, p_94530_2_)
    }
}
