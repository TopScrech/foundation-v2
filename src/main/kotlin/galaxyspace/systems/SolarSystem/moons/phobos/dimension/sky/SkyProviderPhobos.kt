package galaxyspace.systems.SolarSystem.moons.phobos.dimension.sky

import galaxyspace.systems.SolarSystem.moons.phobos.dimension.sky.SkyProviderPhobos
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d

open class SkyProviderPhobos : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        this.renderImage(overworldTexture, 40f, -this.mc.world.getCelestialAngle(ticks) * 360f, 0f, 0.8f)
        this.renderImage(deimosTexture, (this.mc.world.getCelestialAngle(ticks) * 360f) / 4, 95f, 10f, 4.0f)

        this.renderImage(marsTexture, 90f, 90f, 0f, 60f, 1.0f, .4f)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_ALPHA_TEST)

        GL11.glRotatef(90f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(-90f, 0.0f, 0.0f, 1.0f)

        f10 = 60f


        val alpha: Float = 0.7f
        var k: Float = f10
        var l: Float = 20f
        val angle: Float = mc.world.getCelestialAngle(ticks) * 360 % 360



        if (angle > 300 && angle < 360) l = -f10 + angle - 290
        else if (angle > 0 && angle < 50) l = f10 - 50 + angle
        else if (angle > 180 && angle < 300) {
            k = f10 - (angle - 180)
            l = f10
        } else l = f10
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, k.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, k.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -l.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -l.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()

        (tessellator)!!.draw()

        GL11.glPopMatrix()

        GL11.glPushMatrix()
        val f: Float = 0.9f - mc.world.getStarBrightness(ticks)
        this.renderAtmo(
            (tessellator)!!,
            90f,
            90f,
            f10 - 8,
            Vec3d((30 / 255.0f * f).toDouble(), (40 / 255.0f * f).toDouble(), (40 / 255.0f * f).toDouble())
        )

        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 5.2f
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
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
        private val marsTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/mars.png")
        private val overworldTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")
        private val deimosTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/deimos.png")
    }
}
