package galaxyspace.systems.SolarSystem.moons.callisto.dimension.sky

import galaxyspace.GalaxySpace
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.moons.callisto.dimension.sky.SkyProviderCallisto
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import kotlin.math.sin

open class SkyProviderCallisto : SkyProviderBase() {
    @JvmField
    var test: Boolean = false

    @JvmField
    var wait: Int = 5

    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, size: Float, ticks: Float) {
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)

        val x: Float = (15 * (sin(((this.mc.world.getCelestialAngle(ticks) * 360.0f) / 40.0f).toDouble()))).toFloat()
        if (wait == 0 && (x >= 15f || x <= -15f)) {
            wait = 150
            test = !test
        }
        if (!test) {
            this.renderImage(jupiterTexture, 0f, 90f, 0f, 20.0f)
            val f: Float = 0.9f
            this.renderAtmo(
                (tessellator)!!,
                90.0f,
                0.0f,
                (20 - 2).toFloat(),
                Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
            )
        }

        if (wait > 0) wait--

        this.renderImage(europaTexture, x, 100f, 0f, 1.0f)
        this.renderImage(ganymedeTexture, x + 15, 85f, 0f, 1.5f)

        if (test) {
            this.renderImage(jupiterTexture, 0f, 90f, 0f, 20.0f)
            val f: Float = 0.9f
            this.renderAtmo(
                (tessellator)!!,
                90.0f,
                0.0f,
                (20 - 2).toFloat(),
                Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
            )
        }
        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 4.5f
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
        private val jupiterTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/jupiter.png")
        private val europaTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/europa.png")
        private val ganymedeTexture =
            ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/ganymede.png")
    }
}
