package galaxyspace.systems.SolarSystem.moons.titan.dimension.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.moons.titan.dimension.sky.SkyProviderTitan
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderTitan : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        // Render saturn
        f10 = 20.0f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(240f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(330f, 0f, 0f, 1.0f)
        GL11.glRotatef(-this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 1.0f, 0.0f)


        val color: Float =
            if (this.mc.world.isRaining()) (1.0f - (this.mc.world.rainingStrength / 4)) - this.mc.world.getStarBrightness(
                1.0f
            ) else 1.1f - this.mc.world.getStarBrightness(1.0f)
        GL11.glColor4f(color, color, color, 1.0f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(saturnTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        // Render saturn Rings
        f10 = 50.0f
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(saturnRingsTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return false
    }

    override fun sunSize(): Float {
        return 4.0f
    }

    override fun enableStar(): Boolean {
        return false
    }

    override fun sunImage(): ResourceLocation? {
        return null
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
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

    companion object {
        private val saturnTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn.png")
        private val saturnRingsTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/saturn_rings.png")
    }
}
