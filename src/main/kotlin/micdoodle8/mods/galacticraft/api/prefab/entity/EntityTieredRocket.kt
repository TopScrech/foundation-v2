package micdoodle8.mods.galacticraft.api.prefab.entity

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.entity.IWorldTransferCallback
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.max

/**
 * Do not include this prefab class in your released mod download.
 */
abstract class EntityTieredRocket : EntityAutoRocket, IRocketType, IDockable, IWorldTransferCallback,
    ICameraZoomEntity {
    @JvmField
    var rocketType: IRocketType.EnumRocketType? = null

    @JvmField
    var rumble: Float = 0f

    @JvmField
    var launchCooldown: Int = 0
    private val preGenList = ArrayList<BlockVec3?>()
    private var preGenIterator: MutableIterator<BlockVec3>? = null

    constructor(par1World: World?) : super(par1World) {
        this.setSize(0.98f, 4f)
        //        this.yOffset = this.height / 2.0F;
    }

    constructor(world: World?, posX: Double, posY: Double, posZ: Double) : super(world, posX, posY, posZ)

    open fun igniteCheckingCooldown() {
        if (!this.world.isRemote && this.launchCooldown <= 0) {
            this.initiatePlanetsPreGen(this.chunkCoordX, this.chunkCoordZ)

            this.ignite()
        }
    }

    private fun initiatePlanetsPreGen(cx: Int, cz: Int) {
        this.preGenList.clear()

        //Pre-generate terrain on all possible destination planets if the destination is not being controlled by a Launch Controller
        //(note: this does NOT include the Moon!)

        //This generates with a chunk radius of 12: so for 2 planets that's 1250 chunks to pregen
        //It starts at the centre and generates in circles radiating out in case it doesn't have time to finish
        //These will be done: 2 chunks per tick during IGNITE phase (so 800 chunks during the 20 second launch countdown)
        //then the ones that are left 1 chunk per tick during flight (normally flight will last more than 450 ticks)
        //If the server is at less than 20tps then maybe some of the outermost chunks won't be pre-generated but that's probably OK
        if (this.destinationFrequency == -1 && !preGenInProgress) {
            val toPreGen: ArrayList<Int> = ArrayList<Int>()
            for (planet: Planet in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
                if (planet.getDimensionID() == this.dimension) {
                    continue
                }
                if (planet.getReachable() && planet.getTierRequirement() <= this.getRocketTier() && (planet.getTranslationKey() != "planet.asteroids")) {
                    toPreGen.add(planet.getDimensionID())
                }
            }

            if (toPreGen.size > 0) {
                for (dimID: Int in toPreGen) {
                    this.preGenList.add(BlockVec3(cx, dimID, cz))
                    if (ConfigManagerCore.enableDebug) {
                        GCLog.info("Starting terrain pregen for dimension " + dimID + " at " + (cx * 16 + 8) + ", " + (cz * 16 + 8))
                    }
                }
                for (r: Int in 1..11)  //concentric squares with radius r
                {
                    val xmin: Int = cx - r
                    val xmax: Int = cx + r
                    val zmin: Int = cz - r
                    val zmax: Int = cz + r
                    for (i: Int in -r..<r)  //stop before i == r to avoid doing corners twice
                    {
                        for (dimID: Int in toPreGen) {
                            this.preGenList.add(BlockVec3(xmin, dimID, cz + i))
                            this.preGenList.add(BlockVec3(xmax, dimID, cz - i))
                            this.preGenList.add(BlockVec3(cx - i, dimID, zmin))
                            this.preGenList.add(BlockVec3(cx + i, dimID, zmax))
                        }
                    }
                }
                this.preGenIterator = (this.preGenList.iterator() as MutableIterator<BlockVec3>?)
                preGenInProgress = true
            }
        } else {
            this.preGenIterator = null
        }
    }

    override fun onUpdate() {
        if (this.getWaitForPlayer()) {
            if (!this.getPassengers().isEmpty()) {
                val passenger: Entity = this.getPassengers().get(0)
                if (this.ticks >= 40) {
                    if (!this.world.isRemote) {
                        this.removePassengers()
                        passenger.startRiding(this, true)
                        GCLog.debug("Remounting player in rocket.")
                    }

                    this.setWaitForPlayer(false)
                    this.motionY = -0.5
                } else {
                    this.motionZ = 0.0
                    this.motionY = this.motionZ
                    this.motionX = this.motionY
                    passenger.motionZ = 0.0
                    passenger.motionY = passenger.deltaMovement.z
                    passenger.motionX = passenger.deltaMovement.y
                }
            } else {
                this.motionZ = 0.0
                this.motionY = this.motionZ
                this.motionX = this.motionY
            }
        }

        super.onUpdate()

        if (!this.world.isRemote) {
            if (this.launchCooldown > 0) {
                this.launchCooldown--
            }

            if (this.preGenIterator != null) {
                if (this.preGenIterator!!.hasNext()) {
                    val mcserver: MinecraftServer?
                    if (this.world is WorldServer) {
                        mcserver = (this.world as WorldServer).getMinecraftServer()
                        var coords: BlockVec3 = this.preGenIterator!!.next()
                        var w: World = mcserver!!.getWorld(coords.y)
                        if (w != null) {
                            w.getChunk(coords.x, coords.z)
                            //Pregen a second chunk if still on launchpad (low strain on server)
                            if (this.launchPhase < EnumLaunchPhase.LAUNCHED.ordinal && this.preGenIterator!!.hasNext()) {
                                coords = this.preGenIterator!!.next()
                                w = mcserver.getWorld(coords.y)
                                w.getChunk(coords.x, coords.z)
                            }
                        }
                    }
                } else {
                    this.preGenIterator = null
                    preGenInProgress = false
                }
            }
        }

        if (this.rumble > 0) {
            this.rumble--
        } else if (this.rumble < 0) {
            this.rumble++
        }

        val rumbleAmount: Double = this.rumble / (37 - 5 * max(this.getRocketTier(), 5)).toDouble()
        for (passenger: Entity in this.getPassengers()) {
            passenger.posX += rumbleAmount
            passenger.posZ += rumbleAmount
        }

        if (this.launchPhase >= EnumLaunchPhase.IGNITED.ordinal) {
            this.performHurtAnimation()

            this.rumble = this.rand.nextInt(3).toFloat() - 3
        }

        if (!this.world.isRemote) {
            this.lastLastMotionY = this.lastMotionY
            this.lastMotionY = this.motionY
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        this.rocketType = (IRocketType.EnumRocketType.entries)!![(buffer)!!.readInt()]
        super.decodePacketdata(buffer)

        if ((buffer)!!.readBoolean()) {
            this.posX = (buffer)!!.readDouble() / 8000.0
            this.posY = (buffer)!!.readDouble() / 8000.0
            this.posZ = (buffer)!!.readDouble() / 8000.0
        }
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }
        (list)!!.add(if (this.rocketType != null) this.rocketType!!.getIndex() else 0)
        super.getNetworkedData(list)

        val sendPosUpdates: Boolean = this.ticks < 25 || this.launchPhase < EnumLaunchPhase.LAUNCHED.ordinal
        (list)!!.add(sendPosUpdates)

        if (sendPosUpdates) {
            (list)!!.add(this.posX * 8000.0)
            (list)!!.add(this.posY * 8000.0)
            (list)!!.add(this.posZ * 8000.0)
        }
    }

    override fun onReachAtmosphere() {
        //Launch controlled
        if (this.destinationFrequency != -1) {
            if (this.world.isRemote) {
                //stop the sounds on the client - but do not reset, the rocket may start again
                this.stopRocketSound()
                return
            }

            this.setTarget(true, this.destinationFrequency)

            if (this.targetVec != null) {
                if (this.targetDimension != this.world.provider.getDimension()) {
                    val targetDim: WorldProvider? = WorldUtil.getProviderForDimensionServer(this.targetDimension)
                    if (targetDim != null && targetDim.world is WorldServer) {
                        var dimensionAllowed: Boolean = this.targetDimension == ConfigManagerCore.idDimensionOverworld

                        if (targetDim is IGalacticraftWorldProvider) {
                            if ((targetDim as IGalacticraftWorldProvider).canSpaceshipTierPass(this.getRocketTier())) dimensionAllowed =
                                true
                            else dimensionAllowed = false
                        } else  //No rocket flight to non-Galacticraft dimensions other than the Overworld allowed unless config
                            if ((this.targetDimension > 1 || this.targetDimension < -1) && marsConfigAllDimsAllowed != null) {
                                try {
                                    if (marsConfigAllDimsAllowed!!.getBoolean(null)) {
                                        dimensionAllowed = true
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                        if (dimensionAllowed) {
                            if (!this.getPassengers().isEmpty()) {
                                for (passenger: Entity? in this.getPassengers()) {
                                    if (passenger is EntityPlayerMP) {
                                        WorldUtil.transferEntityToDimension(
                                            passenger,
                                            this.targetDimension,
                                            (targetDim.world as WorldServer?)!!,
                                            false,
                                            this
                                        )
                                    }
                                }
                            } else {
                                val e: Entity? = WorldUtil.transferEntityToDimension(
                                    this,
                                    this.targetDimension,
                                    (targetDim.world as WorldServer?)!!,
                                    false,
                                    null
                                )
                                if (e is EntityAutoRocket) {
                                    e.setPosition(
                                        (((this.targetVec)!!.getX() + 0.5f)!!).toDouble(),
                                        (((this.targetVec)!!.getY() + 800)!!).toDouble(),
                                        (((this.targetVec)!!.getZ() + 0.5f)!!).toDouble()
                                    )
                                    e.setLaunchPhase(EnumLaunchPhase.LANDING)
                                    e.setWaitForPlayer(false)
                                } else {
                                    GCLog.info("Error: failed to recreate the unmanned rocket in landing mode on target planet.")
                                    e!!.setDead()
                                    this.setDead()
                                }
                            }
                            return
                        }
                    }
                    //No destination world found - in this situation continue into regular take-off (as if Not launch controlled)
                } else {
                    //Same dimension controlled rocket flight
                    this.setPosition(
                        (((this.targetVec)!!.getX() + 0.5f)!!).toDouble(),
                        (((this.targetVec)!!.getY() + 800)!!).toDouble(),
                        (((this.targetVec)!!.getZ() + 0.5f)!!).toDouble()
                    )
                    //Stop any lateral motion, otherwise it will update to an incorrect x,z position first tick after spawning above target
                    this.motionZ = 0.0
                    this.motionX = this.motionZ
                    //Small upward motion initially, to keep clear of own flame trail from launch
                    this.motionY = 0.1
                    for (passenger: Entity? in this.getPassengers()) {
                        if (passenger is EntityPlayerMP) {
                            WorldUtil.forceMoveEntityToPos(
                                passenger,
                                (this.world as WorldServer?)!!,
                                Vector3(
                                    (((this.targetVec)!!.getX() + 0.5f)!!).toDouble(),
                                    (((this.targetVec)!!.getY() + 800)!!).toDouble(),
                                    (((this.targetVec)!!.getZ() + 0.5f)!!).toDouble()
                                ),
                                false
                            )
                            this.setWaitForPlayer(true)
                            GCLog.debug("Rocket repositioned, waiting for player")
                        }
                    }
                    this.setLaunchPhase(EnumLaunchPhase.LANDING)
                    //Do not destroy the rocket, we still need it!
                    return
                }
            } else {
                //Launch controlled launch but no valid target frequency = rocket loss [INVESTIGATE]
                GCLog.info("Error: the launch controlled rocket failed to find a valid landing spot when it reached space.")
                this.fuelTank.drain(Int.MAX_VALUE, true)
                this.posY = max(
                    255.0,
                    (if (this.world.provider is IExitHeight) (this.world.provider as IExitHeight).getYCoordinateToTeleport() else 1200.0) - 200
                )
                return
            }
        }

        //Not launch controlled
        if (!this.world.isRemote) {
            for (e: Entity? in this.getPassengers()) {
                if (e is EntityPlayerMP) {
                    val player: EntityPlayerMP = e

                    this.onTeleport(player)
                    val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                    WorldUtil.toCelestialSelection(player, stats, this.getRocketTier())
                }
            }

            //Destroy any rocket which reached the top of the atmosphere and is not controlled by a Launch Controller
            this.setDead()
        }


        //Client side, non-launch controlled, do nothing - no reason why it can't continue flying until the GUICelestialSelection activates
    }

    override fun shouldCancelExplosion(): Boolean {
        return this.hasValidFuel() && abs(this.lastLastMotionY) < 4
    }

    open fun onTeleport(player: EntityPlayerMP?) {
    }

    override fun onRocketLand(pos: BlockPos) {
        super.onRocketLand(pos)
        this.launchCooldown = 40
    }

    override fun onLaunch() {
        super.onLaunch()
    }

    override fun shouldMoveClientSide(): Boolean {
        return true
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (hand != EnumHand.MAIN_HAND) {
            return false
        }

        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal) {
            return false
        }

        if (!this.getPassengers().isEmpty() && this.getPassengers().contains(player)) {
            if (!this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                        this.world.provider.getDimension(),
                        arrayOf<Any?>()
                    ), player as EntityPlayerMP
                )
                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                stats.setChatCooldown(0)
                // Prevent player being dropped from the top of the rocket...
                val heightBefore: Float = this.height
                this.height = this.height / 2.0f
                this.removePassengers()
                this.height = heightBefore
            }

            return true
        } else if (player is EntityPlayerMP) {
            if (!this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.C_DISPLAY_ROCKET_CONTROLS,
                        this.world.provider.getDimension(),
                        arrayOf<Any?>()
                    ), player
                )
                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                stats.setChatCooldown(0)
                player.startRiding(this)
            }

            return true
        }

        return false
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        nbt.putInt("Type", this.rocketType!!.getIndex())
        super.writeEntityToNBT(nbt)
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.rocketType = IRocketType.EnumRocketType.entries[nbt.getIntOr("Type", 0)]
        super.readEntityFromNBT(nbt)
    }

    override fun getType(): IRocketType.EnumRocketType? {
        return this.rocketType
    }

    override fun getContainerSize(): Int {
        if (this.rocketType == null) return 2
        return this.rocketType!!.getInventorySpace()
    }

    override fun onWorldTransferred(world: World?) {
        if (this.targetVec != null) {
            this.setPosition(
                (((this.targetVec)!!.getX() + 0.5f)!!).toDouble(),
                (((this.targetVec)!!.getY() + 800)!!).toDouble(),
                (((this.targetVec)!!.getZ() + 0.5f)!!).toDouble()
            )
            this.setLaunchPhase(EnumLaunchPhase.LANDING)
            this.setWaitForPlayer(true)
            this.motionZ = 0.0
            this.motionY = this.motionZ
            this.motionX = this.motionY
        } else {
            this.setDead()
        }
    }

    override fun getRotateOffset(): Float {
        return -1.5f
    }

    override fun isPlayerRocket(): Boolean {
        return true
    }

    companion object {
        @JvmField
        var preGenInProgress: Boolean = false

        @JvmField
        var marsConfigAllDimsAllowed: Field? = null

        init {
            try {
                val marsConfig: Class<*> = Class.forName("micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars")
                marsConfigAllDimsAllowed = marsConfig.getField("launchControllerAllDims")
            } catch (ignore: Exception) {
            }
        }
    }
}
