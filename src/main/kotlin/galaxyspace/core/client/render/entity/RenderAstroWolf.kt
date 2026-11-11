package galaxyspace.core.client.render.entity

import galaxyspace.GalaxySpace
import galaxyspace.core.client.models.entity.ModelAstroWolf
import galaxyspace.core.client.render.entity.layers.LayerAstroWolfCollar
import galaxyspace.core.prefab.entities.EntityAstroWolf
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderAstroWolf constructor(p_i47187_1_: RenderManager) :
    RenderLiving<EntityAstroWolf>(p_i47187_1_, ModelAstroWolf(), 0.5f) {
    init {
        this.addLayer<EntityAstroWolf?, LayerAstroWolfCollar?>(LayerAstroWolfCollar(this))
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    override fun handleRotationFloat(livingBase: EntityAstroWolf, partialTicks: Float): Float {
        return livingBase.getTailRotation()
    }

    /**
     * Renders the desired `T` type Entity.
     */
    override fun doRender(
        entity: EntityAstroWolf,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float
    ) {
        if (entity.isWolfWet()) {
            val f: Float = entity.getBrightness() * entity.getShadingWhileWet(partialTicks)
            GlStateManager.color(f, f, f)
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks)
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    override fun getEntityTexture(entity: EntityAstroWolf): ResourceLocation? {
        if (entity.isTamed()) {
            return if (entity.wolfInventory.getItem(1).isEmpty()) TAMED_WOLF_TEXTURES else WOLF_THERMAL
        } else {
            return if (entity.isAngry()) ANRGY_WOLF_TEXTURES else WOLF_TEXTURES
        }
    }

    companion object {
        private val WOLF_TEXTURES = ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/model/astro_wolf.png")
        private val TAMED_WOLF_TEXTURES =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/model/astro_wolf_tame.png")
        private val ANRGY_WOLF_TEXTURES =
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/model/astro_wolf_angry.png")
        private val WOLF_THERMAL = ResourceLocation.fromNamespaceAndPath(GalaxySpace.ASSET_PREFIX, "textures/model/astro_wolf_thermal.png")
    }
}
