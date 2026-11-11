package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelFlag
import micdoodle8.mods.galacticraft.core.entities.EntityFlag
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

@OnlyIn(Dist.CLIENT)
open class RenderFlag constructor(manager: RenderManager) : Render<EntityFlag>(manager) {
    @JvmField
    protected var modelFlag: ModelFlag

    init {
        this.shadowSize = 1f
        this.modelFlag = ModelFlag()
    }

    override fun getEntityTexture(entity: EntityFlag): ResourceLocation? {
        return flagTexture
    }

    override fun doRender(entity: EntityFlag, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float) {
        GlStateManager.disableRescaleNormal()
        GlStateManager.pushMatrix()
        var seed: Long = entity.getEntityId() * 493286711L
        seed = seed * seed * 4392167121L + seed * 98761L
        val seedX: Float = (((seed shr 16 and 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f
        val seedY: Float = (((seed shr 20 and 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f
        val seedZ: Float = (((seed shr 24 and 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f
        GlStateManager.translate(seedX, seedY + 1.5f, seedZ)
        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        GlStateManager.rotate(180.0f - entity.getFacingAngle(), 0.0f, 1.0f, 0.0f)
        this.bindEntityTexture(entity)
        GlStateManager.scale(-1.0f, -1.0f, 1.0f)
        this.modelFlag.render(entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f)
        GlStateManager.popMatrix()
    }

    override fun shouldRender(lander: EntityFlag, camera: ICamera, camX: Double, camY: Double, camZ: Double): Boolean {
        val axisalignedbb: AxisAlignedBB = lander.getEntityBoundingBox().grow(1.0, 2.0, 1.0)
        return lander.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }

    companion object {
        @JvmField
        var flagTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/flag.png")
    }
}
