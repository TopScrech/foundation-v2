package micdoodle8.mods.galacticraft.core.client.render.item

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f

open class ItemModelRocket constructor(modelToWrap: IBakedModel?) : ModelTransformWrapper((modelToWrap)!!) {
    override fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f? {
        if (cameraTransformType == ItemCameraTransforms.TransformType.GUI) {
            val trans: Vector3f = Vector3f(-0.12f, 0.0f, -0.12f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(30f, 225f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.57f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(-0.5f, -0.6f, 0.0f))
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(Constants.halfPI)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX((Math.PI / 4.0f).toFloat())
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(ClientUtil.getMilliseconds() / 1000.0f)
            ret.mul(mul)
            mul.setIdentity()
            trans.scale(-1.0f)
            mul.setTranslation(trans)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.3f)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.5f, -3.2f, -2.6f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(0f, 45f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX(Constants.halfPI)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotZ(-0.65f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(trans)
            ret.mul(mul)
            return ret
        }

        if (cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            val trans: Vector3f = Vector3f(0.4f, -2.8f, 1.2f)
            val ret: Matrix4f = Matrix4f()
            ret.setIdentity()
            val mul: Matrix4f = Matrix4f()
            mul.setIdentity()
            val rot: Quat4f = TRSRTransformation.quatFromXYZDegrees(Vector3f(75f, 0f, 0f))
            mul.setRotation(rot)
            ret.mul(mul)
            mul.setIdentity()
            mul.setScale(0.5f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotZ(-Constants.halfPI)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(Constants.halfPI)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotX(0.2f)
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
            mul.setScale(0.135f)
            ret.mul(mul)
            mul.setIdentity()
            mul.rotY(0.0f)
            ret.mul(mul)
            mul.setIdentity()
            mul.setTranslation(Vector3f(0.5f, -2.75f, 0.5f))
            ret.mul(mul)
            return ret
        }

        return null
    }
}
