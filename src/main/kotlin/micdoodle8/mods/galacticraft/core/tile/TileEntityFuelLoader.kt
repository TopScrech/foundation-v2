package micdoodle8.mods.galacticraft.core.tile

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.entity.IFuelable
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.blocks.BlockFuelLoader
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityFuelLoader constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IFluidHandlerWrapper, ILandingPadAttachable, IMachineSides {
    private val tankCapacity = 12000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fuelTank: FluidTank? = FluidTank(this.tankCapacity)
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY)

    @JvmField
    var attachedFuelable: IFuelable? = null
    private var loadedFuelLastTick = false

    open fun getScaledFuelLevel(i: Int): Int {
        val fuelLevel: Double =
            (if (this.fuelTank!!.getFluid() == null) 0 else this.fuelTank!!.getFluid()!!.amount).toDouble()

        return (fuelLevel * i / this.tankCapacity).toInt()
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            this.loadedFuelLastTick = false

            val liquidContained: FluidStack? = FluidUtil.getFluidContained(this.stacks.get(1))
            if (FluidUtil.isFuel(liquidContained)) {
                FluidUtil.loadFromContainer((this.fuelTank)!!, (GCFluids.fluidFuel)!!, this.stacks, 1, liquidContained!!.amount)
            }

            if (this.ticks % 100 == 0) {
                this.attachedFuelable = null

                val thisVec: BlockVec3 = BlockVec3(this)
                for (dir: EnumFacing in EnumFacing.values()) {
                    val pad: TileEntity? = thisVec.getTileEntityOnSide(this.world, dir)

                    if (pad is TileEntityMulti) {
                        val mainTile: TileEntity? = pad.getMainBlockTile()

                        if (mainTile is IFuelable) {
                            this.attachedFuelable = mainTile as IFuelable
                            break
                        }
                    } else if (pad is IFuelable) {
                        this.attachedFuelable = pad as IFuelable
                        break
                    }
                }
            }

            if (this.fuelTank != null && this.fuelTank!!.getFluid() != null && this.fuelTank!!.getFluid()!!.amount > 0) {
                val liquid: FluidStack = FluidStack(GCFluids.fluidFuel, 2)

                if (this.attachedFuelable != null && this.hasEnoughEnergyToRun && !this.disabled) {
                    val filled: Int = this.attachedFuelable!!.addFuel(liquid, true)
                    this.loadedFuelLastTick = filled > 0
                    this.fuelTank!!.drain(filled, true)
                }
            }
        }
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        this.stacks = this.readStandardItemsFromNBT(par1NBTTagCompound)

        if (par1NBTTagCompound.containsTag("fuelTank")) {
            this.fuelTank!!.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("fuelTank"))
        }

        this.readMachineSidesFromNBT(par1NBTTagCompound) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        if (this.fuelTank!!.getFluid() != null) {
            nbt.put("fuelTank", this.fuelTank!!.writeToNBT(NBTTagCompound()))
        }

        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides

        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.fuelloader.name")
    }

    override fun getMaxStackSize(): Int {
        return 1
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (slotID == 1 && !itemstack.isEmpty()) {
            return FluidUtil.isEmptyContainer(itemstack)
        }
        return false
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return (slotID == 1 && itemstack != null && itemstack.getItem() === GCItems.fuelCanister) || (if (slotID == 0) ItemElectricBase.isElectricItem(
            itemstack.getItem()
        ) else false)
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (this.getPipeInputDirection() == from) {
            return this.fuelTank!!.getFluid() == null || this.fuelTank!!.getFluidAmount() < this.fuelTank!!.getCapacity()
        }
        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (this.getPipeInputDirection() == from && resource != null) {
            if (FluidUtil.testFuel(FluidRegistry.getFluidName(resource))) {
                used = this.fuelTank!!.fill(resource, doFill)
            }
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?>? {
        if (this.getPipeInputDirection() == from) {
            return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.fuelTank)) as Array<FluidTankInfo?>?))!!
        }
        return null
    }

    override fun shouldUseEnergy(): Boolean {
        return this.fuelTank!!.getFluid() != null && this.fuelTank!!.getFluid()!!.amount > 0 && !this.getDisabled(0) && loadedFuelLastTick
    }

    override fun canAttachToLandingPad(world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockFuelLoader) {
            return state.getValue(BlockFuelLoader.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }
        return super.getCapability<T?>(capability, facing)
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            return direction == this.getPipeInputDirection()
        }
        return false
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

    override fun getPipeInputDirection(): EnumFacing {
        when (this.getSide(IMachineSides.MachineSide.PIPE_IN)) {
            IMachineSides.Face.RIGHT -> return getFront().rotateYCCW()
            IMachineSides.Face.REAR -> return getFront().getOpposite()
            IMachineSides.Face.TOP -> return EnumFacing.UP
            IMachineSides.Face.BOTTOM -> return EnumFacing.DOWN
            IMachineSides.Face.LEFT -> return getFront().rotateY()
            else -> return getFront().rotateYCCW()
        }
    }

    //------------------
    //Added these methods and field to implement IMachineSides properly 
    //------------------
    override fun listConfigurableSides(): Array<IMachineSides.MachineSide> {
        return arrayOf<IMachineSides.MachineSide>(
            IMachineSides.MachineSide.ELECTRIC_IN,
            IMachineSides.MachineSide.PIPE_IN
        )
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face> {
        return arrayOf<IMachineSides.Face>(IMachineSides.Face.LEFT, IMachineSides.Face.RIGHT)
    }

    private var machineSides: Array<IMachineSides.MachineSidePack?>? = null

    init {
        this.storage.setMaxExtract(30f)
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
        return BlockFuelLoader.MACHINESIDES_RENDERTYPE
    } //------------------END OF IMachineSides implementation
}
