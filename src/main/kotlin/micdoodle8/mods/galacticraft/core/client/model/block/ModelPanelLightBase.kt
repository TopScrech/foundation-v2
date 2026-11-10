package micdoodle8.mods.galacticraft.core.client.model.block

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelResourceLocation

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState

open class ModelPanelLightBase constructor(private val callingBlock: ModelResourceLocation) : IBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        if (state?.getBlock() is BlockPanelLighting) {
            val baseState: IBlockState? =
                (state as IExtendedBlockState).getValue(BlockPanelLighting.BASE_STATE)
            val layer: BlockRenderLayer? = MinecraftForgeClient.getRenderLayer()
            if (baseState == null) {
                if (layer == null || layer == BlockRenderLayer.SOLID) {
                    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager()
                        .getModel(this.callingBlock).getQuads(state, side, rand)
                }
            } else if (layer == null || baseState.getBlock().canRenderInLayer(baseState, layer)) {
                return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
                    .getModelForState(baseState).getQuads(baseState, side, rand)
            }
        }
        return ImmutableList.of<BakedQuad?>()
    }

    override fun isAmbientOcclusion(): Boolean {
        return true
    }

    override fun isGui3d(): Boolean {
        return true
    }

    override fun isBuiltInRenderer(): Boolean {
        return false
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/glass")
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return ItemCameraTransforms.DEFAULT
    }

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }
}
