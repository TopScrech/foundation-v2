package galaxyspace.core.client.models

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction as EnumFacing
import org.apache.commons.lang3.tuple.Pair
import javax.vecmath.Matrix4f

open class BakedModelDelegate constructor(@JvmField protected var base: IBakedModel?) : IBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        return base!!.getQuads(state, side, rand)
    }

    override fun isAmbientOcclusion(): Boolean {
        return base!!.isAmbientOcclusion()
    }

    override fun isGui3d(): Boolean {
        return base!!.isGui3d()
    }

    override fun isBuiltInRenderer(): Boolean {
        return base!!.isBuiltInRenderer()
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return base!!.getParticleTexture()
    }

    override fun getOverrides(): ItemOverrideList {
        return base!!.getOverrides()
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return base!!.getItemCameraTransforms()
    }

    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel?, Matrix4f?> {
        return base!!.handlePerspective(cameraTransformType)
    }
}
