package micdoodle8.mods.galacticraft.api.prefab.entity

import net.minecraft.network.chat.Component

import foundation.item.legacyItemValue

import net.minecraft.world.ContainerHelper
import foundation.persistence.containsTag

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.entity.IEntityNoisy
import micdoodle8.mods.galacticraft.api.entity.ILandable
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import micdoodle8.mods.galacticraft.core.client.sounds.SoundUpdaterRocket
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.event.EventLandingPadRemoval
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.level.block.Block
import net.minecraft.client.resources.sounds.SoundInstance as ISound
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.sounds.SoundEvents
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ClassInheritanceMultiMap
import net.minecraft.client.resources.sounds.TickableSoundInstance as ITickable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper

import net.minecraft.util.text.translation.I18n
import net.minecraft.world.level.Level as World
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.util.ArrayList
import java.util.HashSet
import kotlin.math.atan
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Do not include this prefab class in your released mod download.
 */
abstract class EntityAutoRocket(world: World?) : EntitySpaceshipBase(world), ILandable, IInventoryDefaults,
    IEntityNoisy {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<EntitySpaceshipBase>.getDisplayName()

    @JvmField
    var destinationFrequency: Int = -1

    @JvmField
    var targetVec: BlockPos? = null

    @JvmField
    var targetDimension: Int = 0

    @JvmField
    protected var stacks: NonNullList<ItemStack>? = null
    private var landingPad: IFuelDock? = null

    @JvmField
    var autoLaunchSetting: EnumAutoLaunch? = null

    @JvmField
    var autoLaunchCountdown: Int = 0
    private var activeLaunchController: BlockVec3? = null

    @JvmField
    var statusMessage: String? = null

    @JvmField
    var statusColour: String? = null

    @JvmField
    var statusMessageCooldown: Int = 0

    @JvmField
    var lastStatusMessageCooldown: Int = 0

    @JvmField
    var statusValid: Boolean = false

    @JvmField
    protected var lastMotionY: Double = 0.0

    @JvmField
    protected var lastLastMotionY: Double = 0.0
    private var waitForPlayer = false

    @JvmField
    protected var rocketSoundUpdater: ITickable? = null
    private var rocketSoundToStop = false

    init {
        if (world != null && world.isClientSide) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    constructor(world: World?, posX: Double, posY: Double, posZ: Double) : this(world) {
        this.setSize(0.98f, 2f)
        this.setPosition(posX, posY, posZ)
        this.motionX = 0.0
        this.motionY = 0.0
        this.motionZ = 0.0
        this.prevPosX = posX
        this.prevPosY = posY
        this.prevPosZ = posZ
    }

    //Used for Cargo Rockets, client is asking the server what is the status
    fun checkLaunchValidity(): Boolean {
        this.statusMessageCooldown = 40

        if (this.hasValidFuel()) {
            if (this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal && !this.world.isRemote) {
                if (!this.setFrequency()) {
                    this.destinationFrequency = -1
                    this.statusMessage =
                        I18n.translateToLocal("gui.message.frequency.name") + "#" + I18n.translateToLocal("gui.message.not_set.name")
                    this.statusColour = "\u00a7c"
                    return false
                } else {
                    this.statusMessage = I18n.translateToLocal("gui.message.success.name")
                    this.statusColour = "\u00a7a"
                    return true
                }
            }
        } else {
            this.destinationFrequency = -1
            this.statusMessage =
                I18n.translateToLocal("gui.message.not_enough.name") + "#" + I18n.translateToLocal("gui.message.fuel.name")
            this.statusColour = "\u00a7c"
            return false
        }

        this.destinationFrequency = -1
        return false
    }

    fun setFrequency(): Boolean {
        if (!GalacticraftCore.isPlanetsLoaded || controllerClass == null) {
            return false
        }

        if (this.activeLaunchController != null) {
            val launchController: TileEntity? = this.activeLaunchController!!.getTileEntity(this.world)
            if (controllerClass!!.isInstance(launchController)) {
                try {
                    val b: Boolean? = controllerClass!!.getMethod("validFrequency").invoke(launchController) as Boolean?

                    if (b != null && b) {
                        val controllerFrequency: Int =
                            controllerClass!!.getField("destFrequency").getInt(launchController)
                        val foundPad: Boolean = this.setTarget(false, controllerFrequency)

                        if (foundPad) {
                            this.destinationFrequency = controllerFrequency
                            GCLog.debug("Rocket under launch control: going to target frequency " + controllerFrequency)
                            return true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        this.destinationFrequency = -1
        return false
    }

    protected fun setTarget(doSet: Boolean, destFreq: Int): Boolean {
        val servers: Array<WorldServer> = (GCCoreUtil.getWorldServerList(this.world))!!
        if (!GalacticraftCore.isPlanetsLoaded || controllerClass == null) {
            return false
        }

        for (i: Int in servers.indices) {
            val world: WorldServer = servers[i]

            try {
                for (tile: TileEntity? in ArrayList<TileEntity?>(world.loadedTileEntityList)) {
                    var tile: TileEntity? = tile
                    if (!controllerClass!!.isInstance(tile)) continue

                    tile = world.getBlockEntity(tile!!.getBlockPos())
                    if (!controllerClass!!.isInstance(tile)) continue

                    val controllerFrequency: Int = controllerClass!!.getField("frequency").getInt(tile)

                    if (destFreq == controllerFrequency) {
                        var targetSet: Boolean = false

                        blockLoop@ for (x: Int in -2..2) {
                            for (z: Int in -2..2) {
                                val pos: BlockPos = BlockPos(tile!!.getBlockPos().add(x, 0, z))
                                val block: Block = world.getBlockState(pos).getBlock()

                                if (block is BlockLandingPadFull) {
                                    if (doSet) {
                                        this.targetVec = pos
                                    }

                                    targetSet = true
                                    break@blockLoop
                                }
                            }
                        }

                        if (doSet) {
                            this.targetDimension = tile!!.getLevel().provider.getDimension()
                        }

                        if (!targetSet) {
                            if (doSet) {
                                this.targetVec = null
                            }

                            return false
                        } else {
                            return true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return false
    }

    override fun getScaledFuelLevel(scale: Int): Int {
        if (this.getFuelTankCapacity() <= 0) {
            return 0
        }

        return this.fuelTank.getFluidAmount() * scale / this.getFuelTankCapacity() / ConfigManagerCore.rocketFuelFactor
    }

    override fun onUpdate() {
        //Workaround for a weird bug (?) in vanilla 1.8.9 where - if client view distance is shorter
        //than the server's chunk loading distance - chunks will unload on the client, but the
        //entity will still be in the WorldClient.loadedEntityList.  This results in an entity which
        //is in the world, not dead and still updating on both server and client, but not in any chunk's
        //chunk.entityLists.  Also, it won't be reloaded into any chunk's entityLists when the chunk comes
        //back into viewing range - not sure why, maybe because it's already in the World.loadedEntityList?
        //Because it's now not in any chunk's entityLists, it cannot be iterated for rendering by RenderGlobal,
        //so it's gone invisible!

        //Tracing shows that Chunk.onChunkUnload() is called on the chunk clientside when the chunk goes
        //out of the view distance.  However, even after Chunk.onChunkUnload() - which should remove
        //this entity from the WorldClient.loadedEntityList - this entity stays in the world loadedEntityList.
        //That's why an onUpdate() tick is active for it, still!
        //Weird, huh?

        if (this.world.isRemote && this.addedToChunk && !CompatibilityManager.isCubicChunksLoaded) {
            val chunk: Chunk = this.world.getChunk(this.chunkCoordX, this.chunkCoordZ)
            val cx: Int = MathHelper.floor(this.posX) shr 4
            val cz: Int = MathHelper.floor(this.posZ) shr 4
            if (chunk.isLoaded() && this.chunkCoordX == cx && this.chunkCoordZ == cz) {
                var thisfound: Boolean = false
                val mapEntities: ClassInheritanceMultiMap<Entity?> = chunk.getEntityLists()[this.chunkCoordY]
                for (ent: Entity? in mapEntities) {
                    if (ent === this) {
                        thisfound = true
                        break
                    }
                }
                if (!thisfound) {
                    chunk.addEntity(this)
                }
            }
        }

        if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.hasValidFuel()) {
            if (this.targetVec != null) {
                val yDiff: Double = this.posY - this.getOnPadYOffset() - this.targetVec!!.getY()
                this.motionY = max(-2.0, (yDiff - 0.04) / -55.0)

                //Some lateral motion in case not exactly on target (happens if rocket was moving laterally during launch)
                var diff: Double = this.posX - this.targetVec!!.getX() - 0.5
                val motX: Double
                val motZ: Double
                if (diff > 0.0) {
                    motX = max(-0.1, diff / -100.0)
                } else if (diff < 0.0) {
                    motX = min(0.1, diff / -100.0)
                } else motX = 0.0
                diff = this.posZ - this.targetVec!!.getZ() - 0.5
                if (diff > 0.0) {
                    motZ = max(-0.1, diff / -100.0)
                } else if (diff < 0.0) {
                    motZ = min(0.1, diff / -100.0)
                } else motZ = 0.0
                if (motZ != 0.0 || motX != 0.0) {
                    val angleYaw: Double = atan(motZ / motX)
                    val signed: Double = if (motX < 0) 50.0 else -50.0
                    val anglePitch: Double = atan(sqrt(motZ * motZ + motX * motX) / signed) * 100.0
                    this.rotationYaw = angleYaw.toFloat() * Constants.RADIANS_TO_DEGREES
                    this.rotationPitch = anglePitch.toFloat() * Constants.RADIANS_TO_DEGREES
                } else this.rotationPitch = 0f

                if (yDiff > 1.0 && yDiff < 4.0) {
                    for (o: Any? in this.world.getEntitiesInAABBexcluding(
                        this,
                        this.getEntityBoundingBox().offset(0.0, -3.0, 0.0),
                        EntitySpaceshipBase.Companion.rocketSelector
                    )) {
                        if (o is EntitySpaceshipBase) {
                            o.dropShipAsItem()
                            o.setDead()
                        }
                    }
                }
                if (yDiff < 0.4) {
                    val yMin: Int =
                        MathHelper.floor(this.getEntityBoundingBox().minY - this.getOnPadYOffset() - 0.45) - 2
                    val yMax: Int = MathHelper.floor(this.getEntityBoundingBox().maxY) + 1
                    val zMin: Int = MathHelper.floor(this.posZ) - 1
                    val zMax: Int = MathHelper.floor(this.posZ) + 1
                    for (x: Int in MathHelper.floor(this.posX) - 1..MathHelper.floor(this.posX) + 1) {
                        for (z: Int in zMin..zMax) {
                            //Doing y as the inner loop may help with cacheing of chunks
                            for (y: Int in yMin..yMax) {
                                if (this.world.getBlockEntity(BlockPos(x, y, z)) is IFuelDock) {
                                    //Land the rocket on the pad found
                                    this.rotationPitch = 0f
                                    this.failRocket()
                                }
                            }
                        }
                    }
                }
            }
        }

        super.onUpdate()

        if (!this.world.isRemote) {
            if (this.statusMessageCooldown > 0) {
                this.statusMessageCooldown--
            }

            if (this.statusMessageCooldown == 0 && this.lastStatusMessageCooldown > 0 && this.statusValid) {
                this.autoLaunch()
            }

            if (this.autoLaunchCountdown > 0 && (this !is EntityTieredRocket || !this.getPassengers().isEmpty())) {
                if (--this.autoLaunchCountdown <= 0) {
                    this.autoLaunch()
                }
            }

            if (this.autoLaunchSetting == EnumAutoLaunch.ROCKET_IS_FUELED && this.fuelTank.getFluidAmount() == this.fuelTank.getCapacity() && (this !is EntityTieredRocket || !this.getPassengers()
                    .isEmpty())
            ) {
                this.autoLaunch()
            }

            if (this.autoLaunchSetting == EnumAutoLaunch.INSTANT) {
                if (this.autoLaunchCountdown == 0 && (this !is EntityTieredRocket || !this.getPassengers().isEmpty())) {
                    this.autoLaunch()
                }
            }

            if (this.autoLaunchSetting == EnumAutoLaunch.REDSTONE_SIGNAL) {
                if (this.ticks % 11 == 0L && this.activeLaunchController != null) {
                    if (RedstoneUtil.isBlockReceivingRedstone(this.world, this.activeLaunchController!!.toBlockPos())) {
                        this.autoLaunch()
                    }
                }
            }

            if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal) {
                this.setPad(null)
            } else {
                if (this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal && this.landingPad != null && this.ticks % 17 == 0L) {
                    this.updateControllerSettings(this.landingPad!!)
                }
            }

            this.lastStatusMessageCooldown = this.statusMessageCooldown
        }

        if (this.launchPhase >= EnumLaunchPhase.IGNITED.ordinal) {
            if (this.rocketSoundUpdater != null) {
                this.rocketSoundUpdater?.tick()
                this.rocketSoundToStop = true
            }
        } else {
            if (this.rocketSoundToStop) {
                this.stopRocketSound()
                this.rocketSoundUpdater = null
            }
        }
    }

    override fun shouldMoveClientSide(): Boolean {
        return false
    }

    //Server side only - this is a Launch Controlled ignition attempt
    private fun autoLaunch() {
        if (this.autoLaunchSetting != null) {
            if (this.activeLaunchController != null) {
                val tile: TileEntity? = this.activeLaunchController!!.getTileEntity(this.world)

                if (controllerClass!!.isInstance(tile)) {
                    var autoLaunchEnabled: Boolean? = null
                    try {
                        autoLaunchEnabled = controllerClass!!.getField("controlEnabled").getBoolean(tile)
                    } catch (e: Exception) {
                    }

                    if (autoLaunchEnabled != null && autoLaunchEnabled) {
                        if (this.fuelTank.getFluidAmount() > this.fuelTank.getCapacity() * 2 / 5) this.ignite()
                        else this.failMessageInsufficientFuel()
                    } else {
                        this.failMessageLaunchController()
                    }
                }
            }
            this.autoLaunchSetting = null

            return
        } else {
            this.ignite()
        }
    }

    fun igniteWithResult(): Boolean {
        if (this.setFrequency()) {
            super.ignite()
            this.activeLaunchController = null
            return true
        } else {
            if (this.isPlayerRocket()) {
                super.ignite()
                this.activeLaunchController = null
                return true
            }

            this.activeLaunchController = null
            return false
        }
    }

    override fun ignite() {
        this.igniteWithResult()
    }

    abstract fun isPlayerRocket(): Boolean

    override fun landEntity(pos: BlockPos?) {
        val tile: TileEntity? = this.world.getBlockEntity(pos)

        if (tile is IFuelDock) {
            val dock: IFuelDock = tile as IFuelDock

            if (this.isDockValid(dock)) {
                if (!this.world.isRemote) {
                    //Drop any existing rocket on the landing pad
                    if (dock.getDockedEntity() is EntitySpaceshipBase && dock.getDockedEntity() !== this) {
                        (dock.getDockedEntity() as EntitySpaceshipBase).dropShipAsItem()
                        (dock.getDockedEntity() as EntitySpaceshipBase).setDead()
                    }

                    this.setPad(dock)
                }

                this.onRocketLand((pos)!!)
            }
        }
    }

    fun updateControllerSettings(dock: IFuelDock) {
        val connectedTiles: HashSet<ILandingPadAttachable?> = (dock.getConnectedTiles())!!

        try {
            for (updatedTile: ILandingPadAttachable? in connectedTiles) {
                if (controllerClass!!.isInstance(updatedTile)) {
                    //This includes a check for whether it has enough energy to run (if it doesn't, then a launch would not go to the destination frequency and the rocket would be lost!)
                    val autoLaunchEnabled: Boolean =
                        controllerClass!!.getField("controlEnabled").getBoolean(updatedTile)

                    this.activeLaunchController = BlockVec3(updatedTile as TileEntity)

                    if (autoLaunchEnabled) {
                        this.autoLaunchSetting =
                            EnumAutoLaunch.entries[controllerClass!!.getField("launchDropdownSelection")
                                .getInt(updatedTile)]

                        when (this.autoLaunchSetting) {
                            EnumAutoLaunch.INSTANT ->                             //Small countdown to give player a moment to exit the Launch Controller GUI
                                if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 12) this.autoLaunchCountdown =
                                    12

                            EnumAutoLaunch.TIME_10_SECONDS -> if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 200) this.autoLaunchCountdown =
                                200

                            EnumAutoLaunch.TIME_30_SECONDS -> if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 600) this.autoLaunchCountdown =
                                600

                            EnumAutoLaunch.TIME_1_MINUTE -> if (this.autoLaunchCountdown <= 0 || this.autoLaunchCountdown > 1200) this.autoLaunchCountdown =
                                1200

                            else -> {}
                        }
                    } else {
                        //This LaunchController is out of power, disabled, invalid target or set not to launch
                        //No auto launch - but maybe another connectedTile will have some launch settings?
                        this.autoLaunchSetting = null
                        this.autoLaunchCountdown = 0
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun onRocketLand(pos: BlockPos) {
        this.setPositionAndRotation(
            pos.getX() + 0.5,
            pos.getY() + 0.4 + this.getOnPadYOffset(),
            pos.getZ() + 0.5,
            this.rotationYaw,
            0.0f
        )
        this.stopRocketSound()
        this.rocketSoundUpdater = null //Allow sound to be restarted again if it relaunches
    }

    fun stopRocketSound() {
        if (this.rocketSoundUpdater != null) {
            (this.rocketSoundUpdater as SoundUpdaterRocket).stopRocketSound()
        }
        this.rocketSoundToStop = false
    }

    override fun setDead() {
        super.setDead()

        if (this.rocketSoundUpdater != null) {
            this.rocketSoundUpdater?.tick()
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (!this.world.isRemote) {
            val clientPosY: Double = (buffer)!!.readDouble()
            if (clientPosY != Double.NaN && this.hasValidFuel()) {
                if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal) {
                    if (clientPosY > this.posY) {
                        this.motionY += (clientPosY - this.posY) / 40.0
                    }
                } else if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal) {
                    if (clientPosY < this.posY) {
                        this.motionY += (clientPosY - this.posY) / 40.0
                    }
                }
            }
            return
        }
        val launchPhaseLast: Int = this.launchPhase
        super.decodePacketdata(buffer)
        this.fuelTank.setFluid(FluidStack(GCFluids.fluidFuel, (buffer)!!.readInt()))
        val landingNew: Boolean = (buffer)!!.readBoolean()
        if (landingNew && launchPhaseLast != EnumLaunchPhase.LANDING.ordinal) {
            this.rocketSoundUpdater = null //Flag TickHandlerClient to restart it
        }
        this.destinationFrequency = (buffer)!!.readInt()

        if ((buffer)!!.readBoolean()) {
            this.targetVec = BlockPos((buffer)!!.readInt(), (buffer)!!.readInt(), (buffer)!!.readInt())
        }

        val motX: Double = (buffer)!!.readDouble() / 8000.0
        val motY: Double = (buffer)!!.readDouble() / 8000.0
        val motZ: Double = (buffer)!!.readDouble() / 8000.0
        val lastMotY: Double = (buffer)!!.readDouble() / 8000.0
        val lastLastMotY: Double = (buffer)!!.readDouble() / 8000.0

        if (!this.hasValidFuel()) {
            this.motionX = motX
            this.motionY = motY
            this.motionZ = motZ
            this.lastMotionY = lastMotY
            this.lastLastMotionY = lastLastMotY
        }

        if (this.stacks == null) {
            this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        }

        this.setWaitForPlayer((buffer)!!.readBoolean())

        this.statusMessage = ByteBufUtils.readUTF8String(buffer)
        this.statusMessage = if (this.statusMessage == "") null else this.statusMessage
        this.statusMessageCooldown = (buffer)!!.readInt()
        this.lastStatusMessageCooldown = (buffer)!!.readInt()
        this.statusValid = (buffer)!!.readBoolean()

        //Update client with correct rider if needed
        if (this.world.isRemote) {
            val shouldBeMountedId: Int = (buffer)!!.readInt()
            if (this.getPassengers().isEmpty()) {
                if (shouldBeMountedId > -1) {
                    var e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                    if (e != null) {
                        if (e.dimension != this.dimension) {
                            if (e is EntityPlayer) {
                                e = WorldUtil.forceRespawnClient(
                                    this.dimension,
                                    e.level().getDifficulty().getId(),
                                    e.level().getWorldInfo().getTerrainType().getName(),
                                    (e as EntityPlayerMP).interactionManager.getGameType().getID()
                                )
                                (e)!!.startRiding(this)
                            }
                        } else e.startRiding(this)
                    }
                }
            } else if (this.getPassengers().get(0).getId() != shouldBeMountedId) {
                if (shouldBeMountedId == -1) {
                    this.removePassengers()
                } else {
                    var e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                    if (e != null) {
                        if (e.dimension != this.dimension) {
                            if (e is EntityPlayer) {
                                e = WorldUtil.forceRespawnClient(
                                    this.dimension,
                                    e.level().getDifficulty().getId(),
                                    e.level().getWorldInfo().getTerrainType().getName(),
                                    (e as EntityPlayerMP).interactionManager.getGameType().getID()
                                )
                                (e)!!.startRiding(this)
                            }
                        } else e.startRiding(this)
                    }
                }
            }
        }
        this.statusColour = ByteBufUtils.readUTF8String(buffer)
        if (this.statusColour == "") this.statusColour = null
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            if (this.getPassengers()
                    .contains(FMLClientHandler.instance().getClientPlayerEntity()) && this.hasValidFuel()
            ) {
                (list)!!.add(this.posY)
            } else {
                (list)!!.add(Double.NaN)
            }
        }
        super.getNetworkedData(list)

        (list)!!.add(this.fuelTank.getFluidAmount())
        (list)!!.add(this.launchPhase == EnumLaunchPhase.LANDING.ordinal)
        (list)!!.add(this.destinationFrequency)
        (list)!!.add(this.targetVec != null)

        if (this.targetVec != null) {
            (list)!!.add(this.targetVec!!.getX())
            (list)!!.add(this.targetVec!!.getY())
            (list)!!.add(this.targetVec!!.getZ())
        }

        (list)!!.add(this.motionX * 8000.0)
        (list)!!.add(this.motionY * 8000.0)
        (list)!!.add(this.motionZ * 8000.0)
        (list)!!.add(this.lastMotionY * 8000.0)
        (list)!!.add(this.lastLastMotionY * 8000.0)

        (list)!!.add(this.getWaitForPlayer())

        (list)!!.add(if (this.statusMessage != null) this.statusMessage else "")
        (list)!!.add(this.statusMessageCooldown)
        (list)!!.add(this.lastStatusMessageCooldown)
        (list)!!.add(this.statusValid)

        if (!this.world.isRemote) {
            (list)!!.add(if (this.getPassengers().isEmpty()) -1 else this.getPassengers().get(0).getId())
        }
        (list)!!.add(if (this.statusColour != null) this.statusColour else "")
    }

    override fun failRocket() {
        this.stopRocketSound()

        if (this.shouldCancelExplosion()) {
            //TODO: why looking around when already know the target?
            //TODO: it would be good to land on an alternative neighbouring pad if there is already a rocket on the target pad
            for (i: Int in -3..3) {
                val pos: BlockPos =
                    BlockPos(floor(this.posX).toInt(), floor(this.posY + i).toInt(), floor(this.posZ).toInt())
                if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null && this.world.getBlockEntity(
                        pos
                    ) is IFuelDock && this.posY - this.targetVec!!.getY() < 5
                ) {
                    for (x: Int in MathHelper.floor(this.posX) - 1..MathHelper.floor(this.posX) + 1) {
                        for (y: Int in MathHelper.floor(this.posY - 3.0)..MathHelper.floor(this.posY) + 1) {
                            for (z: Int in MathHelper.floor(this.posZ) - 1..MathHelper.floor(this.posZ) + 1) {
                                val pos1: BlockPos = BlockPos(x, y, z)
                                val tile: TileEntity? = this.world.getBlockEntity(pos1)

                                if (tile is IFuelDock) {
                                    this.landEntity(pos1)
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }

        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal) {
            super.failRocket()
        }
    }

    protected open fun shouldCancelExplosion(): Boolean {
        return this.hasValidFuel()
    }

    override fun hasValidFuel(): Boolean {
        return this.fuelTank.getFluidAmount() > 0
    }

    fun cancelLaunch() {
        this.setLaunchPhase(EnumLaunchPhase.UNIGNITED)
        this.timeUntilLaunch = 0
        sendLaunchWarning("gui.rocket.warning.nogyroscope")
    }

    fun failMessageLaunchController() {
        sendLaunchWarning("gui.rocket.warning.launchcontroller")
    }

    fun failMessageInsufficientFuel() {
        sendLaunchWarning("gui.rocket.warning.fuelinsufficient")
    }

    private fun sendLaunchWarning(key: String) {
        if (!level().isClientSide) {
            (passengers.firstOrNull() as? EntityPlayerMP)?.sendSystemMessage(Component.translatable(key))
        }
    }

    override fun onLaunch() {
        if (!(this.world.provider.getDimension() == (GalacticraftCore.planetOverworld)!!.getDimensionID() || this.world.provider is IGalacticraftWorldProvider)) {
            if (ConfigManagerCore.disableRocketLaunchAllNonGC) {
                this.cancelLaunch()
                return
            }


            //No rocket flight in the Nether, the End etc
            for (i: Int in (ConfigManagerCore.disableRocketLaunchDimensions)!!.indices.reversed()) {
                if (((ConfigManagerCore)!!.disableRocketLaunchDimensions)!![i] == this.world.provider.getDimension()) {
                    this.cancelLaunch()
                    return
                }
            }
        }

        super.onLaunch()

        if (!this.world.isRemote) {
            var stats: GCPlayerStats? = null

            if (!this.getPassengers().isEmpty()) {
                for (player: Entity? in this.getPassengers()) {
                    if (player is EntityPlayerMP) {
                        stats = GCPlayerStats.get(player)
                        (stats)!!.setLaunchpadStack(null)

                        if (this.world.provider !is IOrbitDimension) {
                            (stats)!!.setCoordsTeleportedFromX(player.x)
                            (stats)!!.setCoordsTeleportedFromZ(player.z)
                        }
                    }
                }

                val playerMain: Entity? = this.getPassengers().get(0)
                if (playerMain is EntityPlayerMP) stats = GCPlayerStats.get(playerMain)
            }

            var amountRemoved: Int = 0

            PADSEARCH@ for (x: Int in MathHelper.floor(this.posX) - 1..MathHelper.floor(this.posX) + 1) {
                for (y: Int in MathHelper.floor(this.posY) - 3..MathHelper.floor(this.posY) + 1) {
                    for (z: Int in MathHelper.floor(this.posZ) - 1..MathHelper.floor(this.posZ) + 1) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        val block: Block = this.world.getBlockState(pos).getBlock()

                        if (block != null && block is BlockLandingPadFull) {
                            if (amountRemoved < 9) {
                                val event: EventLandingPadRemoval = EventLandingPadRemoval(this.world, pos)
                                MinecraftForge.EVENT_BUS.post(event)

                                if (event.allow) {
                                    this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                                    amountRemoved = 9
                                }
                                break@PADSEARCH
                            }
                        }
                    }
                }
            }

            //Set the player's launchpad item for return on landing - or null if launchpads not removed
            if (stats != null && amountRemoved == 9) {
                stats.setLaunchpadStack(ItemStack(GCBlocks.landingPad, 9, 0))
            }

            this.playSound(
                SoundEvents.ENTITY_ITEM_PICKUP,
                0.2f,
                ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f
            )
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)

        if (this.fuelTank.getFluid() != null) {
            nbt.put("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }

        if (this.getContainerSize() > 0) {
            ItemStackHelper.saveAllItems(nbt, this.stacks)
        }

        nbt.putBoolean("TargetValid", this.targetVec != null)

        if (this.targetVec != null) {
            nbt.putDouble("targetTileX", this.targetVec!!.getX().toDouble())
            nbt.putDouble("targetTileY", this.targetVec!!.getY().toDouble())
            nbt.putDouble("targetTileZ", this.targetVec!!.getZ().toDouble())
        }

        nbt.putBoolean("WaitingForPlayer", this.getWaitForPlayer())
        nbt.putBoolean("Landing", this.launchPhase == EnumLaunchPhase.LANDING.ordinal)
        nbt.putInt(
            "AutoLaunchSetting",
            if (this.autoLaunchSetting != null) this.autoLaunchSetting!!.getIndex() else -1
        )
        nbt.putInt("TimeUntilAutoLaunch", this.autoLaunchCountdown)
        nbt.putInt("DestinationFrequency", this.destinationFrequency)
        if (this.activeLaunchController != null) this.activeLaunchController!!.writeToNBT(nbt, "ALCat")
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)

        if (nbt.containsTag("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundOrEmpty("fuelTank"))
        }

        if (this.getContainerSize() > 0) {
            this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
            ItemStackHelper.loadAllItems(nbt, this.stacks)
        }

        if (nbt.getBooleanOr("TargetValid", false) && nbt.containsTag("targetTileX")) {
            this.targetVec = BlockPos(
                MathHelper.floor(nbt.getDoubleOr("targetTileX", 0.0)),
                MathHelper.floor(nbt.getDoubleOr("targetTileY", 0.0)),
                MathHelper.floor(nbt.getDoubleOr("targetTileZ", 0.0))
            )
        }

        this.setWaitForPlayer(nbt.getBooleanOr("WaitingForPlayer", false))
        val autoLaunchValue: Int = nbt.getIntOr("AutoLaunchSetting", 0)
        this.autoLaunchSetting = if (autoLaunchValue == -1) null else EnumAutoLaunch.entries[autoLaunchValue]
        this.autoLaunchCountdown = nbt.getIntOr("TimeUntilAutoLaunch", 0)
        this.destinationFrequency = nbt.getIntOr("DestinationFrequency", 0)
        this.activeLaunchController = BlockVec3.readFromNBT(nbt, "ALCat")
    }

    override fun addFuel(liquid: FluidStack?, doFill: Boolean): Int {
        return FluidUtil.fillWithGCFuel(this.fuelTank, liquid, doFill)
    }

    override fun removeFuel(amount: Int): FluidStack? {
        return this.fuelTank.drain(amount * ConfigManagerCore.rocketFuelFactor, true)
    }

    override fun setPad(pad: IFuelDock?) {
        //Called either when a rocket lands or when one is placed
        //Can also be called with null param when rocket leaves a pad
        this.landingPad = pad
        if (pad != null) {
            pad.dockEntity(this)
            //NOTE: setPad() is called also when a world or chunk is loaded - if the rocket is Ignited (from NBT save data) do not change those settings
            if (this.launchPhase != EnumLaunchPhase.IGNITED.ordinal) {
                this.setLaunchPhase(EnumLaunchPhase.UNIGNITED)
                this.targetVec = null
                if (GalacticraftCore.isPlanetsLoaded) {
                    this.updateControllerSettings(pad)
                }
            }
        }
    }

    override fun getLandingPad(): IFuelDock? {
        return this.landingPad
    }

    override fun getMaxFuel(): Int {
        return this.fuelTank.getCapacity()
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return (dock is TileEntityLandingPad)
    }

    override fun addCargo(stack: ItemStack?, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState {
        if (this.getContainerSize() <= 3) {
            if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
                this.autoLaunch()
            }

            return ICargoEntity.EnumCargoLoadingState.NOINVENTORY
        }

        var count: Int = 0

        count = 0
        while (count < this.stacks!!.size - 2) {
            val stackAt: ItemStack = this.stacks!!.get(count)

            if (!stackAt.isEmpty() && stackAt.getItem() === (stack)!!.getItem() && stackAt.legacyItemValue == (stack)!!.legacyItemValue && stackAt.getCount() < stackAt.getMaxStackSize()) {
                if (stackAt.getCount() + (stack)!!.getCount() <= stackAt.getMaxStackSize()) {
                    if (doAdd) {
                        stackAt.grow((stack)!!.getCount())
                        this.setChanged()
                    }

                    return ICargoEntity.EnumCargoLoadingState.SUCCESS
                } else {
                    //Part of the stack can fill this slot but there will be some left over
                    val origSize: Int = stackAt.getCount()
                    val surplus: Int = origSize + (stack)!!.getCount() - stackAt.getMaxStackSize()

                    if (doAdd) {
                        stackAt.setCount(stackAt.getMaxStackSize())
                        this.setChanged()
                    }

                    (stack)!!.setCount(surplus)
                    if (this.addCargo(stack, doAdd) == ICargoEntity.EnumCargoLoadingState.SUCCESS) {
                        return ICargoEntity.EnumCargoLoadingState.SUCCESS
                    }

                    stackAt.setCount(origSize)
                    if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
                        this.autoLaunch()
                    }
                    return ICargoEntity.EnumCargoLoadingState.FULL
                }
            }
            count++
        }

        count = 0
        while (count < this.stacks!!.size - 2) {
            val stackAt: ItemStack = this.stacks!!.get(count)

            if (stackAt.isEmpty()) {
                if (doAdd) {
                    this.stacks!!.set(count, stack)
                    this.setChanged()
                }

                return ICargoEntity.EnumCargoLoadingState.SUCCESS
            }
            count++
        }

        if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_FULL) {
            this.autoLaunch()
        }

        return ICargoEntity.EnumCargoLoadingState.FULL
    }

    override fun removeCargo(doRemove: Boolean): ICargoEntity.RemovalResult {
        for (i: Int in 0..<this.stacks!!.size - 2) {
            val stackAt: ItemStack = this.stacks!!.get(i)

            if (!stackAt.isEmpty()) {
                val resultStack: ItemStack = stackAt.copy()
                resultStack.setCount(1)

                if (doRemove) {
                    stackAt.shrink(1)
                }

                if (doRemove) {
                    this.setChanged()
                }
                return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.SUCCESS, resultStack)
            }
        }

        if (this.autoLaunchSetting == EnumAutoLaunch.CARGO_IS_UNLOADED) {
            this.autoLaunch()
        }

        return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.EMPTY, ItemStack.EMPTY)
    }

    override fun getItem(index: Int): ItemStack {
        if (this.stacks == null) {
            return ItemStack.EMPTY
        }

        return this.stacks!!.get(index)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        return ContainerHelper.takeItem(this.stacks, index)
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks!!.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun stillValid(entityplayer: EntityPlayer): Boolean {
        return !this.isDead && entityplayer.getDistanceSq(this) <= 64.0
    }

    override fun canPlaceItem(i: Int, itemstack: ItemStack): Boolean {
        return false
    }

    override fun setChanged() {
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks!!) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun onPadDestroyed() {
        if (!this.isDead && this.launchPhase < EnumLaunchPhase.LAUNCHED.ordinal) {
            this.dropShipAsItem()
            this.setDead()
        }
    }

    override fun getItemsDropped(droppedItemList: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        if (this.stacks != null) {
            for (item: ItemStack? in (this.stacks)!!) {
                if (item != null && !item.isEmpty()) {
                    (droppedItemList)!!.add(item)
                }
            }
        }

        return (droppedItemList as MutableList<ItemStack>?)
    }

    open fun getWaitForPlayer(): Boolean {
        return this.waitForPlayer
    }

    open fun setWaitForPlayer(waitForPlayer: Boolean) {
        this.waitForPlayer = waitForPlayer
    }

    enum class EnumAutoLaunch
        (private val index: Int, private val title: String) {
        CARGO_IS_UNLOADED(0, "cargo_unloaded"),
        CARGO_IS_FULL(1, "cargo_full"),
        ROCKET_IS_FUELED(2, "fully_fueled"),
        INSTANT(3, "instant"),
        TIME_10_SECONDS(4, "ten_sec"),
        TIME_30_SECONDS(5, "thirty_sec"),
        TIME_1_MINUTE(6, "one_min"),
        REDSTONE_SIGNAL(7, "redstone_sig");

        fun getIndex(): Int {
            return this.index
        }

        fun getTitle(): String? {
            return GCCoreUtil.translate("gui.message." + this.title + ".name")
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSoundUpdater(): ITickable? {
        return this.rocketSoundUpdater
    }

    @OnlyIn(Dist.CLIENT)
    override fun setSoundUpdater(player: EntityPlayerSP?): ISound {
        this.rocketSoundUpdater = SoundUpdaterRocket(player, this)
        return this.rocketSoundUpdater as ISound
    }

    @OnlyIn(Dist.CLIENT)
    override fun isInRangeToRender3d(x: Double, y: Double, z: Double): Boolean {
        val d0: Double = this.posX - x
        val d1: Double = this.posY - y
        val d2: Double = this.posZ - z
        val d3: Double = d0 * d0 + d1 * d1 + d2 * d2
        return d3 < Constants.RENDERDISTANCE_LONG
    }

    override fun inFlight(): Boolean {
        return this.getLaunched()
    }

    companion object {
        private var controllerClass: Class<*>? = null

        init {
            try {
                controllerClass =
                    Class.forName("micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController")
            } catch (e: ClassNotFoundException) {
                GCLog.info("Galacticraft-Planets' LaunchController not present, rockets will not be launch controlled.")
            }
        }
    }
}
