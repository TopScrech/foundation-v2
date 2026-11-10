package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.oredict.OreDictionary
import java.util.ArrayList
import java.util.Random

open class TileEntityElectricIngotCompressor constructor() : TileBaseElectricBlock(), IInventoryDefaults,
    ISidedInventory, IMachineSides {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseElectricBlock>.getDisplayName()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var producingStack: ItemStack = ItemStack.EMPTY
    private var machineTickCounter: Long = 0

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(3, ItemStack.EMPTY)

    @JvmField
    var compressingCraftMatrix: PersistantInventoryCrafting = PersistantInventoryCrafting()
    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false

            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    ++this.processTicks

                    this.processTimeRequired = PROCESS_TIME_REQUIRED_BASE * 2 / (1 + this.poweredByTierGC)

                    if (this.processTicks >= this.processTimeRequired) {
                        this.world.playSound(
                            null,
                            this.getPos(),
                            SoundEvents.BLOCK_ANVIL_LAND,
                            SoundCategory.BLOCKS,
                            0.3f,
                            this.world.rand.nextFloat() * 0.1f + 0.9f
                        )
                        this.processTicks = 0
                        this.compressItems()
                        updateInv = true
                    }
                } else {
                    this.processTicks = 0
                }
            } else {
                this.processTicks = 0
            }

            if (updateInv) {
                this.markDirty()
            }
        }

        this.machineTickCounter++
    }

    private fun canCompress(): Boolean {
        val itemstack: ItemStack = this.producingStack
        if (itemstack.isEmpty()) {
            return false
        }
        if (this.stacks.get(1).isEmpty() && this.stacks.get(2).isEmpty()) {
            return true
        }
        if (!this.stacks.get(1).isEmpty() && !this.stacks.get(1).isItemEqual(itemstack) || !this.stacks.get(2)
                .isEmpty() && !this.stacks.get(2).isItemEqual(itemstack)
        ) {
            return false
        }
        val contents1: Int = this.stacks.get(1).getCount()
        val contents2: Int = this.stacks.get(2).getCount()
        var result: Int = itemstack.getCount()
        if (ConfigManagerCore.quickMode && itemstack.getItem().getTranslationKey(itemstack).contains("compressed")) {
            result += result
        }
        result += if (contents2 < contents1) contents2 else contents1
        return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize()
    }

    open fun updateInput() {
        this.producingStack = CompressorRecipes.findMatchingRecipe(this.compressingCraftMatrix, this.world)
    }

    open fun compressItems() {
        val stackSize1: Int = if (this.stacks.get(1).isEmpty()) 0 else this.stacks.get(1).getCount()
        val stackSize2: Int = if (this.stacks.get(2).isEmpty()) 0 else this.stacks.get(2).getCount()

        if (stackSize1 <= stackSize2) {
            this.compressIntoSlot(1)
            this.compressIntoSlot(2)
        } else {
            this.compressIntoSlot(2)
            this.compressIntoSlot(1)
        }
    }

    private fun compressIntoSlot(slot: Int) {
        if (this.canCompress()) {
            val resultItemStack: ItemStack = this.producingStack.copy()
            if (ConfigManagerCore.quickMode) {
                if (resultItemStack.getItem().getTranslationKey(resultItemStack).contains("compressed")) {
                    resultItemStack.grow(resultItemStack.getCount())
                }
            }

            if (this.stacks.get(slot).isEmpty()) {
                this.stacks.set(slot, resultItemStack)
            } else if (this.stacks.get(slot).isItemEqual(resultItemStack)) {
                if (this.stacks.get(slot).getCount() + resultItemStack.getCount() > resultItemStack.getMaxStackSize()) {
                    resultItemStack.grow(this.stacks.get(slot).getCount() - resultItemStack.getMaxStackSize())
                    GCCoreUtil.spawnItem(this.world, this.getPos(), resultItemStack)
                    this.stacks.get(slot).setCount(resultItemStack.getMaxStackSize())
                } else {
                    this.stacks.get(slot).grow(resultItemStack.getCount())
                }
            }

            for (i: Int in 0..<this.compressingCraftMatrix.getSizeInventory()) {
                if (!this.compressingCraftMatrix.getStackInSlot(i)
                        .isEmpty() && this.compressingCraftMatrix.getStackInSlot(i).getItem() === Items.WATER_BUCKET
                ) {
                    this.compressingCraftMatrix.setInventorySlotContentsNoUpdate(i, ItemStack(Items.BUCKET))
                } else {
                    this.compressingCraftMatrix.decrStackSize(i, 1)
                }
            }

            this.updateInput()
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getInteger("smeltingTicks")

        this.stacks = NonNullList.withSize<ItemStack>(
            this.getSizeInventory() - this.compressingCraftMatrix.getSizeInventory(),
            ItemStack.EMPTY
        )
        val nbttaglist: NBTTagList = nbt.getTagList("Items", 10)

        for (i: Int in 0..<nbttaglist.tagCount()) {
            val nbttagcompound: NBTTagCompound = nbttaglist.getCompoundTagAt(i)
            val j: Int = nbttagcompound.getByte("Slot").toInt() and 255

            if (j >= 0 && j < this.stacks.size) {
                this.stacks.set(j, ItemStack(nbttagcompound))
            } else if (j < this.stacks.size + this.compressingCraftMatrix.getSizeInventory()) {
                this.compressingCraftMatrix.setInventorySlotContents(j - this.stacks.size, ItemStack(nbttagcompound))
            }
        }
        this.readMachineSidesFromNBT(nbt) //Needed by IMachineSides
        this.updateInput()
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("smeltingTicks", this.processTicks)
        val var2: NBTTagList = NBTTagList()
        var var3: Int

        var3 = 0
        while (var3 < this.stacks.size) {
            if (!this.stacks.get(var3).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", var3.toByte())
                this.stacks.get(var3).writeToNBT(var4)
                var2.appendTag(var4)
            }
            ++var3
        }

        var3 = 0
        while (var3 < this.compressingCraftMatrix.getSizeInventory()) {
            if (!this.compressingCraftMatrix.getStackInSlot(var3).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", (var3 + this.stacks.size).toByte())
                this.compressingCraftMatrix.getStackInSlot(var3).writeToNBT(var4)
                var2.appendTag(var4)
            }
            ++var3
        }
        nbt.setTag("Items", var2)

        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides
        return nbt
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size + this.compressingCraftMatrix.getSizeInventory()
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            return this.compressingCraftMatrix.getStackInSlot(par1 - this.stacks.size)
        }

        return this.stacks.get(par1)
    }

    override fun decrStackSize(par1: Int, par2: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            val result: ItemStack = this.compressingCraftMatrix.decrStackSize(par1 - this.stacks.size, par2)
            if (!result.isEmpty()) {
                this.updateInput()
            }
            this.markDirty()
            return result
        }

        if (!this.stacks.get(par1).isEmpty()) {
            val var3: ItemStack

            if (this.stacks.get(par1).getCount() <= par2) {
                var3 = this.stacks.get(par1)
                this.stacks.set(par1, ItemStack.EMPTY)
                this.markDirty()
                return var3
            } else {
                var3 = this.stacks.get(par1).splitStack(par2)

                if (this.stacks.get(par1).isEmpty()) {
                    this.stacks.set(par1, ItemStack.EMPTY)
                }

                this.markDirty()
                return var3
            }
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun removeStackFromSlot(par1: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            this.markDirty()
            return this.compressingCraftMatrix.removeStackFromSlot(par1 - this.stacks.size)
        }

        if (!this.stacks.get(par1).isEmpty()) {
            val var2: ItemStack = this.stacks.get(par1)
            this.stacks.set(par1, ItemStack.EMPTY)
            this.markDirty()
            return var2
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun setInventorySlotContents(par1: Int, stack: ItemStack) {
        if (par1 >= this.stacks.size) {
            this.compressingCraftMatrix.setInventorySlotContents(par1 - this.stacks.size, stack)
            this.updateInput()
        } else {
            this.stacks.set(par1, stack)

            if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
                stack.setCount(this.getInventoryStackLimit())
            }
        }
        this.markDirty()
    }

    override fun getName(): String {
        return GCCoreUtil.translate("tile.machine2.4.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(entityplayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && entityplayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun isItemValidForSlot(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return itemStack != null && !itemStack.isEmpty() && ItemElectricBase.isElectricItem(itemStack.getItem())
        } else if (slotID >= 3) {
            if (!this.producingStack.isEmpty()) {
                val stackInSlot: ItemStack = this.getStackInSlot(slotID)
                return !stackInSlot.isEmpty() && stackInSlot.isItemEqual(itemStack)
            }
            return this.isItemCompressorInput(itemStack, slotID - 3)
        }

        return false
    }

    open fun isItemCompressorInput(stack: ItemStack, id: Int): Boolean {
        for (recipe: IRecipe? in CompressorRecipes.getRecipeList()) {
            if (recipe is ShapedRecipesGC) {
                if (id >= recipe.recipeItems.size) continue
                val itemstack1: ItemStack = recipe.recipeItems[id]
                if (stack.getItem() === itemstack1.getItem() && (itemstack1.getItemDamage() == 32767 || stack.getItemDamage() == itemstack1.getItemDamage())) {
                    for (i: Int in recipe.recipeItems.indices) {
                        if (i == id) continue
                        val itemstack2: ItemStack = recipe.recipeItems[i]
                        if (stack.getItem() === itemstack2.getItem() && (itemstack2.getItemDamage() == 32767 || stack.getItemDamage() == itemstack2.getItemDamage())) {
                            val is3: ItemStack = this.getStackInSlot(id + 3)
                            val is4: ItemStack = this.getStackInSlot(i + 3)
                            return is3.isEmpty() || !is4.isEmpty() && is3.getCount() < is4.getCount()
                        }
                    }
                    return true
                }
            } else if (recipe is ShapelessOreRecipeGC) {
                val required: ArrayList<Any?> = ArrayList<Any?>(recipe.getInput())

                val req: MutableIterator<Any?> = required.iterator()

                var match: Int = 0

                while (req.hasNext()) {
                    val next: Any? = req.next()

                    if (next is ItemStack) {
                        if (OreDictionary.itemMatches(next, stack, false)) match++
                    } else if (next is MutableList<*>) {
                        val itr: MutableIterator<ItemStack?> = (next as MutableList<ItemStack?>).iterator()
                        while (itr.hasNext()) {
                            if (OreDictionary.itemMatches(itr.next()!!, stack, false)) {
                                match++
                                break
                            }
                        }
                    }
                }

                if (match == 0) continue

                if (match == 1) return true


                //Shapeless recipe can go into (match) number of slots
                var slotsFilled: Int = 0
                for (i: Int in 3..11) {
                    val inMatrix: ItemStack = this.getStackInSlot(i)
                    if (!inMatrix.isEmpty() && inMatrix.isItemEqual(stack)) slotsFilled++
                }
                if (slotsFilled < match) {
                    return this.getStackInSlot(id + 3).isEmpty()
                }

                return randnum.nextInt(match) == 0
            }
        }

        return false
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(1, 2)
        }
        val slots: IntArray = intArrayOf(0, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val removeSlots: ArrayList<Int> = ArrayList<Int>()

        for (i: Int in 3..11) {
            if (removeSlots.contains(i)) {
                continue
            }
            val stack1: ItemStack = this.getStackInSlot(i)
            if (stack1.isEmpty()) {
                continue
            }

            for (j: Int in i + 1..11) {
                if (removeSlots.contains(j)) {
                    continue
                }
                val stack2: ItemStack = this.getStackInSlot(j)
                if (stack2.isEmpty()) {
                    continue
                }

                if (stack1.isItemEqual(stack2)) {
                    if (stack2.getCount() >= stack1.getCount()) {
                        removeSlots.add(j)
                    } else {
                        removeSlots.add(i)
                    }
                    break
                }
            }
        }

        if (removeSlots.size > 0) {
            val returnSlots: IntArray = IntArray(slots.size - removeSlots.size)
            var j: Int = 0
            for (i: Int in slots.indices) {
                if (i > 0 && removeSlots.contains(slots[i])) {
                    continue
                }
                returnSlots[j] = slots[i]
                j++
            }

            return returnSlots
        }

        return slots
    }

    override fun canInsertItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return this.isItemValidForSlot(slotID, par2ItemStack)
    }

    override fun canExtractItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 1 || slotID == 2
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachine2) {
            return state.getValue(BlockMachine2.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        when (this.getSide(IMachineSides.MachineSide.ELECTRIC_IN)) {
            IMachineSides.Face.RIGHT -> return getFront().rotateYCCW()
            IMachineSides.Face.REAR -> return getFront().getOpposite()
            IMachineSides.Face.TOP -> return EnumFacing.UP
            IMachineSides.Face.BOTTOM -> return EnumFacing.DOWN
            IMachineSides.Face.LEFT -> return getFront().rotateY()
            else -> return getFront().rotateY()
        }
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getStackInSlot(0)
    }

    //------------------
    //Added these methods and field to implement IMachineSides properly 
    //------------------
    override fun listConfigurableSides(): Array<IMachineSides.MachineSide> {
        return arrayOf<IMachineSides.MachineSide>(IMachineSides.MachineSide.ELECTRIC_IN)
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face> {
        return arrayOf<IMachineSides.Face>(IMachineSides.Face.LEFT)
    }

    private var machineSides: Array<IMachineSides.MachineSidePack?>? = null

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 75).toFloat())
        this.setTierGC(2)
    }

    override fun getAllMachineSides(): Array<IMachineSides.MachineSidePack> {
        if (this.machineSides == null) {
            this.initialiseSides()
        }

        return ((this.machineSides as Array<IMachineSides.MachineSidePack>?))!!
    }

    override fun setupMachineSides(length: Int) {
        this.machineSides = arrayOfNulls<IMachineSides.MachineSidePack>(length)
    }

    override fun onLoad() {
        this.clientOnLoad()
    }

    override fun getConfigurationType(): IMachineSidesProperties? {
        return BlockMachine2.MACHINESIDES_RENDERTYPE
    } //------------------END OF IMachineSides implementation

    companion object {
        const val PROCESS_TIME_REQUIRED_BASE: Int = 200
        private val randnum = Random()
    }
}
