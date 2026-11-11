package galaxyspace.systems.SolarSystem.moons.europa.dimension.sky

import org.lwjgl.opengl.GL11
import galaxyspace.GalaxySpace
import galaxyspace.systems.SolarSystem.moons.europa.dimension.sky.SkyProviderEuropa
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraftforge.fml.client.FMLClientHandler
import kotlin.math.sin

open class SkyProviderEuropa : SkyProviderBase() {
    @JvmField
    var test: Boolean = false

    @JvmField
    var wait: Int = 5

    override fun rendererSky(tessellator: Tessellator?, worldRenderer: BufferBuilder?, f10: Float, ticks: Float) {
        var f10: Float = f10
        if (inWater(this.mc.player)) return

        GL11.glPushMatrix()

        val daylength: Long = getDayLength()

        f10 = 1.5f
        GL11.glScalef(0.8f, 0.8f, 0.8f)
        GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(this.getCelestialAngle(daylength / 2), 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(100f, 1.0f, 0.0f, 0.0f)
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

        f10 = 0.8f
        GL11.glScalef(0.8f, 0.8f, 0.8f)
        GL11.glRotatef(0.0f, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(this.getCelestialAngle(daylength * 2), 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(100f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(callistoTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()

        GL11.glPopMatrix()

        val x: Float = (40 * (sin(((this.mc.world.getCelestialAngle(ticks) * 360.0f) / 10.0f).toDouble()))).toFloat()
        if (wait == 0 && (x >= 40f || x <= -40f)) {
            wait = 150
            test = !test
        }
        if (wait > 0) wait--

        if (!test) {
            GL11.glPushMatrix()
            f10 = 60.0f
            GL11.glScalef(0.8f, 0.8f, 0.8f)
            GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(90f, 1.0f, 0.0f, 0.0f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(jupiterTexture)
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
                f10 - 8,
                Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
            )
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }

        GL11.glPushMatrix()

        f10 = 2.0f
        GL11.glScalef(0.8f, 0.8f, 0.8f)
        GL11.glRotatef(x, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(100f, 1.0f, 0.0f, 0.0f)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ioTexture)
        (worldRenderer)!!.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, f10.toDouble()).tex(0.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, f10.toDouble()).tex(1.0, 1.0).endVertex()
        (worldRenderer)!!.pos(f10.toDouble(), -100.0, -f10.toDouble()).tex(1.0, 0.0).endVertex()
        (worldRenderer)!!.pos(-f10.toDouble(), -100.0, -f10.toDouble()).tex(0.0, 0.0).endVertex()
        (tessellator)!!.draw()
        GL11.glPopMatrix()

        if (test) {
            GL11.glPushMatrix()
            f10 = 60.0f
            GL11.glScalef(0.8f, 0.8f, 0.8f)
            GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(90f, 1.0f, 0.0f, 0.0f)
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(jupiterTexture)
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
                f10 - 8,
                Vec3d((120 / 255.0f * f).toDouble(), (110 / 255.0f * f).toDouble(), (120 / 255.0f * f).toDouble())
            )
            GL11.glEnable(GL11.GL_TEXTURE_2D)
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
        return ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sun_blank.png")
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

    override fun modeLight(): ModeLight {
        return ModeLight.DEFAULT
    }

    companion object {
        private val jupiterTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/jupiter.png")
        private val ioTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/io.png")
        private val ganymedeTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/ganymede.png")
        private val callistoTexture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/sol/moons/callisto.png")
    }
}
