package galaxyspace.core.prefab.entities

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.core.GSBlocks
import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import io.netty.buffer.ByteBuf
import asmodeuscore.core.handler.capabilities.ACStatsCapability
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockAdvancedLandingPadFull
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPad
import galaxyspace.core.util.GSUtils
import asmodeuscore.api.entity.ICustomEngine
import asmodeuscore.core.handler.capabilities.IStatsCapability
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import galaxyspace.core.prefab.entities.EntityTier6Rocket
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.event.EventLandingPadRemoval
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraft.world.WorldServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntityTier6Rocket : EntityTieredRocket, ICustomEngine {
    private var engine_type = Engine_Type.FUEL_ENGINE
    private var waitFormMultiPlayer = false

    constructor(par1World: World?) : super(par1World) {
        this.setSize(2.2f, 11f)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?,
        engine: Engine_Type
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getSizeInventory(), ItemStack.EMPTY)
        this.engine_type = engine
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        reversed: Boolean,
        rocketType: IRocketType.EnumRocketType?,
        inv: NonNullList<ItemStack>?
    ) : this(par1World, par2, par4, par6, rocketType) {
        this.stacks = inv
    }

    override fun getYOffset(): Double {
        return 1.4
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GSItems.ROCKET_TIER_6, 1, (this.rocketType)!!.getIndex())
    }

    override fun entityInit() {
        super.entityInit()
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            passenger.setPosition(
                this.posX,
                this.posY + this.getMountedYOffset(passenger) + passenger.getYOffset(),
                this.posZ
            )
        }
    }

    open fun getMountedYOffset(passenger: Entity?): Double {
        if (this.getPassengers().size > 1) {
            if (this.getPassengers().get(0) == passenger) return 5.0
            else return 1.75
        }

        return 2.75
    }

    override fun igniteCheckingCooldown() {
        super.igniteCheckingCooldown()
    }


    @Deprecated("")
    override fun getMountedYOffset(): Double {
        return 2.75
    }

    override fun getRotateOffset(): Float {
        return 1.25f
    }

    override fun getOnPadYOffset(): Double {
        return 0.0
    }

    override fun onLaunch() {
        super.onLaunch()

        if (!this.world.isRemote) {
            var stats: GCPlayerStats? = null

            if (!this.getPassengers().isEmpty()) {
                for (player: Entity? in this.getPassengers()) {
                    if (player is EntityPlayerMP) {
                        stats = GCPlayerStats.get(player)
                        (stats)!!.setLaunchpadStack(null)

                        if (this.world.provider !is IOrbitDimension) {
                            (stats)!!.setCoordsTeleportedFromX(player.posX)
                            (stats)!!.setCoordsTeleportedFromZ(player.posZ)
                        }
                    }
                }

                val playerMain: Entity? = this.getPassengers().get(0)
                if (playerMain is EntityPlayerMP) stats = GCPlayerStats.get(playerMain)
            }

            var amountRemoved: Int = 0

            PADSEARCH@ for (x: Int in MathHelper.floor(this.posX) - 2..MathHelper.floor(this.posX) + 2) {
                for (y: Int in MathHelper.floor(this.posY) - 3..MathHelper.floor(this.posY) + 1) {
                    for (z: Int in MathHelper.floor(this.posZ) - 2..MathHelper.floor(this.posZ) + 2) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        val block: Block = this.world.getBlockState(pos).getBlock()

                        if (block != null && block is BlockAdvancedLandingPadFull) {
                            if (amountRemoved < 25) {
                                val event: EventLandingPadRemoval = EventLandingPadRemoval(this.world, pos)
                                MinecraftForge.EVENT_BUS.post(event)

                                if (event.allow) {
                                    this.world.setBlockToAir(pos)
                                    amountRemoved = 25
                                }
                                break@PADSEARCH
                            }
                        }
                    }
                }
            }

            //Set the player's launchpad item for return on landing - or null if launchpads not removed
            if (stats != null && amountRemoved == 25) {
                stats.setLaunchpadStack(ItemStack(GSBlocks.ADVANCED_LANDING_PAD_SINGLE, 25, 0))
            }

            this.playSound(
                SoundEvents.ENTITY_ITEM_PICKUP,
                0.2f,
                ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f
            )
        }
    }

    override fun onUpdate() {
        //TODO: HARDCODE
        if (this.getWaitForMultiPlayer()) {
            if (!this.getPassengers().isEmpty()) {
                val passenger: Entity = this.getPassengers().get(0)
                var passenger2: Entity? = null

                if (this.getPassengers().size > 1) {
                    passenger2 = this.getPassengers().get(1)
                }

                if (this.ticks >= 40) {
                    if (!this.world.isRemote) {
                        this.removePassengers()
                        passenger.startRiding(this, true)
                        if (passenger2 != null) passenger2.startRiding(this, true)
                        GCLog.debug("Remounting player in rocket.")
                    }

                    this.setWaitForPlayer(false)
                    this.motionY = -0.5
                } else {
                    this.motionZ = 0.0
                    this.motionY = this.motionZ
                    this.motionX = this.motionY
                    passenger.motionZ = 0.0
                    passenger.motionY = passenger.motionZ
                    passenger.motionX = passenger.motionY
                    if (passenger2 != null) {
                        passenger2.motionZ = 0.0
                        passenger2.motionY = passenger2.motionZ
                        passenger2.motionX = passenger2.motionY
                    }
                }
            } else {
                this.motionZ = 0.0
                this.motionY = this.motionZ
                this.motionX = this.motionY
            }
        }

        super.onUpdate()

        val i: Int

        if (this.timeUntilLaunch >= 100) {
            i = abs(this.timeUntilLaunch / 100)
        } else {
            i = 1
        }

        if ((this.getLaunched() || this.launchPhase == EnumLaunchPhase.IGNITED.ordinal && this.rand.nextInt(i) == 0) && !ConfigManagerCore.disableSpaceshipParticles && this.hasValidFuel()) {
            if (this.world.isRemote) {
                this.spawnParticles(this.getLaunched())
            }
        }

        if (FMLCommonHandler.instance()
                .getEffectiveSide() == Side.CLIENT && this.hasValidFuel() && this.rand.nextInt(4) == 0 && !this.getLaunched() && this.getEngine() == Engine_Type.FUEL_ENGINE
        ) {
            //GalacticraftCore.proxy.spawnParticle("whiteSmokeLargeLaunched", new Vector3(this.posX + 0.4 - this.rand.nextDouble(), this.posY + 10, this.posZ + 0.4 - this.rand.nextDouble()), new Vector3(0.5D, -1.5D, 0.5D), new Object[] { });
            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX + 0.4 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + rand.nextDouble()
                ),
                Vector3(0.05 + 0.06, -0.5, 0.0 - 0.03),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.8 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + rand.nextDouble()
                ),
                Vector3(0.05 - 0.25, -0.5, 0.0 - 0.03),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.2 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ + 0.4 + rand.nextDouble()
                ),
                Vector3(0.0, -0.5, 0.1 + 0.06),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )

            (GalaxySpace.proxy)!!.spawnParticle(
                "waterbubbles",
                Vector3(
                    this.posX - 0.2 + rand.nextDouble(),
                    this.posY - 0.4 + rand.nextDouble(),
                    this.posZ - 0.4 + rand.nextDouble()
                ),
                Vector3(0.0, -0.5, -0.1 - 0.06),
                (arrayOf<Any>(20, 5, false, Vector3(1.0), 1.0) as Array<Any?>?)
            )
        }

        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal && this.hasValidFuel()) {
            if (this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal) {
                var d: Double = (this.timeSinceLaunch / 150).toDouble()

                var mod_engine: Int = 1
                if (this.engine_type == Engine_Type.SUBLIGHT_ENGINE) mod_engine = 5

                if (this.world.provider is IGalacticraftWorldProvider && (this.world.provider as IGalacticraftWorldProvider).hasNoAtmosphere()) {
                    d = min(d * 1.2 * mod_engine, (2 * mod_engine).toDouble())
                } else {
                    d = min(d * mod_engine, 1.4 * mod_engine)
                }

                if (d != 0.0) {
                    this.motionY =
                        -d * 2.5 * mod_engine * cos(((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES).toDouble())
                }
            } else {
                this.motionY -= 0.008
            }

            var multiplier: Double = 1.0

            if (this.world.provider is IGalacticraftWorldProvider) {
                multiplier = (this.world.provider as IGalacticraftWorldProvider).getFuelUsageMultiplier()

                if (multiplier <= 0) {
                    multiplier = 1.0
                }
            }

            if (this.timeSinceLaunch % MathHelper.floor(2 * (1 / multiplier)) == 0f) {
                this.removeFuel(4)
                if (!this.hasValidFuel()) {
                    this.stopRocketSound()
                }
            }
        } else if (!this.hasValidFuel() && this.getLaunched() && !this.world.isRemote) {
            if (abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 10 != 0.0) {
                this.motionY -= abs(sin((this.timeSinceLaunch / 1000).toDouble())) / 20
            }
        }


        //GalaxySpace.debug(this.getEngine().getName());
    }

    override fun onTeleport(player: EntityPlayerMP?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)


        if (playerBase != null) {
            val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

            if (this.stacks == null || (this.stacks)!!.isEmpty()) {
                stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
            } else {
                stats.setRocketStacks(this.stacks)
            }

            stats.setRocketType((this.rocketType)!!.getIndex())
            stats.setFuelLevel(this.fuelTank.getFluidAmount())


            if (engine_type == Engine_Type.SUBLIGHT_ENGINE) {
                val withengine: ItemStack = ItemStack(GSItems.ROCKET_TIER_6, 1, (this.rocketType)!!.getIndex())
                if (!withengine.hasTagCompound()) withengine.setTagCompound(NBTTagCompound())
                withengine.getTagCompound()!!.setBoolean(Engine_Type.SUBLIGHT_ENGINE.getName(), true)
                withengine.getTagCompound()!!.setInteger(ItemSpaceSuit.mod_count, 0)

                for (i: Int in (stats.getRocketStacks())!!.indices) {
                    //ItemStack stack = stats.getRocketStacks().get(i);
                    if (i == (stats.getRocketStacks())!!.size - 1) {
                        (stats.getRocketStacks())!!.set(i, withengine)
                        break
                    }
                }


                //stats.getRocketStacks().set(1, withengine);
            } else stats.setRocketItem(GSItems.ROCKET_TIER_6)
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
                                        ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                                        ((this.targetVec)!!.getY() + 800).toDouble(),
                                        ((this.targetVec)!!.getZ() + 0.5f).toDouble()
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
                        ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                        ((this.targetVec)!!.getY() + 800).toDouble(),
                        ((this.targetVec)!!.getZ() + 0.5f).toDouble()
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
                                    ((this.targetVec)!!.getX() + 0.5f).toDouble(),
                                    ((this.targetVec)!!.getY() + 800).toDouble(),
                                    ((this.targetVec)!!.getZ() + 0.5f).toDouble()
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

        //TODO 2-mounts players.
        //Not launch controlled
        if (!this.world.isRemote) {
            for (e: Entity? in this.getPassengers()) {
                if (e is EntityPlayerMP) {
                    val player: EntityPlayerMP = e

                    this.onTeleport(player)

                    val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

                    if (this.getPassengers().size > 1) {
                        val secondPassenger: Entity? = this.getPassengers().get(1)
                        if (secondPassenger is EntityPlayerMP) {
                            val acstats: IStatsCapability = (ACStatsCapability.get(secondPassenger))!!
                            acstats.setSecondPassenger(true)
                        }
                    }

                    WorldUtil.toCelestialSelection(player, stats, this.getRocketTier())
                }
            }

            //Destroy any rocket which reached the top of the atmosphere and is not controlled by a Launch Controller
            this.setDead()
        }


        //Client side, non-launch controlled, do nothing - no reason why it can't continue flying until the GUICelestialSelection activates
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

    override fun getWaitForPlayer(): Boolean {
        return super.getWaitForPlayer()
    }

    override fun setWaitForPlayer(waitForPlayer: Boolean) {
        super.setWaitForPlayer(waitForPlayer)
        this.waitFormMultiPlayer = waitForPlayer
    }

    open fun getWaitForMultiPlayer(): Boolean {
        return this.waitFormMultiPlayer
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return this.getPassengers().size < 1
    }

    protected open fun spawnParticles(launched: Boolean) {
        if (!this.isDead) {
            val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 3.2 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 3.2 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 3.2 * cos((this.rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
            if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal && this.targetVec != null) {
                var modifier: Double = this.posY - (this.targetVec)!!.getY()
                modifier = max(modifier, 180.0)
                x1 *= modifier / 200.0
                y1 *= min(modifier / 200.0, 2.5)
                z1 *= modifier / 200.0
            }

            val y2: Double = this.prevPosY + (this.posY - this.prevPosY) + y1 - 0.75 * this.motionY - 0.3 + 2.5

            val x2: Double = this.posX + x1 + this.motionX
            val z2: Double = this.posZ + z1 + this.motionZ
            val motionVec: Vector3 = Vector3(x1 + this.motionX, y1 + this.motionY, z1 + this.motionZ)
            val d1: Vector3 = Vector3(y1 * 0.1, -x1 * 0.1, z1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d2: Vector3 = Vector3(x1 * 0.1, -z1 * 0.1, y1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d3: Vector3 = Vector3(-y1 * 0.1, x1 * 0.1, z1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            val d4: Vector3 = Vector3(x1 * 0.1, z1 * 0.1, -y1 * 0.1).rotate(315 - this.rotationYaw, motionVec)
            //Vector3 d5 = new Vector3(y1 * 0.1D, -x1 * 0.1D, z1 * 0.1D).rotate(270 - this.rotationYaw, motionVec);
            //Vector3 d6 = new Vector3(x1 * 0.1D, -z1 * 0.1D, y1 * 0.1D).rotate(270 - this.rotationYaw, motionVec);
            //Vector3 d7 = new Vector3(-y1 * 0.1D, x1 * 0.1D, z1 * 0.1D).rotate(270 - this.rotationYaw, motionVec);
            //Vector3 d8 = new Vector3(x1 * 0.1D, z1 * 0.1D, -y1 * 0.1D).rotate(270 - this.rotationYaw, motionVec);
            val mv1: Vector3 = motionVec.clone().translate(d1)
            val mv2: Vector3 = motionVec.clone().translate(d2)
            val mv3: Vector3 = motionVec.clone().translate(d3)
            val mv4: Vector3 = motionVec.clone().translate(d4)

            //T3 - Four flameballs which spread
            makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched())
            makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched())
            makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched())
            makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched())
            makeFlame(x2, y2, z2, Vector3(x1, y1, z1), this.getLaunched())
        }
    }

    private fun makeFlame(x2: Double, y2: Double, z2: Double, motionVec: Vector3, getLaunched: Boolean) {
        var y2: Double = y2
        val riddenByEntity: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                .get(0) !is EntityLivingBase
        ) null else this.getPassengers().get(0) as EntityLivingBase?

        if (getLaunched) {
            if (this.engine_type == Engine_Type.FUEL_ENGINE) {
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2 + 0.4),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
                (GalacticraftCore.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2 - 0.4),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity)
                )
            }

            if (this.engine_type == Engine_Type.ION_ENGINE) {
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 + 0.4, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2 - 0.4, y2, z2),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2 + 0.4),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
                (GalaxySpace.proxy)!!.spawnParticle(
                    "launchFlameLaunched",
                    Vector3(x2, y2, z2 - 0.4),
                    motionVec,
                    arrayOf<Any?>(riddenByEntity, Vector3(0.0, 252.0, 251.0))
                )
            }
            return
        }

        if (this.ticksExisted % 2 == 0) return

        y2 += 1.6
        val x1: Double = motionVec.x
        val y1: Double = motionVec.y
        val z1: Double = motionVec.z

        if (this.engine_type == Engine_Type.FUEL_ENGINE) {
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                Vector3(x1 + 0.1 + this.rand.nextDouble() / 10, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10),
                Vector3(x1 - 0.1 - this.rand.nextDouble() / 10, y1 - 0.3, z1 + 0.1 + this.rand.nextDouble() / 10),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                Vector3(x1 - 0.1 - this.rand.nextDouble() / 10, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10),
                Vector3(x1 + 0.1 + this.rand.nextDouble() / 10, y1 - 0.3, z1 - 0.1 - this.rand.nextDouble() / 10),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 + 0.4, y2, z2),
                Vector3(x1 + 0.3, y1 - 0.3, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2 - 0.4, y2, z2),
                Vector3(x1 - 0.3, y1 - 0.3, z1),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2, y2, z2 + 0.4),
                Vector3(x1, y1 - 0.3, z1 + 0.3),
                arrayOf<Any?>(riddenByEntity)
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameIdle",
                Vector3(x2, y2, z2 - 0.4),
                Vector3(x1, y1 - 0.3, z1 - 0.3),
                arrayOf<Any?>(riddenByEntity)
            )
        }
    }

    override fun isUsableByPlayer(par1EntityPlayer: EntityPlayer): Boolean {
        return !this.isDead && par1EntityPlayer.getDistanceSq(this) <= 64.0
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        super.getNetworkedData(list)
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        super.decodePacketdata(buffer)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.setInteger("engine_type", engine_type.getID())
        nbt.setBoolean("WaitingForMultiPlayer", this.getWaitForMultiPlayer())
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)
        this.engine_type = (Engine_Type.byID(nbt.getInteger("engine_type")))!!
        this.setWaitForPlayer(nbt.getBoolean("WaitingForMultiPlayer"))
    }

    override fun getRocketTier(): Int {
        return 6
    }

    override fun getFuelTankCapacity(): Int {
        return 4500
    }

    override fun getPreLaunchWait(): Int {
        return 400
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getItemsDropped(droppedItems: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItems)
        val rocket: ItemStack = ItemStack(GSItems.ROCKET_TIER_6, 1, (this.rocketType)!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.setInteger("RocketFuel", this.fuelTank.getFluidAmount())
        rocket.getTagCompound()!!.setBoolean(engine_type.getName(), true)
        rocket.getTagCompound()!!.setInteger(ItemSpaceSuit.mod_count, 0)
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun getRenderOffsetY(): Float {
        return -0.2f
    }

    override fun isDockValid(dock: IFuelDock?): Boolean {
        return dock is TileEntityAdvLandingPad
    }

    override fun addFuel(liquid: FluidStack?, doFill: Boolean): Int {
        return GSUtils.fillWithGCFuel(this.fuelTank, liquid, doFill, this)
    }


    /*
    @Override
    public FluidStack removeFuel(int amount)
    {
        return this.fuelTank.drain(amount * ConfigManagerCore.rocketFuelFactor, true);
    }*/
    /*
	@Override
	public Engine_Type getEngine() {
		return Engine_Type.BLACKHOLE_ENGINE;
	}*/
    open fun setEngine(type: Engine_Type) {
        this.engine_type = type
    }

    override fun getEngine(): Engine_Type? {
        return this.engine_type
    }

    companion object {
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
