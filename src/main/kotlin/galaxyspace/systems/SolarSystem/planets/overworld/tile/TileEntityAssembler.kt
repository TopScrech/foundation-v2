package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAssembler
import galaxyspace.core.prefab.inventory.GSPersistantInventoryCrafting
import galaxyspace.systems.SolarSystem.planets.overworld.recipes.AssemblyRecipes
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockAssembler
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
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

open class TileEntityAssembler constructor() : TileEntityElectricInventory("tile.assembly_machine.name"),
    ISidedInventory, IPacketReceiver {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var producingStack: ItemStack = ItemStack.EMPTY

    @JvmField
    var testCraftMatrix: GSPersistantInventoryCrafting = GSPersistantInventoryCrafting()

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 75).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(2 + 4, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false

            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    //////////
                    var boost_speed: Int = 0
                    var energy_boost: Int = 0

                    for (i: Int in 0..3) {
                        if (this.getInventory().get(2 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 2))) boost_speed++
                        if (this.getInventory().get(2 + i)
                                .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))
                        ) energy_boost++
                    }

                    this.processTicks += 1 + boost_speed
                    this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + (40 * boost_speed) - (20 * energy_boost) else 75 + (35 * boost_speed) - (15 * energy_boost)).toFloat())

                    /////////////
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
                        this.compressIntoSlot(1)
                        updateInv = true
                    }
                } else {
                    this.processTicks = 0
                }
            }

            /* else
            {
                this.processTicks = 0;
            }*/
            if (updateInv) {
                this.markDirty()
            }
        }
    }

    private fun canCompress(): Boolean {
        val itemstack: ItemStack = this.producingStack
        if (itemstack.isEmpty()) {
            return false
        }
        if (this.getInventory().get(1).isEmpty()) {
            return true
        }
        if (!this.getInventory().get(1).isEmpty() && !this.getInventory().get(1).isItemEqual(itemstack)) {
            return false
        }
        val contents1: Int = this.getInventory().get(1).getCount()
        var result: Int = itemstack.getCount()

        result += contents1

        return result <= this.getInventoryStackLimit() && result <= itemstack.getMaxStackSize()
    }

    open fun updateInput() {
        this.producingStack = AssemblyRecipes.findMatchingRecipe(this.testCraftMatrix, this.world)
    }

    private fun compressIntoSlot(slot: Int) {
        if (this.canCompress()) {
            val resultItemStack: ItemStack = this.producingStack

            if (this.getInventory().get(slot).isEmpty()) {
                this.getInventory().set(slot, resultItemStack)
            } else if (this.getInventory().get(slot).isItemEqual(resultItemStack)) {
                if (this.getInventory().get(slot)
                        .getCount() + resultItemStack.getCount() > resultItemStack.getMaxStackSize()
                ) {
                    resultItemStack.grow(this.getInventory().get(slot).getCount() - resultItemStack.getMaxStackSize())
                    GCCoreUtil.spawnItem(this.world, this.getPos(), resultItemStack)
                    this.getInventory().get(slot).setCount(resultItemStack.getMaxStackSize())
                } else {
                    this.getInventory().get(slot).grow(resultItemStack.getCount())
                }
            }

            for (i: Int in 0..<this.testCraftMatrix.getSizeInventory()) {
                if (!this.testCraftMatrix.getStackInSlot(i).isEmpty() && this.testCraftMatrix.getStackInSlot(i)
                        .getItem() === Items.WATER_BUCKET
                ) {
                    this.testCraftMatrix.setInventorySlotContentsNoUpdate(i, ItemStack(Items.BUCKET))
                } else {
                    this.testCraftMatrix.decrStackSize(i, 1)
                }
            }

            this.updateInput()
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getInteger("smeltingTicks")

        this.inventory = NonNullList.withSize<ItemStack>(
            this.getSizeInventory() - this.testCraftMatrix.getSizeInventory(),
            ItemStack.EMPTY
        )
        val nbttaglist: NBTTagList = nbt.getTagList("Items", 10)

        for (i: Int in 0..<nbttaglist.tagCount()) {
            val nbttagcompound: NBTTagCompound = nbttaglist.getCompoundTagAt(i)
            val j: Int = nbttagcompound.getByte("Slot").toInt() and 255

            if (j >= 0 && j < this.getInventory().size) {
                this.getInventory().set(j, ItemStack(nbttagcompound))
            } else if (j < this.getInventory().size + this.testCraftMatrix.getSizeInventory()) {
                this.testCraftMatrix.setInventorySlotContents(j - this.getInventory().size, ItemStack(nbttagcompound))
            }
        }

        this.updateInput()
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("smeltingTicks", this.processTicks)
        val var2: NBTTagList = NBTTagList()
        var var3: Int

        var3 = 0
        while (var3 < this.getInventory().size) {
            if (!this.getInventory().get(var3).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", var3.toByte())
                this.getInventory().get(var3).writeToNBT(var4)
                var2.appendTag(var4)
            }
            ++var3
        }

        var3 = 0
        while (var3 < this.testCraftMatrix.getSizeInventory()) {
            if (this.testCraftMatrix.getStackInSlot(var3) != null) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.setByte("Slot", (var3 + this.getInventory().size) as Byte)
                this.testCraftMatrix.getStackInSlot(var3).writeToNBT(var4)
                var2.appendTag(var4)
            }
            ++var3
        }

        nbt.setTag("Items", var2)
        return nbt
    }

    override fun getSizeInventory(): Int {
        return this.getInventory().size + this.testCraftMatrix.getSizeInventory()
    }


    override fun getStackInSlot(par1: Int): ItemStack {
        if (par1 >= this.getInventory().size) {
            return this.testCraftMatrix.getStackInSlot(par1 - this.getInventory().size)
        }

        return this.getInventory().get(par1)
    }

    override fun decrStackSize(par1: Int, par2: Int): ItemStack {
        if (par1 >= this.getInventory().size) {
            val result: ItemStack = this.testCraftMatrix.decrStackSize(par1 - this.getInventory().size, par2)
            if (result != null) {
                this.updateInput()
            }
            return result
        }

        if (!this.getInventory().get(par1).isEmpty()) {
            val var3: ItemStack

            if (this.getInventory().get(par1).getCount() <= par2) {
                var3 = this.getInventory().get(par1)
                this.getInventory().set(par1, ItemStack.EMPTY)
                return var3
            } else {
                var3 = this.getInventory().get(par1).splitStack(par2)

                if (this.getInventory().get(par1).isEmpty()) {
                    this.getInventory().set(par1, ItemStack.EMPTY)
                }

                this.markDirty()
                return var3
            }
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun removeStackFromSlot(par1: Int): ItemStack {
        if (par1 >= this.getInventory().size) {
            return this.testCraftMatrix.removeStackFromSlot(par1 - this.getInventory().size)
        }

        if (!this.getInventory().get(par1).isEmpty()) {
            val var2: ItemStack = this.getInventory().get(par1)
            this.getInventory().set(par1, ItemStack.EMPTY)
            this.markDirty()
            return var2
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun setInventorySlotContents(par1: Int, stack: ItemStack) {
        if (par1 >= this.getInventory().size) {
            this.testCraftMatrix.setInventorySlotContents(par1 - this.getInventory().size, stack)
            this.updateInput()
        } else {
            this.getInventory().set(par1, stack)

            if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
                stack.setCount(this.getInventoryStackLimit())
            }
        }
        this.markDirty()
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(entityplayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && entityplayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun isItemValidForSlot(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem())
        } else if (slotID >= 6) {
            /*if (this.producingStack != null)
        	{
                ItemStack stackInSlot = this.getStackInSlot(slotID);
                return stackInSlot != null && stackInSlot.isItemEqual(itemStack);
        	}*/

            return true //this.isItemCompressorInput(itemStack, slotID - 6);
        }

        return false
    }


    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(1)
        }
        val slots: IntArray = intArrayOf(0, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val removeSlots: ArrayList<Int> = ArrayList<Int>()

        for (i: Int in 2..10) {
            if (removeSlots.contains(i)) {
                continue
            }
            val stack1: ItemStack = this.getStackInSlot(i)
            if (stack1.isEmpty()) {
                continue
            }

            for (j: Int in i + 1..10) {
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

    override fun canInsertItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, par2ItemStack)
    }

    override fun canExtractItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return slotID == 1
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getStackInSlot(0)
    }

    open fun isItemCompressorInput(stack: ItemStack, id: Int): Boolean {
        for (recipe: IRecipe? in AssemblyRecipes.getRecipeList()) {
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


                        //GalaxySpace.instance.debug("" + (ItemStack)next);
                    } else if (next is ArrayList<*>) {
                        val itr: MutableIterator<ItemStack?> = (next as ArrayList<ItemStack?>).iterator()
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

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockAssembler) {
            return state.getValue(BlockAssembler.FACING)
        }
        return EnumFacing.NORTH
    }

    companion object {
        const val PROCESS_TIME_REQUIRED_BASE: Int = 200
        private val randnum = Random()
    }
}
