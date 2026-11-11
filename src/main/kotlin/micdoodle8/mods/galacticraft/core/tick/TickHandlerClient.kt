package micdoodle8.mods.galacticraft.core.tick

import foundation.inventory.getArmorItem
import foundation.block.getMetaFromState

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent

import kotlin.concurrent.Volatile
import com.google.common.collect.Sets
import com.google.common.collect.Lists
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.entity.IEntityNoisy
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.item.ISensorGlassesArmor
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.recipe.CompressorRecipes
import micdoodle8.mods.galacticraft.api.vector.BlockTuple
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.BubbleRenderer
import micdoodle8.mods.galacticraft.core.client.CloudRenderer
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer
import micdoodle8.mods.galacticraft.core.client.SkyProviderMoon
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayDockingRocket
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayLander
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayLaunchCountdown
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayOxygenTanks
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayOxygenWarning
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayRocket
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiNewSpaceRace
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiTeleporting
import micdoodle8.mods.galacticraft.core.client.jei.GalacticraftJEI
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import micdoodle8.mods.galacticraft.core.entities.IBubbleProviderColored
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.network.GalacticraftPacketHandler
import micdoodle8.mods.galacticraft.core.network.PacketRotateRocket
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.MapUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.ThreadVersionCheck
import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.core.util.ThreadRequirementMissing
import micdoodle8.mods.galacticraft.core.wrappers.BlockMetaList
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SoundInstance as ISound
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.client.resources.I18n
import net.minecraft.client.settings.GameSettings
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.WorldProviderSurface
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.input.Mouse
import java.util.HashSet
import java.util.Random
import kotlin.math.abs

open class TickHandlerClient {
    @SubscribeEvent
    open fun worldUnloadEvent(event: WorldEvent.Unload) {
        for (packetHandler: GalacticraftPacketHandler in packetHandlers) {
            packetHandler.unload(event.getWorld())
        }
    }

    @SubscribeEvent
    open fun onRenderTick(event: TickEvent.RenderTickEvent) {
        val minecraft: Minecraft = Minecraft.getInstance()
        val player: EntityPlayerSP? = minecraft.player
        val playerBaseClient: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(player, false)
        if (player == null || playerBaseClient == null) {
            return
        }

        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(playerBaseClient))!!



