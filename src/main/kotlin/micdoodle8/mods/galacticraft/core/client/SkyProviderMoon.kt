package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.BufferBuilder
import java.util.Random

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.client.IRenderHandler
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class SkyProviderMoon constructor() : IRenderHandler() {
    @JvmField
    var starGLCallList: Int = GLAllocation.generateDisplayLists(3)

    @JvmField
    var glSkyList: Int

    @JvmField
    var glSkyList2: Int

    init {
        GL11.glPushMatrix()
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE)
        this.renderStars()
        GL11.glEndList()
        GL11.glPopMatrix()
        val tessellator: Tessellator = Tessellator.getInstance()
        this.glSkyList = this.starGLCallList + 1
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE)
        val byte2: Byte = 64
        val i: Int = 256 / byte2 + 2
        var f: Float = 16f
        val worldRenderer: BufferBuilder = tessellator.getBuffer()

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

        var gcProvider: WorldProviderMoon? = null

        if (world.provider is WorldProviderMoon) {
            gcProvider = world.provider as WorldProviderMoon
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GlStateManager.disableRescaleNormal()
        GL11.glColor3f(1f, 1f, 1f)
        val var23: Tessellator = Tessellator.getInstance()
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glColor3f(0f, 0f, 0f)
        GL11.glCallList(this.glSkyList)
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderHelper.disableStandardItemLighting()
        val var10: Float
        val var11: Float
        var var12: Float

        var var20: Float = 0f

        if (gcProvider != null) {
            var20 = gcProvider.getStarBrightness(partialTicks)
        }

        if (var20 > 0.0f) {
            GL11.glPushMatrix()
            GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(-19.0f, 0f, 1.0f, 0f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, var20)
            GL11.glCallList(this.starGLCallList)
            GL11.glPopMatrix()
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glPushMatrix()

        GL11.glPopMatrix()

        GL11.glPushMatrix()

        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 5f)
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0f, 1.0f, 0.0f, 0.0f)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
        var12 = 20.0f / 3.5f
        val worldRenderer: BufferBuilder = var23.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        worldRenderer.pos(-var12.toDouble(), 99.9, -var12.toDouble()).endVertex()
        worldRenderer.pos(var12.toDouble(), 99.9, -var12.toDouble()).endVertex()
        worldRenderer.pos(var12.toDouble(), 99.9, var12.toDouble()).endVertex()
        worldRenderer.pos(-var12.toDouble(), 99.9, var12.toDouble()).endVertex()
        var23.draw()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        var12 = 20.0f
        Minecraft.getInstance().renderEngine.bindTexture(sunTexture)
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(-var12.toDouble(), 100.0, -var12.toDouble()).tex(0.0, 0.0).endVertex()
        worldRenderer.pos(var12.toDouble(), 100.0, -var12.toDouble()).tex(1.0, 0.0).endVertex()
        worldRenderer.pos(var12.toDouble(), 100.0, var12.toDouble()).tex(1.0, 1.0).endVertex()
        worldRenderer.pos(-var12.toDouble(), 100.0, var12.toDouble()).tex(0.0, 1.0).endVertex()
        var23.draw()

        GL11.glPopMatrix()

        GL11.glPushMatrix()

        GL11.glDisable(GL11.GL_BLEND)

        // HOME:
        var12 = 10.0f
        val earthRotation: Float = (world.getSpawnPoint().getZ() - mc.player.z).toFloat() * 0.01f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(earthRotation, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(200f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)

        if (ClientProxyCore.overworldTexturesValid) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, (ClientProxyCore.overworldTextureClient)!!.getGlTextureId())
        } else {
            // Overworld texture is 48x48 in a 64x64 .png file
            Minecraft.getInstance().renderEngine.bindTexture(overworldTexture)
        }

        world.getMoonPhase()
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

        if (var25 < 0.0) {
            GL11.glPushMatrix()
            GL11.glTranslatef(0.0f, 12.0f, 0.0f)
            GL11.glCallList(this.glSkyList2)
            GL11.glPopMatrix()
            var10 = 1.0f
            var11 = -((var25 + 65.0).toFloat())
            var12 = -var10
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer.pos(-var10.toDouble(), var11.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var11.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f)
                .endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var11.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var11.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f)
                .endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var11.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var11.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var11.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f)
                .endVertex()
            worldRenderer.pos(-var10.toDouble(), var11.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f)
                .endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f)
                .endVertex()
            worldRenderer.pos(-var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            worldRenderer.pos(var10.toDouble(), var12.toDouble(), -var10.toDouble()).color(0f, 0f, 0f, 1.0f).endVertex()
            var23.draw()
        }

        GL11.glColor3f(70f / 256f, 70f / 256f, 70f / 256f)

        GL11.glPushMatrix()
        GL11.glTranslatef(0.0f, -((var25 - 16.0).toFloat()), 0.0f)
        GL11.glCallList(this.glSkyList2)
        GL11.glPopMatrix()
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

        for (var3: Int in 0..<(if (ConfigManagerCore.moreStars) 20000 else 6000)) {
            var var4: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var6: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var8: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            val var10: Double = (0.15f + var1.nextFloat() * 0.1f).toDouble()
            var var12: Double = var4 * var4 + var6 * var6 + var8 * var8

            if (var12 < 1.0 && var12 > 0.01) {
                var12 = 1.0 / sqrt(var12)
                var4 *= var12
                var6 *= var12
                var8 *= var12
                val var14: Double =
                    var4 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 100.0 + 150.0 else 100.0)
                val var16: Double =
                    var6 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 100.0 + 150.0 else 100.0)
                val var18: Double =
                    var8 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 100.0 + 150.0 else 100.0)
                val var20: Double = atan2(var4, var8)
                val var22: Double = sin(var20)
                val var24: Double = cos(var20)
                val var26: Double = atan2(sqrt(var4 * var4 + var8 * var8), var6)
                val var28: Double = sin(var26)
                val var30: Double = cos(var26)
                val var32: Double = var1.nextDouble() * Math.PI * 2.0
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
        val var2: Float = Minecraft.getInstance().level.getCelestialAngle(par1)
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
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")
        private val sunTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/planets/orbitalsun.png")
    }
}
