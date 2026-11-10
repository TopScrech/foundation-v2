package galaxyspace.systems.SolarSystem.moons.triton.dimenson.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.moons.triton.dimenson.sky.SkyProviderTriton
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderTriton : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        f10 = 10.0f
        //GL11.glScalef(0.6F, 0.6F, 0.6F);
        //GL11.glRotatef(100.0F, 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef(-75F, 1.0F, 0.0F, 0.0F);
        //GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(100.0f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(20f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(60f, 0.0f, 0.0f, 1.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(neptuneTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        val f: Float = 0.9f
        this.renderAtmo(
            (tessellator)!!,
            0.0f,
            0.0f,
            f10 - 1.5f,
            Vec3d((120 / 255.0f * f).toDouble(), (160 / 255.0f * f).toDouble(), (180 / 255.0f * f).toDouble())
        )

        val time: Long = this.mc.player.getEntityWorld().getWorldTime() % getDayLength()
        var k: Double = ((time / 8) / (getDayLength() / 24000L)).toDouble()

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.95f)


        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)
        if (k >= 280 && k < 1500) {
            k -= 280.0
            k /= 50.0

            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10 + k).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10 + k).endVertex()
        } else if (k < 280) {
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
        return 1.2f
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun colorSunAura(): StarColor {
        return StarColor.WHITE
    }

    override fun getAtmosphereColor(): Vector3? {
        return null
    }

    override fun enableSmoothRender(): Boolean {
        return true
    }

    companion object {
        private val neptuneTexture =
            ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/neptune.png")
    }
}
