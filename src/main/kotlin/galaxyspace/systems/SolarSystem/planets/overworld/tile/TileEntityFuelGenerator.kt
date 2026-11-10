package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSBlocks
import java.util.EnumSet
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityFuelGenerator
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockFuelGenerator
import galaxyspace.core.configs.GSConfigEnergy
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.core.GCItems
import galaxyspace.core.prefab.tile.TileEntityInventorySource
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

open class TileEntityFuelGenerator constructor() : TileEntityInventorySource("tile.fuel_generator.name"),
    IFluidHandlerWrapper, ISidedInventory, IConnector {
    private val tankCapacity = 4000

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

    init {
        this.storage.setMaxExtract(MAX_GENERATE_GJ_PER_TICK - MIN_GENERATE_GJ_PER_TICK)
        this.inventory = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return this.fuelTank.getFluidAmount() * i / this.fuelTank.getCapacity()
    }

    override fun update() {
        if (this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK > 0) {
            this.receiveEnergyGC(null, (this.heatGJperTick - MIN_GENERATE_GJ_PER_TICK), false)
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

            if (world.rand.nextInt(20) == 0) if (this.world.getBlockState(
                    this.getPos().down()
                ) === GSBlocks.IO_BLOCKS.getDefaultState()
                    .withProperty(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_LAVA_GEYSER)
            ) if (this.world.getBlockState(this.getPos().down(2)) === Blocks.LAVA.getDefaultState()) this.fuelTank.fill(
                FluidStack(FluidRegistry.LAVA, 1),
                true
            )

            this.produce()
            this.smeltItem()
        }
    }


    open fun smeltItem() {
        if (this.canProcess()) {
            val lavaAmount: Int = this.fuelTank.getFluidAmount()
            val fuelSpace: Int = (this.fuelTank.getCapacity() - this.fuelTank.getFluidAmount())

            for (fluid: Fuel in fuel) {
                if (this.fuelTank.getFluid()!!.getFluid() === fluid.getFluid()) {
                    if (this.ticks % fluid.getDuration() == 0) {
                        this.fuelTank.drain(1, true)
                        mod = fluid.getModificator()
                    }
                    break
                }
            }


            this.heatGJperTick = min(
                this.heatGJperTick + max(this.heatGJperTick * 0.005f, BASE_ACCELERATION),
                MAX_GENERATE_GJ_PER_TICK * mod - 1
            )
            this.storage.setMaxExtract(MAX_GENERATE_GJ_PER_TICK * mod - MIN_GENERATE_GJ_PER_TICK)
        }
    }

    open fun canProcess(): Boolean {
        if (this.fuelTank.getFluid() != null && this.fuelTank.getFluid()!!.getFluid().isGaseous()) return false

        if (this.fuelTank.getFluidAmount() <= 0) {
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
        this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime")
        this.heatGJperTick = par1NBTTagCompound.getInteger("generateRateInt").toFloat()

        this.inventory = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())


        if (par1NBTTagCompound.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(par1NBTTagCompound.getCompoundTag("fuelTank"))
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.setInteger("itemCookTime", this.itemCookTime)
        par1NBTTagCompound.setFloat("generateRate", this.heatGJperTick)
        if (this.fuelTank.getFluid() != null) {
            par1NBTTagCompound.setTag("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }

        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())
        return par1NBTTagCompound
    }

    override fun getSizeInventory(): Int {
        return this.getInventory().size
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        return this.getInventory().get(par1)
    }

    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ItemStackHelper.getAndSplit(this.getInventory(), index, count)

        if (!itemstack.isEmpty()) {
            this.markDirty()
        }

        return itemstack
    }

    override fun removeStackFromSlot(index: Int): ItemStack {
        val oldstack: ItemStack = ItemStackHelper.getAndRemove(this.getInventory(), index)
        if (!oldstack.isEmpty()) {
            this.markDirty()
        }
        return oldstack
    }

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        this.getInventory().set(index, stack)

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit())
        }

        this.markDirty()
    }


    override fun getInventoryStackLimit(): Int {
        return 64
    }


    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        return itemstack.getItem() === GCItems.bucketFuel || itemstack.getItem() === GCItems.fuelCanister || itemstack.getItem() === Items.LAVA_BUCKET
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, direction: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, direction: EnumFacing?): Boolean {
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
        if (state.getBlock() is BlockFuelGenerator) {
            return state.getValue(BlockFuelGenerator.FACING)
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
        private val modificator: Float
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
    }

    companion object {
        @JvmField
        val MAX_GENERATE_GJ_PER_TICK: Float = 81 * GSConfigEnergy.coefficientFuelGenerator

        const val MIN_GENERATE_GJ_PER_TICK: Float = 1f

        private const val BASE_ACCELERATION = 0.3f

        private val fuel: MutableList<Fuel> = ArrayList<Fuel>()

        @JvmStatic
        fun registerNewFuel(fluid: Fluid, burn_time: Int, mod_energy: Float) {
            if (fluid.isGaseous()) return

            fuel.add(Fuel(fluid, burn_time, mod_energy))
        }

        @JvmStatic
        fun removeFuel(fluid: Fluid?) {
            for (fluids: Fuel in fuel) {
                if (fluids.fuel == fluid) fuel.remove(fluids)
            }
        }
    }
}