        if (event.phase == TickEvent.Phase.END) {
            if (minecraft.currentScreen is GuiIngameMenu) {
                val i: Int = Mouse.getEventX() * minecraft.currentScreen!!.width / minecraft.displayWidth
                val j: Int =
                    minecraft.currentScreen!!.height - Mouse.getEventY() * minecraft.currentScreen!!.height / minecraft.displayHeight - 1

                val k: Int = Mouse.getEventButton()

                var deltaColor: Int = 0

                if (i > minecraft.currentScreen!!.width - 100 && j > minecraft.currentScreen!!.height - 35) {
                    deltaColor = 20

                    if (k == 0) {
                        if (Mouse.getEventButtonState()) {
                            minecraft.displayGuiScreen(GuiNewSpaceRace(playerBaseClient))
                        }
                    }
                }

                this.drawGradientRect(
                    minecraft.currentScreen!!.width - 100,
                    minecraft.currentScreen!!.height - 35,
                    minecraft.currentScreen!!.width,
                    minecraft.currentScreen!!.height,
                    ColorUtil.to32BitColor(150, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor),
                    ColorUtil.to32BitColor(250, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor)
                )
                minecraft.fontRenderer.drawString(
                    GCCoreUtil.translate("gui.space_race.create.title.name.0"),
                    minecraft.currentScreen!!.width - 50 - minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.space_race.create.title.name.0")) / 2,
                    minecraft.currentScreen!!.height - 26,
                    ColorUtil.to32BitColor(255, 240, 240, 240)
                )
                minecraft.fontRenderer.drawString(
                    GCCoreUtil.translate("gui.space_race.create.title.name.1"),
                    minecraft.currentScreen!!.width - 50 - minecraft.fontRenderer.getStringWidth(GCCoreUtil.translate("gui.space_race.create.title.name.1")) / 2,
                    minecraft.currentScreen!!.height - 16,
                    ColorUtil.to32BitColor(255, 240, 240, 240)
                )
                Gui.drawRect(
                    minecraft.currentScreen!!.width - 100,
                    minecraft.currentScreen!!.height - 35,
                    minecraft.currentScreen!!.width - 99,
                    minecraft.currentScreen!!.height,
                    ColorUtil.to32BitColor(255, 0, 0, 0)
                )
                Gui.drawRect(
                    minecraft.currentScreen!!.width - 100,
                    minecraft.currentScreen!!.height - 35,
                    minecraft.currentScreen!!.width,
                    minecraft.currentScreen!!.height - 34,
                    ColorUtil.to32BitColor(255, 0, 0, 0)
                )
            }

            ClientProxyCore.Companion.playerPosX =
                player.prevPosX + (player.x - player.prevPosX) * event.renderTickTime
            ClientProxyCore.Companion.playerPosY =
                player.prevPosY + (player.y - player.prevPosY) * event.renderTickTime
            ClientProxyCore.Companion.playerPosZ =
                player.prevPosZ + (player.z - player.prevPosZ) * event.renderTickTime
            ClientProxyCore.Companion.playerRotationYaw =
                player.yRotO + (player.yRot - player.yRotO) * event.renderTickTime
            ClientProxyCore.Companion.playerRotationPitch =
                player.xRotO + (player.xRot - player.xRotO) * event.renderTickTime

            //            if (player != null && player.getRidingEntity() != null && player.getRidingEntity() instanceof EntityTier1Rocket)
//            {
//                float f = (((EntityTier1Rocket) player.getRidingEntity()).timeSinceLaunch - 250F) / 175F;
//
//                if (f < 0)
//                {
//                    f = 0F;
//                }
//
//                if (f > 1)
//                {
//                    f = 1F;
//                }
//
//                final ScaledResolution scaledresolution = ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
//                scaledresolution.getScaledWidth();
//                scaledresolution.getScaledHeight();
//                minecraft.entityRenderer.setupOverlayRendering();
//                GL11.glEnable(GL11.GL_BLEND);
//                GL11.glDisable(GL11.GL_DEPTH_TEST);
//                GL11.glDepthMask(false);
//                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//                GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
//                GL11.glDisable(GL11.GL_ALPHA_TEST);
//                GL11.glDepthMask(true);
//                GL11.glEnable(GL11.GL_DEPTH_TEST);
//                GL11.glEnable(GL11.GL_ALPHA_TEST);
//                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            }
            if (minecraft.currentScreen == null && player.getVehicle() is EntitySpaceshipBase && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayRocket.renderSpaceshipOverlay((player.getVehicle() as EntitySpaceshipBase).getSpaceshipGui())
            }

            if (minecraft.currentScreen == null && player.getVehicle() is EntityLander && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayLander.renderLanderOverlay()
            }

            if (minecraft.currentScreen == null && player.getVehicle() is EntityAutoRocket && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI) {
                OverlayDockingRocket.renderDockingOverlay()
            }

            if (minecraft.currentScreen == null && player.getVehicle() is EntitySpaceshipBase && minecraft.gameSettings.thirdPersonView != 0 && !minecraft.gameSettings.hideGUI && !(minecraft.player.getVehicle() as EntitySpaceshipBase).getLaunched()) {
                OverlayLaunchCountdown.renderCountdownOverlay()
            }

            if (player.level().provider is IGalacticraftWorldProvider && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen) && OxygenUtil.noAtmosphericCombustion(
                    player.level().provider
                ) && !playerBaseClient.isSpectator() && !minecraft.gameSettings.showDebugInfo
            ) {
                var var6: Int = (airRemaining - 90) * -1

                if (airRemaining <= 0) {
                    var6 = 90
                }

                var var7: Int = (airRemaining2 - 90) * -1

                if (airRemaining2 <= 0) {
                    var7 = 90
                }

                val thermalLevel: Int = stats.getThermalLevel() + 22
                OverlayOxygenTanks.renderOxygenTankIndicator(
                    thermalLevel,
                    var6,
                    var7,
                    !ConfigManagerCore.oxygenIndicatorLeft,
                    !ConfigManagerCore.oxygenIndicatorBottom,
                    abs(thermalLevel - 22) >= 10 && !stats.isThermalLevelNormalising()
                )
            }

