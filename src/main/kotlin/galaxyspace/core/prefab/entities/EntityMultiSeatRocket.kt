package galaxyspace.core.prefab.entities

import net.minecraft.network.chat.Component

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.GalaxySpace
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.GSItems
import galaxyspace.api.entity.IMultiSeatEntity
import net.minecraft.ChatFormatting
import asmodeuscore.core.handler.capabilities.ACStatsCapability
import galaxyspace.core.prefab.entities.EntityMultiSeatRocket
import galaxyspace.core.network.trackers.MultiSeatRocketDriverTracker
import asmodeuscore.core.handler.capabilities.IStatsCapability
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.NonNullList
import net.minecraft.util.Mth as MathHelper

import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.server.level.ServerLevel as WorldServer
import java.lang.Exception
import java.lang.reflect.Field
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * RUS: Базовый класс для существ с несколькими посадочными местами
 * EN: Parent class for entities with multiply seats
 * @author gug2
 */
abstract class EntityMultiSeatRocket : EntityTieredRocket, IMultiSeatEntity {
    constructor(par1World: World?) : super(par1World)

    constructor(world: World?, posX: Double, posY: Double, posZ: Double) : super(world, posX, posY, posZ)

    override fun onUpdate() {
        if (getWaitForPlayer()) {
            if (!getPassengers().isEmpty()) {
                for (passenger: Entity in getPassengers()) {
                    if (ticks >= 40) {
                        if (!world.isRemote) {
                            removePassengers()
                            passenger.startRiding(this, true)
                            GCLog.debug("Remounting player in rocket.")
                        }

                        setWaitForPlayer(false)
                        motionY = -0.5
                    } else {
                        motionZ = 0.0
                        motionY = motionZ
                        motionX = motionY
                        passenger.motionZ = 0.0
                        passenger.motionY = passenger.deltaMovement.z
                        passenger.motionX = passenger.deltaMovement.y
                    }
                }
            } else {
                motionZ = 0.0
                motionY = motionZ
                motionX = motionY
            }
        }

        super.onUpdate()

        val i: Int

        if (timeUntilLaunch >= 100) {
            i = abs(timeUntilLaunch / 100)
        } else {
            i = 1
        }

        if ((getLaunched() || launchPhase == EnumLaunchPhase.IGNITED.ordinal && rand.nextInt(i) == 0) && !ConfigManagerCore.disableSpaceshipParticles && hasValidFuel()) {
            if (world.isRemote) {
                spawnParticles(getLaunched())
            }
        }

        if (launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal && hasValidFuel()) {
            if (launchPhase == EnumLaunchPhase.LAUNCHED.ordinal) {
                var d: Double = (timeSinceLaunch / 150).toDouble()

                if (world.provider is IGalacticraftWorldProvider && (world.provider as IGalacticraftWorldProvider).hasNoAtmosphere()) {
                    d = min(d * 1.2, 2.0)
                } else {
                    d = min(d, 1.4)
                }

                if (d != 0.0) {
                    motionY = -d * 2.5 * cos(((rotationPitch - 180) / Constants.RADIANS_TO_DEGREES).toDouble())
                }
            } else {
                motionY -= 0.008
            }

            var multiplier: Double = 1.0

            if (world.provider is IGalacticraftWorldProvider) {
                multiplier = (world.provider as IGalacticraftWorldProvider).getFuelUsageMultiplier()

                if (multiplier <= 0) {
                    multiplier = 1.0
                }
            }

            if (timeSinceLaunch % MathHelper.floor(2 * (1 / multiplier)) == 0f) {
                removeFuel(1)
                if (!hasValidFuel()) {
                    stopRocketSound()
                }
            }
        } else if (!hasValidFuel() && getLaunched() && !world.isRemote) {
            if (abs(sin((timeSinceLaunch / 1000).toDouble())) / 10 != 0.0) {
                motionY -= abs(sin((timeSinceLaunch / 1000).toDouble())) / 20
            }
        }
    }

