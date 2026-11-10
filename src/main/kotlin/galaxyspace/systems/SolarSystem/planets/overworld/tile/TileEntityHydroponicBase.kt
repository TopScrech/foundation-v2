package galaxyspace.systems.SolarSystem.planets.overworld.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import galaxyspace.core.GSItems
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicFarm
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase.SeedData
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockHydroponicBase
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import galaxyspace.core.prefab.tile.TileEntityElectricInventory
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList
import java.util.Random

open class TileEntityHydroponicBase constructor() :
    TileEntityElectricInventory("tile.hydroponic_base.name"), IFluidHandlerWrapper, ISidedInventory,
    IPacketReceiver {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED_BASE

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks_0: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks_1: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks_2: Int = 0


    private val tankCapacity = 16000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank: FluidTank = FluidTank(this.tankCapacity)

    //private NonNullList<ItemStack> stacks = NonNullList.withSize(20, ItemStack.EMPTY);
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var moduleLevel: Int = 0

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
        this.inventory = NonNullList.withSize<ItemStack>(20, ItemStack.EMPTY)
        this.setTierGC(1)
    }

    ///////////////////////////////
    open class SeedData
    constructor(
        private val seed: ItemStack?,
        private val product: ItemStack?,
        private val secondproduct: ItemStack?,
        @field:kotlin.jvm.JvmField  val secondchance: Int,
        private val block: Block?,
        private val stages: Int,
        rand: BooleanArray
    ) {
        @field:kotlin.jvm.JvmField  var hasRandCount = BooleanArray(2)

        init {
            this.hasRandCount = rand
        }

        open fun getSeed(): ItemStack? {
            return this.seed
        }

        open fun getProduct(second: Boolean): ItemStack? {
            if (second) return this.secondproduct
            return this.product
        }

        open fun getBlock(): Block? {
            return this.block
        }

        open fun getStages(): Int {
            return this.stages
        }

        open fun hasRandCount(): BooleanArray? {
            return this.hasRandCount
        }
    }

    override fun update() {
        super.update()

        this.checkBlock(world)

        if (!this.world.isRemote) {
            if (!this.getInventory().get(1).isEmpty()) {
                val liquid: FluidStack? = FluidUtil.getFluidContained(this.getInventory().get(1))

                if (liquid != null) {
                    val isWater: Boolean = FluidRegistry.getFluidName(liquid).startsWith("water")

                    if (isWater) {
                        if (this.waterTank.getFluid() == null || this.waterTank.getFluid()!!.amount + liquid.amount <= this.waterTank.getCapacity()) {
                            this.waterTank.fill(FluidStack(FluidRegistry.WATER, liquid.amount), true)
                            val bucket: ItemStack = this.getInventory().get(1)

                            if (bucket.getItem() !== Items.WATER_BUCKET && micdoodle8.mods.galacticraft.core.util.FluidUtil.isBucket(
                                    this.getInventory().get(1)
                                ) || micdoodle8.mods.galacticraft.core.util.FluidUtil.isFilledContainer(
                                    this.getInventory().get(1)
                                )
                            ) {
                                val amount: Int = this.getInventory().get(1).getCount()
                                if (amount > 1) this.waterTank.fill(
                                    FluidStack(
                                        FluidRegistry.WATER,
                                        (amount - 1) * Fluid.BUCKET_VOLUME
                                    ), true
                                )
                                else {
                                    val handlerItem: IFluidHandlerItem? = FluidUtil.getFluidHandler(bucket)
                                    this.getInventory()
                                        .set(1, ItemStack(handlerItem!!.getContainer().getItem(), amount))
                                }
                            }
                            if (bucket.getItem() === Items.WATER_BUCKET) {
                                val amount: Int = this.getInventory().get(1).getCount()
                                if (amount > 1) this.waterTank.fill(
                                    FluidStack(
                                        FluidRegistry.WATER,
                                        (amount - 1) * Fluid.BUCKET_VOLUME
                                    ), true
                                )

                                this.getInventory().set(1, ItemStack(Items.BUCKET, amount))
                            } else {
                                this.getInventory().get(1).shrink(1)

                                if (this.getInventory().get(1).getCount() === 0) {
                                    this.getInventory().set(1, ItemStack.EMPTY)
                                }
                            }
                        }
                    }
                }
            }
            val ticks: Int = 0
            if (this.canProcess()) {
                if (this.hasEnoughEnergyToRun) {
                    /*if (processTicks_0 == 0)
						processTicks_0 = this.processTimeRequired;
					if (processTicks_1 == 0)
						processTicks_1 = this.processTimeRequired;
					if (processTicks_2 == 0)
						processTicks_2 = this.processTimeRequired;*/

                    if (++this.processTicks_0 >= this.processTimeRequired) {
                        this.smeltItem()
                        processTicks_0 = 0
                    }
                    if (++this.processTicks_1 >= this.processTimeRequired) {
                        this.smeltItem()
                        processTicks_1 = 0
                    }
                    if (++this.processTicks_2 >= this.processTimeRequired) {
                        this.smeltItem()
                        processTicks_2 = 0
                    }

                    this.waterTank.drain(1, true)

                    if (!this.getInventory().get(8).isEmpty() && (this.getInventory().get(8).getItem() === Items.DYE
                                && this.getInventory().get(8).getItemDamage() === 15
                                || this.getInventory().get(8).getItem() === GSItems.BASIC
                                && this.getInventory().get(8).getItemDamage() === 4)
                    ) {
                        if (this.processTicks_0 % 100 == 0) {
                            this.processTicks_0 += 2000
                            if (this.getInventory().get(8).getCount() > 1) this.getInventory().get(8).shrink(1)
                            else this.getInventory().set(8, ItemStack.EMPTY)
                        }
                        if (this.processTicks_1 % 100 == 0 && moduleLevel > 1) {
                            this.processTicks_1 += 2000
                            if (this.getInventory().get(8).getCount() > 1) this.getInventory().get(8).shrink(1)
                            else this.getInventory().set(8, ItemStack.EMPTY)
                        }
                        if (this.processTicks_2 % 100 == 0 && moduleLevel > 2) {
                            this.processTicks_2 += 2000
                            if (this.getInventory().get(8).getCount() > 1) this.getInventory().get(8).shrink(1)
                            else this.getInventory().set(8, ItemStack.EMPTY)
                        }
                    }
                } else if (this.processTicks_0 > 0 && this.processTicks_0 < this.processTimeRequired) {
                    if (this.world.rand.nextInt(4) == 0) {
                        this.processTicks_0++
                    }
                } else if (this.processTicks_1 > 0 && this.processTicks_1 < this.processTimeRequired) {
                    if (this.world.rand.nextInt(4) == 0) {
                        this.processTicks_1++
                    }
                } else if (this.processTicks_2 > 0 && this.processTicks_2 < this.processTimeRequired) {
                    if (this.world.rand.nextInt(4) == 0) {
                        this.processTicks_2++
                    }
                }
            } else {
                this.processTicks_0 = 0
                this.processTicks_1 = 0
                this.processTicks_2 = 0
            }
        }
    }

    open fun canProcess(): Boolean {
        if (this.waterTank.getFluidAmount() < 10 * this.getModuleLevel()) return false

        for (i: Int in 1..this.getModuleLevel()) {
            if (!this.getInventory().get(i * 2 + 1).isEmpty() && this.getInventory().get(i * 2 + 1)
                    .getCount() >= 64 && !this.getInventory().get(i + 8).isEmpty() && this.getInventory().get(i + 8)
                    .getCount() >= 64
            ) return false

            if (this.getInventory().get(i * 2).isEmpty()) return false

            val stack: ItemStack = this.getInventory().get(i * 2)
            val data: SeedData? = getSeedData(stack)

            if (data == null) return false
            if (!this.getInventory().get(i * 2 + 1).isEmpty() && this.getInventory().get(i * 2 + 1)
                    .getItem() !== data.getProduct(false)!!.getItem()
            ) return false


            /*
			if(!this.stacks.get(i*2).isEmpty() && this.stacks.get(i*2).getItem() != Items.WHEAT_SEEDS
					&& !this.stacks.get(i*2+1).isEmpty() && (!this.stacks.get(i*2+1).isItemEqual(this.stacks.get(i*2)))) {						
					return false;
			}
			if(!this.stacks.get(i*2).isEmpty() && this.stacks.get(i*2).getItem() == Items.WHEAT_SEEDS)
			{
				if(!this.stacks.get(i*2+1).isEmpty() && this.stacks.get(i*2+1).getItem() != Items.WHEAT) return false;
			}
			*/
        }

        return true
    }

    open fun smeltItem() {
        for (i: Int in 1..this.getModuleLevel()) if (!this.getInventory().get(i * 2).isEmpty()) {
            /*SeedData seed = null;
				for(SeedData seeds : this.seeds)
				{
					if (this.stacks.get(i*2).isItemEqual(seeds.getSeed()))
					{
						seed = seeds;
						break;
					}
				}*/

            val rand: Random = Random()
            val seed: SeedData? = this.getSeedData(this.getInventory().get(i * 2))

            if (seed != null && this.getInventory().get(i * 2).isItemEqual(seed.getSeed())) {
                if (this.getInventory().get(i * 2 + 1).isEmpty()) {
                    val stack: ItemStack = seed.getProduct(false)!!.copy()
                    stack.setCount(stack.getCount() + (if (seed.hasRandCount[0]) rand.nextInt(3) else 0))
                    this.getInventory().set(i * 2 + 1, stack)
                } else this.getInventory().get(i * 2 + 1).grow(1 + (if (seed.hasRandCount[0]) rand.nextInt(3) else 0))

                if (!seed.getProduct(true)!!.isEmpty() && rand.nextInt(101 - seed.secondchance) == 0) {
                    if (this.getInventory().get(i + 8).isEmpty()) {
                        val stack: ItemStack = seed.getProduct(true)!!.copy()
                        stack.setCount(stack.getCount() + (if (seed.hasRandCount[1]) rand.nextInt(2) else 0))
                        this.getInventory().set(i + 8, stack)
                    } else {
                        this.getInventory().get(i + 8).grow(1 + (if (seed.hasRandCount[1]) rand.nextInt(2) else 0))
                    }
                }
            }

            if (this.getInventory().get(i * 2).getCount() > 1) this.getInventory().get(i * 2).shrink(1)
            else this.getInventory().set(i * 2, ItemStack.EMPTY)
        }
    }

    open fun checkBlock(world: World) {
        val check: BooleanArray = BooleanArray(4)

        run {
            var i: Int = 0
            while (world.getBlockState(pos.up(i + 1)).getBlock() === GSBlocks.HYDROPONIC_FARM) {
                check[i] = true
                i++
            }
        }

        var k: Int = 0

        for (i: Int in 0..2) if (check[i]) k++

        this.setModuleLevel(k)


        var ticks: Int = 0
        for (i: Int in 1..this.getModuleLevel()) {
            if (i == 1) ticks = processTicks_0
            else if (i == 2) ticks = processTicks_1
            else if (i == 3) ticks = processTicks_2

            if (world.getTileEntity(pos.up(i)) is TileEntityHydroponicFarm) {
                val farm: TileEntityHydroponicFarm? = world.getTileEntity(pos.up(i)) as TileEntityHydroponicFarm?

                if (farm != null) {
                    if (!this.getInventory().get(i * 2).isEmpty()) {
                        val data: SeedData? = getSeedData(this.getInventory().get(i * 2))

                        if (data != null) {
                            val scale: Int =
                                if (ticks > 0) (ticks.toDouble() / this.processTimeRequired.toDouble() * data.getStages()).toInt() else data.getStages()

                            farm.setPlant(data.getBlock())
                            farm.setMetaPlant(scale)
                            farm.markDirty()
                        } else {
                            farm.setPlant(null)
                            farm.setMetaPlant(0)
                            farm.markDirty()
                        }
                    } else {
                        farm.setPlant(null)
                        farm.setMetaPlant(0)
                        farm.markDirty()
                    }
                }

                if (i > this.getModuleLevel()) {
                    if (!this.getInventory().get(i * 2).isEmpty()) {
                        this.getInventory().set(i * 2, ItemStack.EMPTY)
                    }
                }
            }
        }
    }

    private fun getSeedData(stack: ItemStack): SeedData? {
        for (seeds: SeedData in seeds) {
            if (seeds.getSeed()!!.isItemEqual(stack)) return seeds
        }
        return null
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
        val nbttagcompound: NBTTagCompound = NBTTagCompound()
        writeToNBT(nbttagcompound)
        return SPacketUpdateTileEntity(pos, 1, nbttagcompound)
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        readFromNBT(packet.getNbtCompound())
    }

    override fun readFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound)

        this.moduleLevel = par1NBTTagCompound.getInteger("moduleTier")
        this.processTicks_0 = par1NBTTagCompound.getInteger("smeltingTicks_0")
        this.processTicks_1 = par1NBTTagCompound.getInteger("smeltingTicks_1")
        this.processTicks_2 = par1NBTTagCompound.getInteger("smeltingTicks_2")

        ItemStackHelper.loadAllItems(par1NBTTagCompound, this.getInventory())


        if (par1NBTTagCompound.hasKey("waterTank")) {
            this.waterTank.readFromNBT(par1NBTTagCompound.getCompoundTag("waterTank"))
        }
    }

    override fun writeToNBT(par1NBTTagCompound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(par1NBTTagCompound)
        par1NBTTagCompound.setInteger("smeltingTicks_0", this.processTicks_0)
        par1NBTTagCompound.setInteger("smeltingTicks_1", this.processTicks_1)
        par1NBTTagCompound.setInteger("smeltingTicks_2", this.processTicks_2)
        par1NBTTagCompound.setInteger("moduleTier", this.moduleLevel)

        ItemStackHelper.saveAllItems(par1NBTTagCompound, this.getInventory())

        if (this.waterTank.getFluid() != null) {
            par1NBTTagCompound.setTag("waterTank", this.waterTank.writeToNBT(NBTTagCompound()))
        }
        return par1NBTTagCompound
    }

    open fun getModuleLevel(): Int {
        return this.moduleLevel
    }

    open fun setModuleLevel(newTier: Int) {
        this.moduleLevel = newTier
    }

    override fun getSlotsForFace(side: EnumFacing?): IntArray {
        return intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    }

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.isItemValidForSlot(slotID, itemstack)
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return slotID == 1 || slotID == 3 || slotID == 5 || slotID == 7 || slotID == 9 || slotID == 10 || slotID == 11
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            1 -> return itemstack.getItem() === Items.WATER_BUCKET
            2, 4, 6 -> return itemstack.getItem() === Items.WHEAT_SEEDS || itemstack.getItem() === Items.CARROT || itemstack.getItem() === Items.POTATO
            8 -> return itemstack.getItem() === Items.DYE && itemstack.getItemDamage() == 15 || itemstack.getItem() === GSItems.BASIC && itemstack.getItemDamage() == 4
        }

        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getStackInSlot(0)
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank)) as Array<FluidTankInfo?>?))!!
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (this.getPipe() == from && resource != null) {
            val liquidName: String? = FluidRegistry.getFluidName(resource)

            if (liquidName != null) {
                if (liquidName.startsWith("water")) used = this.waterTank.fill(resource, doFill)
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
            return this.waterTank.getFluid() == null || this.waterTank.getFluidAmount() < this.waterTank.getCapacity()
        }
        return false
    }

    override fun canDrain(from: EnumFacing?, fluid: Fluid?): Boolean {
        return false
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockHydroponicBase) {
            return state.getValue(BlockHydroponicBase.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return EnumFacing.DOWN
    }

    private fun getPipe(): EnumFacing {
        return getFront().getOpposite()
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        if (direction == null) {
            return false
        }
        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection()
        }
        if (type == NetworkType.FLUID) {
            return direction == this.getPipe()
        }
        return false
    }

    open fun getScaledFluidLevel(i: Int): Int {
        return this.waterTank.getFluidAmount() * i / this.waterTank.getCapacity()
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

    companion object {
        const val PROCESS_TIME_REQUIRED_BASE: Int = 12000

        //private static Map list = new HashMap<Block, Integer>();
        private val seeds: MutableList<SeedData> = ArrayList<SeedData>()

        /////////////////////////////////
        @JvmStatic
        fun addPlant(
            seed: ItemStack?,
            product: ItemStack?,
            secproduct: ItemStack?,
            secchance: Int,
            block: Block?,
            stages: Int,
            rand: BooleanArray
        ) {
            var secproduct: ItemStack? = secproduct
            if (secproduct == null) secproduct = ItemStack.EMPTY
            val data: SeedData = SeedData(seed, product, secproduct, secchance, block, stages, rand)
            seeds.add(data)
        }

        @JvmStatic
        fun getSeeds(): MutableList<*> {
            return seeds
        }

        @JvmStatic
        fun getData(seed: ItemStack): SeedData? {
            for (data: SeedData in seeds) if (seed.isItemEqual(data.getSeed())) return data

            return null
        }

        @JvmStatic
        fun removePlant(seed: ItemStack) {
            seeds.remove(getData(seed))
        }
    }
}