            if (playerBaseClient != null && player.level().provider is IGalacticraftWorldProvider && !stats.isOxygenSetupValid() && OxygenUtil.noAtmosphericCombustion(
                    player.level().provider
                ) && minecraft.currentScreen == null && !minecraft.gameSettings.hideGUI && !playerBaseClient.capabilities.isCreativeMode && !playerBaseClient.isSpectator()
            ) {
                OverlayOxygenWarning.renderOxygenWarningOverlay()
            }
        }
    }

    @SubscribeEvent
    open fun onPreGuiRender(event: RenderGameOverlayEvent.Pre) {
        val minecraft: Minecraft = Minecraft.getInstance()
        val player: EntityPlayerSP? = minecraft.player

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (player != null && player.getVehicle() != null && player.getVehicle() is IIgnoreShift && (player.getVehicle() as IIgnoreShift).shouldIgnoreShiftExit()) {
                // Remove "Press shift to dismount" message when shift-exiting is disabled (not ideal, but the only option)
                val str: String = I18n.format(
                    "mount.onboard",
                    *arrayOf<Any>(GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindSneak.getKeyCode()))
                )
                if (minecraft.ingameGUI.overlayMessage == str) {
                    minecraft.ingameGUI.overlayMessage = ""
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onClientTick(event: TickEvent.ClientTickEvent) {
        val minecraft: Minecraft = Minecraft.getInstance()
        val world: WorldClient? = minecraft.world
        val player: EntityPlayerSP? = minecraft.player

        if (teleportingGui != null) {
            if (minecraft.currentScreen !== teleportingGui) {
                minecraft.currentScreen = teleportingGui
            }
        }

        if (menuReset) {
            resetClient()
            menuReset = false
        }

        if (event.phase == TickEvent.Phase.START && player != null) {
            if (ClientProxyCore.Companion.playerHead == null && player.getGameProfile() != null) {
                val map: MutableMap<MinecraftProfileTexture.Type?, MinecraftProfileTexture?> =
                    minecraft.getSkinManager().loadSkinFromCache(player.getGameProfile())

                if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    ClientProxyCore.Companion.playerHead = minecraft.getSkinManager()
                        .loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)
                } else {
                    ClientProxyCore.Companion.playerHead =
                        DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(player.getGameProfile()))
                }
            }

            tickCount++

            if (!(GalacticraftCore.proxy)!!.isPaused()) {
                val it: MutableIterator<FluidNetwork> = (fluidNetworks.iterator() as MutableIterator<FluidNetwork>)
                while (it.hasNext()) {
                    val network: FluidNetwork = it.next()

                    if ((network.getTransmitters())!!.size == 0) {
                        it.remove()
                    } else {
                        network.clientTick()
                    }
                }
            }

            if (tickCount % 20 == 0L) {
                BubbleRenderer.clearBubbles()

                for (tile: TileEntity? in player.level().tickableTileEntities) {
                    if (tile is IBubbleProviderColored) {
                        BubbleRenderer.addBubble(tile as IBubbleProviderColored)
                    }
                }

                if (updateJEIhiding) {
                    updateJEIhiding = false
                    // Update JEI to hide the ingot compressor recipe for GC steel in hard mode
                    // Update JEI to hide adventure mode recipes when not in adventure mode
                    GalacticraftJEI.updateHidden(
                        CompressorRecipes.steelIngotsPresent && ConfigManagerCore.hardMode && !ConfigManagerCore.challengeRecipes,
                        !ConfigManagerCore.challengeRecipes
                    )
                }

                for (fpList: MutableList<Footprint?> in FootprintRenderer.footprints.values) {
                    val fpIt: MutableIterator<Footprint> = (fpList.iterator() as MutableIterator<Footprint>)
                    while (fpIt.hasNext()) {
                        val fp: Footprint = fpIt.next()
                        fp.age = (fp.age + 20).toShort()
                        fp.lightmapVal =
                            player.level().getCombinedLight(BlockPos((fp.position)!!.x, (fp.position)!!.y, (fp.position)!!.z), 0)

                        if (fp.age >= Footprint.MAX_AGE) {
                            fpIt.remove()
                        }
                    }
                }

                if (!player.inventory.getArmorItem(3).isEmpty() && player.inventory.getArmorItem(3)
                        .getItem() is ISensorGlassesArmor
                ) {
                    ClientProxyCore.Companion.valueableBlocks.clear()

                    for (i: Int in -4..4) {
                        val x: Int = MathHelper.floor(player.x + i)
                        for (j: Int in -4..4) {
                            val y: Int = MathHelper.floor(player.y + j)
                            for (k: Int in -4..4) {
                                val z: Int = MathHelper.floor(player.z + k)
                                val pos: BlockPos = BlockPos(x, y, z)

                                val state: IBlockState = player.level().getBlockState(pos)
                                val block: Block = state.getBlock()

                                if (block.getMaterial(state) !== Material.AIR) {
                                    val metadata: Int = block.getMetaFromState(state)
                                    var isDetectable: Boolean = false

                                    for (blockMetaList in ClientProxyCore.Companion.detectableBlocks) {
                                        if ((blockMetaList)!!.getBlock() === block && ((blockMetaList)!!.getMetaList())!!
                                                .contains(metadata)
                                        ) {
                                            isDetectable = true
                                            break
                                        }
                                    }

                                    if (isDetectable || (block is IDetectableResource && (block as IDetectableResource).isValueable(
                                            state
                                        ))
                                    ) {
                                        ClientProxyCore.Companion.valueableBlocks.add(BlockVec3(x, y, z))
                                    }
                                }
                            }
                        }
                    }

                    val nearestSealer: TileEntityOxygenSealer? = TileEntityOxygenSealer.getNearestSealer(
                        (world)!!,
                        MathHelper.floor(player.x).toDouble(),
                        MathHelper.floor(player.y).toDouble(),
                        MathHelper.floor(player.z).toDouble()
                    )
                    if (nearestSealer != null && !nearestSealer.sealed) {
                        ClientProxyCore.Companion.leakTrace = nearestSealer.getLeakTraceClient()
                    } else {
                        ClientProxyCore.Companion.leakTrace = null
                    }
                } else {
                    ClientProxyCore.Companion.leakTrace = null
                }

                if (world != null) {
                    if (MapUtil.resetClientFlag.getAndSet(false)) {
                        MapUtil.resetClientBody()
                    }
                }
            }

            if (ClientProxyCore.Companion.leakTrace != null) this.spawnLeakParticles()

            if (world != null && spaceRaceGuiScheduled && minecraft.currentScreen == null && ConfigManagerCore.enableSpaceRaceManagerPopup) {
                player.openGui(
                    GalacticraftCore.instance,
                    GuiIdsCore.SPACE_RACE_START,
                    player.level(),
                    player.x.toInt(),
                    player.y.toInt(),
                    player.z.toInt()
                )
                spaceRaceGuiScheduled = false
            }

            if (world != null && checkedVersion) {
                ThreadVersionCheck.startCheck()
                checkedVersion = false
            }

            var inSpaceShip: Boolean = false
            if (player.getVehicle() is EntitySpaceshipBase) {
                inSpaceShip = true
                val rocket: EntitySpaceshipBase? = player.getVehicle() as EntitySpaceshipBase?
                if (rocket!!.prevRotationPitch != rocket.rotationPitch || rocket.prevRotationYaw != rocket.rotationYaw) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(PacketRotateRocket((player.getVehicle())!!))
                }
            }

            if (world != null) {
                if (world.provider is WorldProviderSurface) {
                    if (world.provider.getSkyRenderer() == null && inSpaceShip && player.getVehicle()!!.posY > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                        world.provider.setSkyRenderer(SkyProviderOverworld())
                    } else if (world.provider.getSkyRenderer() is SkyProviderOverworld && player.y <= Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                        world.provider.setSkyRenderer(null)
                    }
                } else if (world.provider is WorldProviderSpaceStation) {
                    if (world.provider.getSkyRenderer() == null) {
                        (world.provider as WorldProviderSpaceStation).createSkyProvider()
                    }
                } else if (world.provider is WorldProviderMoon) {
                    if (world.provider.getSkyRenderer() == null) {
                        world.provider.setSkyRenderer(SkyProviderMoon())
                    }

                    if (world.provider.getCloudRenderer() == null) {
                        world.provider.setCloudRenderer(CloudRenderer())
                    }
                }
            }

            if (inSpaceShip) {
                val ship: EntitySpaceshipBase? = player.getVehicle() as EntitySpaceshipBase?
                var hasChanged: Boolean = false

                if (minecraft.gameSettings.keyBindLeft.isKeyDown()) {
                    ship!!.turnYaw(-1.0f)
                    hasChanged = true
                }

                if (minecraft.gameSettings.keyBindRight.isKeyDown()) {
                    ship!!.turnYaw(1.0f)
                    hasChanged = true
                }

                if (minecraft.gameSettings.keyBindForward.isKeyDown()) {
                    if (ship!!.getLaunched()) {
                        ship.turnPitch(-0.7f)
                        hasChanged = true
                    }
                }

                if (minecraft.gameSettings.keyBindBack.isKeyDown()) {
                    if (ship!!.getLaunched()) {
                        ship.turnPitch(0.7f)
                        hasChanged = true
                    }
                }

                if (hasChanged) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(PacketRotateRocket((ship)!!))
                }
            }

            if (world != null) {
                val entityList: MutableList<*> = world.loadedEntityList
                for (e: Any? in entityList) {
                    if (e is IEntityNoisy) {
                        val vehicle: IEntityNoisy = e
                        if (vehicle.getSoundUpdater() == null) {
                            val noise: ISound? = vehicle.setSoundUpdater(Minecraft.getInstance().player)
                            if (noise != null) {
                                Minecraft.getInstance().getSoundManager().play(noise)
                            }
                        }
                    }
                }
            }

            if (Minecraft.getInstance().gui.screen() is GuiCelestialSelection) {
                player.motionY = 0.0
            }

            if (world != null && world.provider is IGalacticraftWorldProvider && OxygenUtil.noAtmosphericCombustion(
                    world.provider
                ) && (world.provider as IGalacticraftWorldProvider).shouldDisablePrecipitation()
            ) {
                world.setRainStrength(0.0f)
            }

            val isPressed: Boolean = (KeyHandlerClient.Companion.spaceKey)!!.isKeyDown()

            if (!isPressed) {
                ClientProxyCore.Companion.lastSpacebarDown = false
            }

            if (player.getVehicle() != null && isPressed && !ClientProxyCore.Companion.lastSpacebarDown) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimple(
                        PacketSimple.EnumSimplePacket.S_IGNITE_ROCKET,
                        GCCoreUtil.getDimensionID(player.level()),
                        arrayOf<Any?>()
                    )
                )
                ClientProxyCore.Companion.lastSpacebarDown = true
            }

            if (!(screenConnectionsUpdateList.isEmpty())) {
                val updateListCopy: HashSet<TileEntityScreen> =
                    screenConnectionsUpdateList.clone() as HashSet<TileEntityScreen>
                screenConnectionsUpdateList.clear()
                for (te: TileEntityScreen in updateListCopy) {
                    if (te.getWorld().getBlockState(te.getPos()).getBlock() === GCBlocks.screen) {
                        if (te.refreshOnUpdate) {
                            te.refreshConnections(true)
                        }
                        te.getWorld().markBlockRangeForRenderUpdate(te.getPos(), te.getPos())
                    }
                }
            }
        } else if (event.phase == TickEvent.Phase.END) {
            if (world != null) {
                for (handler: GalacticraftPacketHandler in packetHandlers) {
                    handler.tick(world)
                }
            }
        }
    }

    private fun spawnLeakParticles() {
        val rand: Random = Random()
        for (i: Int in (ClientProxyCore.Companion.leakTrace)!!.indices.reversed()) {
            if (i == 1) continue
            val curr: BlockVec3 = ((ClientProxyCore.Companion.leakTrace)!!.get(i))!!
            var nx: Int = i - 2
            if (i > 2 && rand.nextInt(3) == 0) nx--
            val vec: BlockVec3
            if (i > 1) vec = ((ClientProxyCore.Companion.leakTrace)!!.get(nx))!!.clone()
            else {
                vec = curr.clone().translate(0, -2, 0)
            }
            val mot: Vector3 = Vector3(vec.subtract(curr))
            val rnd: Vector3 =
                Vector3(rand.nextDouble() / 2 - 0.25, rand.nextDouble() / 2 - 0.25, rand.nextDouble() / 2 - 0.25)
            (GalacticraftCore.proxy)!!.spawnParticle(
                "oxygen",
                curr.midPoint().add(rnd),
                mot,
                (arrayOf<Any>(Vector3(0.7, 0.7, 1.0)) as Array<Any?>?)
            )
        }
    }

    private fun alreadyContainsBlock(x1: Int, y1: Int, z1: Int): Boolean {
        return ClientProxyCore.Companion.valueableBlocks.contains(BlockVec3(x1, y1, z1))
    }

    private fun drawGradientRect(par1: Int, par2: Int, par3: Int, par4: Int, par5: Int, par6: Int) {
        val f: Float = (par5 shr 24 and 255) / 255.0f
        val f1: Float = (par5 shr 16 and 255) / 255.0f
        val f2: Float = (par5 shr 8 and 255) / 255.0f
        val f3: Float = (par5 and 255) / 255.0f
        val f4: Float = (par6 shr 24 and 255) / 255.0f
        val f5: Float = (par6 shr 16 and 255) / 255.0f
        val f6: Float = (par6 shr 8 and 255) / 255.0f
        val f7: Float = (par6 and 255) / 255.0f
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        OpenGlHelper.glBlendFunc(770, 771, 1, 0)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        worldRenderer.pos(par3.toDouble(), par2.toDouble(), 0.0).color(f1, f2, f3, f).endVertex()
        worldRenderer.pos(par1.toDouble(), par2.toDouble(), 0.0).color(f1, f2, f3, f).endVertex()
        worldRenderer.pos(par1.toDouble(), par4.toDouble(), 0.0).color(f5, f6, f7, f4).endVertex()
        worldRenderer.pos(par3.toDouble(), par4.toDouble(), 0.0).color(f5, f6, f7, f4).endVertex()
        tessellator.draw()
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    companion object {
        @JvmField
        var airRemaining: Int = 0

        @JvmField
        var airRemaining2: Int = 0

        @JvmField
        var checkedVersion: Boolean = true
        private const val lastInvKeyPressed = false
        private var tickCount: Long = 0

        @JvmField
        var spaceRaceGuiScheduled: Boolean = false
        private val packetHandlers: MutableList<GalacticraftPacketHandler> =
            Lists.newCopyOnWriteArrayList<GalacticraftPacketHandler>()
        private val fluidNetworks: MutableSet<FluidNetwork?> = Sets.newHashSet<FluidNetwork?>()

        @JvmField
        var teleportingGui: GuiTeleporting? = null

        @JvmField
        @Volatile
        var menuReset: Boolean = true

        @JvmField
        @Volatile
        var updateJEIhiding: Boolean = false

        @JvmStatic
        fun resetClient() {
            ClientProxyCore.Companion.playerItemData.clear()
            ClientProxyCore.Companion.overworldTextureRequestSent = false
            ClientProxyCore.Companion.flagRequestsSent.clear()
            clearLiquidNetworks()
            ClientProxyCore.Companion.clientSpaceStationID.clear()
            ConfigManagerCore.challengeModeUpdate()

            if (missingRequirementThread == null) {
                missingRequirementThread = ThreadRequirementMissing(Side.CLIENT)
                missingRequirementThread!!.start()
            }

            MapUtil.resetClient()
        }

        @JvmStatic
        fun addFluidNetwork(network: FluidNetwork) {
            fluidNetworks.add(network)
        }

        @JvmStatic
        fun removeFluidNetwork(network: FluidNetwork?) {
            fluidNetworks.remove(network)
        }

        @JvmStatic
        fun clearLiquidNetworks() {
            fluidNetworks.clear()
        }

        @JvmStatic
        fun addPacketHandler(handler: GalacticraftPacketHandler) {
            packetHandlers.add(handler)
        }

        private var missingRequirementThread: ThreadRequirementMissing? = null

        @JvmField
        var screenConnectionsUpdateList: HashSet<TileEntityScreen?> = HashSet<TileEntityScreen?>()

        init {
            registerDetectableBlocks(true)
        }

        @JvmStatic
        fun registerDetectableBlocks(logging: Boolean) {
            ClientProxyCore.Companion.detectableBlocks.clear()

            for (s in ConfigManagerCore.detectableIDs) {
                val bt: BlockTuple? = ConfigManagerCore.stringToBlock((s)!!, "External Detectable IDs", logging)
                if (bt == null) {
                    continue
                }

                var meta: Int = bt.meta
                if (meta == -1) {
                    meta = 0
                }

                var flag: Boolean = false
                for (blockMetaList in ClientProxyCore.Companion.detectableBlocks) {
                    if ((blockMetaList)!!.getBlock() === bt.block) {
                        if (!((blockMetaList)!!.getMetaList())!!.contains(meta)) {
                            ((blockMetaList)!!.getMetaList())!!.add(meta)
                        }
                        flag = true
                        break
                    }
                }

                if (!flag) {
                    val metaList: MutableList<Int> = Lists.newArrayList<Int>()
                    metaList.add(meta)
                    ClientProxyCore.Companion.detectableBlocks.add(BlockMetaList(bt.block, metaList))
                }
            }
        }

        @JvmStatic
        fun zoom(value: Float) {
//        Minecraft.getInstance().entityRenderer.thirdPersonDistance = value;
//        Minecraft.getInstance().entityRenderer.thirdPersonDistancePrev = value;
        }
    }
}
