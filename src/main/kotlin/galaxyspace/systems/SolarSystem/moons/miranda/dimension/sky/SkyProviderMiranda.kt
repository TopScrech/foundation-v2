package galaxyspace.systems.SolarSystem.moons.miranda.dimension.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.moons.miranda.dimension.sky.SkyProviderMiranda
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderMiranda : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        f10 = 40.0f
        //GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(100.0f, 1.0f, 0.0f, 0.0f)
        //GL11.glRotatef(-75F, 1.0F, 0.0F, 0.0F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(uranusTexture)
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
            f10 - 6,
            Vec3d((120 / 255.0f * f).toDouble(), (160 / 255.0f * f).toDouble(), (180 / 255.0f * f).toDouble())
        )
        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GL11.glPopMatrix()
        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_ALPHA_TEST)


        //GL11.glEnable(GL11.GL_BLEND);
        f10 = 106f
        GL11.glScalef(0.6f, 1.0f, 1.0f)
        GL11.glRotatef(100.0f, 1.0f, 0.0f, 0.0f)
        GL11.glTranslatef(50f, 0f, 13.5f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(uranusRingTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glDisable(GL11.GL_ALPHA_TEST)

        //GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix()
    }

    override fun enableBaseImages(): Boolean {
        return true
    }

    override fun sunSize(): Float {
        return 3.5f
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
        private val uranusTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus.png")
        private val uranusRingTexture =
            ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/uranus_rings.png")
    }
}
