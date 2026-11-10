package micdoodle8.mods.galacticraft.planets.mars.client

import java.util.Random

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * SkyProviderMars.java
 *
 *
 * This file is part of the Galacticraft project
 *
 * @author micdoodle8
 */
@SideOnly(Side.CLIENT)
open class SkyProviderMars constructor(marsProvider: IGalacticraftWorldProvider) : IRenderHandler() {
    @JvmField
    var starList: Int

    @JvmField
    var glSkyList: Int

    @JvmField
    var glSkyList2: Int
    private val sunSize: Float

    init {
        this.sunSize = 17.5f * marsProvider.getSolarSize()

        val displayLists: Int = GLAllocation.generateDisplayLists(3)
        this.starList = displayLists
        this.glSkyList = displayLists + 1
        this.glSkyList2 = displayLists + 2

        // Bind stars to display list
        GL11.glPushMatrix()
        GL11.glNewList(this.starList, GL11.GL_COMPILE)
        this.renderStars()
        GL11.glEndList()
        GL11.glPopMatrix()

        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE)
        val byte2: Byte = 64
        val i: Int = 256 / byte2 + 2
        var f: Float = 16f

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

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GlStateManager.disableRescaleNormal()
        val vec3: Vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks)
        var f1: Float = vec3.x.toFloat()
        var f2: Float = vec3.y.toFloat()
        var f3: Float = vec3.z.toFloat()
        var f6: Float

        if (mc.gameSettings.anaglyph) {
            val f4: Float = (f1 * 30.0f + f2 * 59.0f + f3 * 11.0f) / 100.0f
            val f5: Float = (f1 * 30.0f + f2 * 70.0f) / 100.0f
            f6 = (f1 * 30.0f + f3 * 70.0f) / 100.0f
            f1 = f4
            f2 = f5
            f3 = f6
        }

        GL11.glColor3f(f1, f2, f3)
        val tessellator1: Tessellator = Tessellator.getInstance()
        val worldRenderer1: BufferBuilder = tessellator1.getBuffer()
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glColor3f(f1, f2, f3)
        GL11.glCallList(this.glSkyList)
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(770, 771, 1, 0)
        RenderHelper.disableStandardItemLighting()
        var f7: Float
        var f8: Float
        var f9: Float
        var f10: Float

        var f18: Float = world.getStarBrightness(partialTicks)

        if (f18 > 0.0f) {
            GL11.glPushMatrix()
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(-19.0f, 0f, 1.0f, 0f)
            GL11.glColor4f(f18, f18, f18, f18)
            GL11.glCallList(this.starList)
            GL11.glPopMatrix()
        }

        val afloat: FloatArray = FloatArray(4)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glPushMatrix()
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
        afloat[0] = 255 / 255.0f
        afloat[1] = 194 / 255.0f
        afloat[2] = 180 / 255.0f
        afloat[3] = 0.3f
        f6 = afloat[0]
        f7 = afloat[1]
        f8 = afloat[2]
        val f11: Float

        if (mc.gameSettings.anaglyph) {
            f9 = (f6 * 30.0f + f7 * 59.0f + f8 * 11.0f) / 100.0f
            f10 = (f6 * 30.0f + f7 * 70.0f) / 100.0f
            f11 = (f6 * 30.0f + f8 * 70.0f) / 100.0f
            f6 = f9
            f7 = f10
            f8 = f11
        }

        f18 = 1.0f - f18

        worldRenderer1.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
        var r: Float = f6 * f18
        var g: Float = f7 * f18
        var b: Float = f8 * f18
        var a: Float = afloat[3] * 2 / f18
        worldRenderer1.pos(0.0, 100.0, 0.0).color(r, g, b, a).endVertex()
        val b0: Byte = 16
        r = afloat[0] * f18
        g = afloat[1] * f18
        b = afloat[2] * f18
        a = 0.0f

        // Render sun aura
        f10 = 20.0f
        worldRenderer1.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(0.0, 100.0, -f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(0.0, 100.0, f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()

        tessellator1.draw()
        worldRenderer1.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
        r = f6 * f18
        g = f7 * f18
        b = f8 * f18
        a = afloat[3] * f18
        worldRenderer1.pos(0.0, 100.0, 0.0).color(r, g, b, a).endVertex()
        r = afloat[0] * f18
        g = afloat[1] * f18
        b = afloat[2] * f18
        a = 0.0f

        // Render larger sun aura
        f10 = 40.0f
        worldRenderer1.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(0.0, 100.0, -f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(0.0, 100.0, f10.toDouble() * 1.5f).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 100.0, f10.toDouble()).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble() * 1.5f, 100.0, 0.0).color(r, g, b, a).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 100.0, -f10.toDouble()).color(r, g, b, a).endVertex()

        tessellator1.draw()
        GL11.glPopMatrix()
        GL11.glShadeModel(GL11.GL_FLAT)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glPushMatrix()
        f7 = 0.0f
        f8 = 0.0f
        f9 = 0.0f
        GL11.glTranslatef(f7, f8, f9)
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
        // Render sun
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
        //Some blanking to conceal the stars
        f10 = this.sunSize / 3.5f
        worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer1.pos(-f10.toDouble(), 99.9, -f10.toDouble()).endVertex()
        worldRenderer1.pos(f10.toDouble(), 99.9, -f10.toDouble()).endVertex()
        worldRenderer1.pos(f10.toDouble(), 99.9, f10.toDouble()).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 99.9, f10.toDouble()).endVertex()
        tessellator1.draw()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        f10 = this.sunSize
        mc.renderEngine.bindTexture(sunTexture)
        worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer1.pos(-f10.toDouble(), 100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer1.pos(f10.toDouble(), 100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer1.pos(-f10.toDouble(), 100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        tessellator1.draw()

        // Render earth
        f10 = 0.5f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(40.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(200f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(overworldTexture)
        worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer1.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer1.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer1.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer1.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        tessellator1.draw()

        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glPopMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor3f(0.0f, 0.0f, 0.0f)
        val d0: Double = mc.player.getPosition().getY() - world.getHorizon()

        if (d0 < 0.0) {
            GL11.glPushMatrix()
            GL11.glTranslatef(0.0f, 12.0f, 0.0f)
            GL11.glCallList(this.glSkyList2)
            GL11.glPopMatrix()
            f8 = 1.0f
            f9 = -((d0 + 65.0).toFloat())
            f10 = -f8
            worldRenderer1.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer1.pos(-f8.toDouble(), f9.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f9.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f9.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f9.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f9.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f9.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f9.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f9.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(-f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer1.pos(f8.toDouble(), f10.toDouble(), -f8.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            tessellator1.draw()
        }

        if (world.provider.isSkyColored()) {
            GL11.glColor3f(f1 * 0.2f + 0.04f, f2 * 0.2f + 0.04f, f3 * 0.6f + 0.1f)
        } else {
            GL11.glColor3f(f1, f2, f3)
        }

        GL11.glPushMatrix()
        GL11.glTranslatef(0.0f, -((d0 - 16.0).toFloat()), 0.0f)
        GL11.glCallList(this.glSkyList2)
        GL11.glPopMatrix()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GlStateManager.enableRescaleNormal()
        GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glDepthMask(true)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_BLEND)
    }

    private fun renderStars() {
        val rand: Random = Random(10842L)
        val var2: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = var2.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        for (starIndex: Int in 0..<(if (ConfigManagerCore.moreStars) 35000 else 6000)) {
            var var4: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var var6: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            var var8: Double = (rand.nextFloat() * 2.0f - 1.0f).toDouble()
            val var10: Double = (0.15f + rand.nextFloat() * 0.1f).toDouble()
            var var12: Double = var4 * var4 + var6 * var6 + var8 * var8

            if (var12 < 1.0 && var12 > 0.01) {
                var12 = 1.0 / sqrt(var12)
                var4 *= var12
                var6 *= var12
                var8 *= var12
                val var14: Double =
                    var4 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var16: Double =
                    var6 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var18: Double =
                    var8 * (if (ConfigManagerCore.moreStars) rand.nextDouble() * 150.0 + 130.0 else 100.0)
                val var20: Double = atan2(var4, var8)
                val var22: Double = sin(var20)
                val var24: Double = cos(var20)
                val var26: Double = atan2(sqrt(var4 * var4 + var8 * var8), var6)
                val var28: Double = sin(var26)
                val var30: Double = cos(var26)
                val var32: Double = rand.nextDouble() * Math.PI * 2.0
                val var34: Double = sin(var32)
                val var36: Double = cos(var32)

                for (var38: Int in 0..3) {
                    val var39: Double = 0.0
                    val var41: Double = ((var38 and 2) - 1) * var10
                    val var43: Double = ((var38 + 1 and 2) - 1) * var10
                    val var47: Double = var41 * var36 - var43 * var34
                    val var49: Double = var43 * var36 + var41 * var34
                    val var53: Double = var47 * var28 + var39 * var30
                    val var55: Double = var39 * var28 - var47 * var30
                    val var57: Double = var55 * var22 - var49 * var24
                    val var61: Double = var49 * var22 + var55 * var24
                    worldRenderer.pos(var14 + var57, var16 + var53, var18 + var61).endVertex()
                }
            }
        }

        var2.draw()
    }

    private fun getCustomSkyColor(): Vec3d {
        return Vec3d(0.26796875, 0.1796875, 0.0)
    }

    open fun getSkyBrightness(par1: Float): Float {
        val var2: Float = FMLClientHandler.instance().getClient().world.getCelestialAngle(par1)
        var var3: Float = 1.0f - (MathHelper.sin(var2 * Constants.twoPI) * 2.0f + 0.25f)

        if (var3 < 0.0f) {
            var3 = 0.0f
        }

        if (var3 > 1.0f) {
            var3 = 1.0f
        }

        return var3 * var3 * 1f
    }

    companion object {
        private val overworldTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")
        private val sunTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/planets/atmosphericsun.png")
    }
}
