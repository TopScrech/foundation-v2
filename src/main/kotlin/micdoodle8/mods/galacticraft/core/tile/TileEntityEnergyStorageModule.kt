package micdoodle8.mods.galacticraft.core.tile

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import java.util.HashSet
import kotlin.math.floor

open class TileEntityEnergyStorageModule @JvmOverloads constructor(tier: Int = 1) :
    TileBaseUniversalElectricalSource(), ISidedInventory, IInventoryDefaults, IConnector, IMachineSides {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileBaseUniversalElectricalSource>.getDisplayName()

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()

    @JvmField
    var scaledEnergyLevel: Int = 0

    @JvmField
    var lastScaledEnergyLevel: Int = 0
    private var lastEnergy = 0f

    private var initialised = false

    private fun setTier2() {
        this.storage.setCapacity(TIER2_CAPACITY)
        this.storage.setMaxExtract(1800f)
        this.setTierGC(2)
    }

    override fun update() {
        if (!this.initialised) {
            val metadata: Int = this.getBlockMetadata()

            //for version update compatibility
            val b: Block = this.world.getBlockState(this.getPos()).getBlock()
            if (b === GCBlocks.machineBase) {
                this.world.setBlockState(this.getPos(), (GCBlocks.machineTiered)!!.getDefaultState(), 2)
            } else if (metadata >= 8) {
                this.setTier2()
            }
            this.initialised = true
        }

        val energy: Float = this.storage.getEnergyStoredGC()
        if (this.getTierGC() == 1 && !this.world.isRemote) {
            if (this.lastEnergy - energy > this.storage.getMaxExtract() - 1) {
                //Deplete faster if being drained at maximum output
                this.storage.extractEnergyGC(25f, false)
            }
        }
        this.lastEnergy = energy

        super.update()

        this.scaledEnergyLevel =
            floor(((this.getEnergyStoredGC() + 49) * 16 / this.getMaxEnergyStoredGC()).toDouble()).toInt()

        if (this.scaledEnergyLevel != this.lastScaledEnergyLevel) {
            this.world.notifyLightSet(this.getPos())
        }

        if (!this.world.isRemote) {
            this.recharge(this.stacks.get(0))
            this.discharge(this.stacks.get(1))
        }

        if (!this.world.isRemote) {
            this.produce()
        }

        this.lastScaledEnergyLevel = this.scaledEnergyLevel
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (this.storage.getEnergyStoredGC() > BASE_CAPACITY) {
            this.setTier2()
            this.initialised = true
        } else {
            this.initialised = false
        }

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        this.readMachineSidesFromNBT(nbt) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > BASE_CAPACITY) {
            this.storage.setEnergyStored(BASE_CAPACITY)
        }

        super.writeToNBT(nbt)

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides

        return nbt
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun getStackInSlot(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): String {
        return GCCoreUtil.translate(if (this.tierGC == 1) "tile.machine.1.name" else "tile.machine.8.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 1
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }


    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return IntArray(0)
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return ItemElectricBase.isElectricItem(itemstack.getItem())
    }

    //    @Override
    //    public int[] getAccessibleSlotsFromSide(int slotID)
    //    {
    //        return new int[] { 0, 1 };
    //    }
    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (itemstack.getItem() is IItemElectricBase) {
            if (slotID == 0) {
                return (itemstack.getItem() as IItemElectricBase).getTransfer(itemstack) > 0
            } else if (slotID == 1) {
                return (itemstack.getItem() as IItemElectricBase).getElectricityStored(itemstack) > 0
            }
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (itemstack.getItem() is IItemElectricBase) {
            if (slotID == 0) {
                return (itemstack.getItem() as IItemElectricBase).getTransfer(itemstack) <= 0
            } else if (slotID == 1) {
                return (itemstack.getItem() as IItemElectricBase).getElectricityStored(itemstack) <= 0 || this.getEnergyStoredGC() >= this.getMaxEnergyStoredGC()
            }
        }

        return false
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getElectricInputDirection())
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(getElectricOutputDirection())
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null || type != NetworkType.POWER) {
            return false
        }

        return getElectricalInputDirections().contains(direction) || getElectricalOutputDirections().contains(direction)
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineTiered) {
            return (state.getValue(BlockMachineTiered.FACING))
        } else if (state.getBlock() is BlockMachine) {
            return (state.getValue(BlockMachine.FACING))
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        when (this.getSide(IMachineSides.MachineSide.ELECTRIC_IN)) {
            IMachineSides.Face.LEFT -> return getFront().rotateY()
            IMachineSides.Face.REAR -> return getFront().getOpposite()
            IMachineSides.Face.TOP -> return EnumFacing.UP
            IMachineSides.Face.BOTTOM -> return EnumFacing.DOWN
            IMachineSides.Face.RIGHT -> return getFront().rotateYCCW()
            else -> return getFront().rotateYCCW()
        }
    }

    override fun getElectricOutputDirection(): EnumFacing {
        when (this.getSide(IMachineSides.MachineSide.ELECTRIC_OUT)) {
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
        return arrayOf<IMachineSides.MachineSide>(
            IMachineSides.MachineSide.ELECTRIC_IN,
            IMachineSides.MachineSide.ELECTRIC_OUT
        )
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face> {
        return arrayOf<IMachineSides.Face>(IMachineSides.Face.RIGHT, IMachineSides.Face.LEFT)
    }

    private var machineSides: Array<IMachineSides.MachineSidePack?>? = null

    /*
     * @param tier: 1 = Electric Furnace  2 = Electric Arc Furnace
     */
    init {
run initialization@ {

        this.initialised = true
        if (tier == 1) {
            //Designed so that Tier 1 Energy Storage can power up to 10 Tier 1 machines
            this.storage.setCapacity(BASE_CAPACITY)
            this.storage.setMaxExtract(300f)
            return@initialization
        }

        this.setTier2()
    
}
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
        return BlockMachineTiered.MACHINESIDES_RENDERTYPE
    } //------------------END OF IMachineSides implementation

    companion object {
        private const val BASE_CAPACITY = 500000f
        private const val TIER2_CAPACITY = 2500000f
    }
}
