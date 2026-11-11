package micdoodle8.mods.galacticraft.planets.asteroids.entities

import net.minecraft.network.chat.Component

import net.minecraft.world.ContainerHelper
import foundation.block.getMetaFromState

import foundation.level.offset

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFlower
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockReed

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav
import micdoodle8.mods.galacticraft.api.entity.IEntityNoisy
import micdoodle8.mods.galacticraft.api.entity.ITelemetry
import micdoodle8.mods.galacticraft.api.vector.BlockTuple
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.*
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.client.sounds.SoundUpdaterMiner
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.tick.AsteroidsTickHandlerServer
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.resources.sounds.SoundInstance as ISound
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.client.resources.sounds.TickableSoundInstance as ITickable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper

import net.minecraft.world.level.Level as World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fluids.IFluidBlock
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.IntArray
import kotlin.String
import kotlin.arrayOf
import kotlin.math.abs
import kotlin.math.max
import kotlin.times
import kotlin.toString

open class EntityAstroMiner constructor(world: World) : Entity(world), IInventoryDefaults, IPacketReceiver,
    IEntityNoisy, IAntiGrav, ITelemetry {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<Entity>.getDisplayName()

    private val TEMPDEBUG = false
    private val TEMPFAST = false

    @JvmField
    var stacks: NonNullList<ItemStack>? = null

    @JvmField
    var energyLevel: Int = 0

    @JvmField
    var mineCount: Int = 0

    @JvmField
    var targetYaw: Float = 0f

    @JvmField
    var targetPitch: Float = 0f

    @JvmField
    var AIstate: Int = 0

    @JvmField
    var timeInCurrentState: Int = 0

    @JvmField
    var playerMP: EntityPlayerMP? = null
    private var playerUUID: UUID? = null
    private var posTarget: BlockVec3? = null
    private var posBase: BlockVec3? = null
    private var waypointBase: BlockVec3? = null
    private val wayPoints = LinkedList<BlockVec3?>()
    private val minePoints = LinkedList<BlockVec3?>()
    private var minePointCurrent: BlockVec3? = null
    private var baseFacing: EnumFacing? = null

    @JvmField
    var facing: EnumFacing
    private var facingAI: EnumFacing? = null
    private var lastFacing: EnumFacing? = null
    private val baseSafeRadius = 32
    private val speedbase = if (TEMPFAST) 0.16 else 0.022
    private var speed = speedbase
    private val rotSpeedBase = if (TEMPFAST) 8f else 1.5f
    private var rotSpeed = rotSpeedBase
    private var speedup: Double = SPEEDUP
    private var noSpeedup = false //This stops the miner getting stuck at turning points

    @JvmField
    var shipDamage: Float = 0f

    @JvmField
    var currentDamage: Int = 0

    @JvmField
    var timeSinceHit: Int = 0
    private var flagLink = false
    private var flagCheckPlayer = false
    private var toAddToServer = AsteroidsTickHandlerServer.loadingSavedChunks.get()

    //To do:
    //   break the entity drops it as an item
    private var turnProgress = 0
    private var minecartX = 0.0
    private var minecartY = 0.0
    private var minecartZ = 0.0
    private var minecartYaw = 0.0
    private var minecartPitch = 0.0

    @OnlyIn(Dist.CLIENT)
    private var velocityX = 0.0

    @OnlyIn(Dist.CLIENT)
    private var velocityY = 0.0

    @OnlyIn(Dist.CLIENT)
    private var velocityZ = 0.0

    private var tryBlockLimit = 0
    private var inventoryDrops = 0

    @JvmField
    var stopForTurn: Boolean = false

    private var givenFailMessage = 0
    private var mineLast: BlockVec3? = null
    private var mineCountDown = 0
    private var pathBlockedCount = 0

    @JvmField
    var laserBlocks: LinkedList<BlockVec3?> = LinkedList<BlockVec3?>()

    @JvmField
    var laserTimes: LinkedList<Int> = LinkedList<Int>()

    @JvmField
    var retraction: Float = 1f

    @JvmField
    protected var soundUpdater: ITickable? = null
    private var soundToStop = false
    private var spawnedInCreative = false
    private var serverIndex = 0

    constructor(world: World, cargo: NonNullList<ItemStack>, energy: Int) : this(world) {
        this.toAddToServer = true
        this.stacks = cargo
        this.energyLevel = energy
    }

    init {
        this.facing = EnumFacing.NORTH
        this.preventEntitySpawning = true
        this.ignoreFrustumCheck = true
        this.isImmuneToFire = true
        this.width = cLENGTH
        this.height = cWIDTH
        this.setSize(cLENGTH, cWIDTH)
        //        this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
//        this.dataManager.addObject(this.currentDamage, new Integer(0));
//        this.dataManager.addObject(this.timeSinceHit, new Integer(0));
        this.noClip = true

        if (world != null && world.isClientSide) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d0: Double = this.getEntityBoundingBox().getAverageEdgeLength()

        if (java.lang.Double.isNaN(d0)) {
            d0 = 1.0
        }

        d0 = d0 * 64.0 * 5.0
        return distance < d0 * d0
    }

    override fun entityInit() {
        this.dataManager.register<Float>(DAMAGE, 0.0f)
    }

    override fun getContainerSize(): Int {
        return this.stacks!!.size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks!!.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks!!.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks!!) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return Component.translatable("entity.astro_miner.name")
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(var1: EntityPlayer): Boolean {
        return !this.isDead && var1.getDistanceSq(this) <= 64.0
    }

    override fun setChanged() {
    }

    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    private fun emptyInventory(minerBase: TileEntityMinerBase): Boolean {
        val doneOne: Boolean = false
        for (i: Int in this.stacks!!.indices) {
            val stack: ItemStack = this.stacks!!.get(i)
            if (stack.isEmpty()) {
                continue
            }
            val sizeprev: Int = stack.getCount()
            minerBase.addToInventory(stack)
            if (stack.isEmpty()) {
                this.stacks!!.set(i, ItemStack.EMPTY)
                this.setChanged()
                return true
            } else if (stack.getCount() < sizeprev) {
                this.stacks!!.set(i, stack)
                this.setChanged()
                //Something was transferred although some stacks remaining
                return true
            }
        }
        //No stacks were transferred
        return false
    }

    override fun onUpdate() {
        if (this.posY < -64.0) {
            this.setDead()
            return
        }

        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f)
        }

        stopForTurn = !this.checkRotation()
        this.facing = this.getFacingFromRotation()
        this.setBoundingBoxForFacing()

        if (this.world.isRemote) {
            //CLIENT CODE
            if (this.turnProgress == 0) {
                this.turnProgress++
                if (this.AIstate < AISTATE_TRAVELLING) {
                    //It should be stationary, so this deals with the spooky movement (due to minor differences between server and client position)
                    this.posX = this.minecartX
                    this.posY = this.minecartY
                    this.posZ = this.minecartZ
                } else {
                    val diffX: kotlin.Double = this.minecartX - this.posX
                    val diffY: kotlin.Double = this.minecartY - this.posY
                    val diffZ: kotlin.Double = this.minecartZ - this.posZ
                    if (abs(diffX) > 1.0 || abs(diffY) > 1.0 || abs(diffZ) > 1.0) {
                        this.posX = this.minecartX
                        this.posY = this.minecartY
                        this.posZ = this.minecartZ
                    } else {
                        if (abs(diffX) > abs(this.motionX)) {
                            this.motionX += diffX / 10.0
                        }
                        if (abs(diffY) > abs(this.motionY)) {
                            this.motionY += diffY / 10.0
                        }
                        if (abs(diffZ) > abs(this.motionZ)) {
                            this.motionZ += diffZ / 10.0
                        }
                    }
                }
            }
            this.posX += this.motionX
            this.posY += this.motionY
            this.posZ += this.motionZ
            setEntityBoundingBox(getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ))
            this.setRotation(this.rotationYaw, this.rotationPitch)
            if (this.AIstate == AISTATE_MINING && this.ticksExisted % 2 == 0) {
                this.prepareMoveClient(if (TEMPFAST) 8 else 1, 2)
            }

            //Sound updates on client
            if (this.AIstate < AISTATE_ATBASE) {
                this.stopRocketSound()
            }
            return
        }

        if (this.toAddToServer) {
            this.toAddToServer = false
            this.serverIndex = AsteroidsTickHandlerServer.monitorMiner(this)
        }

        //SERVER CODE
        if (this.ticksExisted % 10 == 0 || this.flagLink) {
            this.flagLink = false
            this.checkPlayer()
            if (posBase!!.blockExists(this.world)) {
                val tileEntity: TileEntity? = posBase!!.getTileEntity(this.world)
                if (tileEntity is TileEntityMinerBase && tileEntity.isMaster && !tileEntity.isInvalid()) {
                    //Create link with base on loading the EntityAstroMiner
                    val linker: UUID? = tileEntity.getLinkedMiner()
                    if (this.getUniqueID() != linker) {
                        if (linker == null) {
                            tileEntity.linkMiner(this)
                        } else {
                            this.freeze(FAIL_ANOTHERWASLINKED)
                            return
                        }
                    } else if (tileEntity.linkedMiner !== this) {
                        tileEntity.linkMiner(this)
                    }
                } else {
                    if (this.playerMP != null && (this.givenFailMessage and (1 shl FAIL_BASEDESTROYED)) == 0) {
                        this.playerMP!!.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.message.astro_miner" + FAIL_BASEDESTROYED + ".fail")))
                        this.givenFailMessage += (1 shl FAIL_BASEDESTROYED)
                        //Continue mining even though base was destroyed - maybe it will be replaced
                    }
                }
            }
        } else if (this.flagCheckPlayer) {
            this.checkPlayer()
        }

        if (this.playerMP == null) {
            //Go into dormant state if player is offline
            //but do not actually set the dormant state on the server, so can resume immediately if player comes online
            if (this.motionX != 0.0 || this.motionY != 0.0 || this.motionZ != 0.0) {
                this.motionX = 0.0
                this.motionY = 0.0
                this.motionZ = 0.0
                (GalacticraftCore.packetPipeline)!!.sendToDimension(
                    PacketDynamic(this),
                    GCCoreUtil.getDimensionID(this.world)
                )
            }
            return
        }

        if (this.lastFacing != this.facingAI) {
            this.lastFacing = this.facingAI
            this.prepareMove(12, 0)
            this.prepareMove(12, 1)
            this.prepareMove(12, 2)
        }

        this.lastTickPosX = this.posX
        this.lastTickPosY = this.posY
        this.lastTickPosZ = this.posZ
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        this.prevRotationPitch = this.rotationPitch
        this.prevRotationYaw = this.rotationYaw

        if (this.AIstate > AISTATE_ATBASE) {
            if (this.energyLevel <= 0) {
                this.freeze(FAIL_OUTOFENERGY)
            } else if (this.world.provider !is WorldProviderAsteroids && this.ticksExisted % 2 == 0) {
                this.energyLevel--
            }
            //No energy consumption when moving in space in Asteroids dimension (this reduces the risk of the Astro Miner becoming stranded!)
        }

        when (this.AIstate) {
            AISTATE_STUCK ->             //TODO blinking distress light or something?
                //Attempt to re-start every 30 seconds or so
                if (this.ticksExisted % 600 == 0) {
                    if ((this.givenFailMessage and 8) > 0) {
                        //The base was destroyed - see if it has been replaced?
                        this.atBase()
                    } else {
                        //See if the return path has been unblocked, and give a small amount of backup energy to try to get home
                        this.AIstate = AISTATE_RETURNING
                        if (this.energyLevel <= 0) {
                            this.energyLevel = 20
                        }
                    }
                }

            AISTATE_ATBASE -> this.atBase()
            AISTATE_TRAVELLING -> if (!this.moveToTarget()) {
                this.prepareMove(if (TEMPFAST) 8 else 2, 2)
            }

            AISTATE_MINING -> if (!this.doMining() && this.ticksExisted % 2 == 0) {
                this.energyLevel--
                this.prepareMove(if (TEMPFAST) 8 else 1, 2)
            }

            AISTATE_RETURNING -> {
                this.moveToBase()
                this.prepareMove(if (TEMPFAST) 8 else 4, 1)
            }

            AISTATE_DOCKING -> if (this.waypointBase != null) {
                this.speed = speedbase / 1.6
                this.rotSpeed = rotSpeedBase / 1.6f
                if (this.moveToPos(this.waypointBase!!, true)) {
                    this.AIstate = AISTATE_ATBASE
                    this.motionX = 0.0
                    this.motionY = 0.0
                    this.motionZ = 0.0
                    this.speed = speedbase
                    this.rotSpeed = rotSpeedBase
                }
            } else {
                GCLog.severe("AstroMiner missing base position: this is a bug.")
                this.AIstate = AISTATE_STUCK
            }
        }

        (GalacticraftCore.packetPipeline)!!.sendToDimension(PacketDynamic(this), GCCoreUtil.getDimensionID(this.world))

        this.posX += this.motionX
        this.posY += this.motionY
        this.posZ += this.motionZ
        setEntityBoundingBox(getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ))

        /*        if (this.dataManager.get(this.timeSinceHit) > 0)
        {
            this.dataManager.set(this.timeSinceHit, Integer.valueOf(this.dataManager.get(this.timeSinceHit) - 1));
        }

        if (this.dataManager.get(this.currentDamage) > 0)
        {
            this.dataManager.set(this.currentDamage, Integer.valueOf(this.dataManager.get(this.currentDamage) - 1));
        }       
*/
    }

    private fun checkPlayer() {
        if (this.playerMP == null) {
            if (this.playerUUID != null) {
                this.playerMP = PlayerUtil.getPlayerByUUID(this.playerUUID)
            }
        } else {
            if (!PlayerUtil.isPlayerOnline((this.playerMP)!!)) {
                this.playerMP = null
            }
        }
    }

    private fun freeze(i: Int) {
        this.AIstate = AISTATE_STUCK
        this.motionX = 0.0
        this.motionY = 0.0
        this.motionZ = 0.0
        if (this.playerMP != null && (this.givenFailMessage and (1 shl i)) == 0) {
            this.playerMP!!.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.message.astro_miner" + i + ".fail")))
            this.givenFailMessage += (1 shl i)
        }
    }

    //packet with AIstate, energy, rotationP + Y, mining data count
    override fun decodePacketdata(buffer: ByteBuf?) {
        this.AIstate = (buffer)!!.readInt()
        this.energyLevel = (buffer)!!.readInt()
        this.targetPitch = (buffer)!!.readFloat()
        this.targetYaw = (buffer)!!.readFloat()
        this.mineCount = (buffer)!!.readInt()
        val x: Int = (buffer)!!.readInt()
        val y: Int = (buffer)!!.readInt()
        val z: Int = (buffer)!!.readInt()
        val pos: BlockPos = BlockPos(x, y, z)
        if (this.world.isBlockLoaded(pos)) {
            val tile: TileEntity? = this.world.getBlockEntity(pos)
            if (tile is TileEntityMinerBase) {
                tile.linkedMiner = this
                tile.linkCountDown = 20
            }
        }
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }
        (list)!!.add(if (this.playerMP == null) AISTATE_OFFLINE else this.AIstate)
        (list)!!.add(this.energyLevel)
        (list)!!.add(this.targetPitch)
        (list)!!.add(this.targetYaw)
        (list)!!.add(this.mineCount)
        (list)!!.add(this.posBase!!.x)
        (list)!!.add(this.posBase!!.y)
        (list)!!.add(this.posBase!!.z)
    }

    open fun recall() {
        if (this.AIstate > AISTATE_ATBASE && this.AIstate < AISTATE_RETURNING) {
            AIstate = AISTATE_RETURNING
            this.pathBlockedCount = 0
        }
    }

    private fun getFacingFromRotation(): EnumFacing {
        if (this.rotationPitch > 45f) {
            return EnumFacing.UP
        }
        if (this.rotationPitch < -45f) {
            return EnumFacing.DOWN
        }
        val rY: Float = this.rotationYaw % 360f
        //rotationYaw 5 90 4 270 2 180 3 0
        if (rY < 45f || rY > 315f) {
            return EnumFacing.SOUTH
        }
        if (rY < 135f) {
            return EnumFacing.EAST
        }
        if (rY < 225f) {
            return EnumFacing.NORTH
        }
        return EnumFacing.WEST
    }

    private fun atBase() {
        val tileEntity: TileEntity? = posBase!!.getTileEntity(this.world)

        if ((tileEntity !is TileEntityMinerBase) || tileEntity.isInvalid() || !tileEntity.isMaster) {
            this.freeze(FAIL_BASEDESTROYED)
            return
        }

        val minerBase: TileEntityMinerBase = tileEntity
        //If it's successfully reached its base, clear all fail messages except number 6, which is that all mining areas are finished (see below)
        this.givenFailMessage = this.givenFailMessage and 64
        this.wayPoints.clear()

        var somethingTransferred: Boolean = true
        if (this.ticksExisted % 5 == 0) {
            somethingTransferred = this.emptyInventory(minerBase)
        }
        this.inventoryDrops = 0

        // Recharge
        if (minerBase.hasEnoughEnergyToRun && this.energyLevel < MAXENERGY) {
            this.energyLevel += 16
            minerBase.storage.extractEnergyGC(minerBase.storage.getMaxExtract(), false)
        }

        // When fully charged, set off again
        if (this.energyLevel >= MAXENERGY && !somethingTransferred && this.hasHoldSpace()) {
            this.energyLevel = MAXENERGY
            if (this.findNextTarget(minerBase)) {
                this.AIstate = AISTATE_TRAVELLING
                this.wayPoints.add(this.waypointBase!!.clone())
                this.mineCount = 0
            } else {
                if (this.playerMP != null && (this.givenFailMessage and 64) == 0) {
                    this.playerMP!!.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.message.astro_miner6.fail")))
                    this.givenFailMessage += 64
                }
            }
        }
    }

    private fun hasHoldSpace(): Boolean {
        for (i: Int in 0..<this.getContainerSize()) {
            if (this.stacks!!.get(i).isEmpty()) {
                return true
            }
        }
        return false
    }

    private fun findNextTarget(minerBase: TileEntityMinerBase): Boolean {
        //If mining has finished, or path has been blocked two or more times, try mining elsewhere
        if (!this.minePoints.isEmpty() && this.pathBlockedCount < 2) {
            this.posTarget = this.minePoints.getFirst()!!.clone()
            GCLog.debug("Still mining at: " + posTarget.toString() + " Remaining shafts: " + this.minePoints.size)
            return true
        }

        // Target is completely mined: change target
        this.posTarget = minerBase.findNextTarget()
        this.pathBlockedCount = 0

        //No more mining targets, the whole area is mined
        if (this.posTarget == null) {
            return false
        }

        GCLog.debug("Miner target: " + posTarget.toString())

        return true
    }

    /**
     * @return True if reached a turning point
     */
    private fun moveToTarget(): Boolean {
        if (this.energyLevel < RETURNENERGY || this.inventoryDrops > RETURNDROPS) {
            AIstate = AISTATE_RETURNING
            this.pathBlockedCount = 0
            return true
        }

        if (this.posTarget == null) {
            GCLog.severe("AstroMiner missing target: this is a bug.")
            AIstate = AISTATE_STUCK
            return true
        }

        if (this.moveToPos(this.posTarget!!, false)) {
            AIstate = AISTATE_MINING
            wayPoints.add(this.posTarget!!.clone())
            this.setMinePoints()
            return true
        }

        return false
    }

    private fun moveToBase() {
        if (this.wayPoints.size == 0) {
            //When it gets there: stop and reverse in!
            AIstate = AISTATE_DOCKING
            if (this.waypointBase != null) {
                //Teleport back to base in case of any serious problem
                this.setPosition(
                    this.waypointBase!!.x.toDouble(),
                    this.waypointBase!!.y.toDouble(),
                    this.waypointBase!!.z.toDouble()
                )
                this.facingAI = this.baseFacing
            }
            return
        }

        if (this.moveToPos(this.wayPoints.getLast()!!, true)) {
            this.wayPoints.removeLast()
        }
    }

    private fun setMinePoints() {
        //Still some areas left to mine from last visit (maybe it was full or out of power?)
        if (this.minePoints.size > 0) {
            return
        }

        val inFront: BlockVec3 = BlockVec3(
            MathHelper.floor(this.posX + 0.5),
            MathHelper.floor(this.posY + 1.5),
            MathHelper.floor(this.posZ + 0.5)
        )
        var otherEnd: Int = if (this.world.provider is WorldProviderAsteroids) MINE_LENGTH_AST else MINE_LENGTH
        if (this.baseFacing == EnumFacing.NORTH || this.baseFacing == EnumFacing.WEST) {
            otherEnd = -otherEnd
        }
        when (this.baseFacing) {
            EnumFacing.NORTH, EnumFacing.SOUTH -> {
                this.minePoints.add(inFront.clone().translate(0, 0, otherEnd))
                this.minePoints.add(inFront.clone().translate(4, 0, otherEnd))
                this.minePoints.add(inFront.clone().translate(4, 0, 0))
                this.minePoints.add(inFront.clone().translate(2, 3, 0))
                this.minePoints.add(inFront.clone().translate(2, 3, otherEnd))
                this.minePoints.add(inFront.clone().translate(-2, 3, otherEnd))
                this.minePoints.add(inFront.clone().translate(-2, 3, 0))
                this.minePoints.add(inFront.clone().translate(-4, 0, 0))
                this.minePoints.add(inFront.clone().translate(-4, 0, otherEnd))
                this.minePoints.add(inFront.clone().translate(-2, -3, otherEnd))
                this.minePoints.add(inFront.clone().translate(-2, -3, 0))
                this.minePoints.add(inFront.clone().translate(2, -3, 0))
                this.minePoints.add(inFront.clone().translate(2, -3, otherEnd))
                this.minePoints.add(inFront.clone().translate(0, 0, otherEnd))
            }

            EnumFacing.WEST, EnumFacing.EAST -> {
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 0))
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 4))
                this.minePoints.add(inFront.clone().translate(0, 0, 4))
                this.minePoints.add(inFront.clone().translate(0, 3, 2))
                this.minePoints.add(inFront.clone().translate(otherEnd, 3, 2))
                this.minePoints.add(inFront.clone().translate(otherEnd, 3, -2))
                this.minePoints.add(inFront.clone().translate(0, 3, -2))
                this.minePoints.add(inFront.clone().translate(0, 0, -4))
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, -4))
                this.minePoints.add(inFront.clone().translate(otherEnd, -3, -2))
                this.minePoints.add(inFront.clone().translate(0, -3, -2))
                this.minePoints.add(inFront.clone().translate(0, -3, 2))
                this.minePoints.add(inFront.clone().translate(otherEnd, -3, 2))
                this.minePoints.add(inFront.clone().translate(otherEnd, 0, 0))
            }
        
