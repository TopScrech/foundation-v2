package micdoodle8.mods.galacticraft.core.client.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.entity.EntityLivingBase
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

open class ItemModelFlag constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.25f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(-0.22f, -1.6f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX(0.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(-Constants.halfPI / 2.0f)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val xTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) 0.65f else 1.5f
            val yTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) -0.08f else -0.05f
            val trans: Vector3f = Vector3f(xTran, yTran, 0.0f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.rotZ(0.0f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(1.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(-(Math.PI / 3.0f).toFloat())
            ret.mul(mul)
            mul.setIdentity()
            val player: EntityLivingBase? = Minecraft.getMinecraft().player
            if (player != null && player.isHandActive() && !player.getActiveItemStack().isEmpty()) {
                val useTime: Int = Minecraft.getMinecraft().player.getItemInUseMaxCount()
                var interpolate0: Float = useTime / 20.0f
                interpolate0 = (interpolate0 * interpolate0 + interpolate0 * 2.0f) / 3.0f

                if (interpolate0 > 1.0f) {
                    interpolate0 = 1.0f
                }
                val useTimeFuture: Int = Minecraft.getMinecraft().player.getItemInUseMaxCount() + 1
                var interpolate1: Float = useTimeFuture / 20.0f
                interpolate1 = (interpolate1 * interpolate1 + interpolate1 * 2.0f) / 3.0f

                if (interpolate1 > 1.0f) {
                    interpolate1 = 1.0f
                }
                mul.rotX(
                    ((interpolate0 + (interpolate1 - interpolate0) * Minecraft.getMinecraft()
                        .getRenderPartialTicks()) * 75.0f) / Constants.RADIANS_TO_DEGREES
                )
            }
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.54f, -0.4f, 0.54f))
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.0f, 0.5f, 0.0f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.GROUND) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.125f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, 0.0f, 0.5f))
            ret.mul(mul)
            return ret
        }
        if (cameraTransformType == ItemCameraTransforms.TransformType.FIXED) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.2f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(3.15f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, -1.0f, 0.6f))
            ret.mul(mul)
            return ret
        }

        return null
    }
}
