package galaxyspace.systems.SolarSystem.satellites.mars.dimension.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.satellites.mars.dimension.sky.SkyProviderMarsSS
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d

open class SkyProviderMarsSS : SkyProviderBase() {
    @JvmField
    var spinAngle: Float = 0f

    @JvmField
    var spinDeltaPerTick: Float = 0f

    override fun rendererSky(tessellator: Tessellator?, buffer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()
        this.renderImage(overworldTexture, 40f, -this.mc.world.getCelestialAngle(ticks) * 360f, 0f, 0.8f)
        this.renderImage(phobosTexture, 80f, -(this.mc.world.getCelestialAngle(ticks) * 360f), 40f, 4.0f)
        this.renderImage(deimosTexture, 80f, -(this.mc.world.getCelestialAngle(ticks) * 360f) / 2, 195f, 4.0f)

        this.renderImage(marsTexture, 0f, 0f, 0f, 180f, 1.0f, .4f)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_ALPHA_TEST)

        GL11.glRotatef(90f, 0.0f, 1.0f, 0.0f)
        f10 = 180f
        val alpha: Float = 0.9f
        /*
		
		float k = f10;
		float l = 20;
		float angle = mc.world.getCelestialAngle(ticks)*360 % 360;
		
				if (angle > 300 && angle < 360)
			l = -f10 + angle - 300;
		else if (angle > 0 && angle < 50)
			l = f10 - 50 + angle;
		else if (angle > 180 && angle < 300) {
			k = f10 - (angle - 180);
			l = f10;
		} else
			l = f10;
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(-f10, -100.0D, k).color(0.0F, 0.0F, 0.0F, alpha).endVertex();
		buffer.pos(f10, -100.0D, k).color(0.0F, 0.0F, 0.0F, alpha).endVertex();
		buffer.pos(f10, -100.0D, -l).color(0.0F, 0.0F, 0.0F, alpha).endVertex();
		buffer.pos(-f10, -100.0D, -l).color(0.0F, 0.0F, 0.0F, alpha).endVertex();

		tessellator.draw();
		 	*/
        val mod: Double = ((this.mc.world.provider as WorldProviderSpace).getDayLength() / 24000).toDouble()
        val time: Long = (this.mc.player.level().getWorldTime()
                % (this.mc.world.provider as WorldProviderSpace).getDayLength())
        var k: Double = (time / 8) / mod

        (buffer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        if (k >= 0 && k <= f10 * 2) {
            (buffer)!!.pos(-f10.toDouble(), -100.0, f10 - k).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, f10 - k).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        } else if (k >= f10 * 8 && k <= f10 * 9.8) {
            k -= (f10 * 8).toDouble()

            (buffer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, 150 - k).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(-f10.toDouble(), -100.0, 150 - k).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        } else if (k > f10 * 9.8) {
            (buffer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
            (buffer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).color(0.0f, 0.0f, 0.0f, alpha).endVertex()
        }

        (tessellator)!!.draw()
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        val f: Float = 1.0f - mc.world.getStarBrightness(ticks)
        this.renderAtmo(
            (tessellator)!!,
            90f,
            0f,
            f10 - 8,
            Vec3d((120 / 255.0f * f).toDouble(), (90 / 255.0f * f).toDouble(), (90 / 255.0f * f).toDouble())
        )

        GL11.glPopMatrix()
    }

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 5.2f
    }

    override fun sunImage(): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
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

    override fun enableRenderPlanet(): Boolean {
        return false
    }

    companion object {
        private val marsTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/mars.png")
        private val phobosTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/phobos.png")
        private val deimosTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/deimos.png")
        private val overworldTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")
    }
}
