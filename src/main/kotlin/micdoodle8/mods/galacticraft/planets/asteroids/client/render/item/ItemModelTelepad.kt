package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

open class ItemModelTelepad constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(8f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.12f, -0.2f, 0.24f))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX(Math.PI.toFloat() / 12.0f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(Math.PI.toFloat() / 5.0f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.1625f)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(-0.6f, 0.1f, 1.6f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(80f, 225f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.1f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.5f, 1.0f, 0.7f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(-12f, 20f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.1f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            return ret
        }
        if (cameraTransformType == ItemCameraTransforms.TransformType.GROUND) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.1f)
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
            mul.setScale(0.15f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(1.575f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, -1.0f, 0.5f))
            ret.mul(mul)
            return ret
        }
        return null
    }
}
