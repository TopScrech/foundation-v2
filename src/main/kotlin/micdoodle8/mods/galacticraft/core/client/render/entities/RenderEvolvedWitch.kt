package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.client.model.ModelEvolvedWitch
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerHeldItemEvolvedWitch
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedWitch
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
open class RenderEvolvedWitch constructor(manager: RenderManager) :
    RenderLiving<EntityEvolvedWitch>(manager, ModelEvolvedWitch(), 0.5f) {
    private var texSwitch = false

    init {
        this.addLayer<EntityEvolvedWitch?, LayerHeldItemEvolvedWitch?>(LayerHeldItemEvolvedWitch(this))
    }

    override fun transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f)
    }

    override fun doRender(
        entity: EntityEvolvedWitch,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        (this.mainModel as ModelEvolvedWitch).holdingItem = entity.getHeldItemMainhand() != null
        super.doRender(entity, x, y, z, entityYaw, partialTicks)
        if (OverlaySensorGlasses.overrideMobTexture()) {
            texSwitch = true
            super.doRender(entity, x, y, z, entityYaw, partialTicks)
            texSwitch = false
        }
    }

    override fun preRenderCallback(entity: EntityEvolvedWitch, partialTickTime: Float) {
        val f1: Float = 0.9375f
        GlStateManager.scale(f1, f1, f1)
        if (texSwitch) {
            OverlaySensorGlasses.preRenderMobs()
        }
    }

    override fun getEntityTexture(entity: EntityEvolvedWitch): ResourceLocation? {
        return if (texSwitch) OverlaySensorGlasses.altTexture else witchTexture
    }

    companion object {
        private val witchTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/model/evolved_witch.png")
    }
}
