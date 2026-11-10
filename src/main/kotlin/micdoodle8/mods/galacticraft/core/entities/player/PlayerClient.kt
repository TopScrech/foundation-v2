package micdoodle8.mods.galacticraft.core.entities.player

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.block.state.IBlockState
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.FMLClientHandler
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

open class PlayerClient : IPlayerClient {
    private val saveSneak = false
    private var downMot2 = 0.0
    override fun move(player: EntityPlayerSP?, type: MoverType?, x: Double, y: Double, z: Double) {
        this.updateFeet((player)!!, x, z)
    }

    override fun wakeUpPlayer(
        player: EntityPlayerSP?,
        immediately: Boolean,
        updateWorldFlag: Boolean,
        setSpawn: Boolean
    ): Boolean {
        return this.wakeUpPlayer((player)!!, immediately, updateWorldFlag, setSpawn, false)
    }

    override fun onUpdate(player: EntityPlayerSP?) {
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get((player)!!))!!
        stats.setTick(stats.getTick() + 1)

        if (stats.isUsingParachute() && !(player)!!.capabilities.isFlying && !(player)!!.handleWaterMovement()) {
            (player)!!.motionY = -0.5
            (player)!!.motionX *= 0.5
            (player)!!.motionZ *= 0.5
        }
    }

    override fun isEntityInsideOpaqueBlock(player: EntityPlayerSP?, vanillaInside: Boolean): Boolean {
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get((player)!!))!!
        if (vanillaInside && stats.isInFreefall()) {
            stats.setInFreefall(false)
            return false
        }
        return (player)!!.getRidingEntity() !is EntityLanderBase && vanillaInside
    }

    override fun onLivingUpdatePre(player: EntityPlayerSP?) {
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get((player)!!))!!

        if (stats.getPlatformControlled() || (player)!!.world.provider is IGalacticraftWorldProvider) {
            if (!startup) {
                stats.setInFreefallLast(stats.isInFreefall())
                stats.setInFreefall(stats.getPlatformControlled() || (stats.getFreefallHandler())!!.testFreefall((player)!!))
                startup = true
            }
            if (stats.getPlatformControlled() || (player)!!.world.provider is IZeroGDimension) {
                stats.setInFreefallLast(stats.isInFreefall())
                stats.setInFreefall(stats.getPlatformControlled() || (stats.getFreefallHandler())!!.testFreefall((player)!!))
                this.downMot2 = stats.getDownMotionLast()
                stats.setDownMotionLast((player)!!.motionY)
                (stats.getFreefallHandler())!!.preVanillaMotion((player)!!)
                if (stats.getPlatformControlled()) {
                    (player)!!.motionY = stats.getPlatformVelocity((player)!!.posY)
                    (player)!!.motionX = 0.0
                    (player)!!.motionZ = 0.0
                }
            }
        }

        //        if (player.boundingBox != null && stats.boundingBoxBefore == null)
