package micdoodle8.mods.galacticraft.core.tile

import foundation.item.legacyItemValue

import foundation.persistence.listOfType

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.recipe.ShapedRecipesGC
import micdoodle8.mods.galacticraft.api.recipe.ShapelessOreRecipeGC
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.oredict.OreDictionary
import java.util.ArrayList
import java.util.HashSet
import java.util.Random

open class TileEntityIngotCompressor : TileEntityAdvanced(), IInventoryDefaults, ISidedInventory {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileEntityAdvanced>.getDisplayName()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var furnaceBurnTime: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var currentItemBurnTime: Int = 0
    private var machineTickCounter: Long = 0

    private var producingStack: ItemStack = ItemStack.EMPTY
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    @JvmField
    var compressingCraftMatrix: PersistantInventoryCrafting = PersistantInventoryCrafting()

    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()
    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false
            val flag: Boolean = this.furnaceBurnTime > 0

            if (this.furnaceBurnTime > 0) {
                --this.furnaceBurnTime
            }

            if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                val fuel: ItemStack = this.stacks.get(0)
                this.furnaceBurnTime = TileEntityFurnace.getItemBurnTime(fuel)
                this.currentItemBurnTime = this.furnaceBurnTime

                if (this.furnaceBurnTime > 0) {
                    updateInv = true

                    if (!fuel.isEmpty()) {
                        fuel.shrink(1)

                        if (fuel.getCount() == 0) {
                            this.stacks.set(0, fuel.getItem().getContainerItem(fuel))
                        }
                    }
                }
            }

            if (this.furnaceBurnTime > 0 && this.canSmelt()) {
                ++this.processTicks

                if (this.processTicks % 40 == 0 && this.processTicks > PROCESS_TIME_REQUIRED / 2) {
                    this.world.playSound(
                        null,
                        this.getPos(),
                        SoundEvents.BLOCK_ANVIL_LAND,
                        SoundCategory.BLOCKS,
                        0.3f,
                        this.world.rand.nextFloat() * 0.1f + 0.9f
                    )
                }

                if (this.processTicks == PROCESS_TIME_REQUIRED) {
                    this.processTicks = 0
                    this.smeltItem()
                    updateInv = true
                }
            } else {
                this.processTicks = 0
            }

            if (flag != this.furnaceBurnTime > 0) {
                updateInv = true
            }

            if (updateInv) {
                this.setChanged()
            }
        }

        this.machineTickCounter++
    }

    open fun updateInput() {
        this.producingStack = CompressorRecipes.findMatchingRecipe(this.compressingCraftMatrix, this.world)
    }

    private fun canSmelt(): Boolean {
        val itemstack: ItemStack = this.producingStack
        if (itemstack.isEmpty()) {
            return false
        }
        if (this.stacks.get(1).isEmpty()) {
            return true
        }
        if (!this.stacks.get(1).isItemEqual(itemstack)) {
            return false
        }
        val result: Int = this.stacks.get(1).getCount() + itemstack.getCount()
        return result <= this.getMaxStackSize() && result <= itemstack.getMaxStackSize()
    }

    private fun smeltItem() {
        if (this.canSmelt()) {
            val resultItemStack: ItemStack = this.producingStack
            if (ConfigManagerCore.quickMode) {
                if (resultItemStack.getItem().getTranslationKey(resultItemStack).contains("compressed")) {
                    resultItemStack.grow(resultItemStack.getCount())
                }
            }

            if (this.stacks.get(1).isEmpty()) {
                this.stacks.set(1, resultItemStack.copy())
            } else if (this.stacks.get(1).isItemEqual(resultItemStack)) {
                if (this.stacks.get(1).getCount() + resultItemStack.getCount() > 64) {
                    resultItemStack.grow(this.stacks.get(1).getCount() - 64)
                    GCCoreUtil.spawnItem(this.world, this.getPos(), resultItemStack)
                    this.stacks.get(1).setCount(64)
                } else {
                    this.stacks.get(1).grow(resultItemStack.getCount())
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
        this.processTicks = nbt.getIntOr("smeltingTicks", 0)
        val var2: NBTTagList = nbt.listOfType("Items", 10)

        this.stacks = NonNullList.withSize<ItemStack>(
            this.getContainerSize() - this.compressingCraftMatrix.getSizeInventory(),
            ItemStack.EMPTY
        )

        val nbttaglist: NBTTagList = nbt.listOfType("Items", 10)

        for (i: Int in 0..<nbttaglist.size) {
            val nbttagcompound: NBTTagCompound = nbttaglist.getCompoundOrEmpty(i)
            val j: Int = nbttagcompound.getByteOr("Slot", 0).toInt() and 255

            if (j >= 0 && j < this.stacks.size) {
                this.stacks.set(j, ItemStack(nbttagcompound))
            } else if (j < this.stacks.size + this.compressingCraftMatrix.getSizeInventory()) {
                this.compressingCraftMatrix.setInventorySlotContents(j - this.stacks.size, ItemStack(nbttagcompound))
            }
        }

        this.updateInput()
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)
        val var2: NBTTagList = NBTTagList()
        var i: Int

        i = 0
        while (i < this.stacks.size) {
            if (!this.stacks.get(i).isEmpty()) {
                val tagCompound: NBTTagCompound = NBTTagCompound()
                tagCompound.putByte("Slot", i.toByte())
                this.stacks.get(i).writeToNBT(tagCompound)
                var2.add(tagCompound)
            }
            ++i
        }

        i = 0
        while (i < this.compressingCraftMatrix.getSizeInventory()) {
            if (!this.compressingCraftMatrix.getStackInSlot(i).isEmpty()) {
                val tagCompound: NBTTagCompound = NBTTagCompound()
                tagCompound.putByte("Slot", (i + this.stacks.size).toByte())
                this.compressingCraftMatrix.getStackInSlot(i).writeToNBT(tagCompound)
                var2.add(tagCompound)
            }
            ++i
        }

        nbt.put("Items", var2)
        return nbt
    }


    override fun getContainerSize(): Int {
        return this.stacks.size + this.compressingCraftMatrix.getSizeInventory()
    }

    override fun getItem(par1: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            return this.compressingCraftMatrix.getStackInSlot(par1 - this.stacks.size)
        }

        return this.stacks.get(par1)
    }

    override fun removeItem(par1: Int, par2: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            val result: ItemStack = this.compressingCraftMatrix.decrStackSize(par1 - this.stacks.size, par2)
            if (!result.isEmpty()) {
                this.updateInput()
            }
            this.setChanged()
            return result
        }

        if (!this.stacks.get(par1).isEmpty()) {
            val var3: ItemStack

            if (this.stacks.get(par1).getCount() <= par2) {
                var3 = this.stacks.get(par1)
                this.stacks.set(par1, ItemStack.EMPTY)
                this.setChanged()
                return var3
            } else {
                var3 = this.stacks.get(par1).splitStack(par2)

                if (this.stacks.get(par1).isEmpty()) {
                    this.stacks.set(par1, ItemStack.EMPTY)
                }

                this.setChanged()
                return var3
            }
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun removeItemNoUpdate(par1: Int): ItemStack {
        if (par1 >= this.stacks.size) {
            this.setChanged()
            return this.compressingCraftMatrix.removeStackFromSlot(par1 - this.stacks.size)
        }

        if (!this.stacks.get(par1).isEmpty()) {
            val var2: ItemStack = this.stacks.get(par1)
            this.stacks.set(par1, ItemStack.EMPTY)
            this.setChanged()
            return var2
        } else {
            return ItemStack.EMPTY
        }
    }

    override fun setItem(par1: Int, stack: ItemStack) {
        if (par1 >= this.stacks.size) {
            this.compressingCraftMatrix.setInventorySlotContents(par1 - this.stacks.size, stack)
            this.updateInput()
        } else {
            this.stacks.set(par1, stack)

            if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize())
            }
        }
        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.machine.3.name")
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return TileEntityFurnace.getItemBurnTime(itemStack) > 0
        } else if (slotID >= 2) {
            if (!this.producingStack.isEmpty()) {
                val stackInSlot: ItemStack = this.getItem(slotID)
                return !stackInSlot.isEmpty() && stackInSlot.isItemEqual(itemStack)
            }
            return isItemCompressorInput(itemStack)
        }

        return false
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(1)
        }
        val slots: IntArray = intArrayOf(0, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val removeSlots: ArrayList<Int> = ArrayList<Int>()

        for (i: Int in 2..10) {
            if (removeSlots.contains(i)) {
                continue
            }
            val stack1: ItemStack = this.getItem(i)
            if (stack1.isEmpty()) {
                continue
            }

            for (j: Int in i + 1..10) {
                if (removeSlots.contains(j)) {
                    continue
                }
                val stack2: ItemStack = this.getItem(j)
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

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 1
    }

    override fun getPacketRange(): Double {
        return 12.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    companion object {
        const val PROCESS_TIME_REQUIRED: Int = 200
        private val random = Random()

        @JvmStatic
        fun isItemCompressorInput(stack: ItemStack): Boolean {
            for (recipe: IRecipe? in CompressorRecipes.getRecipeList()) {
                if (recipe is ShapedRecipesGC) {
                    for (itemstack1: ItemStack in recipe.recipeItems) {
                        if (stack.getItem() === itemstack1.getItem() && (itemstack1.legacyItemValue == 32767 || stack.legacyItemValue == itemstack1.legacyItemValue)) {
                            return true
                        }
                    }
                } else if (recipe is ShapelessOreRecipeGC) {
                    val required: ArrayList<Any?> = ArrayList<Any?>(recipe.getInput())

                    val req: MutableIterator<Any?> = required.iterator()

                    var match: Int = 0

                    while (req.hasNext()) {
                        val next: Any? = req.next()

                        if (next is ItemStack) {
                            if (OreDictionary.itemMatches(next, stack, false)) {
                                match++
                            }
                        } else if (next is MutableList<*>) {
                            for (itemStack: ItemStack in (next as MutableList<ItemStack>)) {
                                if (OreDictionary.itemMatches(itemStack, stack, false)) {
                                    match++
                                    break
                                }
                            }
                        }
                    }

                    if (match == 0) {
                        continue
                    }

                    if (match == 1) {
                        return true
                    }

                    return random.nextInt(match) == 0
                }
            }

            return false
        }
    }
}
