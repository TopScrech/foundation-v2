package micdoodle8.mods.galacticraft.core.client.screen

import micdoodle8.mods.galacticraft.api.client.IGameScreen
import micdoodle8.mods.galacticraft.api.client.IScreenManager
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent
import micdoodle8.mods.galacticraft.api.galaxies.*
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.render.RenderPlanet
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import net.minecraft.world.WorldProvider
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.nio.DoubleBuffer
import kotlin.DoubleArray
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

open class GameScreenCelestial constructor() : IGameScreen {
    private var renderEngine: TextureManager? = null

    private var frameA = 0f
    private var frameBx = 0f
    private var frameBy = 0f
    private var centreX = 0f
    private var centreY = 0f
    private var scale = 0f

    private val lineSegments = 90
    private val cos = MathHelper.cos(Constants.twoPI / lineSegments)
    private val sin = MathHelper.sin(Constants.twoPI / lineSegments)

    private var planes: DoubleBuffer? = null


    init {
        if (GCCoreUtil.getEffectiveSide().isClient()) {
            renderEngine = FMLClientHandler.instance().getClient().renderEngine
            planes = BufferUtils.createDoubleBuffer(4 * java.lang.Double.SIZE)
        }
    }

    override fun setFrameSize(frameSize: Float) {
        this.frameA = frameSize
    }

    override fun render(type: Int, ticks: Float, scaleX: Float, scaleY: Float, scr: IScreenManager?) {
        centreX = scaleX / 2
        centreY = scaleY / 2
        frameBx = scaleX - frameA
        frameBy = scaleY - frameA
        this.scale = max(scaleX, scaleY) - 0.2f

        drawBlackBackground(0.0f)

        planeEquation(frameA, frameA, 0f, frameA, frameBy, 0f, frameA, frameBy, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE0)
        planeEquation(frameBx, frameBy, 0f, frameBx, frameA, 0f, frameBx, frameA, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE1)
        planeEquation(frameA, frameBy, 0f, frameBx, frameBy, 0f, frameBx, frameBy, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE2, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE2)
        planeEquation(frameBx, frameA, 0f, frameA, frameA, 0f, frameA, frameA, 1f)
        GL11.glClipPlane(GL11.GL_CLIP_PLANE3, planes)
        GL11.glEnable(GL11.GL_CLIP_PLANE3)

        when (type) {
            2 -> {
                val wp: WorldProvider? = (scr)!!.getWorldProvider()
                var body: CelestialBody? = null
                if (wp is IGalacticraftWorldProvider) {
                    body = (wp as IGalacticraftWorldProvider).getCelestialBody()
                }
                if (body == null) {
                    body = GalacticraftCore.planetOverworld
                }
                drawCelestialBodies(body, ticks)
            }

            3 -> drawCelestialBodiesZ((GalacticraftCore.planetOverworld)!!, ticks)
            4 -> drawPlanetsTest(ticks)
        }

        GL11.glDisable(GL11.GL_CLIP_PLANE3)
        GL11.glDisable(GL11.GL_CLIP_PLANE2)
        GL11.glDisable(GL11.GL_CLIP_PLANE1)
        GL11.glDisable(GL11.GL_CLIP_PLANE0)
    }

    private fun drawBlackBackground(greyLevel: Float) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val tess: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tess.getBuffer()
        GL11.glColor4f(greyLevel, greyLevel, greyLevel, 1.0f)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        worldRenderer.pos(frameA.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameBy.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameBx.toDouble(), frameA.toDouble(), 0.005).endVertex()
        worldRenderer.pos(frameA.toDouble(), frameA.toDouble(), 0.005).endVertex()
        tess.draw()

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    private fun drawCelestialBodies(body: CelestialBody?, ticks: Float) {
        var star: Star? = null
        var solarSystem: SolarSystem? = null
        if (body is Planet) {
            solarSystem = body.getParentSolarSystem()
        } else if (body is Moon) {
            solarSystem = (body.getParentPlanet())!!.getParentSolarSystem()
        } else if (body is Satellite) {
            solarSystem = (body.getParentPlanet())!!.getParentSolarSystem()
        }

        if (solarSystem == null) {
            solarSystem = GalacticraftCore.solarSystemSol
        }
        star = (solarSystem)!!.getMainStar()

        if (star != null && star.getBodyIcon() != null) {
            this.drawCelestialBody(star, 0f, 0f, ticks, 6f)
        }

        val mainSolarSystem: String? = (solarSystem)!!.getTranslationKey()
        for (planet: Planet in (GalaxyRegistry.getRegisteredPlanets())!!.values) {
            if (planet.getParentSolarSystem() != null && planet.getBodyIcon() != null) {
                if (((planet.getParentSolarSystem())!!.getTranslationKey().equals(mainSolarSystem, ignoreCase = true))!!) {
                    val pos: Vector3f = this.getCelestialBodyPosition(planet, ticks)
                    this.drawCircle(planet)
                    this.drawCelestialBody(
                        planet,
                        pos.x,
                        pos.y,
                        ticks,
                        if (planet.getRelativeDistanceFromCenter().unScaledDistance < 1.5f) 2f else 2.8f
                    )
                }
            }
        }
    }

