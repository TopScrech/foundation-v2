package galaxyspace.systems.SolarSystem.planets.ceres.dimension.sky

import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import galaxyspace.systems.SolarSystem.planets.ceres.dimension.sky.SkyProviderCeres
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderCeres : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        for (i: Int in 0..2) {
            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)

            f10 = 80.0f * i
            GL11.glScalef(0.8f, 0.6f, 0.8f)
            //GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f)
            //GL11.glRotatef(-90.0f, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 0.0f, 1.0f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f)
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(meteorpoleTexture)
            (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
            (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
            (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
            (tessellator)!!.draw()
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
        }

        GL11.glPushMatrix()
        //JUPITER
        f10 = 5.0f
        GL11.glScalef(0.8f, 0.6f, 0.8f)
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f)
        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 1.0f, 0.0f)
        //GL11.glRotatef(-this.mc.world.getCelestialAngle(this.ticks) * 360.0F * 0.02F, 0.0F, 0.0F, 1.0F);
        GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(jupiterTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        var f: Float = 0.3f
        this.renderAtmo(
            (tessellator)!!,
            0.0f,
            0.0f,
            f10 - 0.5f,
            Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
        )
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()
        GL11.glPushMatrix()

        //MARS
        f10 = 1.0f
        GL11.glScalef(0.8f, 0.6f, 0.8f)
        GL11.glRotatef(30.0f, 1.0f, 0.0f, 0.0f)
        //GL11.glRotatef(90.0f, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(this.mc.world.getCelestialAngle(ticks) * 360.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(188f, 0.0f, 0.0f, 1.0f)

        GL11.glColor4f(0.2f, 0.2f, 0.2f, 1.0f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(marsTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        f = 0.3f

        //this.renderAtmo(tessellator, 0.0F, 0.0F, f10 - 0.5F, new Vector3(120 / 255.0F * f, 110 / 255.0F * f, 120 / 255.0F * f));
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
        //private static final ResourceLocation meteor1Texture = new ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/environment/background/meteor1.png");
        //private static final ResourceLocation meteor2Texture = new ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/environment/background/meteor2.png");
        //private static final ResourceLocation meteor3Texture = new ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/environment/background/meteor3.png");
        private val meteorpoleTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/environment/background/meteorpole.png")

        private val jupiterTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/jupiter.png")
        private val marsTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/mars.png")
    }
}
