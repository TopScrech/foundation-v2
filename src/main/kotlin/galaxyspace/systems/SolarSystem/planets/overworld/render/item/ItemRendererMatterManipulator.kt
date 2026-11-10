package galaxyspace.systems.SolarSystem.planets.overworld.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import javax.vecmath.Quat4f
import javax.vecmath.AxisAngle4f
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

open class ItemRendererMatterManipulator constructor(modelToWrap: IBakedModel?) :
    ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.28f, 0.45f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.7f)
            mul.setRotation(AxisAngle4f(0f, 1f, 0f, 0.7f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(38f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.68f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, 0.4f, -0.1f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.0f, 1.3f, 0.8f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(-10f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.38f)
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
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(0f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.65f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, 1.0f, 0.0f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIXED) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.8f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(3.15f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.0f, 0.0f, 0.4f))
            ret.mul(mul)
            return ret
        }

        return null
    }
}
