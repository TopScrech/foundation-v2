package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderEntityFake constructor(manager: RenderManager) : Render<EntityCelestialFake>(manager) {
    override fun getEntityTexture(par1Entity: EntityCelestialFake): ResourceLocation? {
        return null
    }

    override fun doRender(
        par1Entity: EntityCelestialFake,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Float,
        par9: Float
    ) {
    }

    override fun shouldRender(
        lander: EntityCelestialFake,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        return false
    }
}
