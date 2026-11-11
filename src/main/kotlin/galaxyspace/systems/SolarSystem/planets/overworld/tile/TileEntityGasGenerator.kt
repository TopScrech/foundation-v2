package galaxyspace.systems.SolarSystem.planets.overworld.tile

import net.minecraft.world.ContainerHelper
import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import java.util.EnumSet
import galaxyspace.core.configs.GSConfigEnergy
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasGenerator
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

open class TileEntityGasGenerator constructor() : TileEntityInventorySource("tile.gas_generator.name"),
    IFluidHandlerWrapper, ISidedInventory, IConnector {
    private val tankCapacity = 5000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fuelTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    var prevGenerateWatts: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var heatGJperTick: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var itemCookTime: Int = 0

    private var mod = 1.0f
    private var current_fuel: Fuel? = null

    init {
        this.storage.setMaxExtract(GENERATE_GJ_PER_TICK - 1)
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return this.fuelTank.getFluidAmount() * i / this.fuelTank.getCapacity()
    }

    override fun update() {
        if (this.heatGJperTick > 0) {
            this.receiveEnergyGC(null, (this.heatGJperTick), false)
        }

        super.update()

        if (!this.world.isRemote) {
            val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(0))

            for (fuel: Fuel in fuel) {
                if (FluidUtil.isFluidStrict(liquid, fuel.getFluid().getName())) {
                    if (fuelTank.getFluid() == null || fuelTank.getFluid() != null && fuelTank.getFluid()!!
                            .getFluid() == liquid!!.getFluid()
                    ) FluidUtil.loadFromContainer(fuelTank, fuel.getFluid(), this.getInventory(), 0, liquid!!.amount)
                }
            }


            for (fuel: Fuel in fuel) {
                if (fuelTank.getFluid() != null && fuelTank.getFluid()!!.getFluid() == fuel.getFluid()) {
                    current_fuel = fuel
                    break
                }
            }

            this.produce()
            this.smeltItem()
        }
    }


    open fun smeltItem() {
        if (this.canProcess()) {
            //final int lavaAmount = this.fuelTank.getFluidAmount();
            //final int fuelSpace = (this.fuelTank.getCapacity() - this.fuelTank.getFluidAmount());
            /*
            for(Fuel fluid : fuel)
            {
            	if(this.fuelTank.getFluid().getFluid() == fluid.getFluid())
            	{
            		if(this.ticks % fluid.getDuration() == 0) {
            			this.fuelTank.drain(fluid.getDrainAmount(), true);
            			mod = fluid.getModificator(); 
            			  
            		}
            		break;            		
            	}
            }*/

            if (this.fuelTank.getFluid()!!.getFluid() === current_fuel!!.getFluid()) {
                if (this.ticks % current_fuel!!.getDuration() == 0) {
                    this.fuelTank.drain(current_fuel!!.getDrainAmount(), true)
                    mod = current_fuel!!.getModificator()
                }
            }

            this.heatGJperTick = GENERATE_GJ_PER_TICK * mod
            this.storage.setMaxExtract(GENERATE_GJ_PER_TICK * mod + 1)
        }
    }

    open fun canProcess(): Boolean {
        if (current_fuel == null) return false

        if (this.fuelTank.getFluid() != null && !this.fuelTank.getFluid()!!.getFluid().isGaseous()) return false

        if (this.fuelTank.getFluidAmount() <= current_fuel!!.getDrainAmount()) {
            this.heatGJperTick = 0f
            return false
        }

        if (this.storage.getEnergyStoredGC() >= this.storage.getCapacityGC()) return false

        return true
    }

    /**
     * Reads a tile entity from NBT.
     */
    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)
        this.itemCookTime = par1NBTTagCompound.getIntOr("itemCookTime", 0)
        this.heatGJperTick = par1NBTTagCompound.getIntOr("generateRateInt", 0).toFloat()

        this.inventory = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())


        if (par1NBTTagCompound.containsTag("fuelTank")) {
            this.fuelTank.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("fuelTank"))
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("itemCookTime", this.itemCookTime)
        par1NBTTagCompound.putFloat("generateRate", this.heatGJperTick)
        if (this.fuelTank.getFluid() != null) {
            par1NBTTagCompound.put("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }

        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())
        return par1NBTTagCompound
    }

    override fun getContainerSize(): Int {
        return this.getInventory().size
    }

    override fun getItem(par1: Int): ItemStack {
        return this.getInventory().get(par1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.getInventory(), index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.getInventory(), index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.getInventory().set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }


    override fun getMaxStackSize(): Int {
        return 64
    }


    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, direction: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, direction: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun receiveElectricity(from: EnumFacing?, energy: Float, tier: Int, doReceive: Boolean): Float {
        return 0f
    }

    override fun getElectricalInputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.of<EnumFacing?>(this.getElectricOutputDirection())
    }

    open fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockGasGenerator) {
            return state.getValue(BlockGasGenerator.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricOutputDirection(): EnumFacing {
        return getFront().rotateYCCW()
    }

    private fun getPipe(): EnumFacing {
        return getFront().rotateY()
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricOutputDirection()
        }
        if (type == NetworkType.FLUID) {
            return direction == this.getPipe()
        }
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (this.getPipe() == from && resource != null) {
            val liquidName: String? = FluidRegistry.getFluidName(resource)

            if (liquidName != null) {
                for (fluid: Fuel in fuel) {
                    if (liquidName.startsWith(fluid.getFluid().getName())) {
                        used = this.fuelTank.fill(resource, doFill)
                        break
                    }
                }
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

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getPipe()) {
            return this.fuelTank.getFluid() == null || this.fuelTank.getFluidAmount() < this.fuelTank.getCapacity()
        }

        return false
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.fuelTank)) as Array<FluidTankInfo?>?))!!
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

    ////////////////////////////////////////////////////////////////////////////////////////
    private open class Fuel constructor(
        @field:kotlin.jvm.JvmField  val fuel: Fluid,
        private val duration: Int,
        private val modificator: Float,
        private val drain_amount: Int
    ) {
        open fun getFluid(): Fluid {
            return this.fuel
        }

        open fun getDuration(): Int {
            return this.duration
        }

        open fun getModificator(): Float {
            return this.modificator
        }

        open fun getDrainAmount(): Int {
            return this.drain_amount
        }
    }

    companion object {
        @JvmField
        val GENERATE_GJ_PER_TICK: Float = 180 * GSConfigEnergy.coefficientGasGenerator

        private const val BASE_ACCELERATION = 0.3f

        private val fuel: MutableList<Fuel> = ArrayList<Fuel>()

        @JvmStatic
        fun registerNewFuel(fluid: Fluid, burn_time: Int, mod_energy: Float, drain_amount: Int) {
            if (!fluid.isGaseous()) return

            fuel.add(Fuel(fluid, burn_time, mod_energy, drain_amount))
        }

        @JvmStatic
        fun removeFuel(fluid: Fluid?) {
            for (fluids: Fuel in fuel) {
                if (fluids.fuel == fluid) fuel.remove(fluids)
            }
        }
    }
}
