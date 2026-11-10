package galaxyspace.systems.SolarSystem.planets.haumea.dimension.sky

import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.planets.haumea.dimension.sky.SkyProviderHaumea
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderHaumea : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_BLEND)

        f10 = 500.0f
        GL11.glScalef(0.8f, 0.6f, 0.8f)
        GL11.glRotatef(80.0f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 1.0f, 0.0f)

        //GL11.glRotatef(-this.mc.world.getCelestialAngle(this.ticks) * 360.0F * 0.02F, 0.0F, 0.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(haumeaRingTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0)
            .color(1f, 1f, 1f, this.mc.world.getStarBrightness(ticks)).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0)
            .color(1f, 1f, 1f, this.mc.world.getStarBrightness(ticks)).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0)
            .color(1f, 1f, 1f, this.mc.world.getStarBrightness(ticks)).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0)
            .color(1f, 1f, 1f, this.mc.world.getStarBrightness(ticks)).endVertex()
        (tessellator)!!.draw()
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 0.35f
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    override fun colorSunAura(): StarColor {
        return StarColor.WHITE
    }

    override fun getAtmosphereColor(): Vector3? {
        return null
    }

    override fun enableSmoothRender(): Boolean {
        return false
    }

    override fun expandSizeAura(): Int {
        return -4
    }

    companion object {
        private val haumeaRingTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/haumea_rings.png")
    }
}