    private fun drawCelestialBodiesZ(planet: CelestialBody, ticks: Float) {
        this.drawCelestialBody(planet, 0f, 0f, ticks, 11f)

        for (moon: Moon in (GalaxyRegistry.getRegisteredMoons())!!.values) {
            if (moon.getParentPlanet() === planet && moon.getBodyIcon() != null) {
                val pos: Vector3f = this.getCelestialBodyPosition(moon, ticks)
                this.drawCircle(moon)
                this.drawCelestialBody(moon, pos.x, pos.y, ticks, 4f)
            }
        }

        for (satellite: Satellite in (GalaxyRegistry.getRegisteredSatellites())!!.values) {
            if (satellite.getParentPlanet() === planet) {
                val pos: Vector3f = this.getCelestialBodyPosition(satellite, ticks)
                this.drawCircle(satellite)
                this.drawCelestialBody(satellite, pos.x, pos.y, ticks, 3f)
            }
        }
    }

    private fun drawTexturedRectCBody(x: Float, y: Float, width: Float, height: Float) {
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
    }

    private fun drawCelestialBody(planet: CelestialBody, xPos: Float, yPos: Float, ticks: Float, relSize: Float) {
        if (xPos + centreX > frameBx || xPos + centreX < frameA) {
            return
        }
        if (yPos + centreY > frameBy || yPos + centreY < frameA) {
            return
        }

        GL11.glPushMatrix()
        GL11.glTranslatef(xPos + centreX, yPos + centreY, 0f)

        val alpha: Float = 1.0f

        val preEvent: CelestialBodyRenderEvent.Pre = CelestialBodyRenderEvent.Pre(planet, planet.getBodyIcon(), 12)
        MinecraftForge.EVENT_BUS.post(preEvent)

        GL11.glColor4f(1f, 1f, 1f, alpha)
        if (preEvent.celestialBodyTexture != null) {
            this.renderEngine!!.bindTexture(preEvent.celestialBodyTexture)
        }

        if (!preEvent.isCanceled()) {
            val size: Float = relSize / 70 * scale
            this.drawTexturedRectCBody(-size / 2, -size / 2, size, size)
        }

        val postEvent: CelestialBodyRenderEvent.Post = CelestialBodyRenderEvent.Post(planet)
        MinecraftForge.EVENT_BUS.post(postEvent)

        GL11.glPopMatrix()
    }

