package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.core.util.GSUtils
import galaxyspace.core.GSFluids
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidSeparator.TankLiquids
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockLiquidSeparator
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.state.IBlockState
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

open class TileEntityLiquidSeparator constructor() :
    TileEntityElectricInventory("tile.liquid_separator.name"), IFluidHandlerWrapper, ISidedInventory,
    IPacketReceiver {
    @JvmField
    var PROCESS_TIME_REQUIRED: Int = 50

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    private val tankCapacity = 3000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var baseTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank1: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank2: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var reverse: Boolean = false

    //TODO: NEED rewrite to class for add support custom recipe;
    enum class TankLiquids
        (@JvmField var index: Int, input: FluidStack, output1: FluidStack, output2: FluidStack) {
        /*ETANEMETHANE(0, "liquidethanemethane", "methane", 5, "ethane", 1),
    	WATER(1, "water", "hydrogen", 2, "liquidoxygen", 1),
    	HEAVYWATER(2, "heavywater", "liquidoxygen", 1, "deuterium", 1),
    	HYDROGEN2(3, "hydrogen2", "liquidoxygen", 1, "deuterium", 1),
    	//ROCKETFUEL2(4, "heliumhydrogen", "helium3", 2, "hydrogen", 8),*/
        ETANEMETHANE(
            0,
            FluidStack(GSFluids.LiquidEthaneMethane, 10),
            FluidStack(AsteroidsModule.fluidMethaneGas, 5),
            FluidRegistry.getFluidStack("ethane", 5)!!
        ),
        WATER(
            1,
            FluidStack(FluidRegistry.WATER, 50),
            FluidRegistry.getFluidStack("hydrogen", 100)!!,
            FluidRegistry.getFluidStack("oxygen", 50)!!
        ),
        ROCKETFUEL22(
            2,
            FluidStack(GSFluids.HeliumHydrogen, 10),
            FluidStack(GSFluids.Helium3, 2),
            FluidRegistry.getFluidStack("hydrogen", 8)!!
        );

        @JvmField
var input: FluidStack?

        @JvmField
var output1: FluidStack?

        @JvmField
var output2: FluidStack?

        init {
            this.input = input
            this.output1 = output1
            this.output2 = output2
        }


        fun getFluid(): FluidStack {
            return this.input!!
        }

        fun getOutFluid(): FluidStack? {
            return this.output1
        }

        fun getOutFluid2(): FluidStack? {
            return this.output2
        }
    }

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(4 + 4, ItemStack.EMPTY)
    }

    override fun update() {
        super.update()
        if (!this.world.isRemote) {
            if (!this.getReverse()) {
                GSUtils.checkFluidTankTransfer(this.getInventory(), 1, this.waterTank2)
                GSUtils.checkFluidTankTransfer(this.getInventory(), 3, this.waterTank1)

                if (!this.getInventory().get(2).isEmpty()) {
                    val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(2))
                    if (liquid != null) {
                        FluidUtil.loadFromContainer(
                            this.baseTank,
                            liquid.getFluid(),
                            this.getInventory(),
                            2,
                            liquid.amount
                        )
                    }
                }
            } else {
                GSUtils.checkFluidTankTransfer(this.getInventory(), 2, this.baseTank)

                if (!this.getInventory().get(1).isEmpty()) {
                    val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(1))
                    if (liquid != null) {
                        FluidUtil.loadFromContainer(
                            this.waterTank2,
                            liquid.getFluid(),
                            this.getInventory(),
                            1,
                            liquid.amount
                        )
                    }
                }

                if (!this.getInventory().get(3).isEmpty()) {
                    val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(3))
                    if (liquid != null) {
                        FluidUtil.loadFromContainer(
                            this.waterTank1,
                            liquid.getFluid(),
                            this.getInventory(),
                            3,
                            liquid.amount
                        )
                    }
                }
            }

            if (this.hasEnoughEnergyToRun) {
                if (this.canProcess()) {
                    //////////
                    var boost_speed: Int = 0
                    var energy_boost: Int = 0

                    for (i: Int in 0..3) {
                        if (this.getInventory().get(getInventory().size - i - 1)
                                .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 2))
                        ) boost_speed++
                        if (this.getInventory().get(getInventory().size - i - 1)
                                .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))
                        ) energy_boost++
                    }

                    this.processTicks += 2 + boost_speed
                    this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 + (40 * boost_speed) - (20 * energy_boost) else 75 + (35 * boost_speed) - (15 * energy_boost)).toFloat())

                    /////////////
                    if (this.processTicks >= processTimeRequired) {
                        this.processTicks = 0
                        this.smeltItem()
                    }
                } else {
                    this.processTicks = 0
                }
            } else {
                this.processTicks = 0
            }
        }
    }

    open fun canProcess(): Boolean {
        if (!this.getReverse()) {
            if (this.baseTank.getFluidAmount() <= 0) return false

            if (this.waterTank1.getFluidAmount() >= this.waterTank1.getCapacity() || this.waterTank2.getFluidAmount() >= this.waterTank2.getCapacity()) return false
        } else {
            if (this.waterTank1.getFluidAmount() <= 0 && this.waterTank2.getFluidAmount() <= 0) return false
            if (this.baseTank.getFluidAmount() >= this.baseTank.getCapacity()) return false
            if (this.waterTank1.getFluid() == null || this.waterTank2.getFluid() == null) return false

            if (FluidRegistry.getFluidName(this.waterTank1.getFluid()) == null || FluidRegistry.getFluidName(this.waterTank2.getFluid()) == null) return false
        }

        return true
    }

    open fun smeltItem() {
        if (this.canProcess()) {
            var i: Int = 0
            val ar: Array<TankLiquids> = TankLiquids.entries.toTypedArray()

            if (!this.getReverse()) {
                for (j: Int in ar.indices) {
                    if (ar[j]!!.getFluid() == this.baseTank.getFluid()) i = j
                }


                if (this.baseTank.getFluid() == ar[i]!!.getFluid()) {
                    if (ar[i]!!.getOutFluid() != null) {
                        this.waterTank1.fill(ar[i]!!.getOutFluid(), true)
                        if (ar[i]!!.getOutFluid2() != null) this.waterTank2.fill(ar[i]!!.getOutFluid2(), true)
                        this.baseTank.drain(ar[i]!!.getFluid().amount, true)
                    }
                }
            } else {
                for (j: Int in ar.indices) {
                    if (ar[j]!!.getOutFluid() == this.waterTank1.getFluid() && ar[j]!!.getOutFluid2() == this.waterTank2.getFluid()) i =
                        j
                    else if (ar[j]!!.getOutFluid() == this.waterTank2.getFluid() && ar[j]!!.getOutFluid2() == this.waterTank1.getFluid()) i =
                        j
                }

                if (this.waterTank1.getFluid() == ar[i]!!.getOutFluid() && this.waterTank2.getFluid() == ar[i]!!.getOutFluid2()) {
                    this.baseTank.fill(ar[i]!!.getFluid(), true)
                    this.waterTank1.drain(ar[i]!!.getOutFluid()!!.amount, true)
                    this.waterTank2.drain(ar[i]!!.getOutFluid2()!!.amount, true)
                } else if (this.waterTank2.getFluid() == ar[i]!!.getOutFluid() && this.waterTank1.getFluid() == ar[i]!!.getOutFluid2()) {
                    this.baseTank.fill(ar[i]!!.getFluid(), true)
                    this.waterTank1.drain(ar[i]!!.getOutFluid2()!!.amount, true)
                    this.waterTank2.drain(ar[i]!!.getOutFluid()!!.amount, true)
                }
            }
        }
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks")
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())

        this.reverse = par1NBTTagCompound.getBoolean("reverse")

        if (par1NBTTagCompound.hasKey("baseTank")) this.baseTank.readFromNBT(par1NBTTagCompound.getCompoundTag("baseTank"))
        if (par1NBTTagCompound.hasKey("waterTank1")) this.waterTank1.readFromNBT(par1NBTTagCompound.getCompoundTag("waterTank1"))
        if (par1NBTTagCompound.hasKey("waterTank2")) this.waterTank2.readFromNBT(par1NBTTagCompound.getCompoundTag("waterTank2"))
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        par1NBTTagCompound.setBoolean("reverse", this.reverse)

        if (this.baseTank.getFluid() != null) par1NBTTagCompound.setTag(
            "baseTank",
            this.baseTank.writeToNBT(NBTTagCompound())
        )

        if (this.waterTank1.getFluid() != null) par1NBTTagCompound.setTag(
            "waterTank1",
            this.waterTank1.writeToNBT(NBTTagCompound())
        )

        if (this.waterTank2.getFluid() != null) par1NBTTagCompound.setTag(
            "waterTank2",
            this.waterTank2.writeToNBT(NBTTagCompound())
        )

        return par1NBTTagCompound
    }


    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf(0, 1, 2)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return slotID == 0
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return true
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    private fun getPipe(): EnumFacing {
        return getFront().rotateYCCW()
    }

    private fun getPipeUp(): EnumFacing {
        return EnumFacing.UP
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            return direction == getPipe() || direction == getFront().rotateY() || direction == getPipeUp()
        }
        return false
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (!this.getReverse() && from == this.getPipeUp()) {
            return this.baseTank.getFluid() == null || this.baseTank.getFluidAmount() < this.baseTank.getCapacity()
        } else if (this.getReverse()) {
            if (from == this.getPipe()) {
                return this.waterTank1.getFluid() == null || this.waterTank1.getFluidAmount() < this.waterTank1.getCapacity()
            } else if (from == this.getFront().rotateY()) {
                return this.waterTank2.getFluid() == null || this.waterTank2.getFluidAmount() < this.waterTank2.getCapacity()
            }
        }
        return false
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0
        if (!this.getReverse()) {
            if (resource != null && this.canFill(from, resource.getFluid())) {
                val liquidName: String? = FluidRegistry.getFluidName(resource)
                if (liquidName != null) used = this.baseTank.fill(resource, doFill)
            }
        } else {
            if (resource != null && this.canFill(from, resource.getFluid())) {
                if (from == this.getPipe()) {
                    val liquidName: String? = FluidRegistry.getFluidName(resource)
                    if (liquidName != null) used = this.waterTank1.fill(resource, doFill)
                } else if (from == this.getFront().rotateY()) {
                    val liquidName: String? = FluidRegistry.getFluidName(resource)
                    if (liquidName != null) used = this.waterTank2.fill(resource, doFill)
                }
            }
        }

        return used
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (!this.getReverse()) {
            if (from == this.getFront().rotateY()) {
                if (resource != null && resource.isFluidEqual(this.waterTank2.getFluid())) return this.waterTank2.drain(
                    resource.amount,
                    doDrain
                )
            }

            if (from == getPipe()) {
                if (resource != null && resource.isFluidEqual(this.waterTank1.getFluid())) return this.waterTank1.drain(
                    resource.amount,
                    doDrain
                )
            }
        } else {
            if (from == EnumFacing.UP) if (resource != null && resource.isFluidEqual(this.baseTank.getFluid())) return this.baseTank.drain(
                resource.amount,
                doDrain
            )
        }
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (!this.getReverse()) {
            if (from == this.getFront().rotateY()) {
                return this.waterTank2.drain(maxDrain, doDrain)
            }

            if (from == getPipe()) {
                return this.waterTank1.drain(maxDrain, doDrain)
            }
        } else {
            if (from == EnumFacing.UP) return this.baseTank.drain(maxDrain, doDrain)
        }

        return null
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (!this.getReverse()) {
            if (from == getPipe()) return this.waterTank1.getFluid() != null && this.waterTank1.getFluidAmount() > 0

            if (from == this.getFront()
                    .rotateY()
            ) return this.waterTank2.getFluid() != null && this.waterTank2.getFluidAmount() > 0
        } else {
            if (from == EnumFacing.UP) return this.baseTank.getFluid() != null && this.baseTank.getFluidAmount() > 0
        }
        return false
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        var tankInfo: Array<FluidTankInfo?> = arrayOf<FluidTankInfo?>()
        if (from == EnumFacing.UP) tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.baseTank)) as Array<FluidTankInfo?>?))!!
        else if (from == getPipe()) tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank1)) as Array<FluidTankInfo?>?))!!
        else if (from == this.getFront().rotateY()) tankInfo = ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank2)) as Array<FluidTankInfo?>?))!!

        return tankInfo
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

    override fun getElectricInputDirection(): EnumFacing {
        //return getFront().rotateY();
        return EnumFacing.DOWN
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockLiquidSeparator) {
            return state.getValue(BlockLiquidSeparator.FACING)
        }
        return EnumFacing.NORTH
    }

    open fun setReverse(rev: Boolean) {
        this.reverse = rev
    }

    open fun getReverse(): Boolean {
        return this.reverse
    }

    open fun getScaledTankLevel(tank: FluidTank, i: Int): Int {
        return tank.getFluidAmount() * i / tank.getCapacity()
    }
}