else -> {}
}
    }

    /**
     * @return True if reached a turning point
     */
    private fun doMining(): Boolean {
        if (this.energyLevel < RETURNENERGY || this.inventoryDrops > RETURNDROPS || this.minePoints.size == 0) {
            if (this.minePoints.size > 0 && this.minePointCurrent != null) {
                this.minePoints.addFirst(this.minePointCurrent)
            }
            AIstate = AISTATE_RETURNING
            this.pathBlockedCount = 0
            GCLog.debug("Miner going home: " + this.posBase.toString() + " " + this.minePoints.size + " shafts still to be mined")
            return true
        }

        if (this.moveToPos(this.minePoints.getFirst()!!, false)) {
            this.minePointCurrent = this.minePoints.removeFirst()
            GCLog.debug("Miner mid mining: " + this.minePointCurrent.toString() + " " + this.minePoints.size + " shafts still to be mined")
            return true
        }
        return false
    }

    private fun tryBackIn() {
        if (this.waypointBase!!.distanceSquared(BlockVec3(this)) <= 9.1) {
            this.AIstate = AISTATE_DOCKING
            when (this.baseFacing) {
                EnumFacing.NORTH -> this.targetYaw = 180f
                EnumFacing.SOUTH -> this.targetYaw = 0f
                EnumFacing.WEST -> this.targetYaw = 270f
                EnumFacing.EAST -> this.targetYaw = 90f
            
else -> {}
}
        } else {
            this.freeze(FAIL_RETURNPATHBLOCKED)
        }
    }


    /**
     * Mine out the area in front of the miner (dist blocks from miner centre)
     *
     * @param limit Maximum block count to be mined this tick
     * @param dist
     * @return True if the mining failed (meaning the miner's path is blocked)
     */
    private fun prepareMove(limit: Int, dist: Int): Boolean {
        var dist: Int = dist
        if (this.mineCountDown > 0) {
            this.mineCountDown--
            return false
        }
        val inFront: BlockVec3 = BlockVec3(
            MathHelper.floor(this.posX + 0.5),
            MathHelper.floor(this.posY + 1.5),
            MathHelper.floor(this.posZ + 0.5)
        )
        if (dist == 2) {
            inFront.translate((headings2[this.facingAI!!.getIndex()])!!)
        } else {
            if ((this.facingAI!!.getIndex() and 1) == EnumFacing.DOWN.getIndex()) {
                dist++
            }
            if (dist > 0) {
                inFront.translate(headings[this.facingAI!!.getIndex()]!!.clone().scale(dist))
            }
        }

        if (inFront != this.mineLast && this.AIstate != AISTATE_ATBASE) {
            this.mineCountDown = 3
            this.mineLast = inFront
            return false
        }

        val x: Int = inFront.x
        val y: Int = inFront.y
        val z: Int = inFront.z

        //Test not trying to mine own dock!
        if (y == this.waypointBase!!.y && x == this.waypointBase!!.x - (if (this.baseFacing == EnumFacing.EAST) 1 else 0) && z == this.waypointBase!!.z - (if (this.baseFacing == EnumFacing.SOUTH) 1 else 0)) {
            this.tryBackIn()
            return false
        }
        var wayBarred: Boolean = false
        this.tryBlockLimit = limit

        //Check not obstructed by something immovable e.g. bedrock
        //Mine out the 12 blocks in front of it in direction of travel when getting close
        //There are 12 blocks around ... and 12 in front.  One block per tick?
        //(That means can move at 5/6 block per second when mining, and 1.67 bps when traveling)
        val pos: BlockPos = BlockPos(x, y, z)
        when (EnumFacing.from3DDataValue(this.facingAI!!.getIndex() and 6)) {
            EnumFacing.DOWN -> {
                if (tryMineBlock(pos)) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(1, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(1, 0, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, -2))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 0, -2))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 0, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-2, 0, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-2, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 0, 1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, 1))) {
                    wayBarred = true
                }
            }

            EnumFacing.NORTH -> {
                if (tryMineBlock(pos.offset(0, -2, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, -2, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, -1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(1, -1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-2, -1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(1, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-2, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos)) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 0, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(-1, 1, 0))) {
                    wayBarred = true
                }
            }

            EnumFacing.WEST -> {
                if (tryMineBlock(pos.offset(0, -2, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -1, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -1, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -1, +1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -1, -2))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, 1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, -2))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 0, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, -2, 0))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 1, -1))) {
                    wayBarred = true
                }
                if (tryMineBlock(pos)) {
                    wayBarred = true
                }
                if (tryMineBlock(pos.offset(0, 1, 0))) {
                    wayBarred = true
                }
            }
        
