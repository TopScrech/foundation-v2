package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.core.util.GSUtils
import galaxyspace.core.prefab.tile.TileEntityUpgradeMachine
import galaxyspace.api.tile.ITileEffects
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasCollector
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side

open class TileEntityGasCollector constructor() : TileEntityUpgradeMachine("tile.gas_collector.name"),
    ITileEffects, ISidedInventory, IFluidHandlerWrapper, IPacketReceiver {
    private val PROCESS_TIME_REQUIRED = 30

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED

    private val tankCapacity = 5000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var gasTank: FluidTank = FluidTank(this.tankCapacity)

    private var visible = false

    private var radius = 2

    private var drainX = -radius
    private var drainY = 0
    private var drainZ = -radius


    init {
        this.storage.setCapacity(20000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(3 + 4, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    override fun update() {
        super.update()

        radius = 2
        if (upgradeSlots() != null) {
            for (i: Int in 0..upgradeSlots()!!.size - 1) {
                if (this.getInventory().get(upgradeSlots()[i])
                        .isItemEqual(ItemStack(GSItems.UPGRADES, 1, 0))
                ) radius += 2
            }
        }

        if (!this.world.isRemote) {
            GSUtils.checkFluidTankTransfer(this.getInventory(), 1, this.gasTank)



            if (canProcess() && hasEnoughEnergyToRun) {
                if (this.processTicks >= this.processTimeRequired) {
                    this.smeltItem()
                    this.processTicks = 0
                }
            } else {
                this.processTicks = 0
            }
        }

        if (world.isRemote) {
            if (this.visible) {
                val xMin: Int = -radius
                val xMax: Int = radius + 1
                val zMin: Int = -radius
                val zMax: Int = radius + 1

                for (x: Int in xMin..xMax) for (z: Int in zMin..zMax) for (y: Int in 0..radius + 1) {
                    if (y == 0 || y == radius + 1) {
                        if (x == xMin || x == xMax || z == zMin || z == zMax) world.spawnParticle(
                            EnumParticleTypes.REDSTONE,
                            (this.getPos().getX() + x).toDouble(),
                            (this.getPos().getY() + y).toDouble(),
                            (this.getPos().getZ() + z).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                    } else if ((x == xMin || x == xMax) && (z == zMin || z == zMax)) world.spawnParticle(
                        EnumParticleTypes.REDSTONE,
                        (this.getPos().getX() + x).toDouble(),
                        (this.getPos().getY() + y).toDouble(),
                        (this.getPos().getZ() + z).toDouble(),
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }

            if (canProcess() && this.ticks % 10 == 0 && hasEnoughEnergyToRun) {
                for (particleCount: Int in 0..49) {
                    val x2: Double = (pos.getX() + this.world.rand.nextFloat()).toDouble()
                    val y2: Double = (pos.getY() + this.world.rand.nextFloat() + 1).toDouble()
                    var z2: Double = (pos.getZ() + this.world.rand.nextFloat()).toDouble()
                    var mX: Double = 0.0
                    var mY: Double = 0.0
                    var mZ: Double = 0.0
                    val dir: Int = this.world.rand.nextInt(2) * 2 - 1
                    mX = (this.world.rand.nextFloat() - 0.5) * 0.5
                    mY = (this.world.rand.nextFloat() - 0.5) * 0.5
                    mZ = (this.world.rand.nextFloat() - 0.5) * 0.5


                    z2 = pos.getZ() + 0.5 + 0.25 * dir
                    mZ = (this.world.rand.nextFloat() * 2.0f * dir).toDouble()


                    (GalacticraftCore.proxy)!!.spawnParticle(
                        "oxygen",
                        Vector3(x2, y2, z2),
                        Vector3(mX, mY, mZ),
                        (arrayOf<Any>(Vector3(0.8, 0.7, 0.6)) as Array<Any?>?)
                    )
                }
            }
        }
    }

    override fun canProcess(): Boolean {
        if (this.world.getBlockState(getPos().up()).isFullBlock()) return false

        if (this.gasTank.getFluidAmount() >= this.gasTank.getCapacity()) return false

        if ((this.getStackInSlot(2))!!.isEmpty()) return false

        return true
    }

    open fun smeltItem() {
        val pos: BlockPos = this.getPos().add(drainX, drainY, drainZ)
        val state: IBlockState = this.world.getBlockState(pos)
        val block: Block = state.getBlock()
        val meta: Int = state.getBlock().getMetaFromState(state)

        if (meta == 0) {
            if (block is BlockFluidClassic) {
                val fluid: Fluid = block.getFluid()
                if (!fluid.isGaseous()) return
                this.gasTank.fill(FluidStack(fluid, Fluid.BUCKET_VOLUME), true)
                if (this.gasTank.getFluid()!!
                        .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                ) this.world.setBlockToAir(pos)
                return
            } else if (block is IFluidBlock) {
                val fluid: IFluidBlock = block as IFluidBlock
                if (!fluid.getFluid().isGaseous()) return
                this.gasTank.fill(FluidStack(fluid.getFluid(), Fluid.BUCKET_VOLUME), true)
                if (this.gasTank.getFluid()!!
                        .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                ) this.world.setBlockToAir(pos)
                return
            }
        }



        if (drainX++ == radius) {
            drainZ++
            drainX = -radius
        }

        if (drainZ == radius + 1) {
            drainY++
            drainZ = -radius
        }

        if (drainY == radius + 1) {
            drainY = 0
            drainX = -radius
            drainZ = -radius
        }

        //System.out.println(drainX + " | " + drainY + " | " + drainZ);
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks")
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())

        this.radius = par1NBTTagCompound.getInteger("radius")

        if (par1NBTTagCompound.hasKey("gasTank")) this.gasTank.readFromNBT(par1NBTTagCompound.getCompoundTag("gasTank"))
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks)
        par1NBTTagCompound.setInteger("radius", this.radius)

        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        if (this.gasTank.getFluid() != null) {
            par1NBTTagCompound.setTag("gasTank", this.gasTank.writeToNBT(NBTTagCompound()))
        }

        return par1NBTTagCompound
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
            1, 2 -> return true
        }

        return false
    }

    override fun upgradeSlots(): IntArray {
        return intArrayOf(3, 4, 5, 6)
    }


    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.DOWN
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockGasCollector) {
            return state.getValue(BlockGasCollector.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        return 0
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            if (resource != null && resource.isFluidEqual(this.gasTank.getFluid())) return this.gasTank.drain(
                resource.amount,
                doDrain
            )
        }
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            return this.gasTank.drain(maxDrain, doDrain)
        }
        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == getPipe()) {
            return this.gasTank.getFluid() != null && this.gasTank.getFluidAmount() > 0
        }
        return false
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

    private fun getPipe(): EnumFacing {
        return getFront().rotateYCCW()
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

    open fun getScaledTankLevel(i: Int): Int {
        return this.gasTank.getFluidAmount() * i / this.gasTank.getCapacity()
    }

    override fun setEffectsVisible(shouldRender: Boolean) {
        this.visible = shouldRender
    }

    override fun getEffectsVisible(): Boolean {
        return this.visible
    }
}
