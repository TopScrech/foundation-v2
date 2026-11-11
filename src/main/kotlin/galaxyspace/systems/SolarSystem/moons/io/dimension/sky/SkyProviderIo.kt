package galaxyspace.systems.SolarSystem.moons.io.dimension.sky

import galaxyspace.systems.SolarSystem.moons.io.dimension.sky.SkyProviderIo
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.fml.client.FMLClientHandler

open class SkyProviderIo : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        GL11.glPushMatrix()

        val daylength: Long = (this.mc.world.provider as WorldProviderSpace).getDayLength()


        // Render europa
        f10 = 1.5f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f)

        //GL11.glRotatef(-35.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.getCelestialAngle(daylength / 2), 0.0f, 1.0f, 0.0f)

        GL11.glRotatef(-100f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(europaTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glPopMatrix()
        GL11.glPushMatrix()


        // Render ganymede
        f10 = 1.0f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f)

        //GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(this.getCelestialAngle(daylength), 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(-95f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ganymedeTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glPopMatrix()
        GL11.glPushMatrix()


        // Render callisto
        f10 = 0.8f
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        GL11.glRotatef(-44.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(this.getCelestialAngle(daylength * 2), 0.0f, 1.0f, 0.0f)
        //GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);		
        GL11.glRotatef(-105f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(callistoTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()


        GL11.glPopMatrix()
        GL11.glPushMatrix()


        // Render jupiter
        f10 = 80.0f
        //GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(-180.0f, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(95f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(jupiterTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        val f: Float = 0.8f
        this.renderAtmo(
            (tessellator)!!,
            0.0f,
            0.0f,
            f10 - 8,
            Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
        )

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
        private val jupiterTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/jupiter.png")
        private val callistoTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/callisto.png")
        private val europaTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/europa.png")
        private val ganymedeTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/ganymede.png")
    }
}
