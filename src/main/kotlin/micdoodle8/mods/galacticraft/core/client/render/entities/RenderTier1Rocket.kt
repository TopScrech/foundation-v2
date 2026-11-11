package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.world.entity.Entity
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.util.Mth as MathHelper
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
open class RenderTier1Rocket private constructor(
    manager: RenderManager,
    private val spaceshipTexture: ResourceLocation
) : Render<EntitySpaceshipBase>(manager) {
    @JvmField
    protected var modelSpaceship: ModelBase? = null

    constructor(
        manager: RenderManager,
        spaceshipModel: ModelBase?,
        textureDomain: String,
        texture: String?
    ) : this(manager, ResourceLocation.fromNamespaceAndPath(textureDomain, "textures/model/" + texture + ".png")) {
        this.modelSpaceship = spaceshipModel
    }

    init {
        this.shadowSize = 0.9f
    }

    protected open fun func_110779_a(entity: Entity?): ResourceLocation {
        return this.spaceshipTexture
    }

    override fun getEntityTexture(par1Entity: EntitySpaceshipBase): ResourceLocation? {
        return this.func_110779_a(par1Entity)
    }

    override fun doRender(
        entity: EntitySpaceshipBase,
        par2: Double,
        par4: Double,
        par6: Double,
        par8: Float,
        par9: Float
    ) {
        GL11.glPushMatrix()
        val var24: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9
        val var25: Float = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par9

        GL11.glTranslatef(par2.toFloat(), par4.toFloat(), par6.toFloat())
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f)
        GL11.glRotatef(-var25, 0.0f, 1.0f, 0.0f)
        GL11.glTranslatef(0.0f, entity.getRenderOffsetY(), 0.0f)
        val var28: Float = entity.rollAmplitude - par9

        if (var28 > 0.0f) {
            val i: Float =
                if (entity.getLaunched()) (5 - MathHelper.floor((entity.timeUntilLaunch / 85).toFloat())) / 10f else 0.3f
            GL11.glRotatef(MathHelper.sin(var28) * var28 * i * par9, 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(MathHelper.sin(var28) * var28 * i * par9, 1.0f, 0.0f, 1.0f)
        }

        this.bindEntityTexture(entity)
        GL11.glScalef(-1.0f, -1.0f, 1.0f)
        this.modelSpaceship!!.render(entity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f)

        GL11.glPopMatrix()
    }

    override fun shouldRender(
        rocket: EntitySpaceshipBase,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = rocket.getEntityBoundingBox().grow(0.6, 1.0, 0.6)

        return rocket.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }
}