else -> {}
}

        //If it is obstructed, return to base, or stand still if that is impossible
        if (wayBarred) {
            if (this.playerMP != null && blockingBlock.block !== Blocks.AIR) {
                if (blockingBlock.block === Blocks.STONE) {
                    this.playerMP!!.sendSystemMessage(
                        Component.literal(
                            GCCoreUtil.translate("gui.message.astro_miner1_a.fail") + " " + GCCoreUtil.translate(
                                "gui.message.astro_miner1_b.fail"
                            )
                        )
                    )
                } else {
                    this.playerMP!!.sendSystemMessage(
                        Component.literal(
                            GCCoreUtil.translate("gui.message.astro_miner1_a.fail") + " " + GCCoreUtil.translate(
                                blockingBlock.toString()
                            )
                        )
                    )
                }
            }
            this.motionX = 0.0
            this.motionY = 0.0
            this.motionZ = 0.0
            this.tryBlockLimit = 0
            if (this.AIstate == AISTATE_TRAVELLING) {
                this.AIstate = AISTATE_RETURNING
            } else if (AIstate == AISTATE_MINING) {
                this.pathBlockedCount++
                this.AIstate = AISTATE_RETURNING
            } else if (this.AIstate == AISTATE_RETURNING) {
                this.tryBackIn()
            } else {
                this.freeze(FAIL_RETURNPATHBLOCKED)
            }
            blockingBlock = BlockTuple(Blocks.AIR, 0)
        }

        if (this.tryBlockLimit == limit && !this.noSpeedup) {
            this.motionX *= this.speedup
            this.motionY *= this.speedup
            this.motionZ *= this.speedup
        }

        return wayBarred
    }

    private fun prepareMoveClient(limit: Int, dist: Int): Boolean {
        var dist: Int = dist
        val inFront: BlockVec3 = BlockVec3(
            MathHelper.floor(this.posX + 0.5),
            MathHelper.floor(this.posY + 1.5),
            MathHelper.floor(this.posZ + 0.5)
        )
        if (dist == 2) {
            inFront.translate((headings2[this.facing.getIndex()])!!)
        } else {
            if ((this.facing.getIndex() and 1) == EnumFacing.DOWN.getIndex()) {
                dist++
            }
            if (dist > 0) {
                inFront.translate(headings[this.facing.getIndex()]!!.clone().scale(dist))
            }
        }
        if (inFront == this.mineLast) {
            return false
        }

        val x: Int = inFront.x
        val y: Int = inFront.y
        val z: Int = inFront.z

        var wayBarred: Boolean = false
        this.tryBlockLimit = limit

        //Check not obstructed by something immovable e.g. bedrock
        //Mine out the 12 blocks in front of it in direction of travel when getting close
        //There are 12 blocks around ... and 12 in front.  One block per tick?
        //(That means can move at 5/6 block per second when mining, and 1.67 bps when traveling)
        val pos: BlockPos = BlockPos(x, y, z)
        when (EnumFacing.from3DDataValue(this.facing.getIndex() and 6)) {
            EnumFacing.DOWN -> {
                if (tryBlockClient(pos)) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(1, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(1, 0, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, -2))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 0, -2))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 0, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-2, 0, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-2, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 0, 1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, 1))) {
                    wayBarred = true
                }
            }

            EnumFacing.NORTH -> {
                if (tryBlockClient(pos.offset(0, -2, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, -2, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, -1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(1, -1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-2, -1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(1, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-2, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos)) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 0, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(-1, 1, 0))) {
                    wayBarred = true
                }
            }

            EnumFacing.WEST -> {
                if (tryBlockClient(pos.offset(0, -2, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -1, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -1, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -1, +1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -1, -2))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, 1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, -2))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 0, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, -2, 0))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 1, -1))) {
                    wayBarred = true
                }
                if (tryBlockClient(pos)) {
                    wayBarred = true
                }
                if (tryBlockClient(pos.offset(0, 1, 0))) {
                    wayBarred = true
                }
            }
        
