package galaxyspace.systems.SolarSystem.moons.enceladus.dimension.sky

import galaxyspace.GalaxySpace
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import galaxyspace.systems.SolarSystem.moons.enceladus.dimension.sky.SkyProviderEnceladus
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderEnceladus : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()


        // Render titan
        f10 = 0.5f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(5.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f / 2, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(-110f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(titanTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glPopMatrix()
        GL11.glPushMatrix()

        // Render saturn
        f10 = 50.0f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(270f, 1.0f, 0.0f, 0.0f)

        GL11.glRotatef(-this.mc.world.getCelestialAngle(ticks) * 360.0f + 90, 0.0f, 1.0f, 0.0f)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(saturnTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glPopMatrix()
        GL11.glPushMatrix()

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(270f, 1.0f, 0.0f, 0.0f)

        GL11.glRotatef(-this.mc.world.getCelestialAngle(ticks) * 360.0f + 90, 0.0f, 1.0f, 0.0f)

        val f11: Float = f10
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        (worldRenderer)!!.pos(-20.0, -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, 0.95f).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, 0.95f).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, 0.95f).endVertex()
        (worldRenderer)!!.pos(-20.0, -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, 0.95f).endVertex()
        (tessellator)!!.draw()


        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ZERO);
        GL11.glPopMatrix()
        GL11.glPushMatrix()


        // Render saturn Rings
        f10 = 125.0f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(270f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(-this.mc.world.getCelestialAngle(ticks) * 360.0f + 90, 0.0f, 1.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.6f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(saturnRingsTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()


        //f10 = 25.0F;
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        (worldRenderer)!!.pos(0.0, -100.0, -f10.toDouble()).tex(0.5, 0.0).color(0.0f, 0.0f, 0.0f, 0.9f).endVertex()
        (worldRenderer)!!.pos(0.0, -100.0, f10.toDouble()).tex(0.5, 1.0).color(0.0f, 0.0f, 0.0f, 0.9f).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).color(0.0f, 0.0f, 0.0f, 0.9f)
            .endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.9f)
            .endVertex()
        (tessellator)!!.draw()

        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 3.0f
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
        return true
    }

    companion object {
        private val saturnTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/saturn.png")
        private val saturnRingsTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/saturn_rings.png")

        private val titanTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/titan.png")
    }
}
