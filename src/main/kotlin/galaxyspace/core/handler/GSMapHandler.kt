package galaxyspace.core.handler

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import org.lwjgl.opengl.GL11
import org.lwjgl.Sys
import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection
import galaxyspace.core.handler.GSMapHandler
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class GSMapHandler {
    //private static final ResourceLocation acrdisk = new ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/acrdisk.png");
    @JvmField
    val minecraft: Minecraft = Minecraft.getInstance()

    @JvmField
    var rand: Random? = null


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onBodyRender(renderEvent: CelestialBodyRenderEvent.Pre) {
        if (renderEvent.celestialBody == SolarSystemBodies.planetKuiperBelt) {
            GL11.glRotatef(Sys.getTime() / 10.0f % 360, 0f, 0f, 1f)
        }

        if (renderEvent.celestialBody == SolarSystemBodies.planetHaumea) {
            GL11.glRotatef(Sys.getTime() / 1000.0f % 360, 0f, 0f, 1f)
        }

        if ((renderEvent.celestialBody)!!.getRingColorR() == 1.0f && (renderEvent.celestialBody)!!.getRingColorG() == 0.0f && (renderEvent.celestialBody)!!.getRingColorB() == 0.0f) {
            GL11.glRotatef(Sys.getTime() / 10.0f % 360, 0f, 0f, 1f)
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onRenderPlanetPost(event: CelestialBodyRenderEvent.Post) {
        //FIXME TEMP

        if (minecraft.currentScreen is NewGuiCelestialSelection) {
            if (event.celestialBody === SolarSystemBodies.planetSaturn) {
                minecraft.renderEngine.bindTexture(ClientProxyCore.saturnRingTexture)
                val size: Float =
                    (minecraft.currentScreen as NewGuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.0f
                (minecraft.currentScreen as NewGuiCelestialSelection).drawTexturedModalRect(
                    -7.5f * size,
                    -1.75f * size,
                    16.0f * size,
                    3.5f * size,
                    0f,
                    0f,
                    30f,
                    2f,
                    false,
                    false,
                    30f,
                    7f
                )
            } else if (event.celestialBody === SolarSystemBodies.planetUranus) {
                minecraft.renderEngine.bindTexture(ClientProxyCore.uranusRingTexture)
                val size: Float =
                    (minecraft.currentScreen as NewGuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.0f
                (minecraft.currentScreen as NewGuiCelestialSelection).drawTexturedModalRect(
                    -1.75f * size,
                    -7.0f * size,
                    8.5f * size,
                    16.0f * size,
                    0f,
                    0f,
                    28f,
                    7f,
                    false,
                    false,
                    28f,
                    7f
                )
            } else if (event.celestialBody === SolarSystemBodies.planetHaumea) {
                minecraft.renderEngine.bindTexture(haumeaRingTexture)
                val size: Float =
                    (minecraft.currentScreen as NewGuiCelestialSelection).getWidthForCelestialBody(event.celestialBody) / 6.0f
                GL11.glRotatef(45f, 0f, 0f, 1f)
                (minecraft.currentScreen as NewGuiCelestialSelection).drawTexturedModalRect(
                    -4.45f * size,
                    -8.0f * size,
                    9.5f * size,
                    16.0f * size,
                    0f,
                    0f,
                    28f,
                    7f,
                    false,
                    false,
                    28f,
                    7f
                )
            }
        }
    }

    companion object {
        //private static final ResourceLocation saturnRingTexture = new ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn_rings.png");
        //private static final ResourceLocation uranusRingTexture = new ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus_rings.png");
        private val haumeaRingTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/haumea_rings.png")
    }
}
