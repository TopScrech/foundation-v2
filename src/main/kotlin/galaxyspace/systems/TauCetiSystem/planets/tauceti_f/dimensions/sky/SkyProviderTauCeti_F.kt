package galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky

import galaxyspace.systems.TauCetiSystem.TauCetiSystemBodies
import galaxyspace.systems.TauCetiSystem.planets.tauceti_f.dimensions.sky.SkyProviderTauCeti_F
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.util.ResourceLocation

open class SkyProviderTauCeti_F : SkyProviderBase() {
    //private static final ResourceLocation barnarda_c1_Texture = new ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/barnards/moons/barnarda_c1.png");
    //private static final ResourceLocation barnarda_c2_Texture = new ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/barnards/moons/barnarda_c2.png");
    override fun rendererSky(tessellator: Tessellator?, buffer: BufferBuilder?, size: Float, ticks: Float) {
        GL11.glEnable(GL11.GL_BLEND)

        if (!this.mc.world.isRaining()) {
            //this.renderImage(barnarda_c1_Texture, 0, 0, this.getCelestialAngle(getDayLenght()), 5.5F);
            //this.renderImage(barnarda_c2_Texture, 40, 0, this.getCelestialAngle((long) (getDayLenght() * 1.2)) + 80F, 1.5F);
        }
        GL11.glDisable(GL11.GL_BLEND)
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 4f
    }

    override fun sunImage(): ResourceLocation? {
        return ((TauCetiSystemBodies.TauCetiSystem)!!.getMainStar())!!.getBodyIcon()
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun colorSunAura(): StarColor {
        return StarColor.YELLOW
    }

    override fun getAtmosphereColor(): Vector3 {
        val f: Float = mc.world.getSunBrightness(ticks) + 0.2f
        return Vector3((120 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble(), (160 / 255.0f * f).toDouble())
    }
}