    protected fun spawnParticles(launched: Boolean) {
        if (!isDead) {
            val sinPitch: Double = sin(rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            var x1: Double = 3.2 * cos(rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var z1: Double = 3.2 * sin(rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
            var y1: Double = 3.2 * cos((rotationPitch - 180) / Constants.RADIANS_TO_DEGREES_D)
            if (launchPhase == EnumLaunchPhase.LANDING.ordinal && targetVec != null) {
                var modifier: Double = posY - (targetVec)!!.getY()
                modifier = max(modifier, 180.0)
                x1 *= modifier / 200.0
                y1 *= min(modifier / 200.0, 2.5)
                z1 *= modifier / 200.0
            }

            val y2: Double = prevPosY + (posY - prevPosY) + y1 - 0.75 * motionY - 0.3 + 1.2

            val x2: Double = posX + x1 + motionX
            val z2: Double = posZ + z1 + motionZ
            val motionVec: Vector3 = Vector3(x1 + motionX, y1 + motionY, z1 + motionZ)
            val d1: Vector3 = Vector3(y1 * 0.1, -x1 * 0.1, z1 * 0.1).rotate(315 - rotationYaw, motionVec)
            val d2: Vector3 = Vector3(x1 * 0.1, -z1 * 0.1, y1 * 0.1).rotate(315 - rotationYaw, motionVec)
            val d3: Vector3 = Vector3(-y1 * 0.1, x1 * 0.1, z1 * 0.1).rotate(315 - rotationYaw, motionVec)
            val d4: Vector3 = Vector3(x1 * 0.1, z1 * 0.1, -y1 * 0.1).rotate(315 - rotationYaw, motionVec)
            val mv1: Vector3 = motionVec.clone().translate(d1)
            val mv2: Vector3 = motionVec.clone().translate(d2)
            val mv3: Vector3 = motionVec.clone().translate(d3)
            val mv4: Vector3 = motionVec.clone().translate(d4)
            //T3 - Four flameballs which spread
            val riddenByEntity: EntityLivingBase? = getDriver()
            val rider: Array<Any?> = arrayOf<Any?>(riddenByEntity)
            makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, getLaunched(), rider)
            makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, getLaunched(), rider)
            makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, getLaunched(), rider)
            makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, getLaunched(), rider)
        }
    }

    private fun makeFlame(
        x2: Double,
        y2: Double,
        z2: Double,
        motionVec: Vector3,
        getLaunched: Boolean,
        rider: Array<Any?>
    ) {
        var y2: Double = y2
        if (getLaunched) {
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - rand.nextDouble() / 10.0, y2, z2 + 0.4 - rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + rand.nextDouble() / 10.0, y2, z2 + 0.4 - rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 - 0.4 + rand.nextDouble() / 10.0, y2, z2 - 0.4 + rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle(
                "launchFlameLaunched",
                Vector3(x2 + 0.4 - rand.nextDouble() / 10.0, y2, z2 - 0.4 + rand.nextDouble() / 10.0),
                motionVec,
                rider
            )
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 + 0.4, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2 - 0.4, y2, z2), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2 + 0.4), motionVec, rider)
            (GalacticraftCore.proxy)!!.spawnParticle("launchFlameLaunched", Vector3(x2, y2, z2 - 0.4), motionVec, rider)
            return
        }

        if (ticksExisted % 2 == 0) return

        y2 += 1.6
        val x1: Double = motionVec.x
        val y1: Double = motionVec.y
        val z1: Double = motionVec.z
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - rand.nextDouble() / 10.0, y2, z2 + 0.4 - rand.nextDouble() / 10.0),
            Vector3(x1 + 0.1 + rand.nextDouble() / 10.0, y1 - 0.3, z1 + 0.1 + rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + rand.nextDouble() / 10.0, y2, z2 + 0.4 - rand.nextDouble() / 10.0),
            Vector3(x1 - 0.1 - rand.nextDouble() / 10.0, y1 - 0.3, z1 + 0.1 + rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4 + rand.nextDouble() / 10.0, y2, z2 - 0.4 + rand.nextDouble() / 10.0),
            Vector3(x1 - 0.1 - rand.nextDouble() / 10.0, y1 - 0.3, z1 - 0.1 - rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4 - rand.nextDouble() / 10.0, y2, z2 - 0.4 + rand.nextDouble() / 10.0),
            Vector3(x1 + 0.1 + rand.nextDouble() / 10.0, y1 - 0.3, z1 - 0.1 - rand.nextDouble() / 10.0),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 + 0.4, y2, z2),
            Vector3(x1 + 0.3, y1 - 0.3, z1),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2 - 0.4, y2, z2),
            Vector3(x1 - 0.3, y1 - 0.3, z1),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 + 0.4),
            Vector3(x1, y1 - 0.3, z1 + 0.3),
            rider
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "launchFlameIdle",
            Vector3(x2, y2, z2 - 0.4),
            Vector3(x1, y1 - 0.3, z1 - 0.3),
            rider
        )
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (getMaxSeats() > 1) {
            return interactSpecial(player, hand)
        } else {
            return super.processInitialInteract(player, hand)
        }
    }

    private fun interactSpecial(player: EntityPlayer, hand: EnumHand): Boolean {
        if (hand != EnumHand.MAIN_HAND) {
            return false
        }

        if (launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal) {
            return false
        }

        if (getPassengers().size >= getMaxSeats()) {
            player.displayClientMessage(
                Component.literal("Not enough seats in rocket!").withStyle(ChatFormatting.RED),
                true
            )
            return false
        }

        if (!world.isRemote && player is EntityPlayerMP && !isPassenger(player)) {
            // if riding successfully -> add to passengers list on server side and update list on all clients
            if (player.startRiding(this)) {
                GalaxySpace.info(
                    String.format(
                        "Add passenger to rocket %s [%s]",
                        player.toString(),
                        getPassengers().toString()
                    )
                )

                val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                stats.setChatCooldown(0)


                // display controls in chat for rocket driver
                if (getPassengers().size == 1) {
                    (GalacticraftCore.packetPipeline)!!.sendTo(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_DISPLAY_ROCKET_CONTROLS,
                            world.provider.getDimension(),
                            arrayOf<Any?>()
                        ), player
                    )
                } else {
                    val asCaps: IStatsCapability = (ACStatsCapability.get(player))!!
                    asCaps.setSecondPassenger(true)
                }

                return true
            }
        }

        return false
    }

    override fun removePassenger(passenger: Entity) {
        super.removePassenger(passenger)


        // if dismount successfully
        if (passenger.getVehicle() !== this) {
            GalaxySpace.info(String.format("Remove passenger %s", passenger.toString()))
        }
    }

    override fun onReachAtmosphere() {
        //Launch controlled
        if (destinationFrequency != -1) {
            if (world.isRemote) {
                //stop the sounds on the client - but do not reset, the rocket may start again
                this.stopRocketSound()
                return
            }

            this.setTarget(true, this.destinationFrequency)

            if (targetVec != null) {
                if (targetDimension != world.provider.getDimension()) {
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
                        ((targetVec)!!.getX() + 0.5f).toDouble(),
                        ((targetVec)!!.getY() + 800).toDouble(),
                        ((targetVec)!!.getZ() + 0.5f).toDouble()
                    )
                    //Stop any lateral motion, otherwise it will update to an incorrect x,z position first tick after spawning above target
                    motionZ = 0.0
                    motionX = motionZ
                    //Small upward motion initially, to keep clear of own flame trail from launch
                    motionY = 0.1
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
                fuelTank.drain(Int.MAX_VALUE, true)
                posY = max(
                    255.0,
                    (if (world.provider is IExitHeight) (world.provider as IExitHeight).getYCoordinateToTeleport() else 1200.0) - 200
                )
                return
            }
        }

        //Not launch controlled
        if (!world.isRemote) {
            for (passenger: Entity? in getPassengers()) {
                if (passenger is EntityPlayerMP) {
                    val player: EntityPlayerMP = passenger

                    onTeleport(player)
                    val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

                    if (player == getDriver()) {
                        WorldUtil.toCelestialSelection(player, stats, this.getRocketTier())
                    } else {
                        val asCaps: IStatsCapability = (ACStatsCapability.get(player))!!
                        waitForRocketDriver(player, stats, asCaps, this.getRocketTier())
                    }
                }
            }

            //Destroy any rocket which reached the top of the atmosphere and is not controlled by a Launch Controller
            this.setDead()
        }


        //Client side, non-launch controlled, do nothing - no reason why it can't continue flying until the GUICelestialSelection activates
    }

    private fun waitForRocketDriver(player: EntityPlayerMP, stats: GCPlayerStats, asCaps: IStatsCapability, tier: Int) {
        player.dismountRidingEntity()
        stats.setSpaceshipTier(tier)

        stats.setUsingPlanetSelectionGui(false)
        stats.setSavedPlanetList("")
        asCaps.setSecondPassenger(false)


        // TODO: show wait for driver GUI...
        (GalaxySpace.packetPipeline)!!.sendTo(
            GSPacketSimple(
                GSEnumSimplePacket.C_OPEN_WAITFORDRIVER_GUI,
                GCCoreUtil.getDimensionID(player.level()),
                *arrayOf<Any?>()
            ), player
        )

        //

        // add rocket driver to tracker
        MultiSeatRocketDriverTracker.get().addPassengerToTrackedDriver((getDriver() as EntityPlayerMP?)!!, player)

        //
        val fakeEntity: Entity = EntityCelestialFake(player.level(), player.x, player.y, player.z)
        player.level().spawnEntity(fakeEntity)
        player.startRiding(fakeEntity)
    }

    override fun onTeleport(player: EntityPlayerMP?) {
        val playerBase: EntityPlayerMP? = PlayerUtil.getPlayerBaseServerFromPlayer(player, false)

        if (playerBase != null) {
            // if passenger -> no items in landing pad
            if (playerBase == getDriver() as EntityPlayerMP?) {
                val stats: GCPlayerStats = (GCPlayerStats.get(playerBase))!!

                if (stacks == null || (stacks)!!.isEmpty()) {
                    stats.setRocketStacks(NonNullList.withSize<ItemStack>(2, ItemStack.EMPTY))
                } else {
                    stats.setRocketStacks(stacks)
                }

                stats.setRocketType((rocketType)!!.getIndex())
                stats.setRocketItem(GSItems.ROCKET_MULTI_SEAT)
                stats.setFuelLevel(fuelTank.getFluidAmount())
            }
        }
    }

    override fun canFitPassenger(passenger: Entity): Boolean {
        return getPassengers().size < getMaxSeats()
    }

    override fun updatePassenger(passenger: Entity) {
        if (isPassenger(passenger)) {
            passenger.setPosition(posX, posY + getMountedYOffset(passenger), posZ)
        }
    }

    fun getMountedYOffset(passenger: Entity?): Double {
        return (getPassengers().size - 1 - getPassengers().indexOf(passenger)) * 1.5
    }

    override fun shouldRiderSit(): Boolean {
        return true
    }

    fun getDriver(): EntityPlayer? {
        val lastIndex: Int = getPassengers().size - 1
        return if (getPassengers().isEmpty() || getPassengers().get(lastIndex) !is EntityPlayer) null else getPassengers().get(
            lastIndex
        ) as EntityPlayer?
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
