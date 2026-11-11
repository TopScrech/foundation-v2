package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World

open class BlockCrafting constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), ITileEntityProvider,
    ISortableBlock, IShiftDescription, LegacyBlockMetadata {
    init {
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val heldItem: ItemStack = playerIn.getItemInHand(hand)

        if (this.useWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
            return true
        }

        if (playerIn.isShiftKeyDown()) {
            if (this.onSneakMachineActivated(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
                return true
            }
        }

        if (!worldIn.isClientSide) {
            playerIn.openGui(GalacticraftCore.Companion.instance, -1, worldIn, pos.getX(), pos.getY(), pos.getZ())
        }
        return true
    }

    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        this.rotate6Ways((world)!!, (pos)!!)
        return true
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityCrafting()
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        worldIn.setBlock(
            pos,
            state.setValue(FACING, getFacingFromEntity(worldIn, pos, placer)),
            2
        )
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(FACING, EnumFacing.from3DDataValue(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.FACING

        @JvmStatic
        fun getFacingFromEntity(worldIn: World?, clickedBlock: BlockPos, entityIn: EntityLivingBase): EnumFacing {
            if (MathHelper.abs(entityIn.x.toFloat() - clickedBlock.getX().toFloat()) < 3.0f && MathHelper.abs(
                    entityIn.z.toFloat() - clickedBlock.getZ().toFloat()
                ) < 3.0f
            ) {
                val d0: Double = entityIn.y + entityIn.getEyeHeight().toDouble()

                if (d0 - clickedBlock.getY().toDouble() > 2.0) {
                    return EnumFacing.UP
                }

                if (clickedBlock.getY().toDouble() - d0 > 1.0) {
                    return EnumFacing.DOWN
                }
            }

            return entityIn.getHorizontalFacing().getOpposite()
        }
    }
}
