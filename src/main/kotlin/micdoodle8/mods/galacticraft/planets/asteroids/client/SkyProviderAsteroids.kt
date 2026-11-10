package micdoodle8.mods.galacticraft.planets.asteroids.client

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.BufferBuilder
import java.util.Random

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
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

@SideOnly(Side.CLIENT)
open class SkyProviderAsteroids constructor(asteroidsProvider: IGalacticraftWorldProvider) : IRenderHandler() {
    @JvmField
    var starGLCallList: Int = GLAllocation.generateDisplayLists(3)

    @JvmField
    var glSkyList: Int

    @JvmField
    var glSkyList2: Int

    private val sunSize: Float

    init {
        this.sunSize = 17.5f * asteroidsProvider.getSolarSize()

        GL11.glPushMatrix()
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE)
        this.renderStars()
        GL11.glEndList()
        GL11.glPopMatrix()
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        this.glSkyList = this.starGLCallList + 1
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE)
        val byte2: Byte = 64
        val i: Int = 256 / byte2 + 2
        var f: Float = 16f

        var j: Int = -byte2 * i
        while (j <= byte2 * i) {
            var l: Int = -byte2 * i
            while (l <= byte2 * i) {
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
                worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + 0).toDouble()).endVertex()
                worldRenderer.pos((j + byte2).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                worldRenderer.pos((j + 0).toDouble(), f.toDouble(), (l + byte2).toDouble()).endVertex()
                tessellator.draw()
                l += byte2.toInt()
            }
            j += byte2.toInt()
        }

        GL11.glEndList()
        this.glSkyList2 = this.starGLCallList + 2
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE)
        f = -16f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        var k: Int = -byte2 * i
        while (k <= byte2 * i) {
            var i1: Int = -byte2 * i
            while (i1 <= byte2 * i) {
                worldRenderer.pos((k + byte2).toDouble(), f.toDouble(), (i1 + 0).toDouble()).endVertex()
                worldRenderer.pos((k + 0).toDouble(), f.toDouble(), (i1 + 0).toDouble()).endVertex()
                worldRenderer.pos((k + 0).toDouble(), f.toDouble(), (i1 + byte2).toDouble()).endVertex()
                worldRenderer.pos((k + byte2).toDouble(), f.toDouble(), (i1 + byte2).toDouble()).endVertex()
                i1 += byte2.toInt()
            }
            k += byte2.toInt()
        }

        tessellator.draw()
        GL11.glEndList()
    }

    override fun render(partialTicks: Float, world: WorldClient, mc: Minecraft) {
        var var10: Float
        var var11: Float
        var var12: Float
        val var23: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = var23.getBuffer()

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GlStateManager.disableRescaleNormal()
        GL11.glColor3f(1f, 1f, 1f)
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glColor3f(0f, 0f, 0f)
        GL11.glCallList(this.glSkyList)
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderHelper.disableStandardItemLighting()

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(0.7f, 0.7f, 0.7f, 0.7f)
        GL11.glCallList(this.starGLCallList)

        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        // Sun:
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
        var12 = this.sunSize / 4.2f
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(-var12.toDouble(), 90.0, -var12.toDouble()).endVertex()
        worldRenderer.pos(var12.toDouble(), 90.0, -var12.toDouble()).endVertex()
        worldRenderer.pos(var12.toDouble(), 90.0, var12.toDouble()).endVertex()
        worldRenderer.pos(-var12.toDouble(), 90.0, var12.toDouble()).endVertex()
        var23.draw()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        var12 = this.sunSize / 1.2f
        //110 distance instead of the normal 100, because there is no atmosphere to make the disk seem larger
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(sunTexture)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-var12.toDouble(), 90.0, -var12.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(var12.toDouble(), 90.0, -var12.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(var12.toDouble(), 90.0, var12.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(-var12.toDouble(), 90.0, var12.toDouble()).tex(0.0, 1.0).endVertex()
        var23.draw()

        GL11.glPopMatrix()

        GL11.glPushMatrix()

        // HOME:
        var12 = 0.5f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(40.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(200f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(overworldTexture)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-var12.toDouble(), -100.0, var12.toDouble()).tex(0.0, 1.0).endVertex()
        worldRenderer.pos(var12.toDouble(), -100.0, var12.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(var12.toDouble(), -100.0, -var12.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(-var12.toDouble(), -100.0, -var12.toDouble()).tex(0.0, 0.0).endVertex()
        var23.draw()

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glPopMatrix()

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor3f(0.0f, 0.0f, 0.0f)
        val var25: Double = mc.player.getPosition().getY() - world.getHorizon()

        //		if (var25 < 0.0D)
        //		{
        //			GL11.glPushMatrix();
        //			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
        //			GL11.glCallList(this.glSkyList2);
        //			GL11.glPopMatrix();
        //			var10 = 1.0F;
        //			var11 = -((float) (var25 + 65.0D));
        //			var12 = -var10;
        //			var23.startDrawingQuads();
        //			var23.setColorRGBA_I(0, 255);
        //			var23.addVertex(-var10, var11, var10);
        //			var23.addVertex(var10, var11, var10);
        //			var23.addVertex(var10, var12, var10);
        //			var23.addVertex(-var10, var12, var10);
        //			var23.addVertex(-var10, var12, -var10);
        //			var23.addVertex(var10, var12, -var10);
        //			var23.addVertex(var10, var11, -var10);
        //			var23.addVertex(-var10, var11, -var10);
        //			var23.addVertex(var10, var12, -var10);
        //			var23.addVertex(var10, var12, var10);
        //			var23.addVertex(var10, var11, var10);
        //			var23.addVertex(var10, var11, -var10);
        //			var23.addVertex(-var10, var11, -var10);
        //			var23.addVertex(-var10, var11, var10);
        //			var23.addVertex(-var10, var12, var10);
        //			var23.addVertex(-var10, var12, -var10);
        //			var23.addVertex(-var10, var12, -var10);
        //			var23.addVertex(-var10, var12, var10);
        //			var23.addVertex(var10, var12, var10);
        //			var23.addVertex(var10, var12, -var10);
        //			var23.draw();
        //		}
        GL11.glColor3f(70f / 256f, 70f / 256f, 70f / 256f)

        //		GL11.glPushMatrix();
        //		GL11.glTranslatef(0.0F, -((float) (var25 - 16.0D)), 0.0F);
        //		GL11.glCallList(this.glSkyList2);
        //		GL11.glPopMatrix();
        GlStateManager.enableRescaleNormal()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDepthMask(true)

        GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_BLEND)
    }

    private fun renderStars() {
        val var1: Random = Random(10842L)
        val var2: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = var2.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        for (var3: Int in 0..<(if (ConfigManagerCore.moreStars) 35000 else 6000)) {
            var var4: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var6: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var8: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            val var10: Double = (0.08f + var1.nextFloat() * 0.07f).toDouble()
            var var12: Double = var4 * var4 + var6 * var6 + var8 * var8

            if (var12 < 1.0 && var12 > 0.01) {
                var12 = 1.0 / sqrt(var12)
                var4 *= var12
                var6 *= var12
                var8 *= var12
                val pX: Double = var4 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 75.0 + 65.0 else 80.0)
                val pY: Double = var6 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 75.0 + 65.0 else 80.0)
                val pZ: Double = var8 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 75.0 + 65.0 else 80.0)
                val var20: Double = atan2(var4, var8)
                val var22: Double = sin(var20)
                val var24: Double = cos(var20)
                val var26: Double = atan2(sqrt(var4 * var4 + var8 * var8), var6)
                val var28: Double = sin(var26)
                val var30: Double = cos(var26)
                val var32: Double = var1.nextDouble() * Math.PI * 2.0
                val var34: Double = sin(var32)
                val var36: Double = cos(var32)

                for (i: Int in 0..3) {
                    val i1: Double = ((i and 2) - 1) * var10
                    val i2: Double = ((i + 1 and 2) - 1) * var10
                    val var47: Double = i1 * var36 - i2 * var34
                    val var49: Double = i2 * var36 + i1 * var34
                    val var55: Double = -var47 * var30
                    val dX: Double = var55 * var22 - var49 * var24
                    val dZ: Double = var49 * var22 + var55 * var24
                    val dY: Double = var47 * var28
                    worldRenderer.pos(pX + dX, pY + dY, pZ + dZ).endVertex()
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
        private val galaxyTexture =
            ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/planets/galaxy.png")
        private val sunTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/planets/orbitalsun.png")
    }
}
