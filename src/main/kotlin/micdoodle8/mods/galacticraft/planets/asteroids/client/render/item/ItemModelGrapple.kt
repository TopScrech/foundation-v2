package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

open class ItemModelGrapple constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 45f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.7f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(-0.3f, 1.15f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(Constants.halfPI)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val xTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) 0.3f else 0.3f
            val yTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) 0.2f else -0.2f
            val zTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) 0.2f else 0.8f
            val xRot: Int =
                if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) 180 else 0
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(xRot.toFloat(), 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.35f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(xTran, yTran, zTran))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY((-Math.PI / 2.0f).toFloat())
            mul.rotZ((-Math.PI / 2.0f).toFloat())
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val xTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) -0.275f else 0.5f
            val yTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) 0.925f else 0.05f
            val zTran: Float =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) 0.2f else -0.2f
            val xRot: Int =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) -180 else 0
            val yRot: Int =
                if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) 75 else 45
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(xRot.toFloat(), yRot.toFloat(), 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(1.6f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(xTran, yTran, zTran))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY((-Math.PI / 1.2f).toFloat())
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.GROUND) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.35f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.65f, 0.35f, 0.5f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIXED) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.6f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(3.15f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.8f, 0.5f, 0.525f))
            ret.mul(mul)
            return ret
        }

        return null
    }
}