else -> {}
}

        //If it is obstructed, return to base, or stand still if that is impossible
        if (wayBarred) {
            this.tryBlockLimit = 0
        }

        if (this.tryBlockLimit == limit) {
            this.mineLast = inFront
        }

        return wayBarred
    }

    private fun tryMineBlock(pos: BlockPos): Boolean {
        //Check things to avoid in front of it (see static list for list) including base type things
        //Can move through liquids including flowing lava
        val state: IBlockState = this.world.getBlockState(pos)
        val b: Block = state.getBlock()
        if (b.getMaterial(state) === Material.AIR) {
            return false
        }
        if (noMineList.contains(b)) {
            blockingBlock.block = b
            blockingBlock.meta = b.getMetaFromState(state)
            return true
        }
        if (b is BlockLiquid) {
            if ((b === Blocks.LAVA || b === Blocks.FLOWING_LAVA) && state.getValue(BlockLiquid.LEVEL) == 0 && this.AIstate != AISTATE_RETURNING) {
                blockingBlock.block = Blocks.LAVA
                blockingBlock.meta = 0
                return true
            }
            return false
        }
        if (b is IFluidBlock) {
            return false
        }

        var gtFlag: Boolean = false
        if (b !== GCBlocks.fallenMeteor) {
            if (b is IPlantable && b !== Blocks.TALLGRASS && b !== Blocks.DEADBUSH && b !== Blocks.DOUBLE_PLANT && b !== Blocks.WATERLILY && (b !is BlockFlower) && b !== Blocks.REEDS) {
                blockingBlock.block = b
                blockingBlock.meta = b.getMetaFromState(state)
                return true
            }
            val meta: Int = b.getMetaFromState(state)
            if (b.getBlockHardness(state, this.world, pos) < 0) {
                blockingBlock.block = b
                blockingBlock.meta = meta
                return true
            }
            if (b.hasTileEntity(state)) {
                if (CompatibilityManager.isGTLoaded() && gregTechCheck(b)) {
                    gtFlag = true
                } else {
                    blockingBlock.block = b
                    blockingBlock.meta = meta
                    return true
                }
            }
        }

        if (this.tryBlockLimit == 0) {
            return false
        }
        val result: Int = ForgeHooks.onBlockBreakEvent(
            this.world,
            this.playerMP!!.interactionManager.getGameType(),
            this.playerMP,
            pos
        )
        if (result < 0) {
            blockingBlock.block = Blocks.STONE
            blockingBlock.meta = 0
            return true
        }

        this.tryBlockLimit--


        //Collect the mined block - unless it's a plant or leaves in which case just break it
        if (!((b is IPlantable && b !is BlockReed) || b is BlockLeaves)) {
            val drops: ItemStack? = if (gtFlag) getGTDrops(this.world, pos, b) else getPickBlock(this.world, pos, b)
            if (drops != null && !this.addToInventory(drops)) {
                //drop itemstack if AstroMiner can't hold it
                dropStack(pos, drops)
            }
        }

        this.world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
        return false
    }

    private fun dropStack(pos: BlockPos, drops: ItemStack) {
        val f: Float = 0.7f
        val d0: kotlin.Double = this.world.rand.nextFloat() * f + (1.0f - f) * 0.5
        val d1: kotlin.Double = this.world.rand.nextFloat() * f + (1.0f - f) * 0.5
        val d2: kotlin.Double = this.world.rand.nextFloat() * f + (1.0f - f) * 0.5
        val entityitem: EntityItem = EntityItem(this.world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, drops)
        entityitem.setDefaultPickupDelay()
        this.world.spawnEntity(entityitem)
        this.inventoryDrops++
    }

    private fun gregTechCheck(b: Block): Boolean {
        val clazz: Class<*>? = CompatibilityManager.classGTOre
        return clazz != null && clazz.isInstance(b)
    }

    private fun getGTDrops(w: World, pos: BlockPos, b: Block): ItemStack {
        val array: MutableList<ItemStack?> = b.getDrops(w, pos, b.defaultBlockState(), 1)
        if (array != null && array.size > 0) {
            return array.get(0)!!
        }
        return ItemStack.EMPTY
    }

    private fun tryBlockClient(pos: BlockPos): Boolean {
        val bv: BlockVec3 = BlockVec3(pos.getX(), pos.getY(), pos.getZ())
        if (this.laserBlocks.contains(bv)) {
            return false
        }

        //Add minable blocks to the laser fx list
        val state: IBlockState = this.world.getBlockState(pos)
        val b: Block = state.getBlock()
        if (b.getMaterial(state) === Material.AIR) {
            return false
        }
        if (noMineList.contains(b)) {
            return true
        }
        if (b is BlockLiquid) {
            return false
        }
        if (b is IFluidBlock) {
            return false
        }
        if (b is IPlantable) {
            return true
        }
        if (b.hasTileEntity(state) || b.getBlockHardness(state, this.world, pos) < 0) {
            return true
        }
        if (this.tryBlockLimit == 0) {
            return false
        }

        this.tryBlockLimit--

        this.laserBlocks.add(bv)
        this.laserTimes.add(this.ticksExisted)
        return false
    }


    open fun removeLaserBlocks(removeCount: Int) {
        for (i: Int in 0..<removeCount) {
            this.laserBlocks.removeFirst()
            this.laserTimes.removeFirst()
        }
    }

    private fun getPickBlock(world: World, pos: BlockPos, b: Block): ItemStack? {
        if (b === GCBlocks.fallenMeteor) {
            return ItemStack(GCItems.meteoricIronRaw)
        }
        if (b is BlockSpaceGlass) {
            return b.getDrops(world, pos, b.defaultBlockState(), 0).get(0)
        }

        var i: Int = 0
        val item: Item = Item.getItemFromBlock(b)

        if (item == null) {
            GCLog.info("AstroMiner was unable to mine anything from: " + b.getTranslationKey())
            return null
        }

        if (item.getHasSubtypes()) {
            i = b.getMetaFromState(world.getBlockState(pos))
        }

        return ItemStack(item, 1, i)
    }

    private fun addToInventory(itemstack: ItemStack): Boolean {
        var flag1: Boolean = false
        var k: Int = 0
        val invSize: Int = this.getContainerSize()

        var itemstack1: ItemStack

        if (itemstack.isStackable()) {
            while (!itemstack.isEmpty() && k < invSize) {
                itemstack1 = this.stacks!!.get(k)

                if (RecipeUtil.stacksMatch(itemstack, itemstack1)) {
                    val l: Int = itemstack1.getCount() + itemstack.getCount()

                    if (l <= itemstack.getMaxStackSize()) {
                        itemstack.setCount(0)
                        itemstack1.setCount(l)
                        flag1 = true
                    } else if (itemstack1.getCount() < itemstack.getMaxStackSize()) {
                        itemstack.shrink(itemstack.getMaxStackSize() - itemstack1.getCount())
                        itemstack1.setCount(itemstack.getMaxStackSize())
                        flag1 = true
                    }
                }

                ++k
            }
        }

        if (!itemstack.isEmpty()) {
            k = 0

            while (k < invSize) {
                itemstack1 = this.stacks!!.get(k)

                if (itemstack1.isEmpty()) {
                    this.stacks!!.set(k, itemstack.copy())
                    itemstack.setCount(0)
                    flag1 = true
                    break
                }

                ++k
            }
        }

        if (flag1) {
            this.setChanged()
            this.mineCount++
        }
        return flag1
    }


    /**
     * Logic to move the miner to a given position
     *
     * @param pos
     * @param reverse True if returning home (re-use same tunnels)
     * @return False while the miner is en route, True when the position is reached
     */
    private fun moveToPos(pos: BlockVec3, reverse: Boolean): Boolean {
        this.noSpeedup = false

        if (reverse != (this.baseFacing!!.getIndex() < 4)) {
            if (this.posZ > pos.z + 0.0001 || this.posZ < pos.z - 0.0001) {
                this.moveToPosZ(pos.z, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving Z to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else if (this.posY > pos.y - 0.9999 || this.posY < pos.y - 1.0001) {
                this.moveToPosY(pos.y - 1, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving Y to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else if (this.posX > pos.x + 0.0001 || this.posX < pos.x - 0.0001) {
                this.moveToPosX(pos.x, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving X to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else {
                return true
            }
            //got there
        } else {
            if (this.posX > pos.x + 0.0001 || this.posX < pos.x - 0.0001) {
                this.moveToPosX(pos.x, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving X to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else if (this.posY > pos.y - 0.9999 || this.posY < pos.y - 1.0001) {
                this.moveToPosY(pos.y - 1, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving Y to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else if (this.posZ > pos.z + 0.0001 || this.posZ < pos.z - 0.0001) {
                this.moveToPosZ(pos.z, stopForTurn)
                if (TEMPDEBUG) {
                    GCLog.debug("At " + posX + "," + posY + "," + posZ + "Moving Z to " + pos.toString() + (if (stopForTurn) " : Stop for turn " + this.rotationPitch + "," + this.rotationYaw + " | " + this.targetPitch + "," + this.targetYaw else ""))
                }
            } else {
                return true
            }
            //got there
        }

        return false
    }

    private fun moveToPosX(x: Int, stopForTurn: Boolean) {
        this.targetPitch = 0f

        if (this.posX > x) {
            if (this.AIstate != AISTATE_DOCKING) {
                this.targetYaw = 270f
            }
            this.motionX = -this.speed
            //TODO some acceleration and deceleration
            if (this.motionX * speedup <= x - this.posX) {
                this.motionX = x - this.posX
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.WEST
        } else {
            if (this.AIstate != AISTATE_DOCKING) {
                this.targetYaw = 90f
            }
            this.motionX = this.speed
            if (this.motionX * speedup >= x - this.posX) {
                this.motionX = x - this.posX
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.EAST
        }

        if (stopForTurn) {
            this.motionX = 0.0
        }

        this.motionY = 0.0
        this.motionZ = 0.0
    }

    private fun moveToPosY(y: Int, stopForTurn: Boolean) {
        if (this.posY > y) {
            this.targetPitch = -90f
            this.motionY = -this.speed
            if (this.motionY * speedup <= y - this.posY) {
                this.motionY = y - this.posY
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.DOWN
        } else {
            this.targetPitch = 90f
            this.motionY = this.speed
            if (this.motionY * speedup >= y - this.posY) {
                this.motionY = y - this.posY
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.UP
        }

        if (stopForTurn) {
            this.motionY = 0.0
        }

        this.motionX = 0.0
        this.motionZ = 0.0
    }

    private fun moveToPosZ(z: Int, stopForTurn: Boolean) {
        this.targetPitch = 0f

        if (this.posZ > z) {
            if (this.AIstate != AISTATE_DOCKING) {
                this.targetYaw = 180f
            }
            this.motionZ = -this.speed
            //TODO some acceleration and deceleration
            if (this.motionZ * speedup <= z - this.posZ) {
                this.motionZ = z - this.posZ
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.NORTH
        } else {
            if (this.AIstate != AISTATE_DOCKING) {
                this.targetYaw = 0f
            }
            this.motionZ = this.speed
            if (this.motionZ * speedup >= z - this.posZ) {
                this.motionZ = z - this.posZ
                this.noSpeedup = true
            }
            this.facingAI = EnumFacing.SOUTH
        }

        if (stopForTurn) {
            this.motionZ = 0.0
        }

        this.motionY = 0.0
        this.motionX = 0.0
    }

    private fun checkRotation(): Boolean {
        var flag: Boolean = true
        //Handle the turns when it changes direction
        if (this.rotationPitch > this.targetPitch + 0.001f || this.rotationPitch < this.targetPitch - 0.001f) {
            if (this.rotationPitch > this.targetPitch + 180) {
                this.rotationPitch -= 360f
            } else if (this.rotationPitch < this.targetPitch - 180) {
                this.rotationPitch += 360f
            }

            if (this.rotationPitch > this.targetPitch) {
                this.rotationPitch -= this.rotSpeed
                if (this.rotationPitch < this.targetPitch) {
                    this.rotationPitch = this.targetPitch
                }
            } else {
                this.rotationPitch += this.rotSpeed
                if (this.rotationPitch > this.targetPitch) {
                    this.rotationPitch = this.targetPitch
                }
            }
        }

        if (this.rotationYaw > this.targetYaw + 0.001f || this.rotationYaw < this.targetYaw - 0.001f) {
            if (this.rotationYaw > this.targetYaw + 180) {
                this.rotationYaw -= 360f
            } else if (this.rotationYaw < this.targetYaw - 180) {
                this.rotationYaw += 360f
            }

            if (this.rotationYaw > this.targetYaw) {
                this.rotationYaw -= this.rotSpeed
                if (this.rotationYaw < this.targetYaw) {
                    this.rotationYaw = this.targetYaw
                }
            } else {
                this.rotationYaw += this.rotSpeed
                if (this.rotationYaw > this.targetYaw) {
                    this.rotationYaw = this.targetYaw
                }
            }
            flag = false
        }

        return flag
    }

    open fun setPlayer(player: EntityPlayerMP) {
        this.playerMP = player
        this.playerUUID = player.getUUID()
    }

    private fun setBoundingBoxForFacing() {
        var xsize: Float = cWIDTH
        var ysize: Float = cWIDTH
        var zsize: Float = cWIDTH
        when (this.facing) {
            EnumFacing.DOWN, EnumFacing.UP -> ysize = cLENGTH
            EnumFacing.NORTH, EnumFacing.SOUTH -> {
                ysize = cHEIGHT
                zsize = cLENGTH
            }

            EnumFacing.WEST, EnumFacing.EAST -> {
                ysize = cHEIGHT
                xsize = cLENGTH
            }
        }
        this.width = max(xsize, zsize)
        this.height = ysize
        this.setEntityBoundingBox(
            AxisAlignedBB(
                this.posX - xsize / 2.0, this.posY + 1.0 - ysize / 2.0, this.posZ - zsize / 2.0,
                this.posX + xsize / 2.0, this.posY + 1.0 + ysize / 2.0, this.posZ + zsize / 2.0
            )
        )
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource, par2: Float): Boolean {
        if (this.isDead || par1DamageSource == DamageSource.CACTUS) {
            return true
        }

        if (!this.world.isRemote) {
            val e: Entity? = par1DamageSource.getTrueSource()

            //If creative mode player, kill the entity (even if player owner is offline) and drop nothing
            if (e is EntityPlayer && e.capabilities.isCreativeMode) {
                if (this.playerMP == null && !this.spawnedInCreative) {
                    e.sendSystemMessage(Component.literal("WARNING: that Astro Miner belonged to an offline player, cannot reset player's Astro Miner count."))
                }
                this.setDead()
                return true
            }

            //Invulnerable to mobs
            if (this.isEntityInvulnerable() || (e is EntityLivingBase && e !is EntityPlayer)) {
                return false
            } else {
                this.markVelocityChanged()
                //                this.dataManager.set(this.timeSinceHit, Integer.valueOf(10));
//                this.dataManager.set(this.currentDamage, Integer.valueOf((int) (this.dataManager.get(this.currentDamage) + par2 * 10)));
                this.shipDamage += par2 * 10

                if (e is EntityPlayer) {
                    this.shipDamage += par2 * 21
                    //                    this.dataManager.set(this.currentDamage, 100);
                }

                if (this.shipDamage > 90) {
                    this.setDead()
                    this.dropShipAsItem()
                    return true
                }

                return true
            }
        } else {
            return true
        }
    }

    override fun getCollisionBox(par1Entity: Entity): AxisAlignedBB? {
        return par1Entity.getCollisionBoundingBox()
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }

    override fun performHurtAnimation() {
//	    this.dataManager.set(this.timeSinceHit, Integer.valueOf(10));
//	    this.dataManager.set(this.currentDamage, Integer.valueOf(this.dataManager.get(this.currentDamage) * 5));
    }

    open fun getDamage(): Float {
        return this.dataManager.get<Float>(DAMAGE)
    }

    open fun setDamage(p_70492_1_: Float) {
        this.dataManager.set<Float>(DAMAGE, p_70492_1_)
    }

    override fun setLocationAndAngles(
        x: kotlin.Double,
        y: kotlin.Double,
        z: kotlin.Double,
        rotYaw: Float,
        rotPitch: Float
    ) {
        this.minecartX = x
        this.minecartY = y
        this.minecartZ = z
        super.setLocationAndAngles(x, y, z, rotYaw, rotPitch)
    }

    @OnlyIn(Dist.CLIENT)
    override fun setPositionAndRotationDirect(
        x: kotlin.Double,
        y: kotlin.Double,
        z: kotlin.Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        b: Boolean
    ) {
        this.minecartX = x
        this.minecartY = y
        this.minecartZ = z
        this.minecartYaw = y
        this.minecartPitch = pitch.toDouble()
        this.turnProgress = 0
        this.motionX = this.velocityX
        this.motionY = this.velocityY
        this.motionZ = this.velocityZ
    }

    @OnlyIn(Dist.CLIENT)
    override fun setVelocity(p_70016_1_: kotlin.Double, p_70016_3_: kotlin.Double, p_70016_5_: kotlin.Double) {
        this.motionX = p_70016_1_
        this.velocityX = this.motionX
        this.motionY = p_70016_3_
        this.velocityY = this.motionY
        this.motionZ = p_70016_5_
        this.velocityZ = this.motionZ
        this.turnProgress = 0
    }

    override fun setSize(p_70105_1_: Float, p_70105_2_: Float) {
        this.setBoundingBoxForFacing()
    }

    override fun setPosition(p_70107_1_: kotlin.Double, p_70107_3_: kotlin.Double, p_70107_5_: kotlin.Double) {
        this.setEntityBoundingBox(
            this.getEntityBoundingBox().offset(p_70107_1_ - this.posX, p_70107_3_ - this.posY, p_70107_5_ - this.posZ)
        )
        this.posX = p_70107_1_
        this.posY = p_70107_3_
        this.posZ = p_70107_5_
    }

    override fun setDead() {
        if (!this.world.isRemote && this.playerMP != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get((this.playerMP)!!))!!
            if (!this.spawnedInCreative) {
                val astroCount: Int = stats.getAstroMinerCount()
                if (astroCount > 0) {
                    stats.setAstroMinerCount(stats.getAstroMinerCount() - 1)
                }
            }
            AsteroidsTickHandlerServer.removeChunkData(stats, this)
        }

        super.setDead()
        if (posBase != null) {
            val tileEntity: TileEntity? = posBase!!.getTileEntity(this.world)
            if (tileEntity is TileEntityMinerBase) {
                tileEntity.unlinkMiner()
            }
        }

        if (this.soundUpdater != null) {
            this.soundUpdater?.tick()
        }
    }

    open fun isEntityInvulnerable(): Boolean {
        //Can't be damaged if its player is offline - it's in a fully dormant state
        return this.playerMP == null
    }

    open fun getItemsDropped(droppedItems: MutableList<ItemStack>): MutableList<ItemStack> {
        val rocket: ItemStack = ItemStack(AsteroidsItems.astroMiner, 1, 0)
        droppedItems.add(rocket)
        for (i: Int in this.stacks!!.indices) {
            if (!this.stacks!!.get(i).isEmpty()) {
                droppedItems.add(this.stacks!!.get(i))
            }
            this.stacks!!.set(i, ItemStack.EMPTY)
        }
        return droppedItems
    }

    open fun dropShipAsItem() {
        if (this.world.isRemote) {
            return
        }

        for (item: ItemStack in this.getItemsDropped(ArrayList<ItemStack>())) {
            val entityItem: EntityItem? = this.entityDropItem(item, 0f)

            if (item.hasTagCompound()) {
                entityItem!!.getItem().setTagCompound(item.getTagCompound()!!.copy() as NBTTagCompound)
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSoundUpdater(): ITickable? {
        return this.soundUpdater
    }

    @OnlyIn(Dist.CLIENT)
    override fun setSoundUpdater(player: EntityPlayerSP?): ISound {
        this.soundUpdater = SoundUpdaterMiner(player, this)
        return this.soundUpdater as ISound
    }

    open fun stopRocketSound() {
        if (this.soundUpdater != null) {
            (this.soundUpdater as SoundUpdaterMiner).stopRocketSound()
        }
        this.soundToStop = false
    }

    override fun transmitData(data: IntArray?) {
        (data)!![0] = (this.posX).toInt()
        (data)!![1] = (this.posY).toInt()
        (data)!![2] = (this.posZ).toInt()
        (data)!![3] = this.energyLevel
        (data)!![4] = this.AIstate
    }

    override fun receiveData(data: IntArray?, str: Array<String?>?) {
        (str)!![0] = ""
        (str)!![1] = "x: " + (data)!![0]
        (str)!![2] = "y: " + (data)!![1]
        (str)!![3] = "z: " + (data)!![2]
        val energyPerCent: Int = (data)!![3] / 120
        (str)!![4] = GCCoreUtil.translate("gui.energy_storage.desc.1") + ": " + energyPerCent + "%"
        when ((data)!![4]) {
            AISTATE_STUCK -> (str)!![0] = GCCoreUtil.translate("gui.message.no_energy.name")
            AISTATE_ATBASE -> (str)!![0] = GCCoreUtil.translate("gui.miner.docked")
            AISTATE_TRAVELLING -> (str)!![0] = GCCoreUtil.translate("gui.miner.travelling")
            AISTATE_MINING -> (str)!![0] = GCCoreUtil.translate("gui.miner.mining")
            AISTATE_RETURNING -> (str)!![0] = GCCoreUtil.translate("gui.miner.returning")
            AISTATE_DOCKING -> (str)!![0] = GCCoreUtil.translate("gui.miner.docking")
            AISTATE_OFFLINE -> (str)!![0] = GCCoreUtil.translate("gui.miner.offline")
        }
    }

    override fun adjustDisplay(data: IntArray?) {
        GL11.glScalef(0.9f, 0.9f, 0.9f)
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.stacks = NonNullList.withSize<ItemStack>(INV_SIZE, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)
        var itemCount: Int = 0
        for (stack: ItemStack in this.stacks!!) {
            itemCount += stack.getCount()
        }
        this.mineCount = itemCount

        if (nbt.containsTag("sindex")) {
            this.serverIndex = nbt.getIntOr("sindex", 0)
        } else {
            this.serverIndex = -1
        }

        if (nbt.containsTag("Energy")) {
            this.energyLevel = nbt.getIntOr("Energy", 0)
        }
        if (nbt.containsTag("BaseX")) {
            this.posBase = BlockVec3(nbt.getIntOr("BaseX", 0), nbt.getIntOr("BaseY", 0), nbt.getIntOr("BaseZ", 0))
            this.flagLink = true
        }
        if (nbt.containsTag("TargetX")) {
            this.posTarget = BlockVec3(nbt.getIntOr("TargetX", 0), nbt.getIntOr("TargetY", 0), nbt.getIntOr("TargetZ", 0))
        }
        if (nbt.containsTag("WBaseX")) {
            this.waypointBase = BlockVec3(nbt.getIntOr("WBaseX", 0), nbt.getIntOr("WBaseY", 0), nbt.getIntOr("WBaseZ", 0))
        }
        if (nbt.containsTag("BaseFacing")) {
            this.baseFacing = EnumFacing.from3DDataValue(nbt.getIntOr("BaseFacing", 0))
        }
        if (nbt.containsTag("AIState")) {
            this.AIstate = nbt.getIntOr("AIState", 0)
        }
        if (nbt.containsTag("Facing")) {
            this.facingAI = EnumFacing.from3DDataValue(nbt.getIntOr("Facing", 0))
            when (this.facingAI) {
                EnumFacing.NORTH -> this.targetYaw = 180f
                EnumFacing.SOUTH -> this.targetYaw = 0f
                EnumFacing.WEST -> this.targetYaw = 270f
                EnumFacing.EAST -> this.targetYaw = 90f
            
else -> {}
}
        }
        this.lastFacing = null
        if (nbt.containsTag("WayPoints")) {
            this.wayPoints.clear()
            val wpList: NBTTagList = nbt.listOfType("WayPoints", 10)
            for (j: Int in 0..<wpList.size) {
                val bvTag: NBTTagCompound = wpList.getCompoundOrEmpty(j)
                this.wayPoints.add(BlockVec3.readFromNBT(bvTag))
            }
        }
        if (nbt.containsTag("MinePoints")) {
            this.minePoints.clear()
            val mpList: NBTTagList = nbt.listOfType("MinePoints", 10)
            for (j: Int in 0..<mpList.size) {
                val bvTag: NBTTagCompound = mpList.getCompoundOrEmpty(j)
                this.minePoints.add(BlockVec3.readFromNBT(bvTag))
            }
        }
        if (nbt.containsTag("MinePointCurrent")) {
            this.minePointCurrent = BlockVec3.readFromNBT(nbt.getCompoundOrEmpty("MinePointCurrent"))
        } else {
            this.minePointCurrent = null
        }
        if (nbt.containsTag("playerUUIDMost", 4) && nbt.containsTag("playerUUIDLeast", 4)) {
            this.playerUUID = UUID(nbt.getLongOr("playerUUIDMost", 0L), nbt.getLongOr("playerUUIDLeast", 0L))
        } else {
            this.playerUUID = null
        }
        if (nbt.containsTag("speedup")) {
            this.speedup = nbt.getDoubleOr("speedup", 0.0)
        } else {
            this.speedup =
                if (WorldUtil.getProviderForDimensionServer(this.dimension) is WorldProviderAsteroids) SPEEDUP * 1.6 else SPEEDUP
        }

        this.pathBlockedCount = nbt.getIntOr("pathBlockedCount", 0)
        this.spawnedInCreative = nbt.getBooleanOr("spawnedInCreative", false)
        this.flagCheckPlayer = true
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        val var2: NBTTagList = NBTTagList()

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        nbt.put("Items", var2)
        nbt.putInt("sindex", this.serverIndex)
        nbt.putInt("Energy", this.energyLevel)
        if (this.posBase != null) {
            nbt.putInt("BaseX", this.posBase!!.x)
            nbt.putInt("BaseY", this.posBase!!.y)
            nbt.putInt("BaseZ", this.posBase!!.z)
        }
        if (this.posTarget != null) {
            nbt.putInt("TargetX", this.posTarget!!.x)
            nbt.putInt("TargetY", this.posTarget!!.y)
            nbt.putInt("TargetZ", this.posTarget!!.z)
        }
        if (this.waypointBase != null) {
            nbt.putInt("WBaseX", this.waypointBase!!.x)
            nbt.putInt("WBaseY", this.waypointBase!!.y)
            nbt.putInt("WBaseZ", this.waypointBase!!.z)
        }
        nbt.putInt("BaseFacing", this.baseFacing!!.getIndex())
        nbt.putInt("AIState", this.AIstate)
        nbt.putInt("Facing", this.facingAI!!.getIndex())
        if (this.wayPoints.size > 0) {
            val wpList: NBTTagList = NBTTagList()
            for (j: Int in this.wayPoints.indices) {
                wpList.add(this.wayPoints.get(j)!!.writeToNBT(NBTTagCompound()))
            }
            nbt.put("WayPoints", wpList)
        }
        if (this.minePoints.size > 0) {
            val mpList: NBTTagList = NBTTagList()
            for (j: Int in this.minePoints.indices) {
                mpList.add(this.minePoints.get(j)!!.writeToNBT(NBTTagCompound()))
            }
            nbt.put("MinePoints", mpList)
        }
        if (this.minePointCurrent != null) {
            nbt.put("MinePointCurrent", this.minePointCurrent!!.writeToNBT(NBTTagCompound()))
        }
        if (this.playerUUID != null) {
            nbt.putLong("playerUUIDMost", this.playerUUID!!.getMostSignificantBits())
            nbt.putLong("playerUUIDLeast", this.playerUUID!!.getLeastSignificantBits())
        }
        nbt.putDouble("speedup", this.speedup)
        nbt.putInt("pathBlockedCount", this.pathBlockedCount)
        nbt.putBoolean("spawnedInCreative", this.spawnedInCreative)
    }

    companion object {
        private val DAMAGE: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntityAstroMiner::class.java, DataSerializers.FLOAT)

        const val MINE_LENGTH: Int = 24

        const val MINE_LENGTH_AST: Int = 12
        private const val MAXENERGY = 12000
        private const val RETURNENERGY = 1000
        private const val RETURNDROPS = 10
        private const val INV_SIZE = 227
        private const val cLENGTH = 2.6f
        private const val cWIDTH = 1.8f
        private const val cHEIGHT = 1.7f
        private const val SPEEDUP = 2.5

        @JvmField
        val AISTATE_OFFLINE: Int = -1

        const val AISTATE_STUCK: Int = 0

        const val AISTATE_ATBASE: Int = 1

        const val AISTATE_TRAVELLING: Int = 2

        const val AISTATE_MINING: Int = 3

        const val AISTATE_RETURNING: Int = 4

        const val AISTATE_DOCKING: Int = 5

        const val FAIL_BASEDESTROYED: Int = 3

        const val FAIL_OUTOFENERGY: Int = 4

        const val FAIL_RETURNPATHBLOCKED: Int = 5

        const val FAIL_ANOTHERWASLINKED: Int = 8

        private val headings = arrayOf<BlockVec3?>(
            BlockVec3(0, -1, 0),
            BlockVec3(0, 1, 0),
            BlockVec3(0, 0, -1),
            BlockVec3(0, 0, 1),
            BlockVec3(-1, 0, 0),
            BlockVec3(1, 0, 0)
        )
        private val headings2 = arrayOf<BlockVec3?>(
            BlockVec3(0, -3, 0),
            BlockVec3(0, 2, 0),
            BlockVec3(0, 0, -3),
            BlockVec3(0, 0, 2),
            BlockVec3(-3, 0, 0),
            BlockVec3(2, 0, 0)
        )

        private val noMineList = ArrayList<Block?>()

        @JvmField
        var blockingBlock: BlockTuple = BlockTuple(Blocks.AIR, 0)

        init {
            //Avoid:
            // Overworld: avoid lava source blocks, mossy cobble, End Portal and Fortress blocks
            // railtrack, levers, redstone dust, GC walkways,
            //Anything with a tileEntity will also be avoided:
            // spawners, chests, oxygen pipes, hydrogen pipes, wires
            noMineList.add(Blocks.BEDROCK)
            noMineList.add(Blocks.MOSSY_COBBLESTONE)
            noMineList.add(Blocks.END_PORTAL)
            noMineList.add(Blocks.END_PORTAL_FRAME)
            noMineList.add(Blocks.PORTAL)
            noMineList.add(Blocks.STONEBRICK)
            noMineList.add(Blocks.FARMLAND)
            noMineList.add(Blocks.RAIL)
            noMineList.add(Blocks.LEVER)
            noMineList.add(Blocks.REDSTONE_WIRE)
            noMineList.add(AsteroidBlocks.blockWalkway)
            //TODO:
            //Add configurable blacklist
        }

        /**
         * x y z should be the mid-point of the 4 base blocks
         *
         * @param world
         * @param x
         * @param y
         * @param z
         * @param facing
         * @param base
         * @return
         */
        @JvmStatic
        fun spawnMinerAtBase(
            world: World,
            x: Int,
            y: Int,
            z: Int,
            facing: EnumFacing,
            base: BlockVec3?,
            player: EntityPlayerMP
        ): Boolean {
            if (world.isClientSide) {
                return true
            }
            val miner: EntityAstroMiner =
                EntityAstroMiner(world, NonNullList.withSize<ItemStack>(INV_SIZE, ItemStack.EMPTY), 0)
            miner.setPlayer(player)
            if (player.capabilities.isCreativeMode) {
                miner.spawnedInCreative = true
            }
            miner.waypointBase = BlockVec3(x, y, z).modifyPositionFromSide(facing, 1)
            miner.setPosition(
                miner.waypointBase!!.x.toDouble(),
                (miner.waypointBase!!.y - 1).toDouble(),
                miner.waypointBase!!.z.toDouble()
            )
            miner.baseFacing = facing
            miner.facingAI = facing
            miner.lastFacing = facing
            miner.motionX = 0.0
            miner.motionY = 0.0
            miner.motionZ = 0.0
            miner.targetPitch = 0f
            when (facing) {
                EnumFacing.NORTH -> miner.targetYaw = 180f
                EnumFacing.SOUTH -> miner.targetYaw = 0f
                EnumFacing.WEST -> miner.targetYaw = 270f
                EnumFacing.EAST -> miner.targetYaw = 90f
            
else -> {}
}
            miner.rotationPitch = miner.targetPitch
            miner.rotationYaw = miner.targetYaw
            miner.setBoundingBoxForFacing()
            miner.AIstate = AISTATE_ATBASE
            miner.posBase = base

            //Increase motion speed when moving in empty space between asteroids
            miner.speedup = if (world.provider is WorldProviderAsteroids) SPEEDUP * 2.2 else SPEEDUP

            //Clear blocks, and test to see if its movement area in front of the base is blocked
            if (miner.prepareMove(12, 0)) {
                miner.isDead = true
                return false
            }
            if (miner.prepareMove(12, 1)) {
                miner.isDead = true
                return false
            }
            if (miner.prepareMove(12, 2)) {
                miner.isDead = true
                return false
            }

            world.addFreshEntity(miner)
            miner.flagLink = true
            return true
        }
    }
}
