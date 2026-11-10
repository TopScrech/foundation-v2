package galaxyspace.systems.SolarSystem.planets.mars.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import javax.vecmath.Quat4f
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

open class ItemModelMarsRover constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 225f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.1285f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(-0.8f, -3.0f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(-0.3f)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.0f, -0.4f, -0.3f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            mul.setScale(0.15f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(-1.0f, -2.0f, 1.5f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(105f, 15f, -5f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.45f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(Constants.halfPI)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX((0.2f - Math.PI).toFloat())
            ret.mul(mul)
            mul.setIdentity()
            mul.rotZ(0.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotZ(-0.65f)
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
            mul.setScale(0.075f)
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
            mul.setScale(0.1f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(1.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(1.5f, -1.5f, -0.15f))
            ret.mul(mul)
            return ret
        }

        return null
    }
}
