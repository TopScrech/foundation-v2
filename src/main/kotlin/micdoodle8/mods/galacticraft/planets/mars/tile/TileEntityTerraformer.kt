package micdoodle8.mods.galacticraft.planets.mars.tile

import foundation.item.legacyItemValue

import foundation.block.getStateFromMeta

import foundation.level.offset

import foundation.persistence.containsTag

import micdoodle8.mods.miccore.Annotations.NetworkedField

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerTerraformer
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.BlockSapling
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.FluidTankInfo
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

open class TileEntityTerraformer constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory,
    IDisableableMachine, IBubbleProviderColored, IFluidHandlerWrapper {
    private val tankCapacity = 2000

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var waterTank: FluidTank = FluidTank(this.tankCapacity)

    @JvmField
    var active: Boolean = false

    @JvmField
    var lastActive: Boolean = false
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(14, ItemStack.EMPTY)
    private val terraformableBlocksList = ArrayList<BlockPos?>()
    private val grassBlockList = ArrayList<BlockPos>()
    private val grownTreesList = ArrayList<BlockPos>()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var terraformableBlocksListSize: Int = 0 // used for server->client ease

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var grassBlocksListSize: Int = 0 // used for server->client ease

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var treesDisabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var grassDisabled: Boolean = false

    @JvmField
    val MAX_SIZE: Double = 15.0
    private var useCount = IntArray(2)
    private var saplingIndex = 6

    @JvmField
    var bubbleSize: Float = 0f

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var shouldRenderBubble: Boolean = true

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 30).toFloat())
    }

    open fun getScaledWaterLevel(i: Int): Int {
        val fuelLevel: Double =
            (if (this.waterTank.getFluid() == null) 0 else this.waterTank.getFluid()!!.amount).toDouble()

        return (fuelLevel * i / this.tankCapacity).toInt()
    }

    override fun invalidate() {
        super.invalidate()
    }

    open fun getDistanceFromServer(par1: Double, par3: Double, par5: Double): Double {
        val d3: Double = this.getPos().getX() + 0.5 - par1
        val d4: Double = this.getPos().getY() + 0.5 - par3
        val d5: Double = this.getPos().getZ() + 0.5 - par5
        return d3 * d3 + d4 * d4 + d5 * d5
    }

    override fun update() {
        super.update()

        //        if (this.terraformBubble == null)
//        {
//            if (!this.world.isRemote)
//            {
//                this.terraformBubble = new EntityTerraformBubble(this.world, new Vector3(this), this);
//                this.world.spawnEntity(this.terraformBubble);
//            }
//        }
        if (!this.world.isRemote) {
            val liquid: FluidStack? = FluidUtil.getFluidContained(this.stacks.get(0))
            if (FluidUtil.isFluidStrict(liquid, FluidRegistry.WATER.getName())) {
                FluidUtil.loadFromContainer(waterTank, FluidRegistry.WATER, this.stacks, 0, liquid!!.amount)
            }

            this.active =
                this.bubbleSize.toDouble() == this.MAX_SIZE && this.hasEnoughEnergyToRun && !this.getFirstBonemealStack()
                    .isEmpty() && this.waterTank.getFluid() != null && this.waterTank.getFluid()!!.amount > 0
        }

        if (!this.world.isRemote && (this.active != this.lastActive || this.ticks % 60 == 0)) {
            this.terraformableBlocksList.clear()
            this.grassBlockList.clear()

            if (this.active) {
                val bubbleSize: Int = ceil(this.bubbleSize.toDouble()).toInt()
                var bubbleSizeSq: Double = this.bubbleSize.toDouble()
                bubbleSizeSq *= bubbleSizeSq
                val doGrass: Boolean = !this.grassDisabled && !this.getFirstSeedStack().isEmpty()
                val doTrees: Boolean = !this.treesDisabled && !this.getFirstSaplingStack().isEmpty()
                for (x: Int in this.getPos().getX() - bubbleSize..<this.getPos().getX() + bubbleSize) {
                    for (y: Int in this.getPos().getY() - bubbleSize..<this.getPos().getY() + bubbleSize) {
                        for (z: Int in this.getPos().getZ() - bubbleSize..<this.getPos().getZ() + bubbleSize) {
                            val pos: BlockPos = BlockPos(x, y, z)
                            val blockID: Block = this.world.getBlockState(pos).getBlock()
                            if (blockID == null) {
                                continue
                            }

                            if (!(blockID.isAir(
                                    this.world.getBlockState(pos),
                                    this.world,
                                    pos
                                )) && this.getDistanceFromServer(
                                    x.toDouble(),
                                    y.toDouble(),
                                    z.toDouble()
                                ) < bubbleSizeSq
                            ) {
                                if (doGrass && blockID is ITerraformableBlock && (blockID as ITerraformableBlock).isTerraformable(
                                        this.world,
                                        pos
                                    )
                                ) {
                                    this.terraformableBlocksList.add(BlockPos(x, y, z))
                                } else if (doTrees) {
                                    val blockIDAbove: Block = this.world.getBlockState(pos.above()).getBlock()
                                    if (blockID === Blocks.GRASS && blockIDAbove.isAir(
                                            this.world.getBlockState(pos.above()),
                                            this.world,
                                            pos.above()
                                        )
                                    ) {
                                        this.grassBlockList.add(BlockPos(x, y, z))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!this.world.isRemote && this.terraformableBlocksList.size > 0 && this.ticks % 15 == 0) {
            val terraformableBlocks2: ArrayList<BlockPos> = ArrayList<BlockPos>(this.terraformableBlocksList)

            val randomIndex: Int = this.world.rand.nextInt(this.terraformableBlocksList.size)
            val vec: BlockPos = terraformableBlocks2.get(randomIndex)

            if (this.world.getBlockState(vec).getBlock() is ITerraformableBlock) {
                val id: Block

                when (this.world.rand.nextInt(40)) {
                    0 -> if (this.world.isBlockFullCube(
                            BlockPos(
                                vec.getX() - 1,
                                vec.getY(),
                                vec.getZ()
                            )
                        ) && this.world.isBlockFullCube(
                            BlockPos(
                                vec.getX() + 1,
                                vec.getY(),
                                vec.getZ()
                            )
                        ) && this.world.isBlockFullCube(
                            BlockPos(
                                vec.getX(),
                                vec.getY(),
                                vec.getZ() - 1
                            )
                        ) && this.world.isBlockFullCube(BlockPos(vec.getX(), vec.getY(), vec.getZ() + 1))
                    ) {
                        id = Blocks.FLOWING_WATER
                    } else {
                        id = Blocks.GRASS
                    }

                    else -> id = Blocks.GRASS
                }

                this.world.setBlockAndUpdate(vec, id.defaultBlockState())

                if (id === Blocks.GRASS) {
                    this.useCount[0]++
                    this.waterTank.drain(1, true)
                    this.checkUsage(1)
                } else if (id === Blocks.FLOWING_WATER) {
                    this.checkUsage(2)
                }
            }

            this.terraformableBlocksList.removeAt(randomIndex)
        }

        if (!this.world.isRemote && !this.treesDisabled && this.grassBlockList.size > 0 && this.ticks % 50 == 0) {
            val randomIndex: Int = this.world.rand.nextInt(this.grassBlockList.size)
            val vecGrass: BlockPos = grassBlockList.get(randomIndex)

            if (this.world.getBlockState(vecGrass).getBlock() === Blocks.GRASS) {
                val vecSapling: BlockPos = vecGrass.offset(0, 1, 0)
                val sapling: ItemStack? = this.getFirstSaplingStack()
                var flag: Boolean = false

                //Attempt to prevent placement too close to other trees
                for (testVec: BlockPos in this.grownTreesList) {
                    if (testVec.distanceSq(vecSapling) < 9) {
                        flag = true
                        break
                    }
                }

                if (!flag && sapling != null) {
                    val b: Block = Block.getBlockFromItem(sapling.getItem())
                    this.world.setBlock(vecSapling, b.getStateFromMeta(sapling.legacyItemValue), 3)
                    if (b is BlockSapling) {
                        if (this.world.getLightFromNeighbors(vecSapling) >= 9) {
                            b.grow(this.world, vecSapling, this.world.getBlockState(vecSapling), this.world.rand)
                            this.grownTreesList.add(BlockPos(vecSapling.getX(), vecSapling.getY(), vecSapling.getZ()))
                        }
                    } else if (b is BlockBush) {
                        if (this.world.getLightFromNeighbors(vecSapling) >= 5)  //Hammer the update tick a few times to try to get it to grow - it won't always
                        {
                            for (j: Int in 0..11) {
                                if (this.world.getBlockState(vecSapling).getBlock() === b) {
                                    b.updateTick(
                                        this.world,
                                        vecSapling,
                                        this.world.getBlockState(vecSapling),
                                        this.world.rand
                                    )
                                } else {
                                    this.grownTreesList.add(
                                        BlockPos(
                                            vecSapling.getX(),
                                            vecSapling.getY(),
                                            vecSapling.getZ()
                                        )
                                    )
                                    break
                                }
                            }
                        }
                    }

                    this.useCount[1]++
                    this.waterTank.drain(50, true)
                    this.checkUsage(0)
                }
            }

            this.grassBlockList.removeAt(randomIndex)
        }

        if (!this.world.isRemote) {
            this.terraformableBlocksListSize = this.terraformableBlocksList.size
            this.grassBlocksListSize = this.grassBlockList.size
        }

        if (this.hasEnoughEnergyToRun && (!this.grassDisabled || !this.treesDisabled)) {
            this.bubbleSize = min(max(0f, this.bubbleSize + 0.1f).toDouble(), this.MAX_SIZE).toFloat()
        } else {
            this.bubbleSize = min(max(0f, this.bubbleSize - 0.1f).toDouble(), this.MAX_SIZE).toFloat()
        }

        this.lastActive = this.active
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        if (!this.world.isRemote) {
            (networkedList)!!.add(this.bubbleSize)
        }
    }

    override fun readExtraNetworkedData(dataStream: ByteBuf?) {
        if (this.world.isRemote) {
            this.bubbleSize = (dataStream)!!.readFloat()
        }
    }

    private fun checkUsage(type: Int) {
        var stack: ItemStack? = null

        if ((this.useCount[0] + this.useCount[1]) % 4 == 0) {
            stack = this.getFirstBonemealStack()

            if (!stack.isEmpty()) {
                stack.shrink(1)
            }
        }

        when (type) {
            0 -> {
                stack = this.stacks.get(this.saplingIndex)

                if (!stack.isEmpty()) {
                    stack.shrink(1)
                }
            }

            1 -> if (this.useCount[0] % 4 == 0) {
                stack = this.getFirstSeedStack()

                if (!stack.isEmpty()) {
                    stack.shrink(1)
                }
            }

            2 -> this.waterTank.drain(50, true)
        }
    }

    private fun getSelectiveStack(start: Int, end: Int): Int {
        for (i: Int in start..<end) {
            val stack: ItemStack = this.stacks.get(i)

            if (!stack.isEmpty()) {
                return i
            }
        }

        return -1
    }

    private fun getRandomStack(start: Int, end: Int): Int {
        var stackcount: Int = 0
        for (i: Int in start..<end) {
            if (!this.stacks.get(i).isEmpty()) {
                stackcount++
            }
        }

        if (stackcount == 0) {
            return -1
        }

        var random: Int = this.world.rand.nextInt(stackcount)
        for (i: Int in start..<end) {
            if (!this.stacks.get(i).isEmpty()) {
                if (random == 0) {
                    return i
                }
                random--
            }
        }

        return -1
    }

    open fun getFirstBonemealStack(): ItemStack {
        val index: Int = this.getSelectiveStack(2, 6)

        if (index != -1) {
            return this.stacks.get(index)
        }

        return ItemStack.EMPTY
    }

    open fun getFirstSaplingStack(): ItemStack {
        val index: Int = this.getRandomStack(6, 10)

        if (index != -1) {
            this.saplingIndex = index
            return this.stacks.get(index)
        }

        return ItemStack.EMPTY
    }

    open fun getFirstSeedStack(): ItemStack {
        val index: Int = this.getSelectiveStack(10, 14)

        if (index != -1) {
            return this.stacks.get(index)
        }

        return ItemStack.EMPTY
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.stacks = this.readStandardItemsFromNBT(nbt)

        this.bubbleSize = nbt.getFloatOr("BubbleSize", 0f)
        this.useCount = nbt.getIntArray("UseCountArray")

        if (this.useCount.size == 0) {
            this.useCount = IntArray(2)
        }

        if (nbt.containsTag("waterTank")) {
            this.waterTank.readFromNBT(nbt.getCompoundOrEmpty("waterTank"))
        }

        if (nbt.containsTag("bubbleVisible")) {
            this.setBubbleVisible(nbt.getBooleanOr("bubbleVisible", false))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, this.stacks)
        nbt.putFloat("BubbleSize", this.bubbleSize)
        nbt.putIntArray("UseCountArray", this.useCount)

        if (this.waterTank.getFluid() != null) {
            nbt.put("waterTank", this.waterTank.writeToNBT(NBTTagCompound()))
        }

        nbt.putBoolean("bubbleVisible", this.shouldRenderBubble)
        return nbt
    }

    public override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.tile_terraformer.name")
    }

    // ISidedInventory Implementation:
    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, itemstack)
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (slotID == 0) {
            return FluidUtil.isEmptyContainer(itemstack)
        }
        if (slotID == 1) {
            return ItemElectricBase.isElectricItemEmpty(itemstack)
        }

        return false
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        when (slotID) {
            0 -> {
                val stack: FluidStack? = net.minecraftforge.fluids.FluidUtil.getFluidContained(itemstack)
                return stack != null && stack.getFluid() === FluidRegistry.WATER
            }

            1 -> return ItemElectricBase.isElectricItem(itemstack.getItem())
            2, 3, 4, 5 -> return itemstack.getItem() === Items.DYE && itemstack.legacyItemValue == 15
            6, 7, 8, 9 -> return ContainerTerraformer.isOnSaplingList(itemstack)
            10, 11, 12, 13 -> return itemstack.getItem() === Items.WHEAT_SEEDS
        }
        return false
    }

    override fun shouldUseEnergy(): Boolean {
        return !this.grassDisabled || !this.treesDisabled
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(1)
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown <= 0) {
            when (index) {
                0 -> this.treesDisabled = !this.treesDisabled
                1 -> this.grassDisabled = !this.grassDisabled
            }

            this.disableCooldown = 10
        }
    }

    override fun getDisabled(index: Int): Boolean {
        when (index) {
            0 -> return this.treesDisabled
            1 -> return this.grassDisabled
        }

        return false
    }

    //    @Override
    //    public IBubble getBubble()
    //    {
    //        return this.terraformBubble;
    //    }
    override fun setBubbleVisible(shouldRender: Boolean) {
        this.shouldRenderBubble = shouldRender
    }

    override fun getPacketRange(): Double {
        return 64.0
    }

    //Pipe handling
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
        return (fluid == null || "water" == fluid.getName()) && from != this.getElectricInputDirection()
    }

    override fun fill(from: EnumFacing?, resource: FluidStack?, doFill: Boolean): Int {
        var used: Int = 0

        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = this.waterTank.fill(resource, doFill)
        }

        return used
    }

    override fun getTankInfo(from: EnumFacing?): Array<FluidTankInfo?> {
        return ((arrayOf<FluidTankInfo>(FluidTankInfo(this.waterTank)) as Array<FluidTankInfo?>?))!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (this.getPos().getX() - this.bubbleSize).toDouble(),
            (this.getPos().getY() - this.bubbleSize).toDouble(),
            (this.getPos().getZ() - this.bubbleSize).toDouble(),
            (this.getPos().getX() + this.bubbleSize).toDouble(),
            (this.getPos().getY() + this.bubbleSize).toDouble(),
            (this.getPos().getZ() + this.bubbleSize).toDouble()
        )
    }

    override fun getBubbleSize(): Float {
        return this.bubbleSize
    }

    override fun getBubbleVisible(): Boolean {
        return this.shouldRenderBubble
    }

    override fun getColor(): Vector3 {
        return Vector3(0.125, 0.5, 0.125)
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineMars) {
            return state.getValue(BlockMachineMars.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
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
            return direction != this.getElectricInputDirection()
        }
        return false
    }

    companion object {
        const val WATTS_PER_TICK: Int = 1
    }
}
