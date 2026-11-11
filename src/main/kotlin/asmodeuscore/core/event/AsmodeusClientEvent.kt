package asmodeuscore.core.event

import foundation.item.legacyItemValue

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection
import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection
import asmodeuscore.core.astronomy.sky.CloudProviderBase
import asmodeuscore.core.astronomy.sky.CustomCloudRender
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.api.space.IExBody
import asmodeuscore.core.astronomy.gui.overlay.OverlayDetectors
import asmodeuscore.core.utils.ACAttributePlayer
import asmodeuscore.api.dimension.IAdvancedSpace
import asmodeuscore.core.event.PressureEvent
import asmodeuscore.core.event.AsmodeusClientEvent
import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import asmodeuscore.core.handler.ColorBlockHandler
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderAsteroids
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderMoon
import asmodeuscore.core.astronomy.sky.galacticraft.SkyProviderOverworld
import asmodeuscore.core.utils.Utils
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.WE_PerlinNoise
import com.google.common.base.Strings
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.settings.GameSettings
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Item
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList

open class AsmodeusClientEvent {
    @JvmField
    var mc: Minecraft = Minecraft.getInstance()

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onGuiOpenEvent(event: GuiOpenEvent) {
        if (ACCompatibilityManager.isGalacticraftLoaded()) if (event.getGui() is GuiCelestialSelection && AsmodeusConfig.Companion.enableNewGalaxyMap) {
            /*if(Loader.isModLoaded("ExtraPlanets"))
					event.getGui().mc.displayGuiScreen(null);
				*/
            //if (!ACCompatibilityManager.isPlanetProgressionLoaded())
            if (GameSettings.isKeyDown(KeyHandlerClient.galaxyMap)) event.setGui(
                NewGuiCelestialSelection(
                    true,
                    null,
                    false,
                    null
                )
            )
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && GameSettings.isKeyDown(KeyHandlerClient.galaxyMap)) event.setGui(
                AC_GuiCelestialSelection(true, null, false)
            )
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onTick(e: TickEvent.ClientTickEvent?) {
        val world: World? = mc.world

        if (world != null) {
            if (world.provider.getCloudRenderer() is CloudProviderBase && !mc.isGamePaused()) {
                val clouds: CloudProviderBase? = world.provider.getCloudRenderer() as CloudProviderBase?

                clouds!!.cloudTicksPrev = clouds.cloudTicks
                clouds.cloudTicks = (clouds.cloudTicks + clouds.getCloudMovementSpeed(world)).toLong()
            }

            if (world.provider.getCloudRenderer() is CustomCloudRender && !mc.isGamePaused()) {
                val clouds: CustomCloudRender? = world.provider.getCloudRenderer() as CustomCloudRender?

                clouds!!.cloudTicksPrev = clouds.cloudTicks
                clouds.cloudTicks =
                    (clouds.cloudTicks + clouds.getCloudMovementSpeed(world as WorldClient)).toLong()
            }

            if (world.provider.getDimension() == 0 && AsmodeusConfig.Companion.enableSkyOverworld) {
                if (world.provider.getSkyRenderer() !is SkyProviderOverworld) world.provider.setSkyRenderer(
                    SkyProviderOverworld()
                )
            }

            if (world.provider is WorldProviderMoon && AsmodeusConfig.Companion.enableSkyMoon) {
                if (world.provider.getSkyRenderer() !is SkyProviderMoon) world.provider.setSkyRenderer(SkyProviderMoon())
            }

            if (world.provider is WorldProviderAsteroids && AsmodeusConfig.Companion.enableSkyAsteroids) {
                if (world.provider.getSkyRenderer() !is SkyProviderAsteroids) world.provider.setSkyRenderer(
                    SkyProviderAsteroids()
                )
            }

            if (world.provider is WorldProviderVenus && AsmodeusConfig.Companion.enableCloudsVenus) {
                if (world.provider.getCloudRenderer() !is CustomCloudRender) {
                    val f: FloatArray = floatArrayOf(1.0f, 4.0f, 8f)
                    world.provider.setCloudRenderer(object : CustomCloudRender(f) {
                        public override fun getCloudTexture(): ResourceLocation? {
                            return default_clouds
                        }

                        public override fun getCloudColor(renderPartialTicks: Float): Vec3d {
                            return Vec3d(81.0, 61.0, 0.0)
                        }

                        public override fun getCloudMovementSpeed(world: WorldClient?): Float {
                            return 3f
                        }
                    })
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onRenderTick(event: TickEvent.RenderTickEvent) {
        if (!ACCompatibilityManager.isGalacticraftLoaded()) return

        val minecraft: Minecraft = Minecraft.getInstance()
        val player: EntityPlayerSP = minecraft.player ?: return
        val playerBaseClient: EntityPlayerSP = PlayerUtil.getPlayerBaseClientFromPlayer(player, false) ?: return
        var level: Float = 0f
        if (event.phase == TickEvent.Phase.END) {
            if (player != null) {
                if (player.level().provider is IGalacticraftWorldProvider && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen) && !playerBaseClient.isSpectator() && !minecraft.gameSettings.showDebugInfo) {
                    val provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider
                    val data: BodiesData? = BodiesRegistry.getData(provider.getCelestialBody())

                    val stats: GCPlayerStatsClient? = GCPlayerStatsClient.get(player)


                    if (provider.getCelestialBody() is IExBody && !minecraft.player.isCreative()) {
                        level = (provider.getCelestialBody() as IExBody).getAtmosphericPressure()

                        if (level != 0f) OverlayDetectors.renderPressureIndicator(
                            level,
                            this.getInvalidLevel(1, level),
                            !ConfigManagerCore.oxygenIndicatorLeft,
                            !ConfigManagerCore.oxygenIndicatorBottom
                        )

                        if ((provider.getCelestialBody() as IExBody).isSolarRadiation()) OverlayDetectors.renderRadiationIndicator(
                            player.getAttribute(ACAttributePlayer.RADIATION_LVL)!!.getValue().toInt(),
                            this.getInvalidLevel(2, level),
                            !ConfigManagerCore.oxygenIndicatorLeft,
                            !ConfigManagerCore.oxygenIndicatorBottom
                        )

                        if ((provider.getCelestialBody() as IExBody).getWaterPressure() > 0.0f && Utils.inWater(player)) {
                            var i: Float = 0f
                            var blocks: Int = 0

                            while (player.level().getBlockState(player.getPosition().up(blocks))
                                    .getMaterial() !== Material.AIR
                            ) {
                                i += (0.1 * (provider.getCelestialBody() as IExBody).getWaterPressure()).toFloat()
                                blocks++
                            }

                            OverlayDetectors.renderWaterPressureIndicator(
                                i,
                                this.getInvalidLevel(3, i),
                                !ConfigManagerCore.oxygenIndicatorLeft,
                                !ConfigManagerCore.oxygenIndicatorBottom
                            )
                        }
                    } else {
                        if (data != null) level = data.getPressure().toFloat()

                        if (level != 0f) OverlayDetectors.renderPressureIndicator(
                            level,
                            this.getInvalidLevel(1, level),
                            !ConfigManagerCore.oxygenIndicatorLeft,
                            !ConfigManagerCore.oxygenIndicatorBottom
                        )

                        if ((data != null && data.isSolarRadiation())) OverlayDetectors.renderRadiationIndicator(
                            player.getAttribute(ACAttributePlayer.RADIATION_LVL)!!.getValue().toInt(),
                            this.getInvalidLevel(2, level),
                            !ConfigManagerCore.oxygenIndicatorLeft,
                            !ConfigManagerCore.oxygenIndicatorBottom
                        )
                    }

                    val temp: Float

                    //0.0 == getDefaultDergrees
                    var mod: Float = provider.getThermalLevelModifier()
                    if (player.level().provider is IAdvancedSpace) mod =
                        (provider as IAdvancedSpace).getThermalLevelModifier(player)
                    if (mod > 0) mod += 1.0f
                    temp = mod * BodiesRegistry.getDefaultDergrees


                    //System.out.println(stats.getThermalLevel());
                    OverlayDetectors.renderInfoPanel(
                        temp,
                        this.getInvalidLevel(2, level),
                        !ConfigManagerCore.oxygenIndicatorLeft,
                        !ConfigManagerCore.oxygenIndicatorBottom
                    )
                }

                if (AsmodeusConfig.Companion.enableDebug && minecraft.inGameHasFocus) {
                    this.renderDebugInfo(minecraft)
                }
                //OverlaySpaceSuit.render();
            }
        }
    }

    private fun getInvalidLevel(mode: Int, level: Float): Boolean {
        val minecraft: Minecraft = Minecraft.getInstance()
        val player: EntityPlayer = minecraft.player

        val event: PressureEvent = PressureEvent(player, level)
        if (event.isCanceled()) return false


        /*if(player.capabilities.isCreativeMode) 
        	return false;*/
        if (mode == 1) {
            if (level > 25 && !Utils.getAtmoArmor(player)) return true
        } else if (mode == 2) {
            if (player.getAttribute(ACAttributePlayer.RADIATION_LVL)!!.getValue() > 45.0) return true
        } else if (mode == 3) if (level > 40 && !Utils.getAtmoArmor(player)) return true

        return false
    }

    private fun renderDebugInfo(mc: Minecraft) {
        val list: MutableList<String?> = getDebugList()

        var size: Int = 3
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) size = list.size

        var yOffset: Int = 0
        if (mc.gameSettings.showDebugInfo) {
            val screen: ScaledResolution = ScaledResolution(mc)
            yOffset = screen.getScaledHeight() / 2 - 50
        }
        for (i: Int in 0..<size) {
            val s: String? = list.get(i)
            if (!Strings.isNullOrEmpty(s)) {
                val j: Int = mc.fontRenderer.FONT_HEIGHT
                val k: Int = mc.fontRenderer.getStringWidth(s)

                val i1: Int = 2 + j * i
                Gui.drawRect(1, i1 - 1 + yOffset, 2 + k + 1, i1 + j - 1 + yOffset, -1873784752)
                mc.fontRenderer.drawString(s, 2, i1 + yOffset, 14737632)
            }
        }
    }

    companion object {
        @JvmField
        var inOxygenBlock: Boolean = false

        @JvmStatic
        fun getDebugList(): MutableList<String?> {
            val mc: Minecraft = Minecraft.getInstance()
            val body: String? =
                (if (mc.player.level().provider is IGalacticraftWorldProvider) ((mc.player.level().provider as IGalacticraftWorldProvider).getCelestialBody())!!
                    .getLocalizedName() else "Unnamed")
            val current_item: String = (if (mc.player.inventory.getSelectedItem().getItem() !== Item.getItemFromBlock(
                    Blocks.AIR
                )
            ) Item.REGISTRY.getNameForObject(mc.player.inventory.getSelectedItem().getItem())
                .toString() + ":" + mc.player.inventory.getSelectedItem().legacyItemValue else "None")

            val temp: Float =
                if (mc.player.level().provider is IGalacticraftWorldProvider) (mc.player.level().provider as IGalacticraftWorldProvider).getThermalLevelModifier() else 1.0f
            val t1: Long =
                if (mc.player.level().provider is WorldProviderSpace) (mc.player.level().provider as WorldProviderSpace).getDayLength() else 24000
            val time: Long = mc.player.level().getWorldTime() % (if (t1 > 0) t1 else 1)
            val get_chunk: Chunk = mc.player.level().getChunk(mc.player.getPosition())

            val isWE: Boolean = mc.player.level().provider is WE_WorldProvider
            var count: Double = 0.0

            val world: World = (ColorBlockHandler.Companion.world)!!
            if (isWE && mc.player.level().provider != null && world.provider is WE_WorldProvider) {
                val chunk: WE_ChunkProvider? = (world.provider as WE_WorldProvider).chunk_provider
                if (chunk != null) {
                    val scaleX: Double = chunk.biomemapScaleX
                    val persistance: Double = chunk.biomemapPersistence
                    count = (WE_PerlinNoise.PerlinNoise2D(
                        (mc.player.level().getSeed() * 11) xor 6L,
                        mc.player.level().getChunk(mc.player.getPosition()).x / scaleX,
                        mc.player.level().getChunk(mc.player.getPosition()).z / scaleX,
                        persistance,
                        chunk.biomemapNumberOfOctaves
                    )
                            * chunk.biomemapScaleY)
                }
            }

            val list: MutableList<String?> = ArrayList<String?>()
            list.add(AsmodeusCore.NAME + " " + AsmodeusCore.VERSION + " Debug MODE | FPS: " + Minecraft.getDebugFPS())
            list.add("")
            list.add("Celestial Body: " + EnumColor.BRIGHT_GREEN + body)
            list.add("")
            list.add("Current item: " + EnumColor.ORANGE + current_item)
            list.add(
                String.format(
                    "Player pos: x=%s y=%s z=%s",
                    mc.player.getPosition().getX(),
                    mc.player.getPosition().getY(),
                    mc.player.getPosition().getZ()
                )
            )
            list.add("Chunk pos: x=" + get_chunk.x + " z=" + get_chunk.z)
            list.add("")
            list.add("World Data:")
            list.add(
                String.format(
                    "Dimension: %s (ID: %s)",
                    mc.player.level().provider.getDimensionType().getName(),
                    mc.player.level().provider.getDimensionType().getId()
                )
            )
            list.add("Temperature (F): " + temp)
            list.add("Vanilla Biome: " + mc.player.level().getBiomeForCoordsBody(mc.player.getPosition()).getBiomeName())
            list.add("Current Time: " + time + " | Day Length: " + (if (mc.player.level().provider is WorldProviderSpace) (mc.player.level().provider as WorldProviderSpace).getDayLength() else if (mc.player.level().provider is WE_WorldProviderSpace) (mc.player.level().provider as WE_WorldProviderSpace).getDayLength() else "24000") + " | Total Time: " + mc.player.level().getWorldTime())
            list.add("Moon Phase: " + mc.player.level().getMoonPhase())
            list.add("Is Advance Space Provider: " + (if (mc.player.level().provider is IAdvancedSpace) "Yes" else "No"))

            if (isWE) {
                list.add("[WE] Biome Perlin Count: " + count)
            }
            return list
        }
    }
}
