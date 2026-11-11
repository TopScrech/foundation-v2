package micdoodle8.mods.galacticraft.planets.mars.tile

import foundation.item.legacyItemValue

import foundation.block.getMetaFromState

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

open class TileEntityGasLiquefier constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IDisableableMachine, IFluidHandlerWrapper, IOxygenReceiver {
    private val tankCapacity = 2000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTank: FluidTank = FluidTank(this.tankCapacity * 2)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var liquidTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var liquidTank2: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    var processTimeRequired: Int = 3

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = -10
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(4, ItemStack.EMPTY)
    private var airProducts = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTankType: Int = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fluidTankType: Int = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var fluidTank2Type: Int = -1

    enum class TankGases
        (@JvmField var index: Int, @JvmField var gas: String, @JvmField var liquid: String) {
        METHANE(0, "methane", if (ConfigManagerCore.useOldFuelFluidID) "fuelgc" else "fuel"),
        OXYGEN(1, "oxygen", "liquidoxygen"),
        NITROGEN(2, "nitrogen", "liquidnitrogen"),
        ARGON(3, "argon", "liquidargon"),
        AIR(4, "atmosphericgases", "xxyyzz")
    }


    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 60).toFloat())
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

    override fun invalidate() {
        super.invalidate()
    }

    override fun update() {
        super.update()

        if (this.airProducts == -1) {
            this.airProducts = this.getAirProducts()
        }

        if (!this.world.isRemote) {
            val currentgas: FluidStack? = this.gasTank.getFluid()
            if (currentgas == null || currentgas.amount <= 0) {
                this.gasTankType = -1
            } else {
                this.gasTankType = this.getIdFromName(currentgas.getFluid().getName())
            }

            //If somehow it has air in an airless dimension, flush it out
            if (this.airProducts == 0 && this.gasTankType == TankGases.AIR.index) {
                this.gasTank.drain(this.gasTank.getFluidAmount(), true)
            }

            var currentLiquid: FluidStack? = this.liquidTank.getFluid()
            if (currentLiquid == null || currentLiquid.amount == 0) {
                this.fluidTankType = -1
            } else {
                this.fluidTankType = this.getProductIdFromName(currentLiquid.getFluid().getName())
            }

            currentLiquid = this.liquidTank2.getFluid()
            if (currentLiquid == null || currentLiquid.amount == 0) {
                this.fluidTank2Type = -1
            } else {
                this.fluidTank2Type = this.getProductIdFromName(currentLiquid.getFluid().getName())
            }

            //First, see if any gas needs to be put into the gas storage
            val inputCanister: ItemStack = stacks.get(1)
            if (!inputCanister.isEmpty()) {
                if (inputCanister.getItem() is ItemAtmosphericValve && this.airProducts > 0) {
                    //Air -> Air tank
                    if (this.gasTankType == -1 || (this.gasTankType == TankGases.AIR.index && this.gasTank.getFluid()!!.amount < this.gasTank.getCapacity())) {
                        val stateAbove: IBlockState = this.world.getBlockState(getPos().up())
                        if (stateAbove.getMaterial() === Material.AIR && stateAbove.getBlock() !== GCBlocks.breatheableAir && stateAbove.getBlock() !== GCBlocks.brightBreatheableAir) {
                            val gcAtmosphere: FluidStack? = FluidRegistry.getFluidStack(TankGases.AIR.gas, 4)
                            this.gasTank.fill(gcAtmosphere, true)
                            this.gasTankType = TankGases.AIR.index
                        }
                    }
                } else if (inputCanister.getItem() is ItemCanisterGeneric) {
                    val amount: Int = ItemCanisterGeneric.EMPTY - inputCanister.legacyItemValue
                    if (amount > 0) {
                        val canisterType: Item = inputCanister.getItem()
                        var canisterGas: FluidStack? = null
                        var factor: Int = 1
                        if (this.gasTankType <= 0 && canisterType === AsteroidsItems.methaneCanister) {
                            this.gasTankType = TankGases.METHANE.index
                            canisterGas = FluidRegistry.getFluidStack(TankGases.METHANE.gas, amount)
                        }
                        if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && canisterType === AsteroidsItems.canisterLOX) {
                            this.gasTankType = TankGases.OXYGEN.index
                            canisterGas = FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, amount * 2)
                            factor = 2
                        }
                        if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && canisterType === AsteroidsItems.canisterLN2) {
                            this.gasTankType = TankGases.NITROGEN.index
                            canisterGas = FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, amount * 2)
                            factor = 2
                        }

                        if (canisterGas != null) {
                            val used: Int = this.gasTank.fill(canisterGas, true) / factor
                            if (used == amount) {
                                stacks.set(1, ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY))
                            } else {
                                stacks.set(1, ItemStack(canisterType, 1, ItemCanisterGeneric.EMPTY - amount + used))
                            }
                        }
                    }
                } else {
                    val liquid: FluidStack? = FluidUtil.getFluidContained(inputCanister)
                    if (liquid != null && liquid.amount > 0) {
                        val inputName: String = FluidRegistry.getFluidName(liquid)
                        //Methane -> Methane tank
                        if (this.gasTankType <= 0 && inputName.contains("methane")) {
                            if (currentgas == null || currentgas.amount + liquid.amount <= this.gasTank.getCapacity()) {
                                val gcMethane: FluidStack? =
                                    FluidRegistry.getFluidStack(TankGases.METHANE.gas, liquid.amount)
                                this.gasTank.fill(gcMethane, true)
                                this.gasTankType = 0
                                val stack: ItemStack? =
                                    micdoodle8.mods.galacticraft.core.util.FluidUtil.getUsedContainer(inputCanister)
                                stacks.set(1, if (stack == null) ItemStack.EMPTY else stack)
                            }
                        } else  //Oxygen -> Oxygen tank
                            if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && inputName.contains(
                                    "oxygen"
                                )
                            ) {
                                val tankedAmount: Int = liquid.amount * (if (inputName.contains("liquid")) 2 else 1)
                                if (currentgas == null || currentgas.amount + tankedAmount <= this.gasTank.getCapacity()) {
                                    val gcgas: FluidStack? =
                                        FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, tankedAmount)
                                    this.gasTank.fill(gcgas, true)
                                    this.gasTankType = TankGases.OXYGEN.index
                                    val stack: ItemStack? =
                                        micdoodle8.mods.galacticraft.core.util.FluidUtil.getUsedContainer(inputCanister)
                                    stacks.set(1, if (stack == null) ItemStack.EMPTY else stack)
                                }
                            } else  //Nitrogen -> Nitrogen tank
                                if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && inputName.contains(
                                        "nitrogen"
                                    )
                                ) {
                                    val tankedAmount: Int = liquid.amount * (if (inputName.contains("liquid")) 2 else 1)
                                    if (currentgas == null || currentgas.amount + tankedAmount <= this.gasTank.getCapacity()) {
                                        val gcgas: FluidStack? =
                                            FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, tankedAmount)
                                        this.gasTank.fill(gcgas, true)
                                        this.gasTankType = TankGases.NITROGEN.index
                                        val stack: ItemStack? =
                                            micdoodle8.mods.galacticraft.core.util.FluidUtil.getUsedContainer(
                                                inputCanister
                                            )
                                        stacks.set(1, if (stack == null) ItemStack.EMPTY else stack)
                                    }
                                }
                    }
                }
            }

            //Now see if any liquids from the output tanks need to be put into the output slot
            checkFluidTankTransfer(2, this.liquidTank)
            checkFluidTankTransfer(3, this.liquidTank2)

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
                } else if (--this.processTicks <= -10) {
                    this.processTicks = -10
                }
            }
        }
    }

    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        if (micdoodle8.mods.galacticraft.core.util.FluidUtil.isValidContainer(this.stacks.get(slot))) {
            val liquid: FluidStack? = tank.getFluid()

            if (liquid != null && liquid.amount > 0) {
                val liquidname: String = liquid.getFluid().getName()
                if (liquidname.startsWith("fuel")) {
                    micdoodle8.mods.galacticraft.core.util.FluidUtil.tryFillContainerFuel(tank, this.stacks, slot)
                } else if (liquidname == TankGases.OXYGEN.liquid) {
                    micdoodle8.mods.galacticraft.core.util.FluidUtil.tryFillContainer(
                        tank,
                        liquid,
                        this.stacks,
                        slot,
                        (AsteroidsItems.canisterLOX)!!
                    )
                } else if (liquidname == TankGases.NITROGEN.liquid) {
                    micdoodle8.mods.galacticraft.core.util.FluidUtil.tryFillContainer(
                        tank,
                        liquid,
                        this.stacks,
                        slot,
                        (AsteroidsItems.canisterLN2)!!
                    )
                }
            }
        } else if (!this.stacks.get(slot).isEmpty() && this.stacks.get(slot).getItem() is ItemAtmosphericValve) {
            tank.drain(4, true)
        }
    }

    open fun getIdFromName(gasname: String?): Int {
        for (type: TankGases in TankGases.entries) {
            if (type.gas == gasname) {
                return type.index
            }
        }

        return -1
    }

    open fun getProductIdFromName(gasname: String?): Int {
        for (type: TankGases in TankGases.entries) {
            if (type.liquid == gasname) {
                return type.index
            }
        }

        return -1
    }

    open fun getScaledGasLevel(i: Int): Int {
        return if (this.gasTank.getFluid() != null) this.gasTank.getFluid()!!.amount * i / this.gasTank.getCapacity() else 0
    }

    open fun getScaledFuelLevel(i: Int): Int {
        return if (this.liquidTank.getFluid() != null) this.liquidTank.getFluid()!!.amount * i / this.liquidTank.getCapacity() else 0
    }

    open fun getScaledFuelLevel2(i: Int): Int {
        return if (this.liquidTank2.getFluid() != null) this.liquidTank2.getFluid()!!.amount * i / this.liquidTank2.getCapacity() else 0
    }

    open fun canProcess(): Boolean {
        if (this.gasTank.getFluid() == null || this.gasTank.getFluid()!!.amount <= 0 || this.getDisabled(0)) {
            return false
        }

        if (this.fluidTankType == -1 || this.fluidTank2Type == -1) {
            return true
        }

        val tank1HasSpace: Boolean = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity()
        val tank2HasSpace: Boolean = this.liquidTank2.getFluidAmount() < this.liquidTank2.getCapacity()

        if (this.gasTankType == TankGases.AIR.index) {
            var airProducts: Int = this.airProducts
            do {
                val thisProduct: Int = (airProducts and 15) - 1
                if ((thisProduct == this.fluidTankType && tank1HasSpace) || (thisProduct == this.fluidTank2Type && tank2HasSpace)) {
                    return true
                }
                airProducts = airProducts shr 4
            } while (airProducts > 0)
            return false
        }

        if ((this.gasTankType == this.fluidTankType && tank1HasSpace) || (this.gasTankType == this.fluidTank2Type && tank2HasSpace)) {
            return true
        }

        return false
    }

    open fun getAirProducts(): Int {
        val WP: WorldProvider? = this.world.provider
        if (WP is WorldProviderSpace) {
            var result: Int = 0
            val atmos: ArrayList<EnumAtmosphericGas> = (WP.getCelestialBody())!!.atmosphere.composition
            if (atmos.size > 0) {
                result = this.getIdFromName(atmos.get(0)!!.name.lowercase(Locale.getDefault())) + 1
            }
            if (atmos.size > 1) {
                result += 16 * (this.getIdFromName(atmos.get(1)!!.name.lowercase(Locale.getDefault())) + 1)
            }
            if (atmos.size > 2) {
                result += 256 * (this.getIdFromName(atmos.get(2)!!.name.lowercase(Locale.getDefault())) + 1)
            }

            return result
        }

        return 35
    }

    open fun doLiquefaction() {
        //Can't be called if the gasTank fluid is null
        val gasAmount: Int = this.gasTank.getFluid()!!.amount
        if (gasAmount == 0) {
            return
        }

        if (this.gasTankType == TankGases.AIR.index) {
            var airProducts: Int = this.airProducts
            var amountToDrain: Int = min(gasAmount / 2, if (airProducts > 15) 2 else 3)
            if (amountToDrain == 0) {
                amountToDrain = 1
            }

            do {
                val thisProduct: Int = (airProducts and 15) - 1
                //-1 indicates a gas which can't be liquefied (e.g. Carbon Dioxide)
                if (thisProduct >= 0) {
                    this.gasTank.drain(this.placeIntoFluidTanks(thisProduct, amountToDrain) * 2, true)
                }
                airProducts = airProducts shr 4
                amountToDrain = amountToDrain shr 1
                if (amountToDrain == 0) {
                    amountToDrain = 1
                }
            } while (airProducts > 0)
        } else {
            if (gasAmount == 1) {
                this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, 1), true)
            } else {
                this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, min(gasAmount / 2, 3)) * 2, true)
            }
        }
    }

    private fun placeIntoFluidTanks(thisProduct: Int, amountToDrain: Int): Int {
        var amountToDrain: Int = amountToDrain
        val fuelSpace: Int = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount()
        val fuelSpace2: Int = this.liquidTank2.getCapacity() - this.liquidTank2.getFluidAmount()

        if ((thisProduct == this.fluidTank2Type || this.fluidTank2Type == -1) && fuelSpace2 > 0) {
            if (amountToDrain > fuelSpace2) {
                amountToDrain = fuelSpace2
            }
            this.liquidTank2.fill(
                FluidRegistry.getFluidStack(TankGases.entries[thisProduct].liquid, amountToDrain),
                true
            )
            this.fluidTank2Type = thisProduct
        } else if ((thisProduct == this.fluidTankType || this.fluidTankType == -1) && fuelSpace > 0) {
            if (amountToDrain > fuelSpace) {
                amountToDrain = fuelSpace
            }
            this.liquidTank.fill(
                FluidRegistry.getFluidStack(TankGases.entries[thisProduct].liquid, amountToDrain),
                true
            )
            this.fluidTankType = thisProduct
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

        if (nbt.containsTag("liquidTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundOrEmpty("liquidTank"))
        }
        if (nbt.containsTag("liquidTank2")) {
            this.liquidTank2.readFromNBT(nbt.getCompoundOrEmpty("liquidTank2"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        if (this.gasTank.getFluid() != null) {
            nbt.put("gasTank", this.gasTank.writeToNBT(NBTTagCompound()))
        }

        if (this.liquidTank.getFluid() != null) {
            nbt.put("liquidTank", this.liquidTank.writeToNBT(NBTTagCompound()))
        }
        if (this.liquidTank2.getFluid() != null) {
            nbt.put("liquidTank2", this.liquidTank2.writeToNBT(NBTTagCompound()))
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
        return net.minecraft.network.chat.Component.translatable("tile.mars_machine.4.name")
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (side == EnumFacing.DOWN) {
            return intArrayOf(0, 1, 2, 3)
        }

        if (side == EnumFacing.UP) {
            return intArrayOf(0)
        }

        return intArrayOf(1, 2, 3)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        if (this.canPlaceItem(slotID, itemstack)) {
            when (slotID) {
                0 -> return ItemElectricBase.isElectricItemCharged(itemstack)
                1 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isMethaneContainerAny(itemstack)
                2 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isEmptyContainerFor(
                    itemstack,
                    this.liquidTank.getFluid()
                )

                3 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isEmptyContainerFor(
                    itemstack,
                    this.liquidTank2.getFluid()
                )

                else -> return false
            }
        }
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItemEmpty(itemstack)
            1 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isEmptyContainer(itemstack)
            2 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isFullContainer(itemstack)
            3 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isFullContainer(itemstack)
            else -> return false
        }
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1, 2, 3 -> return micdoodle8.mods.galacticraft.core.util.FluidUtil.isValidContainer(itemstack)
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
        if (from == getGasInputDirection().getOpposite()) {
            return this.liquidTank2.getFluid() != null && this.liquidTank2.getFluidAmount() > 0
        }

        //2->5 3->4 4->2 5->3
        if (getGasInputDirection().rotateY() == from) {
            return this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0
        }

        return false
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == getGasInputDirection().getOpposite()) {
            if (resource != null && resource.isFluidEqual(this.liquidTank2.getFluid())) {
                return this.liquidTank2.drain(resource.amount, doDrain)
            }
        }

        //2->5 3->4 4->2 5->3
        if (getGasInputDirection().rotateY() == from) {
            if (resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
                return this.liquidTank.drain(resource.amount, doDrain)
            }
        }

        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getGasInputDirection().getOpposite()) {
            return this.liquidTank2.drain(maxDrain, doDrain)
        }

        //2->5 3->4 4->2 5->3
        if (getGasInputDirection().rotateY() == from) {
            return this.liquidTank.drain(maxDrain, doDrain)
        }

        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == this.getGasInputDirection()) {
            //Can fill with gases
            return fluid == null || this.getIdFromName(fluid.getName()) > -1
        }

        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (resource != null && this.canFill(from, resource.getFluid())) {
            val type: Int = this.getIdFromName(FluidRegistry.getFluidName(resource))

            if (this.gasTankType == -1 || (this.gasTankType == type && this.gasTank.getFluidAmount() < this.gasTank.getCapacity())) {
                used = this.gasTank.fill(resource, doFill)
            }
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        var tankInfo: Array<FluidTankInfo?> = arrayOf<FluidTankInfo?>()

        if (from == this.getGasInputDirection()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.gasTank)) as Array<FluidTankInfo?>?))!!
        } else if (from == this.getGasInputDirection().getOpposite()) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.liquidTank2)) as Array<FluidTankInfo?>?))!!
        }
        if (getGasInputDirection().rotateY() == from) {
            tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.liquidTank)) as Array<FluidTankInfo?>?))!!
        }

        return tankInfo
    }

    override fun getBlockMetadata(): Int {
        return getBlockType().getMetaFromState(this.world.getBlockState(getPos())) and 3
    }

    override fun shouldPullOxygen(): Boolean {
        return this.gasTankType == -1 || (this.gasTankType == 1 && this.gasTank.getFluidAmount() < this.gasTank.getCapacity())
    }

    override fun receiveOxygen(from: EnumFacing?, receive: Int, doReceive: Boolean): Int {
        if (from == this.getGasInputDirection() && this.shouldPullOxygen()) {
            val conversion: Float = 2f * Constants.LOX_GAS_RATIO //Special conversion ratio for breathable air
            val fluidToFill: FluidStack = FluidStack(AsteroidsModule.fluidOxygenGas, (receive * conversion).toInt())
            val used: Int = MathHelper.ceil(this.gasTank.fill(fluidToFill, doReceive) / conversion)
            return used
        }

        return 0
    }

    override fun provideOxygen(from: EnumFacing?, request: Int, doProvide: Boolean): Int {
        return 0
    }

    override fun getOxygenRequest(direction: EnumFacing?): Int {
        return this.receiveOxygen(direction, 1000000, false)
    }

    override fun getOxygenProvide(direction: EnumFacing?): Int {
        return 0
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }

        if (type == NetworkType.FLUID) {
            return direction == getGasInputDirection() || direction == this.getGasInputDirection()
                .getOpposite() || direction == this.getGasInputDirection().rotateY()
        }

        if (type == NetworkType.POWER) {
            return direction == EnumFacing.DOWN
        }

        return false
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineMarsT2) {
            return state.getValue(BlockMachineMarsT2.FACING)
        }
        return EnumFacing.NORTH
    }

    open fun getGasInputDirection(): EnumFacing {
        return this.getFront().rotateY()
    }
}
