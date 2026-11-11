package asmodeuscore.core.handler

import asmodeuscore.core.astronomy.gui.screen.NewGuiCelestialSelection
import asmodeuscore.core.astronomy.gui.screen.AC_GuiCelestialSelection
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.core.astronomy.BodiesRegistry
import org.lwjgl.opengl.GL11
import asmodeuscore.core.handler.GalaxyMapHandler
import asmodeuscore.core.proxy.ClientProxy
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.util.vector.Vector3f
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

open class GalaxyMapHandler {
    //private static final ResourceLocation acrdisk = new ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/acrdisk.png");
    @JvmField
    val minecraft: Minecraft? = Minecraft.getInstance()

    @JvmField
    var rand: Random? = null


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    open fun onRingRender(renderEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre) {
        if ((renderEvent.celestialBody)!!.getRingColorR() == 1.1f && (renderEvent.celestialBody)!!.getRingColorG() == 0.0f && (renderEvent.celestialBody)!!.getRingColorB() == 0.0f) {
            this.RingRender(renderEvent, renderEvent.celestialBody, 0.7f, 0.0f, 0.0f)
        }


        if ((renderEvent.celestialBody)!!.getRingColorG() == 1.1f && (renderEvent.celestialBody)!!.getRingColorB() == 0.0f && (renderEvent.celestialBody)!!.getRingColorR() == 0.0f) {
            this.RingRender(renderEvent, renderEvent.celestialBody, 0.0f, 0.7f, 0.0f)
        }

        if ((renderEvent.celestialBody)!!.getRingColorB() == 1.1f && (renderEvent.celestialBody)!!.getRingColorG() == 0.0f && (renderEvent.celestialBody)!!.getRingColorR() == 0.0f) {
            this.RingRender(renderEvent, renderEvent.celestialBody, 0.0f, 0.0f, 0.7f)
        }

        if ((renderEvent.celestialBody)!!.getRingColorR() == 0.0f && (renderEvent.celestialBody)!!.getRingColorG() == 0.0f && (renderEvent.celestialBody)!!.getRingColorB() == 0.0f) {
            this.RingRenderNull(renderEvent, renderEvent.celestialBody)
        }

        if (renderEvent.celestialBody is Star && ClientProxy.Companion.renderRingZones) {
            this.RingRenderHabitableZone(renderEvent)
        }
    }

    open fun RingRenderNull(
        renderEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre,
        aroundBody: CelestialBody?
    ) {
        renderEvent.setCanceled(true)
    }

    open fun RingRenderHabitableZone(renderEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre) {
        val data: BodiesData? = BodiesRegistry.getData(renderEvent.celestialBody)
        if (data == null) return

        val mapPos: Vector3f = (renderEvent.parentOffset)!!
        val xOffset: Float = mapPos.x
        val yOffset: Float = mapPos.y

        val sum: Float = data.getStarHZoneDistance() - data.getStarHZoneSize()

        renderEvent.setCanceled(true)

        val theta: Float = (2 * Math.PI / 90).toFloat()
        val cos: Float = cos(theta.toDouble()).toFloat()
        val sin: Float = sin(theta.toDouble()).toFloat()

        val min: Float = 72.0f
        val max: Float = 78.0f

        var x: Float = max * (data.getStarHZoneDistance() + data.getStarHZoneSize())
        var y: Float = 0f

        var temp: Float
        GL11.glColor4f(0.0f, 0.7f, 0.0f, 0.5f)
        //OUTER LINE
        GL11.glBegin(GL11.GL_LINE_LOOP)
        for (i: Int in 0..89) {
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
        }

        GL11.glEnd()
        //INNER LINE
        GL11.glBegin(GL11.GL_LINE_LOOP)
        x = min * (data.getStarHZoneDistance() - data.getStarHZoneSize())
        y = 0f

        for (i: Int in 0..89) {
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
        }
        GL11.glEnd()
        //IN LINES
        GL11.glColor4f(0.0f, 0.7f, 0.0f, 0.1f)
        GL11.glBegin(GL11.GL_QUADS)
        x = min * (data.getStarHZoneDistance() - data.getStarHZoneSize())
        y = 0f
        var x2: Float = max * (data.getStarHZoneDistance() + data.getStarHZoneSize())
        var y2: Float = 0f

        for (i: Int in 0..89) {
            GL11.glVertex2f(x2 + xOffset, y2 + yOffset)
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
            temp = x2
            x2 = cos * x2 - sin * y2
            y2 = sin * temp + cos * y2

            GL11.glVertex2f(x + xOffset, y + yOffset)
            GL11.glVertex2f(x2 + xOffset, y2 + yOffset)
        }
        GL11.glEnd()
    }

    open fun RingRender(
        renderEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre,
        aroundBody: CelestialBody?,
        par1: Float,
        par2: Float,
        par3: Float
    ) {
        val mapPos: Vector3f = (renderEvent.parentOffset)!!

        val sum: Float =
            (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance - (renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().scaledDistance

        val xOffset: Float = mapPos.x
        val yOffset: Float = mapPos.y


        if (FMLClientHandler.instance()
                .getClient().currentScreen is AC_GuiCelestialSelection || FMLClientHandler.instance()
                .getClient().currentScreen is NewGuiCelestialSelection
        ) GL11.glColor4f(par1, par2, par3, 0.5f)
        else GL11.glColor4f(0.3f, 0.1f, 0.1f, 0.0f)

        renderEvent.setCanceled(true)

        val theta: Float = (2 * Math.PI / 90).toFloat()
        val cos: Float = cos(theta.toDouble()).toFloat()
        val sin: Float = sin(theta.toDouble()).toFloat()

        var min: Float = 72.0f
        var max: Float = 78.0f

        if (aroundBody is Planet) {
            min = 72f
            max = 78f
        } else if (aroundBody is Moon) {
            max = 1 / 1.5f
            min = 1 / 1.9f
        }

        var x: Float = max * ((renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance + sum)
        var y: Float = 0f

        var temp: Float
        //OUTER LINE
        GL11.glBegin(GL11.GL_LINE_LOOP)
        for (i: Int in 0..89) {
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
        }

        GL11.glEnd()

        GL11.glBegin(GL11.GL_LINE_LOOP)
        x = min * ((renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance - sum)
        y = 0f

        for (i: Int in 0..89) {
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
        }
        GL11.glEnd()
        GL11.glColor4f(par1, par2, par3, 0.1f)

        GL11.glBegin(GL11.GL_QUADS)
        x = min * ((renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance - sum)
        y = 0f
        var x2: Float = max * ((renderEvent.celestialBody)!!.getRelativeDistanceFromCenter().unScaledDistance + sum)
        var y2: Float = 0f

        for (i: Int in 0..89) {
            GL11.glVertex2f(x2 + xOffset, y2 + yOffset)
            GL11.glVertex2f(x + xOffset, y + yOffset)

            temp = x
            x = cos * x - sin * y
            y = sin * temp + cos * y
            temp = x2
            x2 = cos * x2 - sin * y2
            y2 = sin * temp + cos * y2

            GL11.glVertex2f(x + xOffset, y + yOffset)
            GL11.glVertex2f(x2 + xOffset, y2 + yOffset)
        }
        GL11.glEnd()
    }

    companion object {
        private val saturnRingTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn_rings.png")
        private val uranusRingTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus_rings.png")
    }
}
