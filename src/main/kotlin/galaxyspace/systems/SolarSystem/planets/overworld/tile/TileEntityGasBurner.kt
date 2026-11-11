package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasBurner
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasBurner
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityGasBurner constructor() : TileEntityElectricInventory("tile.gas_burner.name"), IFluidHandlerWrapper,
    ISidedInventory, IPacketReceiver {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    private val tankCapacity = 3000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTank: FluidTank = FluidTank(this.tankCapacity)


    // private NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
    init {
        this.storage.setCapacity(15000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 45 else 25).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            if (this.canProcess()) {
                if (this.hasEnoughEnergyToRun) {
                    if (this.processTicks == 0) {
                        this.processTicks = this.processTimeRequired
                    } else {
                        if (--this.processTicks <= 0) {
                            this.smeltItem()
                            this.processTicks = if (this.canProcess()) this.processTimeRequired else 0
                        }
                    }
                } else if (this.processTicks > 0 && this.processTicks < this.processTimeRequired) {
                    //Apply a "cooling down" process if the electric furnace runs out of energy while smelting
                    if (this.world.rand.nextInt(4) == 0) {
                        this.processTicks++
                    }
                }
            } else {
                this.processTicks = 0
            }
        }
    }

    open fun canProcess(): Boolean {
        if (this.gasTank.getFluidAmount() <= 0) return false

        return true
    }

    open fun smeltItem() {
        if (OxygenUtil.isAABBInBreathableAirBlock(world, AxisAlignedBB(getPos().add(0, 1, 0)))) {
            this.world.setBlockAndUpdate(this.getPos().up(), Blocks.FIRE.defaultBlockState())
        }
        this.gasTank.drain(100, true)
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)
        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())

        if (par1NBTTagCompound.containsTag("gasTank")) this.gasTank.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("gasTank"))
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("smeltingTicks", this.processTicks)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.inventory)

        if (this.gasTank.getFluid() != null) par1NBTTagCompound.put(
            "gasTank",
            this.gasTank.writeToNBT(NBTTagCompound())
        )

        return par1NBTTagCompound
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(0)
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (fluid != null && from == this.getPipe() && fluid.isGaseous()) {
            return this.gasTank.getFluid() == null || (this.gasTank.getFluid()!!
                .getFluid() == fluid && this.gasTank.getFluidAmount() < this.gasTank.getCapacity())
        }
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (resource != null && this.canFill(from, resource.getFluid())) {
            if (from == this.getPipe()) {
                val liquidName: String? = FluidRegistry.getFluidName(resource)
                if (liquidName != null) used = this.gasTank.fill(resource, doFill)
            }
        }

        return used
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        return null
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.gasTank)) as Array<FluidTankInfo?>?))!!
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

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockGasBurner) {
            return state.getValue(BlockGasBurner.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            val pipeSide: EnumFacing = getPipe()
            return direction == pipeSide
        }
        return false
    }

    private fun getPipe(): EnumFacing {
        return getFront().rotateYCCW()
    }

    override fun getElectricInputDirection(): EnumFacing {
        //return getFront().rotateY();
        return EnumFacing.DOWN
    }

    open fun getScaledTankLevel(i: Int): Int {
        return this.gasTank.getFluidAmount() * i / this.gasTank.getCapacity()
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf()
    }

    companion object {
        const val PROCESS_TIME_REQUIRED_BASE: Int = 20
    }
}
