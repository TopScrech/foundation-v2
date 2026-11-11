package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky

import galaxyspace.systems.BarnardsSystem.BarnardsSystemBodies
import galaxyspace.GalaxySpace
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import org.lwjgl.opengl.GL11
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.dimension.sky.SkyProviderBarnarda_C1
import asmodeuscore.core.astronomy.sky.SkyProviderBase
import asmodeuscore.core.astronomy.sky.SkyProviderBase.ModeLight
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.Vec3 as Vec3d

open class SkyProviderBarnarda_C1 : SkyProviderBase() {
    override fun rendererSky(tessellator: Tessellator?, buffer: BufferBuilder?, size: Float, ticks: Float) {
        GL11.glPushMatrix()

        if (!this.mc.world.isRaining() && this.mc.world.provider is IGalacticraftWorldProvider) {
            val gc_provider: IGalacticraftWorldProvider = this.mc.world.provider as IGalacticraftWorldProvider
            val fsd: FrozenStormSaveData =
                FrozenStormSaveData.get(this.mc.world, (gc_provider.getCelestialBody())!!.getName())

            if (!fsd.isFrozenStorm()) {
                this.renderImage(barnarda_c_Texture, 0f, 0f, this.getCelestialAngle(getDayLength()), 15.5f, 1.0f, 0.55f)
                this.renderImage(
                    barnarda_c2_Texture,
                    10f,
                    40f,
                    this.getCelestialAngle((getDayLength() * 1.2).toLong()) + 80f,
                    2.5f,
                    0.4f
                )


                val f: Float = 0.9f
                GL11.glPushMatrix()
                this.renderAtmo(
                    (tessellator)!!,
                    -90.0f,
                    this.getCelestialAngle(getDayLength()),
                    15.2f,
                    Vec3d((61 / 255.0f * f).toDouble(), (86 / 255.0f * f).toDouble(), (175 / 255.0f * f).toDouble())
                )
                GL11.glPopMatrix()
            }
        }

        GL11.glPopMatrix()
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
        return ((BarnardsSystemBodies.Companion.BarnardsSystem)!!.getMainStar())!!.getBodyIcon()
    }

    override fun enableStar(): Boolean {
        return true
    }

    override fun colorSunAura(): StarColor {
        return StarColor.ORANGE
    }

    override fun getAtmosphereColor(): Vector3? {
        if (this.mc.world.provider is WE_WorldProviderSpace) return (this.mc.world.provider as WE_WorldProviderSpace).getSkyColor()

        return (this.mc.world.provider as WorldProviderSpace).getSkyColor()
    }

    companion object {
        private val barnarda_c_Texture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/barnards/barnarda_c.png")
        private val barnarda_c2_Texture =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/gui/celestialbodies/barnards/moons/barnarda_c2.png")
    }
}
