package micdoodle8.mods.galacticraft.planets.mars.tile

import foundation.block.getMetaFromState

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.fluid.NetworkHelper
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

open class TileEntityMethaneSynthesizer constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IDisableableMachine, IFluidHandlerWrapper {
    private val tankCapacity = 4000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTank2: FluidTank = FluidTank(this.tankCapacity / 2)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var liquidTank: FluidTank = FluidTank(this.tankCapacity / 2)

    @JvmField
    var processTimeRequired: Int = 3

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = -8
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(5, ItemStack.EMPTY)
    private var hasCO2 = -1
    private var noCoal = true
    private var coalPartial = 0

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 45).toFloat())
        this.setTierGC(2)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true



        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }

        return super.getCapability<T?>(capability, facing)
    }

    override fun update() {
        super.update()

        if (this.hasCO2 == -1) {
            this.hasCO2 = this.getAirProducts()
        }

        if (!this.world.isRemote) {
            //If somehow it has CO2 in a CO2-free dimension, flush it out
            if (this.hasCO2 == 0 && this.gasTank2.getFluidAmount() > 0) {
                this.gasTank2.drain(this.gasTank2.getFluidAmount(), true)
            }

            //First, see if any gas needs to be put into the hydogen storage

            //TODO add support for hydrogen atmospheres

            //Now check the CO2 storage
            val inputCanister: ItemStack = this.stacks.get(2)
            if (!inputCanister.isEmpty()) {
                if (inputCanister.getItem() is ItemAtmosphericValve && this.hasCO2 > 0) {
                    //CO2 -> CO2 tank
                    if (this.gasTank2.getFluidAmount() < this.gasTank2.getCapacity()) {
                        val stateAbove: IBlockState = this.world.getBlockState(this.getPos().up())
                        val blockAbove: Block = stateAbove.getBlock()
                        if (blockAbove.getMaterial(stateAbove) === Material.AIR && blockAbove !== GCBlocks.breatheableAir && blockAbove !== GCBlocks.brightBreatheableAir) {
                            if (!OxygenUtil.inOxygenBubble(
                                    this.world,
                                    this.getPos().getX() + 0.5,
                                    this.getPos().getY() + 1.0,
                                    this.getPos().getZ() + 0.5
                                )
                            ) {
                                val gcAtmosphere: FluidStack? = FluidRegistry.getFluidStack("carbondioxide", 4)
                                this.gasTank2.fill(gcAtmosphere, true)
                            }
                        }
                    }
                }
            }


            //Now see if any methane from the methane tank needs to be put into the output slot
            checkFluidTankTransfer(4, this.liquidTank)

            if (this.hasEnoughEnergyToRun && this.canProcess()) {
                //50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                if (this.tierGC == 2) {
                    this.processTimeRequired = max(1, 4 - this.poweredByTierGC)
                }

                if (this.processTicks <= 0) {
                    this.processTicks = this.processTimeRequired
                } else {
                    if (--this.processTicks <= 0) {
                        this.doLiquefaction()
                        this.processTicks = if (this.canProcess()) this.processTimeRequired else 0
                    }
                }
            } else {
                if (this.processTicks > 0) {
                    this.processTicks = 0
                } else if (--this.processTicks <= -8) {
                    this.processTicks = -8
                }
            }

            this.produceOutput(this.getHydrogenInputDirection().getOpposite())
        }
    }

    private fun produceOutput() {
        // TODO Auto-generated method stub
    }

    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        if (FluidUtil.isValidContainer(this.stacks.get(slot))) {
            val liquid: FluidStack? = tank.getFluid()

            if (liquid != null) {
                FluidUtil.tryFillContainer(tank, liquid, this.stacks, slot, (AsteroidsItems.methaneCanister)!!)
            }
        } else if (!this.stacks.get(slot).isEmpty() && this.stacks.get(slot).getItem() is ItemAtmosphericValve) {
            tank.drain(4, true)
        }
    }

    open fun getScaledGasLevel(i: Int): Int {
        return if (this.gasTank.getFluid() != null) this.gasTank.getFluid()!!.amount * i / this.gasTank.getCapacity() else 0
    }

    open fun getScaledGasLevel2(i: Int): Int {
        return if (this.gasTank2.getFluid() != null) this.gasTank2.getFluid()!!.amount * i / this.gasTank2.getCapacity() else 0
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return if (this.liquidTank.getFluid() != null) this.liquidTank.getFluid()!!.amount * i / this.liquidTank.getCapacity() else 0
    }

    open fun canProcess(): Boolean {
        if (this.gasTank.getFluid() == null || this.gasTank.getFluid()!!.amount <= 0 || this.getDisabled(0)) {
            return false
        }

        this.noCoal = this.stacks.get(3).isEmpty() || this.stacks.get(3).getItem() !== MarsItems.carbonFragments

        if (this.noCoal && this.coalPartial == 0 && (this.gasTank2.getFluid() == null || this.gasTank2.getFluidAmount() <= 0)) {
            return false
        }

        return this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity()
    }

    open fun getAirProducts(): Int {
        val WP: WorldProvider? = this.world.provider
        if (WP is WorldProviderSpace) {
            val atmos: ArrayList<EnumAtmosphericGas> = (WP.getCelestialBody())!!.atmosphere.composition
            if (atmos.size > 0) {
                if (atmos.get(0) == EnumAtmosphericGas.CO2) {
                    return 1
                }
            }
            if (atmos.size > 1) {
                if (atmos.get(1) == EnumAtmosphericGas.CO2) {
                    return 1
                }
            }
            if (atmos.size > 2) {
                if (atmos.get(2) == EnumAtmosphericGas.CO2) {
                    return 1
                }
            }

            return 0
        }

        return 0
    }

    open fun doLiquefaction() {
        if (this.noCoal && this.coalPartial == 0) {
            if (this.gasTank2.getFluid() == null || this.gasTank2.drain(1, true)!!.amount < 1) {
                return
            }
        } else {
            if (this.coalPartial == 0) {
                this.removeItem(3, 1)
            }
            this.coalPartial++
            if (this.coalPartial == 40) {
                this.coalPartial = 0
            }
        }
        this.gasTank.drain(this.placeIntoFluidTanks(2) * 8, true)
    }

    private fun placeIntoFluidTanks(amountToDrain: Int): Int {
        var amountToDrain: Int = amountToDrain
        val fuelSpace: Int = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount()

        if (fuelSpace > 0) {
            if (amountToDrain > fuelSpace) {
                amountToDrain = fuelSpace
            }
            this.liquidTank.fill(FluidRegistry.getFluidStack("methane", amountToDrain), true)
        } else {
            amountToDrain = 0
        }

        return amountToDrain
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.processTicks = nbt.getIntOr("smeltingTicks", 0)
        this.stacks = this.readStandardItemsFromNBT(nbt)

        if (nbt.containsTag("gasTank")) {
            this.gasTank.readFromNBT(nbt.getCompoundOrEmpty("gasTank"))
        }

        if (nbt.containsTag("gasTank2")) {
            this.gasTank2.readFromNBT(nbt.getCompoundOrEmpty("gasTank2"))
        }

        if (nbt.containsTag("liquidTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundOrEmpty("liquidTank"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        if (this.gasTank.getFluid() != null) {
            nbt.put("gasTank", this.gasTank.writeToNBT(NBTTagCompound()))
        }

        if (this.gasTank2.getFluid() != null) {
            nbt.put("gasTank2", this.gasTank2.writeToNBT(NBTTagCompound()))
        }

        if (this.liquidTank.getFluid() != null) {
            nbt.put("liquidTank", this.liquidTank.writeToNBT(NBTTagCompound()))
        }

        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.mars_machine.5.name")
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 2, 3, 4)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                3 -> return itemstack.getItem() === MarsItems.carbonFragments
                4 -> return FluidUtil.isPartialContainer(itemstack, AsteroidsItems.methaneCanister)
                else -> return false
            }
        }
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemEmpty(itemstack) || !this.shouldPullEnergy()
                4 -> return FluidUtil.isFullContainer(itemstack)
                else -> return false
            }
        }
        return false
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return false
            2 -> return itemstack.getItem() is ItemAtmosphericValve
            3 -> return itemstack.getItem() === MarsItems.carbonFragments
            4 -> return FluidUtil.isValidContainer(itemstack)
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getPacketRange(): Double {
        return 320.0
    }

    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.DOWN
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getHydrogenInputDirection().getOpposite()) {
            return this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0
        }

        return false
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == this.getHydrogenInputDirection().getOpposite()) {
            if (resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
                return this.liquidTank.drain(resource.amount, doDrain)
            }
        }

        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getHydrogenInputDirection().getOpposite()) {
            return this.liquidTank.drain(maxDrain, doDrain)
        }

        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getHydrogenInputDirection()) {
            return fluid == null || "hydrogen" == fluid.getName()
        }

        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (resource != null && this.canFill(from, resource.getFluid())) {
            if (this.gasTank.getFluidAmount() < this.gasTank.getCapacity()) {
                used = this.gasTank.fill(resource, doFill)
            }
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        var tankInfo: Array<FluidTankInfo?> = arrayOf<FluidTankInfo?>()

        if (from == this.getHydrogenInputDirection()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.gasTank)) as Array<FluidTankInfo?>?))!!
        } else if (from == this.getHydrogenInputDirection().getOpposite()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.liquidTank)) as Array<FluidTankInfo?>?))!!
        }

        return tankInfo
    }

    override fun getBlockMetadata(): Int {
        return this.getBlockType().getMetaFromState(this.world.getBlockState(getPos()))
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }

        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }

        //Hydrogen pipe
        if (type == NetworkType.FLUID) {
            return direction == this.getHydrogenInputDirection() || direction == this.getHydrogenInputDirection()
                .getOpposite()
        }

        return false
    }

    open fun getHydrogenRequest(direction: EnumFacing?): Int {
        return this.receiveHydrogen(direction, 1000000f, false)
    }

    open fun shouldPullHydrogen(): Boolean {
        return this.gasTank.getFluidAmount() < this.gasTank.getCapacity()
    }

    open fun receiveHydrogen(from: EnumFacing?, receive: Float, doReceive: Boolean): Int {
        if (from == this.getHydrogenInputDirection() && this.shouldPullHydrogen()) {
            val fluidToFill: FluidStack? = FluidRegistry.getFluidStack("hydrogen", (receive).toInt())
            return this.gasTank.fill(fluidToFill, doReceive)
        }

        return 0
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineMarsT2) {
            return state.getValue(BlockMachineMarsT2.FACING)
        }
        return EnumFacing.NORTH
    }

    open fun getHydrogenInputDirection(): EnumFacing {
        return this.getFront().rotateY()
    }

    private fun produceOutput(outputDirection: EnumFacing): Boolean {
        val provide: Int = this.getMethaneProvide()

        if (provide > 0) {
            val outputTile: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, outputDirection)
            val outputNetwork: FluidNetwork? = NetworkHelper.getFluidNetworkFromTile(outputTile, outputDirection)

            if (outputNetwork != null) {
                val gasRequested: Int = outputNetwork.getRequest()

                if (gasRequested > 0) {
                    val usedGas: Int = outputNetwork.emitToBuffer(
                        FluidStack(
                            AsteroidsModule.fluidMethaneGas,
                            min(gasRequested, provide)
                        ), true
                    )
                    this.liquidTank.drain(usedGas, true)
                    return true
                }
            }
        }

        return false
    }

    private fun getMethaneProvide(): Int {
        return min(TileEntityOxygenStorageModule.OUTPUT_PER_TICK, this.liquidTank.getFluidAmount())
    }
}
