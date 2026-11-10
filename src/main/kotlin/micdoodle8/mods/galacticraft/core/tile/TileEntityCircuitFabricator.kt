package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList

open class TileEntityCircuitFabricator constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IMachineSides {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0
    private var producingStack: ItemStack = ItemStack.EMPTY
    private var machineTickCounter: Long = 0

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(7, ItemStack.EMPTY)

    override fun update() {
        super.update()

        this.updateInput()

        if (!this.world.isRemote) {
            var updateInv: Boolean = false

            if (this.hasEnoughEnergyToRun) {
                if (this.canCompress()) {
                    ++this.processTicks

                    if (this.processTicks >= this.getProcessTimeRequired()) {
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

    open fun getProcessTimeRequired(): Int {
        return PROCESS_TIME_REQUIRED * 2 / (1 + this.poweredByTierGC)
    }

    open fun updateInput() {
        this.producingStack = (CircuitFabricatorRecipes.getOutputForInput((this.stacks.subList(1, 6) as MutableList<ItemStack>)))!!
    }

    private fun canCompress(): Boolean {
        if (this.producingStack.isEmpty()) {
            return false
        }
        if (this.stacks.get(6).isEmpty()) {
            return true
        }
        if (!this.stacks.get(6).isEmpty() && !this.stacks.get(6).isItemEqual(this.producingStack)) {
            return false
        }
        val result: Int =
            if (this.stacks.get(6).isEmpty()) 0 else this.stacks.get(6).getCount() + this.producingStack.getCount()
        return result <= this.getInventoryStackLimit() && result <= this.producingStack.getMaxStackSize()
    }

    open fun compressItems() {
        if (this.canCompress()) {
            val resultItemStack: ItemStack = this.producingStack.copy()
            if (this.world.provider is IZeroGDimension) {
                if (resultItemStack.getItem() === GCItems.basicItem) {
                    if (resultItemStack.getItemDamage() == ItemBasic.WAFER_BASIC) {
                        resultItemStack.setCount(5)
                    } else if (resultItemStack.getItemDamage() == 12)  //Solar panels
                    {
                        resultItemStack.setCount(15)
                    } else {
                        resultItemStack.setCount(resultItemStack.getCount() * 2)
                    }
                }
            }

            if (this.stacks.get(6).isEmpty()) {
                this.stacks.set(6, resultItemStack)
            } else if (this.stacks.get(6).isItemEqual(resultItemStack)) {
                if (this.stacks.get(6).getCount() + resultItemStack.getCount() > 64) {
                    resultItemStack.setCount(this.stacks.get(6).getCount() + resultItemStack.getCount() - 64)
                    GCCoreUtil.spawnItem(this.world, this.getPos(), resultItemStack)
                    this.stacks.get(6).setCount(64)
                } else {
                    this.stacks.get(6).grow(resultItemStack.getCount())
                }
            }
        }

        for (i: Int in 1..5) {
            this.decrStackSize(i, 1)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getInteger("smeltingTicks")
        this.stacks = this.readStandardItemsFromNBT(nbt)
        this.readMachineSidesFromNBT(nbt) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("smeltingTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)
        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides
        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): String {
        return GCCoreUtil.translate("tile.machine2.5.name")
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemStack: ItemStack): Boolean {
        if (slotID == 0) {
            return itemStack != null && ItemElectricBase.isElectricItem(itemStack.getItem())
        }

        if (slotID > 5) {
            return false
        }

        val list: ArrayList<ItemStack?> = CircuitFabricatorRecipes.slotValidItems.get(slotID - 1)

        for (testEntry in list) {
                val test = testEntry!!
            if (test.isItemEqual(itemStack)) {
                return true
            }
        }

        return false
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(6)
        }

        //Offer whichever silicon slot has less silicon
        val siliconFlag: Boolean = !this.stacks.get(2).isEmpty() && (this.stacks.get(3).isEmpty() || this.stacks.get(3)
            .getCount() < this.stacks.get(2).getCount())
        return if (siliconFlag) intArrayOf(0, 1, 3, 4, 5) else intArrayOf(0, 1, 2, 4, 5)
    }

    override fun canInsertItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID < 6 && this.isItemValidForSlot(slotID, par2ItemStack)
    }

    override fun canExtractItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 6
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachine2) {
            return (state.getValue(BlockMachine2.FACING))
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
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 40 else 20).toFloat())
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
        const val PROCESS_TIME_REQUIRED: Int = 300
    }
}
