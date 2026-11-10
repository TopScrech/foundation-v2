package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.BufferBuilder
import java.util.Random

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
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.*

open class SkyProviderOrbit constructor(
    private val planetToRender: ResourceLocation?,
    private val renderMoon: Boolean,
    private val renderSun: Boolean
) : IRenderHandler() {
    @JvmField
    var starGLCallList: Int = GLAllocation.generateDisplayLists(3)

    @JvmField
    var glSkyList: Int

    @JvmField
    var glSkyList2: Int

    @JvmField
    var spinAngle: Float = 0f

    @JvmField
    var spinDeltaPerTick: Float = 0f
    private var prevPartialTicks = 0f
    private var prevTick: Long = 0

    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    init {
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

    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft?) {
        val var20: Float = 400.0f + this.minecraft.player.posY.toFloat() / 2f

        // if (this.minecraft.player.getRidingEntity() != null)
        run {}

        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GlStateManager.disableRescaleNormal()
        val var2: Vec3d = this.minecraft.world.getSkyColor(this.minecraft.getRenderViewEntity(), partialTicks)
        var var3: Float = var2.x.toFloat()
        var var4: Float = var2.y.toFloat()
        var var5: Float = var2.z.toFloat()
        var var8: Float

        if (this.minecraft.gameSettings.anaglyph) {
            val var6: Float = (var3 * 30.0f + var4 * 59.0f + var5 * 11.0f) / 100.0f
            val var7: Float = (var3 * 30.0f + var4 * 70.0f) / 100.0f
            var8 = (var3 * 30.0f + var5 * 70.0f) / 100.0f
            var3 = var6
            var4 = var7
            var5 = var8
        }

        GL11.glColor3f(var3, var4, var5)
        val var23: Tessellator = Tessellator.getInstance()
        GL11.glDepthMask(false)
        GL11.glEnable(GL11.GL_FOG)
        GL11.glColor3f(var3, var4, var5)
        GL11.glCallList(this.glSkyList)
        GL11.glDisable(GL11.GL_FOG)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderHelper.disableStandardItemLighting()
        val var24: FloatArray? = this.minecraft.world.provider.calcSunriseSunsetColors(
            this.minecraft.world.getCelestialAngle(partialTicks),
            partialTicks
        )
        var var9: Float
        var var10: Float
        var var11: Float
        var var12: Float

        if (var24 != null) {
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
            var8 = var24[0]
            var9 = var24[1]
            var10 = var24[2]
            var var13: Float

            if (this.minecraft.gameSettings.anaglyph) {
                var11 = (var8 * 30.0f + var9 * 59.0f + var10 * 11.0f) / 100.0f
                var12 = (var8 * 30.0f + var9 * 70.0f) / 100.0f
                var13 = (var8 * 30.0f + var10 * 70.0f) / 100.0f
                var8 = var11
                var9 = var12
                var10 = var13
            }

            val worldRenderer: BufferBuilder = var23.getBuffer()
            worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
            worldRenderer.pos(0.0, 100.0, 0.0).color(var8, var9, var10, var24[3]).endVertex()
            val var26: Byte = 16

            for (var27: Int in 0..var26) {
                var13 = var27 * Constants.twoPI / var26
                val var14: Float = MathHelper.sin(var13)
                val var15: Float = MathHelper.cos(var13)
                worldRenderer.pos(
                    (var14 * 120.0f).toDouble(),
                    (var15 * 120.0f).toDouble(),
                    (-var15 * 40.0f * var24[3]).toDouble()
                ).color(var24[0], var24[1], var24[2], 0.0f).endVertex()
            }

            var23.draw()
            GL11.glPopMatrix()
            GL11.glShadeModel(GL11.GL_FLAT)
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        GL11.glPushMatrix()
        var8 = 1.0f - this.minecraft.world.getRainStrength(partialTicks)
        var9 = 0.0f
        var10 = 0.0f
        var11 = 0.0f
        GL11.glColor4f(1.0f, 1.0f, 1.0f, var8)
        GL11.glTranslatef(var9, var10, var11)
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f)

        //Code for rendering spinning spacestations
        var deltaTick: Float = partialTicks - this.prevPartialTicks
        //while (deltaTick < 0F) deltaTick += 1.0F;
        this.prevPartialTicks = partialTicks
        val curTick: Long = this.minecraft.world.getTotalWorldTime()
        val tickDiff: Int = (curTick - this.prevTick).toInt()
        this.prevTick = curTick
        if (tickDiff > 0 && tickDiff < 20) {
            deltaTick += tickDiff.toFloat()
        }
        this.spinAngle = this.spinAngle - this.spinDeltaPerTick * deltaTick
        while (this.spinAngle < -180f) {
            this.spinAngle += 360f
        }
        GL11.glRotatef(this.spinAngle, 0.0f, 1.0f, 0.0f)

        //At 0.8, these will look bright against a black sky - allows some headroom for them to
        //look even brighter in outer dimensions (further from the sun)
        GL11.glColor4f(0.8f, 0.8f, 0.8f, 0.8f)
        GL11.glCallList(this.starGLCallList)

        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GL11.glPushMatrix()
        val celestialAngle: Float = this.minecraft.world.getCelestialAngle(partialTicks)
        GL11.glRotatef(celestialAngle * 360.0f, 1.0f, 0.0f, 0.0f)
        if (this.renderSun) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
            var12 = 8.0f
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
            var12 = 28.0f
            this.minecraft.renderEngine.bindTexture(sunTexture)
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(-var12.toDouble(), 100.0, -var12.toDouble()).tex(0.0, 0.0).endVertex()
            worldRenderer.pos(var12.toDouble(), 100.0, -var12.toDouble()).tex(1.0, 0.0).endVertex()
            worldRenderer.pos(var12.toDouble(), 100.0, var12.toDouble()).tex(1.0, 1.0).endVertex()
            worldRenderer.pos(-var12.toDouble(), 100.0, var12.toDouble()).tex(0.0, 1.0).endVertex()
            var23.draw()
        }

        if (this.renderMoon) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f)
            var12 = 11.3f
            val worldRenderer: BufferBuilder = var23.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(-var12.toDouble(), -99.9, var12.toDouble()).endVertex()
            worldRenderer.pos(var12.toDouble(), -99.9, var12.toDouble()).endVertex()
            worldRenderer.pos(var12.toDouble(), -99.9, -var12.toDouble()).endVertex()
            worldRenderer.pos(-var12.toDouble(), -99.9, -var12.toDouble()).endVertex()
            var23.draw()
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
            var12 = 40.0f
            this.minecraft.renderEngine.bindTexture(moonTexture)
            val var28: Float = this.minecraft.world.getMoonPhase().toFloat()
            val var30: Int = (var28 % 4).toInt()
            val var29: Int = (var28 / 4 % 2).toInt()
            val var16: Float = (var30 + 0) / 4.0f
            val var17: Float = (var29 + 0) / 2.0f
            val var18: Float = (var30 + 1) / 4.0f
            val var19: Float = (var29 + 1) / 2.0f
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(-var12.toDouble(), -100.0, var12.toDouble()).tex(var18.toDouble(), var19.toDouble())
                .endVertex()
            worldRenderer.pos(var12.toDouble(), -100.0, var12.toDouble()).tex(var16.toDouble(), var19.toDouble())
                .endVertex()
            worldRenderer.pos(var12.toDouble(), -100.0, -var12.toDouble()).tex(var16.toDouble(), var17.toDouble())
                .endVertex()
            worldRenderer.pos(-var12.toDouble(), -100.0, -var12.toDouble()).tex(var18.toDouble(), var17.toDouble())
                .endVertex()
            var23.draw()
        }

        GL11.glPopMatrix()
        GL11.glDisable(GL11.GL_BLEND)

        if (this.planetToRender != null) {
            GL11.glPushMatrix()
            GL11.glTranslatef(0.0f, -var20 / 10, 0.0f)
            var scale: Float = 100 * (0.3f - var20 / 10000.0f)
            scale = max(scale, 0.2f)
            GL11.glScalef(scale, 0.0f, scale)
            GL11.glTranslatef(0.0f, -var20, 0.0f)
            GL11.glRotatef(90f, 0.0f, 1.0f, 0.0f)
            this.minecraft.renderEngine.bindTexture(this.planetToRender)

            var10 = 1.0f
            val alpha: Float = 0.5f
            GL11.glColor4f(min(alpha, 1.0f), min(alpha, 1.0f), min(alpha, 1.0f), min(alpha, 1.0f))
            val worldRenderer: BufferBuilder = var23.getBuffer()
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            worldRenderer.pos(-var10.toDouble(), 0.0, var10.toDouble()).tex(0.0, 1.0).endVertex()
            worldRenderer.pos(var10.toDouble(), 0.0, var10.toDouble()).tex(1.0, 1.0).endVertex()
            worldRenderer.pos(var10.toDouble(), 0.0, -var10.toDouble()).tex(1.0, 0.0).endVertex()
            worldRenderer.pos(-var10.toDouble(), 0.0, -var10.toDouble()).tex(0.0, 0.0).endVertex()
            var23.draw()
            GL11.glPopMatrix()
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glPopMatrix()

        GL11.glEnable(GL11.GL_ALPHA_TEST)

        GL11.glColor3f(0.0f, 0.0f, 0.0f)

        /* This all does nothing!
        double var25 = 0.0D;

		// if (this.minecraft.player.getRidingEntity() != null)
		{
			var25 = this.minecraft.player.posY - 64;

			if (var25 < 0.0D)
			{
				// GL11.glPushMatrix();
				// GL11.glTranslatef(0.0F, 12.0F, 0.0F);
				// GL11.glCallList(this.glSkyList2);
				// GL11.glPopMatrix();
				// var10 = 1.0F;
				// var11 = -((float)(var25 + 65.0D));
				// var12 = -var10;
				// var23.startDrawingQuads();
				// var23.setColorRGBA_I(0, 255);
				// var23.addVertex(-var10, var11, var10);
				// var23.addVertex(var10, var11, var10);
				// var23.addVertex(var10, var12, var10);
				// var23.addVertex(-var10, var12, var10);
				// var23.addVertex(-var10, var12, -var10);
				// var23.addVertex(var10, var12, -var10);
				// var23.addVertex(var10, var11, -var10);
				// var23.addVertex(-var10, var11, -var10);
				// var23.addVertex(var10, var12, -var10);
				// var23.addVertex(var10, var12, var10);
				// var23.addVertex(var10, var11, var10);
				// var23.addVertex(var10, var11, -var10);
				// var23.addVertex(-var10, var11, -var10);
				// var23.addVertex(-var10, var11, var10);
				// var23.addVertex(-var10, var12, var10);
				// var23.addVertex(-var10, var12, -var10);
				// var23.addVertex(-var10, var12, -var10);
				// var23.addVertex(-var10, var12, var10);
				// var23.addVertex(var10, var12, var10);
				// var23.addVertex(var10, var12, -var10);
				// var23.draw();
			}
		}

		if (this.minecraft.world.provider.isSkyColored())
		{
			GL11.glColor3f(0.0f, 0.0f, 0.0f);
		}
		else
		{
			GL11.glColor3f(var3, var4, var5);
		}
		GL11.glColor3f(0.0f, 0.0f, 0.0f);

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float) (var25 - 16.0D)), 0.0F);
		GL11.glPopMatrix();
		*/
        GlStateManager.enableRescaleNormal()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_COLOR_MATERIAL)
        GL11.glDepthMask(true)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_BLEND)
    }

    private fun renderStars() {
        val var1: Random = Random(10842L)
        val var2: Tessellator = Tessellator.getInstance()
        var2.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        for (var3: Int in 0..<(if (ConfigManagerCore.moreStars) 20000 else 6000)) {
            var var4: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var6: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            var var8: Double = (var1.nextFloat() * 2.0f - 1.0f).toDouble()
            val var10: Double = (0.07f + var1.nextFloat() * 0.06f).toDouble()
            var var12: Double = var4 * var4 + var6 * var6 + var8 * var8

            if (var12 < 1.0 && var12 > 0.01) {
                var12 = 1.0 / sqrt(var12)
                var4 *= var12
                var6 *= var12
                var8 *= var12
                val var14: Double = var4 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 50.0 + 75.0 else 50.0)
                val var16: Double = var6 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 50.0 + 75.0 else 50.0)
                val var18: Double = var8 * (if (ConfigManagerCore.moreStars) var1.nextDouble() * 50.0 + 75.0 else 50.0)
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
                    var2.getBuffer().pos(var14 + var57, var16 + var53, var18 + var61).endVertex()
                }
            }
        }

        var2.draw()
    }

    companion object {
        private val moonTexture = ResourceLocation("textures/environment/moon_phases.png")
        private val sunTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/planets/orbitalsun.png")
    }
}
