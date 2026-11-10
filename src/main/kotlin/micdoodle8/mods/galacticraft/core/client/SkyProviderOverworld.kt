package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.BufferBuilder
import java.util.Random

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project
import java.lang.reflect.Method
import java.util.*
import kotlin.math.*

open class SkyProviderOverworld constructor() : IRenderHandler() {
    private val starGLCallList = GLAllocation.generateDisplayLists(7)
    private val glSkyList: Int
    private val glSkyList2: Int
    private val planetToRender = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")

    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    init {
        GL11.glPushMatrix()
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldrenderer: BufferBuilder = tessellator.getBuffer()
        val rand: Random = Random(10842L)
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE)
        this.renderStars(worldrenderer, rand)
        tessellator.draw()
        GL11.glEndList()
        GL11.glNewList(this.starGLCallList + 1, GL11.GL_COMPILE)
        this.renderStars(worldrenderer, rand)
        tessellator.draw()
        GL11.glEndList()
        GL11.glNewList(this.starGLCallList + 2, GL11.GL_COMPILE)
        this.renderStars(worldrenderer, rand)
        tessellator.draw()
        GL11.glEndList()
        GL11.glNewList(this.starGLCallList + 3, GL11.GL_COMPILE)
        this.renderStars(worldrenderer, rand)
        tessellator.draw()
        GL11.glEndList()
        GL11.glNewList(this.starGLCallList + 4, GL11.GL_COMPILE)
        this.renderStars(worldrenderer, rand)
        tessellator.draw()
        GL11.glEndList()
        GL11.glPopMatrix()
        this.glSkyList = this.starGLCallList + 5
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE)
        val byte2: Byte = 5
        val i: Int = 256 / byte2 + 2
        var f: Float = 16f
        val worldRenderer: BufferBuilder = tessellator.getBuffer()

        var j: Int = -byte2 * i
        while (j <= byte2 * i) {
            var l: Int = -byte2 * i
            while (l <= byte2 * i) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                worldRenderer.pos(j.toDouble(), f.toDouble(), l.toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), l.toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                worldRenderer.pos(j.toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                tessellator.draw()
                l += byte2.toInt()
            }
            j += byte2.toInt()
        }

        GL11.glEndList()
        this.glSkyList2 = this.starGLCallList + 6
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE)
        f = -16f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        var k: Int = -byte2 * i
        while (k <= byte2 * i) {
            var i1: Int = -byte2 * i
            while (i1 <= byte2 * i) {
                worldRenderer.pos((k + byte2).toDouble(), f.toDouble(), i1.toDouble()).endVertex()
                worldRenderer.pos(k.toDouble(), f.toDouble(), i1.toDouble()).endVertex()
                worldRenderer.pos(k.toDouble(), f.toDouble(), (i1 + byte2).toDouble()).endVertex()
                worldRenderer.pos((k + byte2).toDouble(), f.toDouble(), (i1 + byte2).toDouble()).endVertex()
                i1 += byte2.toInt()
            }
            k += byte2.toInt()
        }

        tessellator.draw()
        GL11.glEndList()
    }

    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft) {
        if (!ClientProxyCore.overworldTextureRequestSent) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                    GCCoreUtil.getDimensionID(mc.world),
                    arrayOf<Any?>()
                )
            )
            ClientProxyCore.overworldTextureRequestSent = true
        }

        var zoom: Double = 0.0
        var yaw: Double = 0.0
        var pitch: Double = 0.0
        val m: Method? = null

        if (!optifinePresent) {
            try {
                val c: Class<*> = mc.entityRenderer.javaClass
                zoom = mc.entityRenderer.cameraZoom
                yaw = mc.entityRenderer.cameraYaw
                pitch = mc.entityRenderer.cameraPitch

                GL11.glMatrixMode(GL11.GL_PROJECTION)
                GL11.glLoadIdentity()

                if (zoom != 1.0) {
                    GL11.glTranslatef(yaw.toFloat(), (-pitch).toFloat(), 0.0f)
                    GL11.glScaled(zoom, zoom, 1.0)
                }

                Project.gluPerspective(
                    mc.gameSettings.fovSetting,
                    mc.displayWidth.toFloat() / mc.displayHeight.toFloat(),
                    0.05f,
                    1400.0f
                )
                GL11.glMatrixMode(GL11.GL_MODELVIEW)
                GL11.glLoadIdentity()

                mc.entityRenderer.orientCamera(partialTicks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var theta: Float =
            MathHelper.sqrt(((mc.player.posY).toFloat() - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 1000.0f)
        val var21: Float = max(1.0f - theta * 4.0f, 0.0f)

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GlStateManager.disableRescaleNormal()
        val var2: Vec3d = this.minecraft.world.getSkyColor(this.minecraft.getRenderViewEntity(), partialTicks)
        var i: Float = var2.x.toFloat() * var21
        var x: Float = var2.y.toFloat() * var21
        var var5: Float = var2.z.toFloat() * var21
        var z: Float

        if (this.minecraft.gameSettings.anaglyph) {
            val y: Float = (i * 30.0f + x * 59.0f + var5 * 11.0f) / 100.0f
            val var7: Float = (i * 30.0f + x * 70.0f) / 100.0f
            z = (i * 30.0f + var5 * 70.0f) / 100.0f
            i = y
            x = var7
            var5 = z
        }

        GL11.glColor3f(i, x, var5)
        val var23: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = var23.getBuffer()
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glColor3f(i, x, var5)
        if (mc.player.posY < 214) {
            GL11.glCallList(this.glSkyList)
        }
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderHelper.disableStandardItemLighting()
        val costh: FloatArray? = this.minecraft.world.provider.calcSunriseSunsetColors(
            this.minecraft.world.getCelestialAngle(partialTicks),
            partialTicks
        )
        var var9: Float
        var size: Float
        var rand1: Float
        var r: Float

        if (costh != null) {
            val sunsetModInv: Float = min(1.0f, max(1.0f - theta * 50.0f, 0.0f))

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            GL11.glPushMatrix()
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(
                if (MathHelper.sin(this.minecraft.world.getCelestialAngleRadians(partialTicks)) < 0.0f) 180.0f else 0.0f,
                0.0f,
                0.0f,
                1.0f
            )
            GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
            z = costh[0] * sunsetModInv
            var9 = costh[1] * sunsetModInv
            size = costh[2] * sunsetModInv
            var rand3: Float

            if (this.minecraft.gameSettings.anaglyph) {
                rand1 = (z * 30.0f + var9 * 59.0f + size * 11.0f) / 100.0f
                r = (z * 30.0f + var9 * 70.0f) / 100.0f
                rand3 = (z * 30.0f + size * 70.0f) / 100.0f
                z = rand1
                var9 = r
                size = rand3
            }

            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)

            worldRenderer.pos(0.0, 100.0, 0.0)
                .color(z * sunsetModInv, var9 * sunsetModInv, size * sunsetModInv, costh[3]).endVertex()
            val phi: Byte = 16

            for (var27: Int in 0..phi) {
                rand3 = var27 * Constants.twoPI / phi
                val xx: Float = MathHelper.sin(rand3)
                val rand5: Float = MathHelper.cos(rand3)
                worldRenderer.pos(
                    (xx * 120.0f).toDouble(),
                    (rand5 * 120.0f).toDouble(),
                    (-rand5 * 40.0f * costh[3]).toDouble()
                ).color(costh[0] * sunsetModInv, costh[1] * sunsetModInv, costh[2] * sunsetModInv, 0.0f).endVertex()
            }

            var23.draw()
            GL11.glPopMatrix()
            GL11.glShadeModel(GL11.GL_FLAT)
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

        GL11.glPushMatrix()
        z = 1.0f - this.minecraft.world.getRainStrength(partialTicks)
        var9 = 0.0f
        size = 0.0f
        rand1 = 0.0f
        GL11.glColor4f(1.0f, 1.0f, 1.0f, z)
        GL11.glTranslatef(var9, size, rand1)
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)

        GL11.glRotatef(this.minecraft.world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
        val playerHeight: Double = this.minecraft.player.posY

        //Draw stars
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val threshold: Float
        val vec: Vec3d = TransformerHooks.getFogColorHook(this.minecraft.world)
        threshold = max(0.1f, vec.length() as Float - 0.1f)
        var var20: Float = (playerHeight.toFloat() - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 1000.0f
        var20 = MathHelper.sqrt(var20)
        val bright1: Float = min(0.9f, var20 * 3)
        //        float bright2 = Math.min(0.85F, var20 * 2.5F);
//        float bright3 = Math.min(0.8F, var20 * 2);
//        float bright4 = Math.min(0.75F, var20 * 1.5F);
//        float bright5 = Math.min(0.7F, var20 * 0.75F);
        if (bright1 > threshold) {
            GL11.glColor4f(bright1, bright1, bright1, 1.0f)
            GL11.glCallList(this.starGLCallList)
        }
        //        if (bright2 > threshold)
//        {
//	        GL11.glColor4f(bright2, bright2, bright2, 1.0F);
//	        GL11.glCallList(this.starGLCallList + 1);
//        }
//        if (bright3 > threshold)
//        {
//	        GL11.glColor4f(bright3, bright3, bright3, 1.0F);
        GL11.glCallList(this.starGLCallList + 2)
        //        }
//        if (bright4 > threshold)
//        {
//	        GL11.glColor4f(bright4, bright4, bright4, 1.0F);
        GL11.glCallList(this.starGLCallList + 3)
        //        }
//        if (bright5 > threshold)
//        {
//	        GL11.glColor4f(bright5, bright5, bright5, 1.0F);
        GL11.glCallList(this.starGLCallList + 4)

        //        }

        //Draw sun
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        r = 30.0f
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        this.minecraft.renderEngine.bindTexture(sunTexture)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-r.toDouble(), 100.0, -r.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(r.toDouble(), 100.0, -r.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(r.toDouble(), 100.0, r.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(-r.toDouble(), 100.0, r.toDouble()).tex(0.0, 1.0).endVertex()
        var23.draw()

        //Draw moon
        r = 40.0f
        this.minecraft.renderEngine.bindTexture(moonTexture)
        val sinphi: Float = this.minecraft.world.getMoonPhase().toFloat()
        val cosphi: Int = (sinphi % 4).toInt()
        val var29: Int = (sinphi / 4 % 2).toInt()
        val yy: Float = (cosphi) / 4.0f
        val rand7: Float = (var29) / 2.0f
        val zz: Float = (cosphi + 1) / 4.0f
        val rand9: Float = (var29 + 1) / 2.0f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-r.toDouble(), -100.0, r.toDouble()).tex(zz.toDouble(), rand9.toDouble()).endVertex()
        worldRenderer.pos(r.toDouble(), -100.0, r.toDouble()).tex(yy.toDouble(), rand9.toDouble()).endVertex()
        worldRenderer.pos(r.toDouble(), -100.0, -r.toDouble()).tex(yy.toDouble(), rand7.toDouble()).endVertex()
        worldRenderer.pos(-r.toDouble(), -100.0, -r.toDouble()).tex(zz.toDouble(), rand7.toDouble()).endVertex()
        var23.draw()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glPopMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor3f(0.0f, 0.0f, 0.0f)

        //TODO get exact height figure here
        val var25: Double = playerHeight - 64

        if (var25 > this.minecraft.gameSettings.renderDistanceChunks * 16) {
            theta *= 400.0f

            val sinth: Float = max(min(theta / 100.0f - 0.2f, 0.5f), 0.0f)

            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_FOG)
            var scale: Float = 850 * (0.25f - theta / 10000.0f)
            scale = max(scale, 0.2f)
            GL11.glScalef(scale, 1.0f, scale)
            GL11.glTranslatef(0.0f, -mc.player.posY.toFloat(), 0.0f)
            //	        if (ClientProxyCore.overworldTextureLocal != null)
//	        {
//	            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientProxyCore.overworldTextureLocal.getGlTextureId());
//	        }
//	        else
            run {
                this.minecraft.renderEngine.bindTexture(this.planetToRender)
            }

            size = 1.0f

            GL11.glColor4f(sinth, sinth, sinth, 1.0f)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

            //	        float zoomIn = (1F - (float) var25 / 768F) / 5.86F;
//	        if (zoomIn < 0F) zoomIn = 0F;
            val zoomIn: Double = 0.0
            val cornerB: Double = 1.0 - zoomIn
            worldRenderer.pos(-size.toDouble(), 0.0, size.toDouble()).tex(zoomIn, cornerB).endVertex()
            worldRenderer.pos(size.toDouble(), 0.0, size.toDouble()).tex(cornerB, cornerB).endVertex()
            worldRenderer.pos(size.toDouble(), 0.0, -size.toDouble()).tex(cornerB, zoomIn).endVertex()
            worldRenderer.pos(-size.toDouble(), 0.0, -size.toDouble()).tex(zoomIn, zoomIn).endVertex()
            var23.draw()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }

        GL11.glColor3f(0.0f, 0.0f, 0.0f)

        GlStateManager.enableRescaleNormal()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDepthMask(true)

        if (!optifinePresent) {
            try {
                GL11.glMatrixMode(GL11.GL_PROJECTION)
                GL11.glLoadIdentity()

                if (zoom != 1.0) {
                    GL11.glTranslatef(yaw.toFloat(), (-pitch).toFloat(), 0.0f)
                    GL11.glScaled(zoom, zoom, 1.0)
                }

                Project.gluPerspective(
                    mc.gameSettings.fovSetting,
                    mc.displayWidth.toFloat() / mc.displayHeight.toFloat(),
                    0.05f,
                    this.minecraft.gameSettings.renderDistanceChunks * 16 * 2.0f
                )
                GL11.glMatrixMode(GL11.GL_MODELVIEW)
                GL11.glLoadIdentity()

                mc.entityRenderer.orientCamera(partialTicks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_BLEND)
    }

    private fun renderStars(worldRenderer: BufferBuilder, rand: Random) {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        for (i: Int in 0..<(if (ConfigManagerCore.moreStars) 4000 else 1200)) {
            var x: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var y: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var z: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            val size: Double = (0.15f + rand.nextFloat() * 0.1f).toDouble()
            var r: Double = x * x + y * y + z * z

            if (r < 1.0 && r > 0.01) {
                r = 1.0 / sqrt(r)
                x *= r
                y *= r
                z *= r
                val xx: Double = x * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 100.0 + 150.0 else 100.0)
                val zz: Double = z * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 100.0 + 150.0 else 100.0)
                if (abs(xx) < 29.0 && abs(zz) < 29.0) {
                    continue
                }
                val yy: Double = y * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 100.0 + 150.0 else 100.0)
                val theta: Double = atan2(x, z)
                val sinth: Double = sin(theta)
                val costh: Double = cos(theta)
                val phi: Double = atan2(sqrt(x * x + z * z), y)
                val sinphi: Double = sin(phi)
                val cosphi: Double = cos(phi)
                val rho: Double = rand.nextDouble() * Math.PI * 2.0
                val sinrho: Double = sin(rho)
                val cosrho: Double = cos(rho)

                for (j: Int in 0..3) {
                    val a: Double = 0.0
                    val b: Double = ((j and 2) - 1) * size
                    val c: Double = ((j + 1 and 2) - 1) * size
                    val d: Double = b * cosrho - c * sinrho
                    val e: Double = c * cosrho + b * sinrho
                    val dy: Double = d * sinphi + a * cosphi
                    val ff: Double = a * sinphi - d * cosphi
                    val dx: Double = ff * sinth - e * costh
                    val dz: Double = e * sinth + ff * costh
                    worldRenderer.pos(xx + dx, yy + dy, zz + dz).endVertex()
                }
            }
        }
    }

    companion object {
        private val moonTexture = ResourceLocation("textures/environment/moon_phases.png")
        private val sunTexture = ResourceLocation("textures/environment/sun.png")

        private var optifinePresent = false

        init {
            try {
                optifinePresent = Launch.classLoader.getClassBytes("CustomColorizer") != null
            } catch (e: Exception) {
            }
        }
    }
}
