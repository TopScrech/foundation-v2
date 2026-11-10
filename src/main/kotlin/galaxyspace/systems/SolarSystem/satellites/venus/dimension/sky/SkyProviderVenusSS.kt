package galaxyspace.systems.SolarSystem.satellites.venus.dimension.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.satellites.venus.dimension.sky.SkyProviderVenusSS
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderVenusSS : SkyProviderBase() {
    private val venus_texture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/venus.png")

    @JvmField
    var spinAngle: Float = 0f

    @JvmField
    var spinDeltaPerTick: Float = 0f

    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        //GL11.glEnable(GL11.GL_BLEND);
        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        f10 = 180.0f
        GL11.glScalef(0.8f, 0.8f, 0.8f)
        //GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f)
        //GL11.glRotatef(-90.0f, 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef(this.mc.world.getCelestialAngle(this.ticks) * 360.0F, 0.0F, 0.0F, 1.0F);
        val color: Float = mc.world.getStarBrightness(ticks)
        GL11.glColor4f(1.0f - color, 1.0f - color, 1.0f - color, 1.0f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(venus_texture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        //GL11.glDisable(GL11.GL_BLEND);
        val mod: Double = ((this.mc.world.provider as WorldProviderSpace).getDayLength() / 24000).toDouble()
        val time: Long = this.mc.player.getEntityWorld()
            .getWorldTime() % (this.mc.world.provider as WorldProviderSpace).getDayLength()
        var k: Double = (time / 8) / mod

        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_DST_ALPHA)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.8f)
        GL11.glRotatef(90f, 0.0f, 1.0f, 0.0f)

        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        //GalaxySpace.instance.debug(k + " | " + (f10 - k) + " | " + mod);
        if (k >= 0 && k <= f10 * 2) {
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10 - k).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10 - k).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).endVertex()
        } else if (k >= f10 * 8 && k <= f10 * 8.5) {
            k -= 1200.0

            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, 150 - k).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, 150 - k).endVertex()
        } else if (k > f10 * 8) {
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).endVertex()
        }

        (tessellator)!!.draw()
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 4.0f
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun colorSunAura(): StarColor? {
        return null
    }

    override fun getAtmosphereColor(): Vector3? {
        return null
    }

    override fun enableSmoothRender(): Boolean {
        return false
    }

    override fun enableRenderPlanet(): Boolean {
        return false
    }
}
