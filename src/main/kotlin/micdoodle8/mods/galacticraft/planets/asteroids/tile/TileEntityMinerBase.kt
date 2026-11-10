package micdoodle8.mods.galacticraft.planets.asteroids.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField
import net.minecraft.util.EnumFacing.Axis

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
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
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
                    this.world.setBlockState(this.getPos(), (AsteroidBlocks.blockMinerBase)!!.getDefaultState(), 2)
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
            val tile: TileEntity? = world.getTileEntity(vecToAdd)
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
            val tileEntity: TileEntity? = this.world.getTileEntity(this.mainBlockPosition)

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
        this.facing = EnumFacing.byHorizontalIndex(nbt.getInteger("facing"))
        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.isMaster = nbt.getBoolean("isMaster")
            if (this.isMaster) {
                this.updateClientFlag = true
            } else {
                val tagCompound: NBTTagCompound = nbt.getCompoundTag("masterpos")
                if (tagCompound.getKeySet().isEmpty()) this.setMainBlockPosition(null)
                else {
                    this.setMainBlockPosition(
                        BlockPos(
                            tagCompound.getInteger("x"),
                            tagCompound.getInteger("y"),
                            tagCompound.getInteger("z")
                        )
                    )
                    this.updateClientFlag = true
                }
            }
            if (nbt.hasKey("LinkedUUIDMost", 4) && nbt.hasKey("LinkedUUIDLeast", 4)) {
                this.linkedMinerID = UUID(nbt.getLong("LinkedUUIDMost"), nbt.getLong("LinkedUUIDLeast"))
            } else {
                this.linkedMinerID = null
            }
            if (nbt.hasKey("TargetPoints")) {
                this.targetPoints.clear()
                val mpList: NBTTagList = nbt.getTagList("TargetPoints", 10)
                for (j: Int in 0..<mpList.tagCount()) {
                    val bvTag: NBTTagCompound = mpList.getCompoundTagAt(j)
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
        nbt.setBoolean("isMaster", this.isMaster)
        if (!this.isMaster && this.mainBlockPosition != null) {
            val masterTag: NBTTagCompound = NBTTagCompound()
            masterTag.setInteger("x", this.mainBlockPosition!!.getX())
            masterTag.setInteger("y", this.mainBlockPosition!!.getY())
            masterTag.setInteger("z", this.mainBlockPosition!!.getZ())
            nbt.setTag("masterpos", masterTag)
        }
        nbt.setInteger("facing", this.facing.getHorizontalIndex())
        if (this.isMaster && this.linkedMinerID != null) {
            nbt.setLong("LinkedUUIDMost", this.linkedMinerID!!.getMostSignificantBits())
            nbt.setLong("LinkedUUIDLeast", this.linkedMinerID!!.getLeastSignificantBits())
        }
        val mpList: NBTTagList = NBTTagList()
        for (j: Int in this.targetPoints.indices) {
            mpList.appendTag(this.targetPoints.get(j)!!.writeToNBT(NBTTagCompound()))
        }
        nbt.setTag("TargetPoints", mpList)
        return nbt
    }

    override fun getInventoryStackLimit(): Int {
        return 64
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.getPos()) === this && par1EntityPlayer.getDistanceSq(
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
        val invSize: Int = this.getSizeInventory()

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

        this.markDirty()
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

    override fun getName(): String {
        return GCCoreUtil.translate("tile.miner_base.name")
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
        this.markDirty()
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
            val holding: ItemStack = (entityPlayer)!!.getActiveItemStack()
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
    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(2, 2, 2))
        }
        return this.renderAABB!!
    }

    @SideOnly(Side.CLIENT)
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

        for (facing: EnumFacing in EnumFacing.VALUES) {
            val offset: BlockPos = this.getPos().offset(facing)
            val tileOffset: TileEntity? = this.world.getTileEntity(offset)
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

        this.markDirty()
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
        this.markDirty()
    }

    open fun unlinkMiner() {
        this.linkedMiner = null
        this.linkedMinerID = null
        this.updateClientFlag = true
        this.markDirty()
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

    override fun canInsertItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        return false
    }

    override fun canExtractItem(slotID: Int, itemstack: ItemStack, side: EnumFacing): Boolean {
        if (this.isMaster) {
            if (side != this.facing) {
                return slotID > 0 || ItemElectricBase.isElectricItemEmpty(itemstack)
            }

            return false
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.canExtractItem(slotID, itemstack, side)
        }

        return false
    }

    override fun isItemValidForSlot(slotID: Int, itemstack: ItemStack): Boolean {
        if (this.isMaster) {
            return slotID > 0 || ItemElectricBase.isElectricItem(itemstack.getItem())
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.isItemValidForSlot(slotID, itemstack)
        }

        return false
    }

    override fun getStackInSlot(par1: Int): ItemStack {
        if (this.isMaster) {
            return super.getStackInSlot(par1)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.getStackInSlot(par1)
        }

        return ItemStack.EMPTY
    }

    override fun decrStackSize(par1: Int, par2: Int): ItemStack {
        if (this.isMaster) {
            return super.decrStackSize(par1, par2)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.decrStackSize(par1, par2)
        }

        return ItemStack.EMPTY
    }

    override fun removeStackFromSlot(par1: Int): ItemStack {
        if (this.isMaster) {
            return super.removeStackFromSlot(par1)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            return master.removeStackFromSlot(par1)
        }

        return ItemStack.EMPTY
    }

    override fun setInventorySlotContents(par1: Int, par2ItemStack: ItemStack) {
        if (this.isMaster) {
            super.setInventorySlotContents(par1, par2ItemStack)
            return
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            master.setInventorySlotContents(par1, par2ItemStack)
        }

        return
    }

    override fun getBatteryInSlot(): ItemStack? {
        if (this.isMaster) {
            return this.getStackInSlot(0)
        }
        val master: TileEntityMinerBase? = this.getMaster()
        if (master != null) {
            master.getBatteryInSlot()
        }

        return null
    }

    override fun getSizeInventory(): Int {
        return HOLDSIZE + 1
    }

    open fun findNextTarget(): BlockVec3? {
        if (!this.targetPoints.isEmpty()) {
            val pos: BlockVec3? = this.targetPoints.removeFirst()
            this.markDirty()
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

        this.markDirty()
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
        this.facing = EnumFacing.byIndex(data1 and 7)
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
                                val pos: BlockPos = posMain.add(x, y, z)
                                w.setBlockState(pos, (AsteroidBlocks.minerBaseFull)!!.getDefaultState(), 2)
                                val tile: TileEntity? = w.getTileEntity(pos)

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
