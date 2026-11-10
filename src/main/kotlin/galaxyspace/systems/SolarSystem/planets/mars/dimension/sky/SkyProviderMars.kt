package galaxyspace.systems.SolarSystem.planets.mars.dimension.sky

import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData
import galaxyspace.systems.SolarSystem.planets.mars.dimension.WorldProviderMars_WE
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderMars : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        GL11.glEnable(GL11.GL_BLEND)

        // OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE,
        // GL11.GL_ZERO);
        f10 = 3.5f
        GL11.glScalef(0.8f, 0.8f, 0.8f)

        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(80.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(40.0f, 1.0f, 0.0f, 0.0f)

        val data: DustStormSaveData = DustStormSaveData.get(this.mc.world, WorldProviderMars_WE.Companion.DATA)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f - data.getStormStrength(ticks))

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(phobosTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        f10 = 1.5f
        GL11.glScalef(0.8f, 0.8f, 0.8f)
        GL11.glRotatef(-80.0f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(140.0f, 0.0f, 0.0f, 1.0f)

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(deimosTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glDisable(GL11.GL_BLEND)

        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 17.5f
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun sunImage(): ResourceLocation? {
        return null
    }

    override fun modeLight(): ModeLight {
        val msd: DustStormSaveData = DustStormSaveData.get(this.mc.world, WorldProviderMars_WE.Companion.DATA)

        return if (msd.isDustStorm()) ModeLight.WITHOUT_SUN else ModeLight.DEFAULT
    }

    override fun colorSunAura(): StarColor {
        return StarColor.WHITE
    }

    override fun getAtmosphereColor(): Vector3 {
        val f: Float = mc.world.getSunBrightness(ticks) - 0.2f
        return Vector3((160 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
    }

    override fun enableSmoothRender(): Boolean {
        return false
    }

    companion object {
        private val phobosTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/phobos.png")
        private val deimosTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/deimos.png")
    }
}
