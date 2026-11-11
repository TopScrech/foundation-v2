package asmodeuscore.core.astronomy.sky.galacticraft

import org.lwjgl.opengl.GL11
import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import asmodeuscore.core.astronomy.sky.SkyProviderBase.StarCount
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import kotlin.math.cos
import kotlin.math.sin

open class SkyProviderOverworld : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()


        //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        val f24: Float = 1.0f - this.mc.world.getRainStrength(ticks)
        GlStateManager.color(1.0f, 1.0f, 1.0f, f24)
        GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(this.mc.world.getCelestialAngle(ticks) * 360.0f, 1.0f, 0.0f, 0.0f)

        f10 = 20.0f
        mc.renderEngine.bindTexture(moonTexture)
        val k: Int = mc.world.getMoonPhase()
        val l: Int = k % 4
        val i1: Int = k / 4 % 2
        val f14: Float = (l + 0).toFloat() / 4.0f
        val f15: Float = (i1 + 0).toFloat() / 2.0f
        val f16: Float = (l + 1).toFloat() / 4.0f
        val f17: Float = (i1 + 1).toFloat() / 2.0f

        val worldtime: Long = mc.world.getWorldTime()
        val offangle: Double = worldtime % 192000.0 * 2.0 * 3.141592653589793 / 192000.0 + 2.748893571891069
        val moony: Double = 100.0 * cos(offangle)
        val moonz: Double = -100.0 * sin(offangle)
        val moonx: Double = 30.0 * cos(offangle / 12.0)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        if (AsmodeusConfig.enableBetterMoonOnSky) {
            (worldRenderer)!!.pos(moonx - f10, moony - f10 * sin(offangle), moonz - f10 * cos(offangle))
                .tex(f14.toDouble(), f17.toDouble()).endVertex()
            (worldRenderer)!!.pos(moonx + f10, moony - f10 * sin(offangle), moonz - f10 * cos(offangle))
                .tex(f14.toDouble(), f15.toDouble()).endVertex()
            (worldRenderer)!!.pos(moonx + f10, moony + f10 * sin(offangle), moonz + f10 * cos(offangle))
                .tex(f16.toDouble(), f15.toDouble()).endVertex()
            (worldRenderer)!!.pos(moonx - f10, moony + f10 * sin(offangle), moonz + f10 * cos(offangle))
                .tex(f16.toDouble(), f17.toDouble()).endVertex()
        } else {
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(f16.toDouble(), f17.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(f14.toDouble(), f17.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(f14.toDouble(), f15.toDouble()).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(f16.toDouble(), f15.toDouble()).endVertex()
        }
        (tessellator)!!.draw()

        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 19.5f
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation.parse("textures/environment/sun.png")
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    override fun colorSunAura(): StarColor? {
        return null
    }

    override fun getAtmosphereColor(): Vector3 {
        val f: Float = mc.world.getSunBrightness(ticks) + 0.2f
        return Vector3((120 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble(), (160 / 255.0f * f).toDouble())
    }

    override fun enableLargeSunAura(): Boolean {
        return false
    }

    override fun getStarCount(): StarCount {
        return StarCount.SMALL
    }

    companion object {
        private val moonTexture = ResourceLocation.parse("textures/environment/moon_phases.png")
    }
}
