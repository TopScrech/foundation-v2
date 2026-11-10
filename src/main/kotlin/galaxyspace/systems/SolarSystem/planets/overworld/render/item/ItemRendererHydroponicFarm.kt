package galaxyspace.systems.SolarSystem.planets.overworld.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import javax.vecmath.Quat4f
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

open class ItemRendererHydroponicFarm constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.0f, -0.8f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.025f)
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
            mul.setScale(0.01f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(-0.025f, -8.4f, -10.3f))
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.0f, -16.3f, -12.0f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(-30f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.015f)
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
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(-30f, 0f, 0f))
            mul.setRotation(rot)
            mul.setTranslation(Vector3f(0.0f, -0.4f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.0085f)
            ret.mul(mul)

            mul.setIdentity()
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
