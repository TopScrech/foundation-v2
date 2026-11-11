package micdoodle8.mods.galacticraft.core.client.model.block

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelManager
import net.minecraft.client.renderer.block.model.ModelResourceLocation

import com.google.common.collect.ImmutableList
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.TransformerHooks
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.property.IExtendedBlockState

open class ModelGrating constructor(blockLoc: ModelResourceLocation, modelManager: ModelManager) : IBakedModel {
    private val gratingMetal: IBakedModel

    init {
        this.gratingMetal = modelManager.getModel(blockLoc)
    }

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        if (side == EnumFacing.DOWN && state!!.getBlock() is BlockGrating) {
            val baseState: IBlockState? = (state as IExtendedBlockState).getValue(BlockGrating.BASE_STATE)
            if (baseState != null) {
                val blockAccess: IBlockAccess = (BlockGrating.savedBlockAccess)!!
                val pos: BlockPos = (BlockGrating.savedPos)!!
                val buffer: BufferBuilder? = TransformerHooks.renderBuilder.get()
                if (buffer != null) {
                    if (baseState.getBlock().canRenderInLayer(baseState, MinecraftForgeClient.getRenderLayer())) try {
                        Minecraft.getInstance().getBlockRendererDispatcher()
                            .renderBlock(baseState, pos, blockAccess, buffer)
                    } catch (ignore: Exception) {
                        ignore.printStackTrace()
                    }
                }
            }
        }
        if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
            return this.gratingMetal.getQuads(state, side, rand)
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
        return Minecraft.getInstance().getTextureMapBlocks()
            .getAtlasSprite(Constants.ASSET_PREFIX + ":blocks/screen_side")
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return ItemCameraTransforms.DEFAULT
    }

    override fun getOverrides(): ItemOverrideList {
        return ItemOverrideList.NONE
    }
}
