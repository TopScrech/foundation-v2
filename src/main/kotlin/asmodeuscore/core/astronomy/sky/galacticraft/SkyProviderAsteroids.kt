package asmodeuscore.core.astronomy.sky.galacticraft

import org.lwjgl.opengl.GL11
import asmodeuscore.core.utils.ACCompatibilityManager
import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import galaxyspace.GalaxySpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderAsteroids : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        if (ceresTexture == null && ACCompatibilityManager.isGalaxySpaceLoaded()) ceresTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/ceres.png")

        if (ceresTexture != null) {
            GL11.glPushMatrix()


            // Render ceres
            f10 = 4.5f
            GL11.glScalef(0.6f, 0.6f, 0.6f)
            GL11.glRotatef(-170.0f, 50.0f, 1.0f, 0.0f)
            GL11.glRotatef(90f, 190.0f, 50.0f, 0.0f)
            GL11.glRotatef(80f, 0.0f, 1.0f, 0.0f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ceresTexture)
            (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
            (tessellator)!!.draw()

            GL11.glPopMatrix()
        }
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
        return ResourceLocation.fromNamespaceAndPath(AsmodeusCore.ASSET_PREFIX, "textures/gui/celestialbodies/white.png")
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

    override fun enableRenderPlanet(): Boolean {
        return false
    }

    companion object {
        private var ceresTexture: ResourceLocation? = null
    }
}
