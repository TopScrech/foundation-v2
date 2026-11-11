package galaxyspace.systems.SolarSystem.planets.overworld.tile

import foundation.block.getMetaFromState

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockLiquidExtractor
import galaxyspace.core.GSFluids
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList
import java.util.HashMap
import java.util.function.Consumer

open class TileEntityLiquidExtractor constructor() :
    TileEntityElectricInventory("tile.liquid_extractor.name"), IFluidHandlerWrapper, ISidedInventory,
    IPacketReceiver {
    private val PROCESS_TIME_REQUIRED = 30

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    private val tankCapacity = 3000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank: FluidTank = FluidTank(this.tankCapacity)

    private val blocks: MutableList<BlockPos?> = ArrayList<BlockPos?>()
    private var number = 0

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(3 + 4, ItemStack.EMPTY)
        this.setTierGC(1)

        extractfluid.put(Blocks.ICE, FluidStack(FluidRegistry.WATER, 100))
    }

    override fun update() {
        super.update()


        if (!this.world.isRemote) {
            var range: Int = 1
            GSUtils.checkFluidTankTransfer(this.getInventory(), 1, this.waterTank)

            if (this.canProcess()) {
                var energy_boost: Int = 0

                for (i: Int in 0..3) {
                    if (this.getInventory().get(3 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 0))) range += 2
                    if (this.getInventory().get(3 + i).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 3))) energy_boost++
                }
                this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 - (20 * energy_boost) else 45 - (15 * energy_boost)).toFloat())


                blocks.clear()
                val pos1: BlockPos =
                    BlockPos(this.getPos().getX() - range, this.getPos().getY(), this.getPos().getZ() - range)
                val pos2: BlockPos =
                    BlockPos(this.getPos().getX() + range, this.getPos().getY(), this.getPos().getZ() + range)
                BlockPos.getAllInBox(pos1, pos2).forEach(Consumer { block: BlockPos? -> blocks.add(block) })

                if (number > blocks.size - 1) number = 0

                if (this.hasEnoughEnergyToRun) {
                    //50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                    // if (this.tierGC == 2) this.processTimeRequired = 200 / (1 + this.poweredByTierGC);


                    if (this.processTicks == 0) {
                        this.processTicks = this.processTimeRequired
                    } else {
                        if (--this.processTicks <= 0) {
                            this.smeltItem(number++)
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
        if (this.waterTank.getFluidAmount() >= this.waterTank.getCapacity()) return false

        return true
    }

    open fun smeltItem(num: Int) {
        if (this.canProcess()) {
            val pos: BlockPos = this.blocks.get(num)!!.below()
            val state: IBlockState = this.world.getBlockState(pos)
            val block: Block = state.getBlock()
            val meta: Int = state.getBlock().getMetaFromState(state)

            val ext: FluidStack? = extractfluid.get(this.world.getBlockState(pos).getBlock())
            if (ext != null) {
                val water: Fluid = ext.getFluid()
                this.waterTank.fill(FluidStack(water, ext.amount), true)
                //if(this.waterTank.getFluid().isFluidEqual(ext) && (block != Blocks.ICE || block != Blocks.PACKED_ICE)) this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                return
            }


            //System.out.println(this.extractfluid);
            if (meta == 0) {
                if (block === Blocks.WATER) {
                    //this.waterTank.fill(new FluidStack(!this.stacks.get(2).isEmpty() && this.stacks.get(2) == new ItemStack(GSItems.BASIC, 1, 3) ? GSFluids.Hydrogen2 : FluidRegistry.WATER, block == Blocks.ice ? 20 : this.containingItems[2] != null ? 100 : 1000), true);

                    val water: Fluid =
                        /*!this.stacks.get(2).isEmpty() && this.stacks.get(2).isItemEqual(new ItemStack(GSItems.BASIC, 1, 11)) ? GSFluids.Hydrogen2 :*/
                        FluidRegistry.WATER
                    this.waterTank.fill(FluidStack(water, Fluid.BUCKET_VOLUME), true)
                    if (this.waterTank.getFluid()!!
                            .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                    ) this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    return
                } else if (block === Blocks.LAVA) {
                    this.waterTank.fill(FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME), true)
                    if (this.waterTank.getFluid()!!
                            .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                    ) this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    return
                } else if (block is BlockFluidClassic) {
                    val fluid: Fluid = block.getFluid()

                    if (fluid.isGaseous()) return
                    this.waterTank.fill(FluidStack(fluid, Fluid.BUCKET_VOLUME), true)
                    if (this.waterTank.getFluid()!!
                            .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                    ) this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    return
                } else if (block is IFluidBlock) {
                    val fluid: IFluidBlock = block as IFluidBlock
                    if (fluid.getFluid().isGaseous()) return

                    this.waterTank.fill(FluidStack(fluid.getFluid(), Fluid.BUCKET_VOLUME), true)
                    if (this.waterTank.getFluid()!!
                            .isFluidEqual(FluidStack(FluidRegistry.lookupFluidForBlock(block), 0))
                    ) this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                    return
                }
            }
        }
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.processTicks = par1NBTTagCompound.getIntOr("smeltingTicks", 0)
        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())

        this.number = par1NBTTagCompound.getIntOr("number", 0)

        //this.z = par1NBTTagCompound.getIntOr("posZ", 0);
        if (par1NBTTagCompound.containsTag("waterTank")) this.waterTank.readFromNBT(par1NBTTagCompound.getCompoundOrEmpty("waterTank"))
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.putInt("smeltingTicks", this.processTicks)
        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        par1NBTTagCompound.putInt("number", this.number)

        if (this.waterTank.getFluid() != null) {
            par1NBTTagCompound.put("waterTank", this.waterTank.writeToNBT(NBTTagCompound()))
        }

        return par1NBTTagCompound
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockLiquidExtractor) {
            return state.getValue(BlockLiquidExtractor.FACING)
        }
        return EnumFacing.NORTH
    }


    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun getItem(var1: Int): ItemStack {
        return this.getInventory().get(var1)
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 2)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return true
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.processTicks > 0
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        return 0
    }

    override fun drain(from: EnumFacing?, resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            if (resource != null && resource.isFluidEqual(this.waterTank.getFluid())) return this.waterTank.drain(
                resource.amount,
                doDrain
            )
        }
        return null
    }

    override fun drain(from: EnumFacing?, maxDrain: Int, doDrain: Boolean): FluidStack? {
        if (from == getPipe()) {
            return this.waterTank.drain(maxDrain, doDrain)
        }
        return null
    }

    override fun canFill(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        if (from == getPipe()) {
            return this.waterTank.getFluid() != null && this.waterTank.getFluidAmount() > 0
        }
        return false
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank)) as Array<FluidTankInfo?>?))!!
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

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
        //return EnumFacing.UP;
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
        return this.waterTank.getFluidAmount() * i / this.waterTank.getCapacity()
    }

    companion object {
        // private NonNullList<ItemStack> stacks = NonNullList.withSize(3 + 4, ItemStack.EMPTY);
        private val extractfluid: HashMap<Block?, FluidStack?> = HashMap<Block?, FluidStack?>()


        @JvmStatic
        fun addBlockAndFluid(block: Block?, fluidstack: FluidStack?) {
            extractfluid.put(block, fluidstack)
        }
    }
}
