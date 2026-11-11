package micdoodle8.mods.galacticraft.planets.asteroids.tile

import foundation.level.offset

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField
import net.minecraft.core.Direction.Axis

import com.google.common.collect.Lists
import java.util.UUID
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.tile.IMachineSides
import micdoodle8.mods.galacticraft.core.tile.IMachineSidesProperties
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RecipeUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList
import kotlin.math.min

open class TileEntityMinerBase constructor() : TileBaseElectricBlockWithInventory(), ISidedInventory, IMultiBlock,
    IMachineSides {
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(HOLDSIZE + 1, ItemStack.EMPTY)
    private val slotArray: IntArray

    @JvmField
    var isMaster: Boolean = false

    @JvmField
    var facing: EnumFacing = EnumFacing.NORTH
    private var mainBlockPosition: BlockPos? = null
    private val targetPoints = LinkedList<BlockVec3?>()
    private var masterTile: WeakReference<TileEntityMinerBase?>? = null

    @JvmField
    var updateClientFlag: Boolean = false

    @JvmField
    var findTargetPointsFlag: Boolean = false

    @JvmField
    var linkCountDown: Int = 0
    private var renderAABB: AxisAlignedBB? = null

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var linkedMinerDataAIState: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var linkedMinerDataDX: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var linkedMinerDataDY: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var linkedMinerDataDZ: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var linkedMinerDataCount: Int = 0


    open fun setMainBlockPosition(mainBlockPosition: BlockPos?) {
        this.mainBlockPosition = mainBlockPosition
    }

    /**
     * The number of players currently using this chest
     */
    @JvmField
    var numUsingPlayers: Int = 0

    /**
     * Server sync counter (once per 20 ticks)
     */
    private val ticksSinceSync = 0

    private val spawnedMiner = false

    @JvmField
    var linkedMiner: EntityAstroMiner? = null

    @JvmField
    var linkedMinerID: UUID? = null
    private var initialised = false

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 20 else 12).toFloat())
        this.slotArray = IntArray(HOLDSIZE)
        for (i: Int in 0..<HOLDSIZE) {
            this.slotArray[i] = i + 1
        }
    }

    override fun update() {
        super.update()

        if (!this.initialised) {
            this.initialised = true
            if (!this.world.isRemote && !this.isMaster) {
                if (this.getMaster() == null) {
                    this.world.setBlock(this.getPos(), (AsteroidBlocks.blockMinerBase)!!.defaultBlockState(), 2)
                }
            }
        }

        if (this.updateClientFlag) {
            assert(!this.world.isRemote) //Just checking: updateClientFlag should not be capable of being set on clients
            this.updateAllInDimension()
            this.updateClientFlag = false
        }

        if (this.findTargetPointsFlag) {
            if (this.isMaster && this.linkedMiner != null) {
                this.findTargetPoints()
            }
            this.findTargetPointsFlag = false
        }

        //TODO: Find linkedminer by UUID and update it if not chunkloaded?
        if (!this.isMaster) {
            val master: TileEntityMinerBase? = this.getMaster()

            if (master != null) {
                var energyLimit: Float = master.storage.getCapacityGC() - master.storage.getEnergyStoredGC()
                if (energyLimit < 0f) {
                    energyLimit = 0f
                }
                this.storage.setCapacity(energyLimit)
                this.storage.setMaxExtract(energyLimit)
                this.storage.setMaxReceive(energyLimit)
                val hasEnergy: Float = this.getEnergyStoredGC()
                if (hasEnergy > 0f) {
                    this.extractEnergyGC(null, master.receiveEnergyGC(null, hasEnergy, false), false)
                }
            }
        }

        //Used for refreshing client with linked miner position data
        if (this.linkCountDown > 0) {
            this.linkCountDown--
        }

        if (this.isMaster && !this.world.isRemote) {
            this.updateGUIstate()
            //System.out.println("Miner base state " + this.linkedMinerDataAIState);
        }
    }

    open fun updateGUIstate() {
        if (this.linkedMinerID == null) {
            this.linkedMinerDataAIState = -3
            return
        }
        val miner: EntityAstroMiner? = this.linkedMiner
        if (miner == null || miner.isDead) {
            this.linkedMinerDataAIState = -3
            return
        }
        if (this.linkCountDown > 0) {
            this.linkedMinerDataAIState = -2
            return
        }

        this.linkedMinerDataAIState = miner.AIstate
        this.linkedMinerDataDX = (MathHelper.floor(this.linkedMiner!!.posX) - this.getPos().getX() - 1)
        this.linkedMinerDataDY = (MathHelper.floor(this.linkedMiner!!.posY) - this.getPos().getY() - 1)
        this.linkedMinerDataDZ = (MathHelper.floor(this.linkedMiner!!.posZ) - this.getPos().getZ() - 1)
        this.linkedMinerDataCount = miner.mineCount
    }

    //TODO - currently unused, the master position replaces this?
    protected open fun initialiseMultiTiles(pos: BlockPos, world: World) {
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(pos, (positions as MutableList<BlockPos?>?))
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getBlockEntity(vecToAdd)
            if (tile is TileEntityMinerBase) {
                tile.mainBlockPosition = pos
            }
        }
    }

    open fun spawnMiner(player: EntityPlayerMP?): Boolean {
        if (this.isMaster) {
            if (this.linkedMiner != null) {
                if (this.linkedMiner!!.isDead) {
                    this.unlinkMiner()
                }
            }
            if (this.linkedMinerID == null) {
//                System.err.println("" + this.facing);
                if (EntityAstroMiner.spawnMinerAtBase(
                        this.world,
                        this.getPos().getX() + 1,
                        this.getPos().getY() + 1,
                        this.getPos().getZ() + 1,
                        this.facing,
                        BlockVec3(this),
                        (player)!!
                    )
                ) {
                    this.findTargetPoints()
                    return true
                }
            }
            return false
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.spawnMiner(player)
        }
        return false
    }

    open fun getMaster(): TileEntityMinerBase? {
        if (this.mainBlockPosition == null) {
            return null
        }

        if (masterTile == null) {
            val tileEntity: TileEntity? = this.world.getBlockEntity(this.mainBlockPosition)

            if (tileEntity is TileEntityMinerBase) {
                masterTile = WeakReference<TileEntityMinerBase?>(tileEntity)
            }

            if (masterTile == null) {
                return null
            }
        }

        val master: TileEntityMinerBase? = this.masterTile!!.get()

        if (master != null && master.isMaster) {
            return master
        }

        return null
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.stacks = this.readStandardItemsFromNBT(nbt)
        this.facing = EnumFacing.byHorizontalIndex(nbt.getIntOr("facing", 0))
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.isMaster = nbt.getBooleanOr("isMaster", false)
            if (this.isMaster) {
                this.updateClientFlag = true
            } else {
                val tagCompound: NBTTagCompound = nbt.getCompoundOrEmpty("masterpos")
                if (tagCompound.getKeySet().isEmpty()) this.setMainBlockPosition(null)
                else {
                    this.setMainBlockPosition(
                        BlockPos(
                            tagCompound.getIntOr("x", 0),
                            tagCompound.getIntOr("y", 0),
                            tagCompound.getIntOr("z", 0)
                        )
                    )
                    this.updateClientFlag = true
                }
            }
            if (nbt.containsTag("LinkedUUIDMost", 4) && nbt.containsTag("LinkedUUIDLeast", 4)) {
                this.linkedMinerID = UUID(nbt.getLongOr("LinkedUUIDMost", 0L), nbt.getLongOr("LinkedUUIDLeast", 0L))
            } else {
                this.linkedMinerID = null
            }
            if (nbt.containsTag("TargetPoints")) {
                this.targetPoints.clear()
                val mpList: NBTTagList = nbt.listOfType("TargetPoints", 10)
                for (j: Int in 0..<mpList.size) {
                    val bvTag: NBTTagCompound = mpList.getCompoundOrEmpty(j)
                    this.targetPoints.add(BlockVec3.readFromNBT(bvTag))
                }
            } else {
                this.findTargetPointsFlag = this.isMaster
            }
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, this.stacks)
        nbt.putBoolean("isMaster", this.isMaster)
        if (!this.isMaster && this.mainBlockPosition != null) {
            val masterTag: NBTTagCompound = NBTTagCompound()
            masterTag.putInt("x", this.mainBlockPosition!!.getX())
            masterTag.putInt("y", this.mainBlockPosition!!.getY())
            masterTag.putInt("z", this.mainBlockPosition!!.getZ())
            nbt.put("masterpos", masterTag)
        }
        nbt.putInt("facing", this.facing.getHorizontalIndex())
        if (this.isMaster && this.linkedMinerID != null) {
            nbt.putLong("LinkedUUIDMost", this.linkedMinerID!!.getMostSignificantBits())
            nbt.putLong("LinkedUUIDLeast", this.linkedMinerID!!.getLeastSignificantBits())
        }
        val mpList: NBTTagList = NBTTagList()
        for (j: Int in this.targetPoints.indices) {
            mpList.add(this.targetPoints.get(j)!!.writeToNBT(NBTTagCompound()))
        }
        nbt.put("TargetPoints", mpList)
        return nbt
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    open fun addToInventory(itemstack: ItemStack): Boolean {
        //TODO - add test for is container open and if so use Container.mergeItemStack

        var flag1: Boolean = false
        var k: Int = 1
        val invSize: Int = this.getContainerSize()

        var existingStack: ItemStack

        if (itemstack.isStackable()) {
            while (!itemstack.isEmpty() && k < invSize) {
                existingStack = this.stacks.get(k)

                if (RecipeUtil.stacksMatch(itemstack, existingStack)) {
                    val combined: Int = existingStack.getCount() + itemstack.getCount()

                    if (combined <= itemstack.getMaxStackSize()) {
                        itemstack.setCount(0)
                        existingStack.setCount(combined)
                        flag1 = true
                    } else if (existingStack.getCount() < itemstack.getMaxStackSize()) {
                        itemstack.shrink(itemstack.getMaxStackSize() - existingStack.getCount())
                        existingStack.setCount(itemstack.getMaxStackSize())
                        flag1 = true
                    }
                }

                ++k
            }
        }

        if (!itemstack.isEmpty()) {
            k = 1

            while (k < invSize) {
                existingStack = this.stacks.get(k)

                if (existingStack.isEmpty()) {
                    this.stacks.set(k, itemstack.copy())
                    itemstack.setCount(0)
                    flag1 = true
                    break
                }

                ++k
            }
        }

        this.setChanged()
        return flag1
    }

    override fun onLoad() {
        this.clientOnLoad()
    }

    /**
     * invalidates a tile entity
     */
    override fun invalidate() {
        super.invalidate()
        this.updateContainingBlockInfo()
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("tile.miner_base.name")
    }

    override fun getPacketRange(): Double {
        return 20.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        if (this.isMaster) {
            return this.stacks
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.getContainingItems()
        }

        return this.stacks
    }

    override fun shouldUseEnergy(): Boolean {
        return false
    }

    open fun setMainBlockPos(master: BlockPos?) {
        this.masterTile = null
        if (this.getPos() == master) {
            this.isMaster = true
            this.setMainBlockPosition(null)
            return
        }
        this.isMaster = false
        this.setMainBlockPosition(master)
        this.setChanged()
    }

    open fun onBlockRemoval() {
        if (this.isMaster) {
            this.invalidate()
            this.onDestroy(this)
            return
        }

        val master: TileEntityMinerBase? = this.getMaster()

        if (master != null && !master.isInvalid()) {
            this.world.destroyBlock(master.getPos(), false)
        }
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        if (this.isMaster) {
            val holding: ItemStack = (entityPlayer)!!.getUseItem()
            if (holding != null && holding.getItem() === AsteroidsItems.astroMiner) {
                return false
            }

            (entityPlayer)!!.openGui(
                GalacticraftPlanets.instance,
                GuiIdsPlanets.MACHINE_ASTEROIDS,
                this.world,
                this.getPos().getX(),
                this.getPos().getY(),
                this.getPos().getZ()
            )
            return true
        } else {
            val master: TileEntityMinerBase? = this.getMaster()
            return master != null && master.onActivated(entityPlayer)
        }
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.MINER_BASE
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        for (y: Int in 0..1) {
            if ((placedPosition)!!.getY() + y >= this.world.getHeight()) break
            for (x: Int in 0..1) {
                for (z: Int in 0..1) {
                    if (x + y + z == 0) continue
                    (positions)!!.add(
                        BlockPos(
                            (placedPosition)!!.getX() + x,
                            (placedPosition)!!.getY() + y,
                            (placedPosition)!!.getZ() + z
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === AsteroidBlocks.minerBaseFull)  //GCBlocks.fakeBlock && (EnumBlockMultiType) stateAt.getValue(BlockMulti.MULTI_TYPE) == EnumBlockMultiType.MINER_BASE)
            {
                if (this.world.isRemote && this.world.rand.nextDouble() < 0.1) {
                    FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                        pos,
                        this.world.getBlockState(pos)
                    )
                }
                this.world.destroyBlock(pos, false)
            }
        }
        this.world.destroyBlock(thisBlock, true)
    }

    //TODO
    //maybe 2 electrical inputs are needed?
    //chest goes above (could be 2 chests or other mods storage)
    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(2, 2, 2))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun updateFacing() {
        if (this.isMaster && this.linkedMinerID == null) {
            // Re-orient the block
            when (this.facing) {
                EnumFacing.SOUTH -> this.facing = EnumFacing.WEST
                EnumFacing.EAST -> this.facing = EnumFacing.SOUTH
                EnumFacing.WEST -> this.facing = EnumFacing.NORTH
                EnumFacing.NORTH -> this.facing = EnumFacing.EAST
            
else -> {}
}

            super.updateFacing()
        } else {
            val master: TileEntityMinerBase? = this.getMaster()
            if (master != null) {
                master.updateFacing()
            }
        }

        if (!this.world.isRemote) {
            this.updateAllInDimension()
        }

        for (facing: EnumFacing in EnumFacing.values()) {
            val offset: BlockPos = this.getPos().offset(facing)
            val tileOffset: TileEntity? = this.world.getBlockEntity(offset)
            if (tileOffset != null && tileOffset !is TileEntityMinerBase) {
                val state: IBlockState = this.world.getBlockState(offset)
                state.getBlock().neighborChanged(
                    state,
                    this.world,
                    offset,
                    this.world.getBlockState(this.getPos()).getBlock(),
                    this.getPos()
                )
                world.markBlockRangeForRenderUpdate(offset, offset)
            }
        }

        this.setChanged()
    }

    override fun buildDataPacket(data: IntArray?) {
        val x: Int
        val y: Int
        val z: Int
        if (this.mainBlockPosition != null) {
            x = this.mainBlockPosition!!.getX()
            y = this.mainBlockPosition!!.getY()
            z = this.mainBlockPosition!!.getZ()
        } else {
            x = this.getPos().getX()
            y = this.getPos().getY()
            z = this.getPos().getZ()
        }
        val link: Int = if (this.linkedMinerID != null) 8 else 0
        (data)!![0] = link + this.facing.ordinal
        (data)!![1] = x
        (data)!![2] = y
        (data)!![3] = z
    }

    override fun getElectricInputDirection(): EnumFacing? {
        if (this.isMaster) {
            return this.facing.getOpposite()
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.facing.getOpposite()
        }
        return null
    }

    open fun linkMiner(entityAstroMiner: EntityAstroMiner?) {
        this.linkedMiner = entityAstroMiner
        this.linkedMinerID = this.linkedMiner!!.getUniqueID()
        this.updateClientFlag = true
        this.setChanged()
    }

    open fun unlinkMiner() {
        this.linkedMiner = null
        this.linkedMinerID = null
        this.updateClientFlag = true
        this.setChanged()
    }

    open fun getLinkedMiner(): UUID? {
        if (this.isMaster) {
            return this.linkedMinerID
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.linkedMinerID
        }
        return null
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (this.isMaster) {
            return if (side != this.facing) slotArray else intArrayOf()
        }

        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.getSlotsForFace(side)
        }

        return intArrayOf()
    }

    override fun canPlaceItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing?): Boolean {
        return false
    }

    override fun canTakeItemThroughFace(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isMaster) {
            if (side != this.facing) {
                return slotID > 0 || ItemElectricBase.isElectricItemEmpty(itemstack)
            }

            return false
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.canTakeItemThroughFace(slotID, itemstack, side)
        }

        return false
    }

    override fun canPlaceItem(slotID: Int, itemstack: ItemStack): Boolean {
        if (this.isMaster) {
            return slotID > 0 || ItemElectricBase.isElectricItem(itemstack.getItem())
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.canPlaceItem(slotID, itemstack)
        }

        return false
    }

    override fun getItem(par1: Int): ItemStack {
        if (this.isMaster) {
            return super.getItem(par1)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.getItem(par1)
        }

        return ItemStack.EMPTY
    }

    override fun removeItem(par1: Int, par2: Int): ItemStack {
        if (this.isMaster) {
            return super.removeItem(par1, par2)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.removeItem(par1, par2)
        }

        return ItemStack.EMPTY
    }

    override fun removeItemNoUpdate(par1: Int): ItemStack {
        if (this.isMaster) {
            return super.removeItemNoUpdate(par1)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.removeItemNoUpdate(par1)
        }

        return ItemStack.EMPTY
    }

    override fun setItem(par1: Int, par2ItemStack: ItemStack) {
        if (this.isMaster) {
            super.setItem(par1, par2ItemStack)
            return
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            master.setItem(par1, par2ItemStack)
        }

        return
    }

    override fun getBatteryInSlot(): ItemStack? {
        if (this.isMaster) {
            return this.getItem(0)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            master.getBatteryInSlot()
        }

        return null
    }

    override fun getContainerSize(): Int {
        return HOLDSIZE + 1
    }

    open fun findNextTarget(): BlockVec3? {
        if (!this.targetPoints.isEmpty()) {
            val pos: BlockVec3? = this.targetPoints.removeFirst()
            this.setChanged()
            if (pos != null) {
                return pos.clone()
            }
        }

        //No more mining targets, the whole area is mined
        return null
    }

    private fun findTargetPoints() {
        this.targetPoints.clear()
        val posnTarget: BlockVec3 = BlockVec3(this)

        if (this.world.provider is WorldProviderAsteroids) {
            val roids: ArrayList<BlockVec3?>? = (this.world.provider as WorldProviderAsteroids).getClosestAsteroidsXZ(
                posnTarget.x,
                posnTarget.y,
                posnTarget.z,
                this.facing.getIndex(),
                100
            )
            if (roids != null && roids.size > 0) {
                this.targetPoints.addAll(roids)
                return
            }
        }

        posnTarget.modifyPositionFromSide(this.facing, this.world.rand.nextInt(16) + 32)
        var miny: Int = min(this.getPos().getY() * 2 - 90, this.getPos().getY() - 22)
        if (miny < 5) {
            miny = 5
        }
        posnTarget.y = miny + 5 + this.world.rand.nextInt(4)

        this.targetPoints.add(posnTarget)

        var lateral: EnumFacing = EnumFacing.NORTH
        val inLine: EnumFacing = this.facing
        if (inLine.getAxis() == EnumFacing.Axis.Z) {
            lateral = EnumFacing.WEST
        }

        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 13))
        this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -13))
        if (posnTarget.y > 17) {
            this.targetPoints.add(
                posnTarget.clone().modifyPositionFromSide(lateral, 7).modifyPositionFromSide(EnumFacing.DOWN, 11)
            )
            this.targetPoints.add(
                posnTarget.clone().modifyPositionFromSide(lateral, -7).modifyPositionFromSide(EnumFacing.DOWN, 11)
            )
        } else {
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, 26))
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(lateral, -26))
        }
        this.targetPoints.add(
            posnTarget.clone().modifyPositionFromSide(lateral, 7).modifyPositionFromSide(EnumFacing.UP, 11)
        )
        this.targetPoints.add(
            posnTarget.clone().modifyPositionFromSide(lateral, -7).modifyPositionFromSide(EnumFacing.UP, 11)
        )
        if (posnTarget.y < this.getPos().getY() - 38) {
            this.targetPoints.add(
                posnTarget.clone().modifyPositionFromSide(lateral, 13).modifyPositionFromSide(EnumFacing.UP, 22)
            )
            this.targetPoints.add(posnTarget.clone().modifyPositionFromSide(EnumFacing.UP, 22))
            this.targetPoints.add(
                posnTarget.clone().modifyPositionFromSide(lateral, -13).modifyPositionFromSide(EnumFacing.UP, 22)
            )
        }

        val s: Int = this.targetPoints.size
        for (i: Int in 0..<s) {
            this.targetPoints.add(
                this.targetPoints.get(i)!!.clone().modifyPositionFromSide(inLine, EntityAstroMiner.MINE_LENGTH + 6)
            )
        }

        this.setChanged()
        return
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        //Used to recall miner
        val master: TileEntityMinerBase?
        if (!this.isMaster) {
            master = this.getMaster()
            if (master == null) {
                return
            }
        } else {
            master = this
        }
        if (master.linkedMiner != null) {
            master.linkedMiner!!.recall()
        }
    }

    override fun getFront(): EnumFacing? {
        return this.facing
    }

    /**
     * This would normally be used by IMachineSides
     * but here it's overridden to get at the same facing packet for the MinerBase's own purposes
     * (IMachineSides won't be using it because as implemented
     * here, extending TileEntityElectricBlock, sides are not configurable)
     */
    override fun updateClient(data: MutableList<Any?>?) {
        val data1: Int = (data)!!.get(1) as Int
        this.facing = EnumFacing.from3DDataValue(data1 and 7)
        this.setMainBlockPos(
            BlockPos(
                ((data)!!.get(2) as kotlin.Int?)!!,
                ((data)!!.get(3) as kotlin.Int?)!!,
                ((data)!!.get(4) as kotlin.Int?)!!
            )
        )
        if (data1 > 7) {
            this.linkedMinerID = UUID.randomUUID()
        } else {
            this.linkedMinerID = null
        }
    }

    override fun getConfigurationType(): IMachineSidesProperties? {
        return IMachineSidesProperties.NOT_CONFIGURABLE
    }

    override fun listConfigurableSides(): Array<IMachineSides.MachineSide>? {
        return null
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face>? {
        return null
    }

    override fun getAllMachineSides(): Array<IMachineSides.MachineSidePack>? {
        return null
    }

    override fun setupMachineSides(length: Int) {
    }

    companion object {
        const val HOLDSIZE: Int = 72

        @JvmField
        var newMinerBases: MutableMap<Int, MutableList<BlockPos?>?> = HashMap<Int, MutableList<BlockPos?>?>()

        @JvmStatic
        fun checkNewMinerBases() {
            val entries: MutableIterator<MutableMap.MutableEntry<Int, MutableList<BlockPos>?>> =
                (newMinerBases.entries.iterator() as MutableIterator<MutableMap.MutableEntry<Int, MutableList<BlockPos>?>>)
            while (entries.hasNext()) {
                val entry: MutableMap.MutableEntry<Int, MutableList<BlockPos>?> = entries.next()
                if (entry.value!!.isEmpty()) continue

                val w: World? = WorldUtil.getWorldForDimensionServer(entry.key!!)
                if (w == null) {
                    GCLog.severe("Astro Miner Base placement: Unable to find server world for dim " + entry.key)
                    entries.remove()
                    continue
                }

                for (posMain: BlockPos in entry.value!!) {
                    val master: BlockPos = BlockPos(posMain)
                    for (x: Int in 0..1) {
                        for (y: Int in 0..1) {
                            for (z: Int in 0..1) {
                                val pos: BlockPos = posMain.offset(x, y, z)
                                w.setBlock(pos, (AsteroidBlocks.minerBaseFull)!!.defaultBlockState(), 2)
                                val tile: TileEntity? = w.getBlockEntity(pos)

                                if (tile is TileEntityMinerBase) {
                                    tile.setMainBlockPos(master)
                                    tile.updateClientFlag = true
                                }
                            }
                        }
                    }
                }

                entry.value!!.clear()
            }
        }

        @JvmStatic
        fun addNewMinerBase(dimID: Int, blockPos: BlockPos) {
            if (newMinerBases.containsKey(dimID)) {
                newMinerBases.get(dimID)!!.add(blockPos)
            } else {
                val blockPositions: MutableList<BlockPos?> = Lists.newArrayList<BlockPos?>()
                newMinerBases.put(dimID, blockPositions)
                blockPositions.add(blockPos)
            }
        }
    }
}
