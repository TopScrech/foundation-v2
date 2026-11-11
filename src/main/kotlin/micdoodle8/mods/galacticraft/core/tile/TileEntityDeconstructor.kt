package micdoodle8.mods.galacticraft.core.tile

import foundation.item.legacyItemValue

import foundation.item.FoundationItemVariants

import foundation.persistence.listOfType

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe
import java.util.LinkedList

open class TileEntityDeconstructor constructor() : TileBaseElectricBlock(), IInventoryDefaults, ISidedInventory,
    IMachineSides {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileBaseElectricBlock>.getDisplayName()

    @JvmField
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private val producingStack: ItemStack = ItemStack.EMPTY
    private var machineTickCounter: Long = 0

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(11, ItemStack.EMPTY)

    private var recursiveCount = 0

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false

            if (this.hasEnoughEnergyToRun) {
                if (this.canDeconstruct()) {
                    ++this.processTicks

                    if ((this.processTicks * 5) % this.processTimeRequired == 5) {
                        this.world.playSound(
                            null,
                            this.getPos(),
                            GCSounds.deconstructor,
                            SoundCategory.BLOCKS,
                            0.25f,
                            this.world.rand.nextFloat() * 0.04f + 0.38f
                        )
                    }

                    if (this.processTicks >= this.processTimeRequired) {
                        this.processTicks = 0
                        this.deconstruct()
                        updateInv = true
                    }
                } else {
                    this.processTicks = 0
                }
            } else {
                this.processTicks = 0
            }

            if (updateInv) {
                this.setChanged()
            }
        }

        this.machineTickCounter++
    }

    private fun canDeconstruct(): Boolean {
        return !this.stacks.get(1).isEmpty()
    }

    open fun deconstruct() {
        val ingredients: MutableList<ItemStack> = LinkedList<ItemStack>()
        ingredients.add(ItemStack(this.stacks.get(1).getItem(), 1, this.stacks.get(1).legacyItemValue))
        this.recursiveCount = 0
        var salvaged: MutableList<ItemStack>? = this.getSalvageable(ingredients)
        salvaged = this.squashList(salvaged)
        salvaged = this.randomChanceList(salvaged)
        if (salvaged != null) {
            for (output: ItemStack in salvaged) {
                this.addToOutputMatrix(output)
            }
        }
        this.removeItem(1, 1)
    }

    private fun getSalvageable(ingredients: MutableList<ItemStack>?): MutableList<ItemStack>? {
        if (ingredients == null || ingredients.isEmpty()) {
            return null
        }
        if (this.recursiveCount++ > 10) {
            this.recursiveCount = 0
            return null
        }

        val ret: MutableList<ItemStack> = LinkedList<ItemStack>()
        for (stack: ItemStack in ingredients) {
            if (isSalvage(stack)) {
                ret.add(stack)
            } else {
                GCLog.debug("Trying to " + this.recursiveCount + " break down " + stack.toString())
                val ingredients2: MutableList<ItemStack>? = this.getIngredients(stack)
                val recursive: MutableList<ItemStack>? = this.getSalvageable(ingredients2)
                if (recursive != null && !recursive.isEmpty()) {
                    ret.addAll(recursive)
                }
            }
        }
        this.recursiveCount--
        return ret
    }

    private fun squashList(ingredients: MutableList<ItemStack>?): MutableList<ItemStack>? {
        if (ingredients == null || ingredients.isEmpty()) {
            return null
        }
        val ret: MutableList<ItemStack> = LinkedList<ItemStack>()
        for (stack: ItemStack in ingredients) {
            var matched: Boolean = false
            for (stack1: ItemStack in ret) {
                if (ItemStack.areItemsEqual(stack, stack1)) {
                    matched = true
                    stack1.grow(stack.getCount())
                    break
                }
            }
            if (!matched) {
                ret.add(stack)
            }
        }
        return ret
    }

    private fun randomChanceList(ingredients: MutableList<ItemStack>?): MutableList<ItemStack>? {
        if (ingredients == null || ingredients.isEmpty()) {
            return null
        }
        val ret: MutableList<ItemStack> = LinkedList<ItemStack>()
        for (stack: ItemStack in ingredients) {
            val count: Int = stack.getCount()
            var result: Int = 0
            for (i: Int in 0..<count) {
                if (this.world.rand.nextFloat() < SALVAGE_CHANCE) {
                    result++
                }
            }
            if (result > 0) {
                stack.setCount(result)
                ret.add(stack)
            }
        }
        return ret
    }

    private fun getIngredients(stack: ItemStack): MutableList<ItemStack>? {
        for (recipe: INasaWorkbenchRecipe in knownRecipes) {
            val test: ItemStack = recipe.getRecipeOutput()
            if (ItemStack.areItemsEqual(test, stack) && test.getCount() == 1) {
                return toItemStackList((recipe.getRecipeInput())!!.values)
            }
        }
        for (recipe: IRecipe in CraftingManager.REGISTRY) {
            val test: ItemStack = recipe.getRecipeOutput()
            if (ItemStack.areItemsEqual(test, stack) && test.getCount() == 1) {
                if (recipe is ShapedRecipes) {
                    return expandRecipeInputs(recipe.recipeItems)
                } else if (recipe is ShapelessRecipes) {
                    return expandRecipeInputs(recipe.recipeItems)
                } else if (recipe is ShapedOreRecipe) {
                    return expandRecipeInputs(recipe.getIngredients())
                } else if (recipe is ShapelessOreRecipe) {
                    return expandRecipeInputs(recipe.getIngredients())
                }
            }
        }
        return null
    }

    private fun expandRecipeInputs(inputs: MutableList<*>): MutableList<ItemStack> {
        val ret: MutableList<ItemStack> = LinkedList<ItemStack>()
        for (input: Any? in inputs) {
            val toAdd: ItemStack? = parseRecipeInput(input)
            if (toAdd != null && !toAdd.isEmpty()) {
                ret.add(toAdd)
            }
        }
        return ret
    }

    private fun parseRecipeInput(input: Any?): ItemStack? {
        if (input is Ingredient) {
            for (obj: ItemStack? in input.getMatchingStacks()) {
                val ret: ItemStack? = parseRecipeInput(obj)
                if (ret != null) {
                    return ret
                }
            }
        } else if (input is ItemStack) {
            val stack: ItemStack = input
            if (stack.legacyItemValue == OreDictionary.WILDCARD_VALUE) {
                return ItemStack(stack.getItem(), stack.getCount(), 0)
            } else {
                return stack.copy()
            }
        } else if (input is String) {
            val stacks: MutableList<ItemStack> = OreDictionary.getOres(input)
            if (stacks.isEmpty()) {
                return null
            }
            for (stack: ItemStack in stacks) {
                if (isSalvage(stack));
                run {
                    return stack.copy()
                }
            }
            return if (stacks.get(0) == null) null else stacks.get(0).copy()
        } else if (input is Iterable<*>) {
            for (obj: Any? in input) {
                val ret: ItemStack? = parseRecipeInput(obj)
                if (ret != null) {
                    return ret
                }
            }
        }

        return null
    }

    private fun toItemStackList(inputs: MutableCollection<ItemStack?>): MutableList<ItemStack> {
        val ret: MutableList<ItemStack> = LinkedList<ItemStack>()
        for (o: ItemStack? in inputs) {
            if (o != null && !o.isEmpty()) {
                ret.add(o.copy())
            }
        }
        return ret
    }

    private fun addToOutputMatrix(stack: ItemStack) {
        for (i: Int in 2..10) {
            if (this.stacks.get(i).isEmpty()) {
                this.stacks.set(i, stack)
                return
            }
            if (!(ItemStack.areItemsEqual(stack, this.stacks.get(i)))) {
                continue
            }
            val size: Int = this.stacks.get(i).getCount()
            if (size + stack.getCount() < this.getMaxStackSize()) {
                this.stacks.get(i).grow(stack.getCount())
                return
            }
            this.stacks.get(i).setCount(this.getMaxStackSize())
            stack.shrink(this.getMaxStackSize() - size)
        }
        GCCoreUtil.spawnItem(this.world, this.getPos(), stack)
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        this.processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)
        val var2: NBTTagList = par1NBTTagCompound.listOfType("Items", 10)
        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)

        for (i: Int in 0..<var2.size) {
            val nbttagcompound: NBTTagCompound = var2.getCompoundOrEmpty(i)
            val j: Int = nbttagcompound.getByteOr("Slot", 0).toInt() and 255

            if (j >= 0 && j < this.stacks.size) {
                this.stacks.set(j, ItemStack(nbttagcompound))
            }
        }

        this.readMachineSidesFromNBT(par1NBTTagCompound) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)
        val var2: NBTTagList = NBTTagList()
        var var3: Int

        var3 = 0
        while (var3 < this.stacks.size) {
            if (!this.stacks.get(var3).isEmpty()) {
                val var4: NBTTagCompound = NBTTagCompound()
                var4.putByte("Slot", var3.toByte())
                this.stacks.get(var3).writeToNBT(var4)
                var2.add(var4)
            }
            ++var3
        }
        nbt.put("Items", var2)

        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides
        return nbt
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(par1: Int): ItemStack {
        return this.stacks.get(par1)
    }

    override fun removeItem(par1: Int, par2: Int): ItemStack {
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
            return ItemStack.EMPTY
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
        if (par1 < this.stacks.size) {
            this.stacks.set(par1, stack)

            if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize())
            }
        }
        this.setChanged()
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.machine2.10.name")
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(entityplayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && entityplayer.getDistanceSq(
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
    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return itemStack != null && !itemStack.isEmpty() && ItemElectricBase.isElectricItem(itemStack.getItem())
        }

        return slotID == 1
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        return intArrayOf(1)
    }

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID >= 2
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
        return this.getItem(0)
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
        const val SALVAGE_CHANCE: Float = 0.75f

        const val PROCESS_TIME_REQUIRED_BASE: Int = 250

        @JvmField
        var salvageable: MutableList<ItemStack> = LinkedList<ItemStack>()

        @JvmField
        var knownRecipes: MutableList<INasaWorkbenchRecipe> = LinkedList<INasaWorkbenchRecipe>()

        init {
            initialiseItemList()
        }

        @JvmStatic
        private fun initialiseItemList() {
            if (GalacticraftCore.isPlanetsLoaded) {
                addSalvage(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 5)) //T3 plate
                addSalvage(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 6)) //Compressed titanium
                addSalvage(FoundationItemVariants.stack(requireNotNull(AsteroidsItems.basicItem), 1, 0)) //Titanium ingot
                addSalvage(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 3)) //T2 plate
                addSalvage(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 5)) //Compressed desh
                addSalvage(FoundationItemVariants.stack(requireNotNull(MarsItems.marsItemBasic), 1, 2)) //Desh ingot
            }
            addSalvage(ItemStack(GCItems.flagPole))
            addSalvage(ItemStack(GCItems.heavyPlatingTier1))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 1)) //Compressed meteoric iron
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 0)) //Meteoric iron ingot
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 9)) //Compressed steel
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 10)) //Compressed bronze
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 8))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 11)) //Compressed iron
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 3))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 4))
            addSalvage(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 5))
            addSalvage(ItemStack(Items.IRON_INGOT))
            addSalvage(ItemStack(Items.GOLD_INGOT))
            addSalvage(ItemStack(Items.GOLD_NUGGET))
        }

        @JvmStatic
        fun initialiseRecipeList() {
            knownRecipes.addAll((GalacticraftRegistry.getRocketT1Recipes() as Collection<INasaWorkbenchRecipe>))
            knownRecipes.addAll((GalacticraftRegistry.getBuggyBenchRecipes() as Collection<INasaWorkbenchRecipe>))
        }

        @JvmStatic
        fun initialiseRecipeListPlanets() {
            knownRecipes.addAll((GalacticraftRegistry.getRocketT2Recipes() as Collection<INasaWorkbenchRecipe>))
            knownRecipes.addAll((GalacticraftRegistry.getCargoRocketRecipes() as Collection<INasaWorkbenchRecipe>))
            knownRecipes.addAll((GalacticraftRegistry.getRocketT3Recipes() as Collection<INasaWorkbenchRecipe>))
            knownRecipes.addAll((GalacticraftRegistry.getAstroMinerRecipes() as Collection<INasaWorkbenchRecipe>))
        }

        @JvmStatic
        fun addSalvage(itemStack: ItemStack) {
            for (inList: ItemStack in salvageable) {
                if (ItemStack.areItemsEqual(inList, itemStack)) return
            }
            salvageable.add(itemStack.copy())
        }

        @JvmStatic
        fun isSalvage(stack: ItemStack): Boolean {
            for (inList: ItemStack in salvageable) {
                if (ItemStack.areItemsEqual(inList, stack)) return true
            }
            return false
        }
    }
}
