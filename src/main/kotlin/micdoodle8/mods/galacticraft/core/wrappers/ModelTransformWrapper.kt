package micdoodle8.mods.galacticraft.core.wrappers

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair
import javax.vecmath.Matrix4f

abstract class ModelTransformWrapper(private val parent: IBakedModel) : IBakedModel {
    override fun isAmbientOcclusion(): Boolean {
        return parent.isAmbientOcclusion()
    }

    override fun isGui3d(): Boolean {
        return parent.isGui3d()
    }

    override fun isBuiltInRenderer(): Boolean {
        return parent.isBuiltInRenderer()
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return parent.getParticleTexture()
    }

    @Suppress("deprecation")
    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return parent.getItemCameraTransforms()
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        return parent.getQuads(state, side, rand)
    }

    override fun getOverrides(): ItemOverrideList {
        return parent.getOverrides()
    }

    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel?, Matrix4f?> {
        val matrix4f: Matrix4f? = getTransformForPerspective(cameraTransformType)

        if (matrix4f == null) {
            return Pair.of<ModelTransformWrapper?, Matrix4f?>(
                this,
                TRSRTransformation.blockCornerToCenter(TRSRTransformation.identity()).getMatrix()
            )
        }

        return Pair.of<ModelTransformWrapper?, Matrix4f?>(this, matrix4f)
    }

    protected abstract fun getTransformForPerspective(cameraTransformType: ItemCameraTransforms.TransformType?): Matrix4f?
}
