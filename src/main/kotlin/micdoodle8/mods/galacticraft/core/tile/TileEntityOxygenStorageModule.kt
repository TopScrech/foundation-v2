package micdoodle8.mods.galacticraft.core.tile

import java.util.EnumSet
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTankInfo
import java.util.HashSet
import kotlin.math.floor
import kotlin.math.min

open class TileEntityOxygenStorageModule constructor() : TileEntityOxygen(OXYGEN_CAPACITY, 40), IInventoryDefaults,
    ISidedInventory, IMachineSides {
    override fun getDisplayName(): net.minecraft.util.text.ITextComponent? = super<TileEntityOxygen>.getDisplayName()

    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()

    @JvmField
    var scaledOxygenLevel: Int = 0
    private var lastScaledOxygenLevel = 0

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    override fun update() {
        if (!this.world.isRemote) {
            val oxygenItemStack: ItemStack = this.getStackInSlot(0)
            if (oxygenItemStack != null && oxygenItemStack.getItem() is IItemOxygenSupply) {
                val oxygenItem: IItemOxygenSupply = oxygenItemStack.getItem() as IItemOxygenSupply
                val oxygenDraw: Int = floor(
                    min(
                        this.oxygenPerTick * 2.5f,
                        (this.getMaxOxygenStored() - this.getOxygenStored()).toFloat()
                    ).toDouble()
                ).toInt()
                this.setOxygenStored(getOxygenStored() + oxygenItem.discharge(oxygenItemStack, oxygenDraw))
                if (this.getOxygenStored() > this.getMaxOxygenStored()) {
                    this.setOxygenStored(this.getOxygenStored())
                }
            }
        }

        super.update()

        this.scaledOxygenLevel = this.getScaledOxygenLevel(16)

        if (this.scaledOxygenLevel != this.lastScaledOxygenLevel) {
            this.world.notifyLightSet(this.getPos())
        }

        this.lastScaledOxygenLevel = this.scaledOxygenLevel

        this.produceOxygen(getFront().rotateY().getOpposite())

        // if (!this.world.isRemote)
        // {
        // int gasToSend = Math.min(this.storedOxygen,
        // GCCoreTileEntityOxygenStorageModule.OUTPUT_PER_TICK);
        // GasStack toSend = new GasStack(GalacticraftCore.gasOxygen,
        // gasToSend);
        // this.storedOxygen -= GasTransmission.emitGasToNetwork(toSend, this,
        // this.getOxygenOutputDirection());
        //
        // Vector3 thisVec = new Vector3(this);
        // TileEntity tileEntity =
        // thisVec.modifyPositionFromSide(this.getOxygenOutputDirection()).getTileEntity(this.world);
        //
        // if (tileEntity instanceof IGasAcceptor)
        // {
        // if (((IGasAcceptor)
        // tileEntity).canReceiveGas(this.getOxygenInputDirection(),
        // GalacticraftCore.gasOxygen))
        // {
        // double sendingGas = 0;
        //
        // if (this.storedOxygen >=
        // GCCoreTileEntityOxygenStorageModule.OUTPUT_PER_TICK)
        // {
        // sendingGas = GCCoreTileEntityOxygenStorageModule.OUTPUT_PER_TICK;
        // }
        // else
        // {
        // sendingGas = this.storedOxygen;
        // }
        //
        // this.storedOxygen -= sendingGas - ((IGasAcceptor)
        // tileEntity).receiveGas(new GasStack(GalacticraftCore.gasOxygen, (int)
        // Math.floor(sendingGas)));
        // }
        // }
        // }
        this.lastScaledOxygenLevel = this.scaledOxygenLevel
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
        this.readMachineSidesFromNBT(nbt) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.stacks)
        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides
        return nbt
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun shouldPullEnergy(): Boolean {
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return false
    }

    override fun getElectricInputDirection(): EnumFacing? {
        return null
    }

    override fun getBatteryInSlot(): ItemStack {
        return ItemStack.EMPTY
    }

    override fun shouldUseOxygen(): Boolean {
        return false
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return if (this.getOxygenOutputDirections().contains(direction)) min(
            OUTPUT_PER_TICK,
            this.getOxygenStored()
        ) else 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(this.getPos())
        if (state.getBlock() !== GCBlocks.machineBase2) {
            return EnumFacing.NORTH
        }
        return state.getValue(BlockMachine2.FACING)
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
        return GCCoreUtil.translate("tile.machine2.6.name")
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return slotID == 0 && itemstack != null && itemstack.getItem() is IItemOxygenSupply
    }

    //ISidedInventory
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (slotID == 0 && this.isItemValidForSlot(slotID, itemstack)) {
            return itemstack.getItemDamage() < itemstack.getItem().getMaxDamage()
        }
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (slotID == 0 && !itemstack.isEmpty()) {
            return FluidUtil.isEmptyContainer(itemstack)
        }
        return false
    }

    //IFluidHandler methods - to allow this to accept Liquid Oxygen
    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return super.canDrain(from, fluid)
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return super.drain(from, resource, doDrain)
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return super.drain(from, maxDrain, doDrain)
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
//        if (from.ordinal() == this.getBlockMetadata() - BlockMachine2.OXYGEN_STORAGE_MODULE_METADATA + 2 && GalacticraftCore.isPlanetsLoaded)
//        {
//            //Can fill with LOX only
//            return fluid != null && fluid.getName().equals(AsteroidsModule.fluidLiquidOxygen.getName());
//        }

        return super.canFill(from, fluid)
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
//        int used = 0;
//
//        if (resource != null && this.canFill(from, resource.getFluid()))
//        {
//            used = (int) (this.receiveOxygen((int) Math.floor(resource.amount / Constants.LOX_GAS_RATIO), doFill) * Constants.LOX_GAS_RATIO);
//        }

        return super.fill(from, resource, doFill)
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
//        FluidTankInfo[] tankInfo = new FluidTankInfo[] {};
//        int metaside = this.getBlockMetadata() - BlockMachine2.OXYGEN_STORAGE_MODULE_METADATA + 2;
//        int side = from.ordinal();
//
//        if (metaside == side && GalacticraftCore.isPlanetsLoaded)
//        {
//            tankInfo = new FluidTankInfo[] { new FluidTankInfo(new FluidStack(AsteroidsModule.fluidLiquidOxygen, (int) (this.getOxygenStored() * Constants.LOX_GAS_RATIO)), (int) (OXYGEN_CAPACITY * Constants.LOX_GAS_RATIO)) };
//        }
//        return tankInfo;
        return super.getTankInfo(from)
    }

    override fun getOxygenInputDirections(): EnumSet<EnumFacing?> {
        val dir: EnumFacing
        when (this.getSide(IMachineSides.MachineSide.ELECTRIC_IN)) {
            IMachineSides.Face.REAR -> dir = getFront().getOpposite()
            IMachineSides.Face.TOP -> dir = EnumFacing.UP
            IMachineSides.Face.BOTTOM -> dir = EnumFacing.DOWN
            IMachineSides.Face.RIGHT -> dir = getFront().rotateYCCW()
            IMachineSides.Face.LEFT -> dir = getFront().rotateY()
            else -> dir = getFront().rotateY()
        }
        return EnumSet.of<EnumFacing?>(dir)
    }

    override fun getOxygenOutputDirections(): EnumSet<EnumFacing?> {
        val dir: EnumFacing
        when (this.getSide(IMachineSides.MachineSide.PIPE_OUT)) {
            IMachineSides.Face.REAR -> dir = getFront().getOpposite()
            IMachineSides.Face.TOP -> dir = EnumFacing.UP
            IMachineSides.Face.BOTTOM -> dir = EnumFacing.DOWN
            IMachineSides.Face.LEFT -> dir = getFront().rotateY()
            IMachineSides.Face.RIGHT -> dir = getFront().rotateYCCW()
            else -> dir = getFront().rotateYCCW()
        }
        return EnumSet.of<EnumFacing?>(dir)
    }

    //------------------
    //Added these methods and field to implement IMachineSides properly 
    //------------------
    override fun listConfigurableSides(): Array<IMachineSides.MachineSide> {
        //Have to use Electric_In for compatibility with other BlockMachine2, as all use same blockstate
        return arrayOf<IMachineSides.MachineSide>(
            IMachineSides.MachineSide.ELECTRIC_IN,
            IMachineSides.MachineSide.PIPE_OUT
        )
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face> {
        return arrayOf<IMachineSides.Face>(IMachineSides.Face.LEFT, IMachineSides.Face.RIGHT)
    }

    private var machineSides: Array<IMachineSides.MachineSidePack?>? = null

    init {
        this.storage.setCapacity(0f)
        this.storage.setMaxExtract(0f)
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
        const val OUTPUT_PER_TICK: Int = 500

        const val OXYGEN_CAPACITY: Int = 60000
    }
}
