package micdoodle8.mods.galacticraft.planets.venus.client.render.entity

import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.venus.client.model.ModelJuicer
import micdoodle8.mods.galacticraft.planets.venus.entities.EntityJuicer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderJuicer constructor(renderManager: RenderManager) :
    RenderLiving<EntityJuicer>(renderManager, ModelJuicer(), 0.5f) {
    private var texSwitch = false

    override fun preRenderCallback(entity: EntityJuicer, partialTickTime: Float) {
        GlStateManager.rotate(if (entity.isHanging()) 180.0f else 0.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.translate(0.0f, if (entity.isHanging()) 1.8f else 1.3f, 0.0f)
        super.preRenderCallback(entity, partialTickTime)
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    override fun doRender(
        entity: EntityJuicer,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, x, y, z, entityYaw, partialTicks)
            texSwitch = false
        }
    }

    override fun getEntityTexture(juicer: EntityJuicer): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else juicerTexture
    }

    companion object {
        private val juicerTexture = ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/model/juicer.png")
    }
}
