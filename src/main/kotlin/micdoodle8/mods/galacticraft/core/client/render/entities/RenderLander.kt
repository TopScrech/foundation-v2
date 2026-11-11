package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.model.ModelLander
import micdoodle8.mods.galacticraft.core.entities.EntityLander
import net.minecraft.client.renderer.culling.ICamera
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import kotlin.math.sin

@OnlyIn(Dist.CLIENT)
open class RenderLander constructor(manager: RenderManager) : Render<EntityLander>(manager) {
    @JvmField
    protected var landerModel: ModelLander

    init {
        this.shadowSize = 2f
        this.landerModel = ModelLander()
    }

    override fun getEntityTexture(par1Entity: EntityLander): ResourceLocation? {
        return landerTexture
    }

    override fun doRender(lander: EntityLander, par2: Double, par4: Double, par6: Double, par8: Float, par9: Float) {
        GL11.glPushMatrix()
        val var24: Float = lander.prevRotationPitch + (lander.rotationPitch - lander.prevRotationPitch) * par9
        GL11.glTranslatef(par2.toFloat(), par4.toFloat() + 1.55f, par6.toFloat())
        GL11.glRotatef(180.0f - par8, 0.0f, 1.0f, 0.0f)
        GL11.glRotatef(-var24, 0.0f, 0.0f, 1.0f)

        val f6: Float = lander.timeSinceHit - par9
        var f7: Float = lander.currentDamage - par9

        if (f7 < 0.0f) {
            f7 = 0.0f
        }

        if (f6 > 0.0f) {
            GL11.glRotatef(sin(f6.toDouble()).toFloat() * 0.2f * f6 * f7 / 25.0f, 1.0f, 0.0f, 0.0f)
        }

        this.bindEntityTexture(lander)
        GL11.glScalef(-1.0f, -1.0f, 1.0f)
        this.landerModel.render(lander, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f)
        GL11.glPopMatrix()
    }

    override fun shouldRender(
        lander: EntityLander,
        camera: ICamera,
        camX: Double,
        camY: Double,
        camZ: Double
    ): Boolean {
        val axisalignedbb: AxisAlignedBB = lander.getEntityBoundingBox().grow(2.0, 1.0, 2.0)
        return lander.isInRangeToRender3d(camX, camY, camZ) && camera.isBoundingBoxInFrustum(axisalignedbb)
    }

    companion object {
        private val landerTexture = ResourceLocation.fromNamespaceAndPath(Constants.ASSET_PREFIX, "textures/model/lander.png")
    }
}
