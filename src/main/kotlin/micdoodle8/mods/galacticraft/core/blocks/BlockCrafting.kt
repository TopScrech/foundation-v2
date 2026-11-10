package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

open class BlockCrafting constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), ITileEntityProvider,
    ISortableBlock, IShiftDescription {
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
        val heldItem: ItemStack = playerIn.getHeldItem(hand)

        if (this.useWrench(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
            return true
        }

        if (playerIn.isSneaking()) {
            if (this.onSneakMachineActivated(worldIn, pos, playerIn, hand, heldItem, side, hitX, hitY, hitZ)) {
                return true
            }
        }

        if (!worldIn.isRemote) {
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
        worldIn.setBlockState(
            pos,
            state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)),
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
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    companion object {
        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing")

        @JvmStatic
        fun getFacingFromEntity(worldIn: World?, clickedBlock: BlockPos, entityIn: EntityLivingBase): EnumFacing {
            if (MathHelper.abs(entityIn.posX.toFloat() - clickedBlock.getX().toFloat()) < 3.0f && MathHelper.abs(
                    entityIn.posZ.toFloat() - clickedBlock.getZ().toFloat()
                ) < 3.0f
            ) {
                val d0: Double = entityIn.posY + entityIn.getEyeHeight().toDouble()

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
