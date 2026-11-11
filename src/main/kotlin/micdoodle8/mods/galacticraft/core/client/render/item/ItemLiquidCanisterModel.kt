package micdoodle8.mods.galacticraft.core.client.render.item

import com.google.common.collect.ImmutableList
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler

open class ItemLiquidCanisterModel constructor(private val iBakedModel: IBakedModel) : IBakedModel {
    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad?> {
        return iBakedModel.getQuads(state, side, rand)
    }

    override fun getOverrides(): ItemOverrideList {
        return BakedLiquidCanisterOverrideHandler.Companion.INSTANCE
    }

    override fun isAmbientOcclusion(): Boolean {
        return iBakedModel.isAmbientOcclusion()
    }

    override fun isGui3d(): Boolean {
        return iBakedModel.isGui3d()
    }

    override fun isBuiltInRenderer(): Boolean {
        return iBakedModel.isBuiltInRenderer()
    }

    override fun getParticleTexture(): TextureAtlasSprite {
        return iBakedModel.getParticleTexture()
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms {
        return ItemCameraTransforms.DEFAULT
    }

    private class BakedLiquidCanisterOverrideHandler : ItemOverrideList(ImmutableList.of<ItemOverride?>()) {
        override fun handleItemState(
            originalModel: IBakedModel,
            stack: ItemStack,
            world: World?,
            entity: EntityLivingBase?
        ): IBakedModel {
            if (stack.getTagCompound() == null) {
                val copy: ItemStack = stack.copy()
                copy.setTagCompound(NBTTagCompound())
                copy.getTagCompound()!!.putBoolean("Unbreakable", true)
                return FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher().getItemModel(copy)
            }
            return originalModel
        }

        companion object {
            @JvmField
            val INSTANCE: BakedLiquidCanisterOverrideHandler = BakedLiquidCanisterOverrideHandler()
        }
    }
}
