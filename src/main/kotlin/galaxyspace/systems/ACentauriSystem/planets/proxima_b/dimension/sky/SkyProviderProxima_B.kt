package galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.sky

import galaxyspace.GalaxySpace
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.dimension.sky.SkyProviderProxima_B
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.level.Level as World

open class SkyProviderProxima_B : SkyProviderBase() {
    private val acentauri_a =
        ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/acentauri/centauri_a.png")
    private val acentauri_b =
        ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/acentauri/centauri_b.png")

    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, size: Float, ticks: Float) {
        var rain: Float = 1.0f - this.mc.world.getRainStrength(ticks)

        if (rain < 0.3f) rain = 0.3f
        val sunBrightness: Float =
            if (this.mc.world.isRaining()) this.mc.world.getSunBrightness(ticks) * rain else this.mc.world.getSunBrightness(
                ticks
            )

        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(770, 771, 1, 0)
        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(60f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(1.5f, 0.0f, 0.0f, 1.0f)
        this.renderSunAura((tessellator)!!, 0.1f, sunBrightness, StarColor.YELLOW)
        GL11.glRotatef(9f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(-1.5f, 0.0f, 0.0f, 1.0f)
        this.renderSunAura((tessellator)!!, 0.1f, sunBrightness, StarColor.YELLOW)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        val world: World? = mc.world

        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 0.0f, 1.0f)
        if (!this.mc.world.isRaining()) {
            this.renderImage(acentauri_a, -90f, 182f, 60f, 1.2f, sunBrightness)
            this.renderImage(acentauri_b, -90f, 180f, 69f, 1.0f, sunBrightness)
        }

        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 8.5f
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/proxima/proxima.png")
    }

    override fun modeLight(): ModeLight {
        if (mc.world.isRaining()) return ModeLight.WITHOUT_SUN
        return ModeLight.DEFAULT
    }

    override fun colorSunAura(): StarColor {
        return StarColor.ORANGE
    }

    override fun getAtmosphereColor(): Vector3? {
        return null
    }

    override fun enableSmoothRender(): Boolean {
        return false
    }

    override fun expandSizeAura(): Int {
        return 25
    }

    override fun enableLargeSunAura(): Boolean {
        return true
    }
}
