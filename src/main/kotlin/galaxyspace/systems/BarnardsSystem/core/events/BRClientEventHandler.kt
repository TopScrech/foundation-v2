package galaxyspace.systems.BarnardsSystem.core.events

import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import galaxyspace.GalaxySpace
import galaxyspace.systems.BarnardsSystem.core.events.BRClientEventHandler
import org.lwjgl.opengl.GL11
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky.WeatherRendererBarnarda_C1
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BRClientEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun onRenderPlanetPost(event: CelestialBodyRenderEvent.Post) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
        val ringTexture: ResourceLocation =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/rings_type_1.png")

        if (minecraft.currentScreen is GuiCelestialSelection) {
            if (event.celestialBody === BarnardsSystemBodies.Companion.Barnarda_B) {
                GL11.glEnable(GL11.GL_BLEND)
                minecraft.renderEngine.bindTexture(ringTexture)
                val size: Float =
                    (minecraft.currentScreen as GuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.1f
                (minecraft.currentScreen as GuiCelestialSelection).drawTexturedModalRect(
                    -8.6f * size,
                    -.7f * size,
                    17.9f * size,
                    3.5f * size,
                    0f,
                    0f,
                    32f,
                    7f,
                    false,
                    false,
                    32f,
                    7f
                )
            }
        }
    }

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun onClientTick(event: TickEvent.ClientTickEvent?) {
            val mc: Minecraft = Minecraft.getMinecraft()

            if (!mc.isGamePaused() && mc.world != null && mc.world.provider.getWeatherRenderer() is WeatherRendererBarnarda_C1) {
                (mc.world.provider.getWeatherRenderer() as WeatherRendererBarnarda_C1).tick()
            }
        }
    }
}