    private fun drawCircle(cBody: CelestialBody) {
        GL11.glPushMatrix()
        GL11.glTranslatef(centreX, centreY, 0.002f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        val sd: Float = 0.002514f * scale
        var x: Float = this.getScale(cBody)
        var y: Float = 0f
        val grey: Float = 0.1f + 0.65f * max(0f, (0.5f - x))
        x = x * scale / sd

        GL11.glColor4f(grey, grey, grey, 1.0f)
        GL11.glLineWidth(0.002f)

        GL11.glScalef(sd, sd, sd)
        val preEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre =
            CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(cBody, Vector3f(0.0f, 0.0f, 0.0f))
        MinecraftForge.EVENT_BUS.post(preEvent)

        if (!preEvent.isCanceled()) {
            GL11.glBegin(GL11.GL_LINE_LOOP)

            var temp: Float
            for (i: Int in 0..<lineSegments) {
                GL11.glVertex2f(x, y)

                temp = x
                x = cos * x - sin * y
                y = sin * temp + cos * y
            }

            GL11.glEnd()
        }

        val postEvent: CelestialBodyRenderEvent.CelestialRingRenderEvent.Post =
            CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(cBody)
        MinecraftForge.EVENT_BUS.post(postEvent)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()
    }

    private fun getCelestialBodyPosition(cBody: CelestialBody, ticks: Float): Vector3f {
        val timeScale: Float = if (cBody is Planet) 200.0f else 2.0f
        val distanceFromCenter: Float = this.getScale(cBody) * scale
        return Vector3f(
            sin((ticks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter,
            cos((ticks / (timeScale * cBody.getRelativeOrbitTime()) + cBody.getPhaseShift()).toDouble()).toFloat() * distanceFromCenter,
            0f
        )
    }

    private fun getScale(celestialBody: CelestialBody): Float {
        var distance: Float = celestialBody.getRelativeDistanceFromCenter().unScaledDistance
        if (distance >= 1.375f) {
            if (distance >= 1.5f) {
                distance *= 1.15f
            } else {
                distance += 0.075f
            }
        }
        return 1 / 140.0f * distance * (if (celestialBody is Planet) 25.0f else 3.5f)
    }

    private fun planeEquation(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        x3: Float,
        y3: Float,
        z3: Float
    ) {
        val result: DoubleArray = DoubleArray(4)
        result[0] = (y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2)).toDouble()
        result[1] = (z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2)).toDouble()
        result[2] = (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)).toDouble()
        result[3] = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1)).toDouble()
        planes!!.put(result, 0, 4)
        planes!!.position(0)
    }

    private fun drawPlanetsTest(ticks: Float) {
        GL11.glPushMatrix()
        GL11.glTranslatef(centreX, centreY, 0f)

        val id: Int = (ticks / 600f).toInt() % 5
        RenderPlanet.renderID(id, scale, ticks)
        GL11.glPopMatrix()
    }

    private fun drawTexturedRectUV(x: Float, y: Float, width: Float, height: Float, ticks: Float) {
        for (ysect: Int in 0..5) {
//    		drawTexturedRectUVSixth(x, y, width, height, (ticks / 600F) % 1F, ysect / 6F);
            val angle: Float = 7.5f + 15f * ysect
            drawTexturedRectUVSixth(
                x,
                y,
                width,
                height,
                (ticks / (900f - 80f * MathHelper.cos(angle))) % 1f,
                ysect / 6f
            )
        }
    }

    private fun drawTexturedRectUVSixth(x: Float, y: Float, width: Float, height: Float, prog: Float, y0: Float) {
        var prog: Float = prog
        var y0: Float = y0
        y0 /= 2f
        prog = 1.0f - prog
        val y1: Float = y0 + 1 / 12f
        val y2: Float = 1f - y1
        val y3: Float = 1f - y0
        val yaa: Float = y + height * y0
        val yab: Float = y + height * y1
        val yba: Float = y + height * y2
        val ybb: Float = y + height * y3
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        if (prog <= 0.75f) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), yab.toDouble(), 0.0).tex(prog.toDouble(), y1.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yab.toDouble(), 0.0).tex((prog + 0.25f).toDouble(), y1.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yaa.toDouble(), 0.0).tex((prog + 0.25f).toDouble(), y0.toDouble())
                .endVertex()
            worldRenderer.pos(x.toDouble(), yaa.toDouble(), 0.0).tex(prog.toDouble(), y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), ybb.toDouble(), 0.0).tex(prog.toDouble(), y3.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), ybb.toDouble(), 0.0).tex((prog + 0.25f).toDouble(), y3.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yba.toDouble(), 0.0).tex((prog + 0.25f).toDouble(), y2.toDouble())
                .endVertex()
            worldRenderer.pos(x.toDouble(), yba.toDouble(), 0.0).tex(prog.toDouble(), y2.toDouble()).endVertex()
            tessellator.draw()
        } else {
            val xp: kotlin.Double = (x + width * (1f - prog) / 0.25f).toDouble()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), yab.toDouble(), 0.0).tex(prog.toDouble(), y1.toDouble()).endVertex()
            worldRenderer.pos(xp, yab.toDouble(), 0.0).tex(1.0, y1.toDouble()).endVertex()
            worldRenderer.pos(xp, yaa.toDouble(), 0.0).tex(1.0, y0.toDouble()).endVertex()
            worldRenderer.pos(x.toDouble(), yaa.toDouble(), 0.0).tex(prog.toDouble(), y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(x.toDouble(), ybb.toDouble(), 0.0).tex(prog.toDouble(), y3.toDouble()).endVertex()
            worldRenderer.pos(xp, ybb.toDouble(), 0.0).tex(1.0, y3.toDouble()).endVertex()
            worldRenderer.pos(xp, yba.toDouble(), 0.0).tex(1.0, y2.toDouble()).endVertex()
            worldRenderer.pos(x.toDouble(), yba.toDouble(), 0.0).tex(prog.toDouble(), y2.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(xp, yab.toDouble(), 0.0).tex(0.0, y1.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), yab.toDouble(), 0.0).tex((prog - 0.75f).toDouble(), y1.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yaa.toDouble(), 0.0).tex((prog - 0.75f).toDouble(), y0.toDouble())
                .endVertex()
            worldRenderer.pos(xp, yaa.toDouble(), 0.0).tex(0.0, y0.toDouble()).endVertex()
            tessellator.draw()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(xp, ybb.toDouble(), 0.0).tex(0.0, y3.toDouble()).endVertex()
            worldRenderer.pos((x + width).toDouble(), ybb.toDouble(), 0.0).tex((prog - 0.75f).toDouble(), y3.toDouble())
                .endVertex()
            worldRenderer.pos((x + width).toDouble(), yba.toDouble(), 0.0).tex((prog - 0.75f).toDouble(), y2.toDouble())
                .endVertex()
            worldRenderer.pos(xp, yba.toDouble(), 0.0).tex(0.0, y2.toDouble()).endVertex()
            tessellator.draw()
        }
    }
}
