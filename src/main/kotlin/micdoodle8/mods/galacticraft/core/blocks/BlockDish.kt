package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import foundation.level.offset

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World

open class BlockDish constructor(assetName: String?) : BlockTileGC(Material.IRON), IShiftDescription,
    IPartialSealableBlock, ISortableBlock {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
        for (y: Int in 1..2) {
            for (x: Int in -1..1) {
                for (z: Int in -1..1) {
                    val pos1: BlockPos = pos.offset((if (y == 2) x else 0), y, (if (y == 2) z else 0))
                    val block: Block = world.getBlockState(pos1).getBlock()

                    if (block.getMaterial(world.getBlockState(pos)) !== Material.AIR && !block.isReplaceable(
                            world,
                            pos1
                        )
                    ) {
                        return false
                    }
                }
            }
        }

        val facing: EnumFacing = EnumFacing.from3DDataValue(side.getIndex() xor 1)
        return world.getBlockState(
            pos.offset(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
            )
        ).getBlock() !== GCBlocks.fakeBlock
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val metadata: Int = state.getBlock().getMetaFromState(state)

        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        var change: Int = 0

        when (angle) {
            0 -> change = 1
            1 -> change = 2
            2 -> change = 0
            3 -> change = 3
        }

        worldIn.setBlock(pos, state.getBlock().getStateFromMeta(change), 3)

        BlockMulti.onPlacement(worldIn, pos, placer, this)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        if (tile is TileEntityDish) {
            tile.onDestroy(tile)
        }

        super.breakBlock(worldIn, pos, state)
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
//        IBlockState state = world.getBlockState(pos);
//        int original = state.getBlock().getMetaFromState(state);
//        int change = world.getBlockState(pos).getValue(FACING).rotateY().getHorizontalIndex();

//        TileEntity te = world.getBlockEntity(pos);
//        if (te instanceof TileBaseUniversalElectrical)
//        {
//            ((TileBaseUniversalElectrical) te).updateFacing();
//        }
//
//        world.setBlock(pos, state.getBlock().getStateFromMeta(change), 3);

        return true
    }

    override fun onSneakMachineActivated(
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
        //entityPlayer.openGui(GalacticraftCore.instance, -1, world, x, y, z);
        return true
    }

    override fun createTileEntity(world: World, metadata: IBlockState): TileEntity? {
        return TileEntityDish()
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate("tile.radio_telescope.description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isSealed(world: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }
}