//        {
//            GCLog.debug("Changed player BB from " + player.boundingBox.minY);
//            stats.boundingBoxBefore = player.boundingBox;
//            player.boundingBox.setBounds(stats.boundingBoxBefore.minX + 0.4, stats.boundingBoxBefore.minY + 0.9, stats.boundingBoxBefore.minZ + 0.4, stats.boundingBoxBefore.maxX - 0.4, stats.boundingBoxBefore.maxY - 0.9, stats.boundingBoxBefore.maxZ - 0.4);
//            GCLog.debug("Changed player BB to " + player.boundingBox.minY);
//        }
//        else if (player.boundingBox != null && stats.boundingBoxBefore != null)
//        {
//            player.boundingBox.setBB(stats.boundingBoxBefore);
//            GCLog.debug("Changed player BB to " + player.boundingBox.minY);
//        }
    }

    open fun cancelLimbSwing(player: EntityPlayerSP) {
        player.limbSwing -= player.limbSwingAmount
        player.limbSwingAmount = player.prevLimbSwingAmount
        val adjust: Float = min(abs(player.limbSwing), abs(player.limbSwingAmount) / 3)
        if (player.limbSwing < 0) {
            player.limbSwing += adjust
        } else if (player.limbSwing > 0) {
            player.limbSwing -= adjust
        }
        player.limbSwingAmount *= 0.9.toFloat()
    }

    override fun onLivingUpdatePost(player: EntityPlayerSP?) {
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get((player)!!))!!
        val ridingThirdPersonEntity: Boolean =
            (player)!!.getRidingEntity() is ICameraZoomEntity && ((player)!!.getRidingEntity() as ICameraZoomEntity).defaultThirdPerson()

        if (stats.getPlatformControlled() || (player)!!.world.provider is IZeroGDimension) {
            (stats.getFreefallHandler())!!.postVanillaMotion((player)!!)

            if (stats.isInFreefall() || ridingThirdPersonEntity) {
                this.cancelLimbSwing((player)!!)
            } else {
                if (stats.isInFreefallLast() && this.downMot2 < -0.008) {
                    stats.setLandingTicks(5 - (min(this.downMot2, stats.getDownMotionLast()) * 40).toInt())
                    if (stats.getLandingTicks() > stats.getMaxLandingticks()) {
                        if (stats.getLandingTicks() > stats.getMaxLandingticks() + 4) {
                            (stats.getFreefallHandler())!!.pjumpticks =
                                stats.getLandingTicks() - stats.getMaxLandingticks() - 5
                        }
                        stats.setLandingTicks(stats.getMaxLandingticks())
                    }
                    val dYmax: Float = 0.3f * stats.getLandingTicks() / stats.getMaxLandingticks()
                    var factor: Float = 1f
                    for (i: Int in 0..stats.getLandingTicks()) {
                        ((stats)!!.getLandingYOffset())!![i] =
                            dYmax * MathHelper.sin(i * 3.1415926f / stats.getLandingTicks()) * factor
                        factor *= 0.97f
                    }
                }
            }

            if (stats.getLandingTicks() > 0) {
                stats.setLandingTicks(stats.getLandingTicks() - 1)
                (player)!!.limbSwing *= 0.8f
                (player)!!.limbSwingAmount = 0f
            }
        } else {
            stats.setInFreefall(false)
            if (ridingThirdPersonEntity) {
                this.cancelLimbSwing((player)!!)
            }
        }

        if (ridingThirdPersonEntity && !stats.isLastRidingCameraZoomEntity()) {
            if (!ConfigManagerCore.disableVehicleCameraChanges) FMLClientHandler.instance()
                .getClient().gameSettings.thirdPersonView = 1
        }

        if ((player)!!.getRidingEntity() is ICameraZoomEntity) {
            if (!ConfigManagerCore.disableVehicleCameraChanges) {
                stats.setLastZoomed(true)
                TickHandlerClient.zoom(((player)!!.getRidingEntity() as ICameraZoomEntity).getCameraZoom())
            }
        } else if (stats.isLastZoomed()) {
            if (!ConfigManagerCore.disableVehicleCameraChanges) {
                stats.setLastZoomed(false)
                TickHandlerClient.zoom(4.0f)
            }
        }

        stats.setLastRidingCameraZoomEntity(ridingThirdPersonEntity)

        if (stats.isUsingParachute()) {
            (player)!!.fallDistance = 0.0f
        }

        val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData((player)!!)

        stats.setUsingParachute(false)

        if (gearData != null) {
            stats.setUsingParachute(gearData.getParachute() != null)
            if (!GalacticraftCore.isHeightConflictingModInstalled) {
                if (gearData.getMask() != GCPlayerHandler.GEAR_NOT_PRESENT) {
                    (player)!!.height = 1.9375f
                } else {
                    (player)!!.height = 1.8f
                }
                val bounds: AxisAlignedBB = (player)!!.getEntityBoundingBox()
                (player)!!.setEntityBoundingBox(
                    AxisAlignedBB(
                        bounds.minX,
                        bounds.minY,
                        bounds.minZ,
                        bounds.maxX,
                        bounds.minY + (player)!!.height.toDouble(),
                        bounds.maxZ
                    )
                )
            }
        }

        if (stats.isUsingParachute() && (player)!!.onGround) {
            stats.setUsingParachute(false)
            stats.setLastUsingParachute(false)
            FMLClientHandler.instance().getClient().gameSettings.thirdPersonView = stats.getThirdPersonView()
        }

        if (!stats.isLastUsingParachute() && stats.isUsingParachute()) {
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(
                PositionedSoundRecord(
                    GCSounds.parachute,
                    SoundCategory.PLAYERS,
                    0.95f + (player)!!.getRNG().nextFloat() * 0.1f,
                    1.0f,
                    (player)!!.posX.toFloat(),
                    (player)!!.posY.toFloat(),
                    (player)!!.posZ.toFloat()
                )
            )
        }

        stats.setLastUsingParachute(stats.isUsingParachute())
        stats.setLastOnGround((player)!!.onGround)
    }

    override fun getBedOrientationInDegrees(player: EntityPlayerSP?, vanillaDegrees: Float): Float {
        if ((player)!!.bedLocation != null) {
            if ((player)!!.world.getTileEntity((player)!!.bedLocation) is TileEntityAdvanced) {
//                int j = player.world.getBlock(x, y, z).getBedDirection(player.world, x, y, z);
                val state: IBlockState = (player)!!.world.getBlockState((player)!!.bedLocation)
                when (state.getBlock().getMetaFromState(state) - 4) {
                    0 -> return 90.0f
                    1 -> return 270.0f
                    2 -> return 180.0f
                    3 -> return 0.0f
                }
            } else {
                return vanillaDegrees
            }
        }

        return vanillaDegrees
    }

    private fun updateFeet(player: EntityPlayerSP, motionX: Double, motionZ: Double) {
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(player))!!
        val motionSqrd: Double = motionX * motionX + motionZ * motionZ

        // If the player is on the moon, not airbourne and not riding anything
        if (motionSqrd > 0.001 && player.world != null && player.world.provider is WorldProviderMoon && player.getRidingEntity() == null && !player.capabilities.isFlying) {
            val iPosX: Int = MathHelper.floor(player.posX)
            val iPosY: Int = MathHelper.floor(player.posY - 0.05)
            val iPosZ: Int = MathHelper.floor(player.posZ)
            val pos1: BlockPos = BlockPos(iPosX, iPosY, iPosZ)
            val state: IBlockState = player.world.getBlockState(pos1)

            // If the block below is the moon block
            if (state.getBlock() === GCBlocks.blockMoon) {
                // And is the correct metadata (moon turf)
                if (state.getValue(BlockBasicMoon.BASIC_TYPE_MOON) == BlockBasicMoon.EnumBlockBasicMoon.MOON_TURF) {
                    // If it has been long enough since the last step
                    if (stats.getDistanceSinceLastStep() > 0.35) {
                        var pos: Vector3 = Vector3(player)
                        // Set the footprint position to the block below and add random number to stop z-fighting
                        pos.y = (MathHelper.floor(player.posY) + player.getRNG().nextFloat() / 100.0f).toDouble()

                        // Adjust footprint to left or right depending on step count
                        when (stats.getLastStep()) {
                            0 -> pos.translate(
                                Vector3(
                                    sin(Math.toRadians((-player.rotationYaw + 90).toDouble())) * 0.25,
                                    0.0,
                                    cos(Math.toRadians((-player.rotationYaw + 90).toDouble())) * 0.25
                                )
                            )

                            1 -> pos.translate(
                                Vector3(
                                    sin(Math.toRadians((-player.rotationYaw - 90).toDouble())) * 0.25,
                                    0.0,
                                    cos(Math.toRadians((-player.rotationYaw - 90).toDouble())) * 0.25
                                )
                            )
                        }

                        pos = WorldUtil.getFootprintPosition(
                            player.world,
                            player.rotationYaw - 180,
                            pos,
                            BlockVec3(player)
                        )

                        val chunkKey: Long = ChunkPos.asLong(pos.intX() shr 4, pos.intZ() shr 4)
                        val lightmapVal: Int =
                            player.world.getCombinedLight(BlockPos(pos.intX(), pos.intY(), pos.intZ()), 0)
                        FootprintRenderer.addFootprint(
                            chunkKey,
                            GCCoreUtil.getDimensionID(player.world),
                            pos,
                            player.rotationYaw,
                            player.getName(),
                            lightmapVal
                        )

                        // Increment and cap step counter at 1
                        stats.setLastStep((stats.getLastStep() + 1) % 2)
                        stats.setDistanceSinceLastStep(0.0)
                    } else {
                        stats.setDistanceSinceLastStep(stats.getDistanceSinceLastStep() + motionSqrd)
                    }
                }
            }
        }
    }

    open fun wakeUpPlayer(
        player: EntityPlayerSP,
        immediately: Boolean,
        updateWorldFlag: Boolean,
        setSpawn: Boolean,
        bypass: Boolean
    ): Boolean {
        val c: BlockPos? = player.bedLocation

        if (c != null) {
            val event: EventWakePlayer = EventWakePlayer(player, c, immediately, updateWorldFlag, setSpawn, bypass)
            MinecraftForge.EVENT_BUS.post(event)

            if (bypass || event.result == null || event.result == EntityPlayer.SleepResult.OK) {
                return false
            }
        }

        return true
    }

    override fun onBuild(i: Int, player: EntityPlayerSP?) {
        // 0 : opened GC inventory tab
        // 1,2,3 : Compressor, CF, Standard Wrench
        // 4,5,6 : Fuel loader, Launchpad, NASA Workbench
        // 7: oil found 8: placed rocket

        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get((player)!!))!!
        var flag: Int = stats.getBuildFlags()
        if (flag == -1) {
            flag = 0
        }
        var repeatCount: Int = flag shr 9
        if (repeatCount <= 3) {
            repeatCount++
        }
        if ((flag and (1 shl i)) > 0) {
            return
        }
        flag = flag or (1 shl i)
        stats.setBuildFlags((flag and 511) + (repeatCount shl 9))
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_BUILDFLAGS_UPDATE,
                GCCoreUtil.getDimensionID((player)!!.world),
                arrayOf<Any>(stats.getBuildFlags())
            )
        )
        when (i) {
            0, 1, 2, 3 -> {
                (player)!!.sendMessage(
                    ITextComponent.Serializer.jsonToComponent(
                        "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help1") + ": \",\"color\":\"white\"}," + "{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + Constants.PREFIX + "com/wiki/1" + "\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" + GCCoreUtil.translate(
                            "gui.message.clicklink"
                        ) + "\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "http://wiki." + Constants.PREFIX + "com/wiki/1" + "\"}}]"
                    )
                )
                (player)!!.sendMessage(TextComponentString(GCCoreUtil.translate("gui.message.help1a") + EnumColor.AQUA + " /gchelp"))
            }

            4, 5, 6 -> (player)!!.sendMessage(
                ITextComponent.Serializer.jsonToComponent(
                    "[{\"text\":\"" + GCCoreUtil.translate(
                        "gui.message.help2"
                    ) + ": \",\"color\":\"white\"}," + "{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + Constants.PREFIX + "com/wiki/2" + "\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" + GCCoreUtil.translate(
                        "gui.message.clicklink"
                    ) + "\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "http://wiki." + Constants.PREFIX + "com/wiki/2" + "\"}}]"
                )
            )

            7 -> (player)!!.sendMessage(
                ITextComponent.Serializer.jsonToComponent(
                    "[{\"text\":\"" + GCCoreUtil.translate("gui.message.help3") + ": \",\"color\":\"white\"}," + "{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + Constants.PREFIX + "com/wiki/oil" + "\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" + GCCoreUtil.translate(
                        "gui.message.clicklink"
                    ) + "\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "http://wiki." + Constants.PREFIX + "com/wiki/oil" + "\"}}]"
                )
            )

            8 -> (player)!!.sendMessage(
                ITextComponent.Serializer.jsonToComponent(
                    "[{\"text\":\"" + GCCoreUtil.translate("gui.message.prelaunch") + ": \",\"color\":\"white\"}," + "{\"text\":\" " + EnumColor.BRIGHT_GREEN + "wiki." + Constants.PREFIX + "com/wiki/pre" + "\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" + GCCoreUtil.translate(
                        "gui.message.clicklink"
                    ) + "\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "http://wiki." + Constants.PREFIX + "com/wiki/pre" + "\"}}]"
                )
            )
        }
    }

    companion object {
        @JvmField
        var startup: Boolean = false
    }
}
