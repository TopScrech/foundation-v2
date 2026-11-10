package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting
import micdoodle8.mods.galacticraft.core.network.PacketDynamicInventory
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import java.lang.Exception

open class TileEntityCrafting constructor() : TileEntity(), IInventoryDefaults, ISidedInventory {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntity>.getDisplayName()

    @JvmField
    var craftMatrix: PersistantInventoryCrafting = PersistantInventoryCrafting()

    @JvmField
    var memory: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(SIZEINVENTORY, ItemStack.EMPTY)

    override fun onLoad() {
        if (this.world.isRemote) {
            //Request size + contents information from server
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamicInventory(this))
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }

    open fun updateMemory() {
        if (CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld()) == ItemStack.EMPTY) return
        for (i: Int in 0..<SIZEINVENTORY) {
            val stack: ItemStack = this.craftMatrix.getStackInSlot(i)
            if (stack.isEmpty()) {
                this.memory.set(i, ItemStack.EMPTY)
            } else {
                val stack2: ItemStack = stack.copy()
                stack2.setCount(1)
                this.memory.set(i, stack2.copy())
            }
        }
    }

    open fun getMemory(i: Int): ItemStack {
        return this.memory.get(i)
    }

    override fun getSizeInventory(): Int {
        return SIZEINVENTORY
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        if (par1 < SIZEINVENTORY) return this.craftMatrix.getStackInSlot(par1)


        // Crafting Manager can produce concurrent modification exception in single player
        // if a server-side tick (e.g. from a Hopper) calls this while client-side is still initialising recipes
        try {
            return CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld())
        } catch (ignore: Exception) {
        }
        return ItemStack.EMPTY
    }

    override fun decrStackSize(par1: Int, par2: Int): ItemStack {
        if (par1 >= 0 && par1 < SIZEINVENTORY) {
            val result: ItemStack = this.craftMatrix.decrStackSize(par1, par2)
            this.markDirty()
            return result
        } else if (par1 == SIZEINVENTORY) {
            if (this.stillMatchesRecipe()) {
                val craftingResult: ItemStack = CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld())
                if (!craftingResult.isEmpty()) {
                    this.pullOneResultStack()
                    this.markDirty()
                    return craftingResult
                }
            }
        }
        return ItemStack.EMPTY
    }

    private fun pullOneResultStack() {
        val aitemstack: NonNullList<ItemStack> = CraftingManager.getRemainingItems(this.craftMatrix, this.world)

        for (i: Int in aitemstack.indices) {
            val itemstack: ItemStack = this.craftMatrix.getStackInSlot(i)
            val itemstack1: ItemStack = aitemstack.get(i)

            if (!itemstack.isEmpty()) {
                this.craftMatrix.decrStackSize(i, 1)
            }

            if (!itemstack1.isEmpty()) {
                if (this.craftMatrix.getStackInSlot(i).isEmpty()) {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1)
                } else {
                    //TODO - things like buckets which can't go back into this - drop?
                }
            }
        }
    }

    protected open fun stillMatchesRecipe(): Boolean {
        val emptyCount: Int = 0
        for (i: Int in 0..<SIZEINVENTORY) {
            val stack: ItemStack = this.craftMatrix.getStackInSlot(i)
            val targetOther: ItemStack = this.memory.get(i)
            if (targetOther.isEmpty() && stack.isEmpty()) continue

            if (targetOther.isEmpty() || stack.isEmpty() || !sameItem(targetOther, stack)) {
                return false
            }
        }
        return emptyCount < SIZEINVENTORY
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.memory) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun removeStackFromSlot(par1: Int): ItemStack {
        if (par1 >= 0) {
            this.markDirty()
            return this.craftMatrix.removeStackFromSlot(par1)
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun setInventorySlotContents(par1: Int, stack: ItemStack) {
        if (par1 >= 0 && par1 < SIZEINVENTORY) {
            this.craftMatrix.setInventorySlotContents(par1, stack)
            this.markDirty()
        } else if (par1 == SIZEINVENTORY && stack.isEmpty()) {
            if (this.stillMatchesRecipe()) {
                val craftingResult: ItemStack = CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld())
                if (!craftingResult.isEmpty()) {
                    this.pullOneResultStack()
                }
            }
        }
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.magneticcrafting.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.craftMatrix.clear()
            for (i: Int in 0..<SIZEINVENTORY) this.memory.set(i, ItemStack.EMPTY)
            val contents: NBTTagList = nbt.getTagList("Items", 10)
            if (contents != null && contents.tagCount() > 0) {
                for (i: Int in 0..<contents.tagCount()) {
                    val var4: NBTTagCompound = contents.getCompoundTagAt(i)
                    val slot: Int = var4.getByte("Slot").toInt() and 255

                    if (slot < SIZEINVENTORY) {
                        this.craftMatrix.setInventorySlotContents(slot, ItemStack(var4))
                    } else if (slot < 18) {
                        this.memory.set(slot - SIZEINVENTORY, ItemStack(var4))
                    }
                }
            }
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        val var2: NBTTagList = NBTTagList()
        for (i: Int in 0..<SIZEINVENTORY) {
            if (!this.craftMatrix.getStackInSlot(i).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", (i).toByte())
                this.craftMatrix.getStackInSlot(i).writeToNBT(var4)
                var2.appendTag(var4)
            }
        }
        for (i: Int in 0..<SIZEINVENTORY) {
            if (!this.memory.get(i).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", (i + SIZEINVENTORY).toByte())
                this.memory.get(i).writeToNBT(var4)
                var2.appendTag(var4)
            }
        }

        nbt.setTag("Items", var2)
        return nbt
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && player.getDistanceSq(
            this.getPos().getX() + 0.5,
            this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun isItemValidForSlot(par1: Int, par2ItemStack: ItemStack): Boolean {
        return true
    }

    override fun invalidate() {
        super.invalidate()
        this.updateContainingBlockInfo()
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    }

    override fun canInsertItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean {
        if (index >= SIZEINVENTORY) return false
        val override: Boolean = this.overriddenMemory()
        val target: ItemStack = if (override) this.craftMatrix.getStackInSlot(index).copy() else this.memory.get(index)

        if (!target.isEmpty() && !stack.isEmpty() && sameItem(target, stack)) {
            val is3: ItemStack = this.getStackInSlot(index)
            if (is3.isEmpty()) return true
            val currentSize: Int = is3.getCount()


            //If any other slot matching this item has a smaller stacksize, return false (and hopefully that slot will be filled instead)
            for (i: Int in 0..<SIZEINVENTORY) {
                if (i == index) continue
                val targetOther: ItemStack =
                    if (override) this.craftMatrix.getStackInSlot(i).copy() else this.memory.get(i)
                if (targetOther.isEmpty()) continue
                //It's another memory slot matching this item 
                if (sameItem(targetOther, stack)) {
                    val itemstack2: ItemStack = this.craftMatrix.getStackInSlot(i)
                    if (itemstack2.isEmpty()) return false

                    if (sameItem(stack, itemstack2)) {
                        if (itemstack2.getCount() < currentSize) return false
                    }
                }
            }

            return true
        }
        return false
    }

    override fun canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean {
        return index == SIZEINVENTORY && this.stillMatchesRecipe()
    }

    /**
     * Does not include a null check, do a null check first
     */
    private fun sameItem(target: ItemStack, stack: ItemStack): Boolean {
        return target.getItem() === stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == target.getMetadata()) && RecipeUtil.areItemStackTagsEqual(
            stack,
            target
        )
    }

    open fun overrideMemory(itemstack1: ItemStack, memory2: NonNullList<ItemStack>): Boolean {
        var allEmpty: Boolean = true
        for (i: Int in 0..8) {
            if (!this.craftMatrix.getStackInSlot(i).isEmpty()) {
                allEmpty = false
                break
            }
        }
        if (allEmpty) return false
        if (!CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld()).isEmpty()) {
            //Valid recipe on the table.  Does it fuzzy match this tile's memory (empty slots which should have recipe components are OK)
            var fuzzyMatch: Boolean = true
            for (i: Int in 0..8) {
                if (!this.craftMatrix.getStackInSlot(i).isEmpty() && !matchingStacks(
                        this.craftMatrix.getStackInSlot(i),
                        this.getMemory(i)
                    )
                ) {
                    fuzzyMatch = false
                    break
                }
            }


            //If it's a valid recipe and CAN'T match the memory, then override the remembered recipe
            if (!fuzzyMatch) {
                for (i: Int in 0..8) {
                    if (matchingStacks(itemstack1, this.craftMatrix.getStackInSlot(i))) {
                        for (j: Int in 0..8) {
                            memory2.set(j, this.craftMatrix.getStackInSlot(j).copy())
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    open fun overriddenMemory(): Boolean {
        if (!CraftingManager.findMatchingResult(this.craftMatrix, this.getWorld()).isEmpty()) {
            //Valid recipe on the table.  Does it fuzzy match this tile's memory (empty slots which should have recipe components are OK)
            for (i: Int in 0..8) {
                if (!this.craftMatrix.getStackInSlot(i).isEmpty() && !matchingStacks(
                        this.craftMatrix.getStackInSlot(i),
                        this.getMemory(i)
                    )
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun matchingStacks(stack: ItemStack, target: ItemStack): Boolean {
        return !target.isEmpty() && target.getItem() === stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == target.getMetadata()) && RecipeUtil.areItemStackTagsEqual(
            stack,
            target
        ) && target.isStackable() && target.getCount() < target.getMaxStackSize()
    }

    companion object {
        private const val SIZEINVENTORY = 9
    }
}
