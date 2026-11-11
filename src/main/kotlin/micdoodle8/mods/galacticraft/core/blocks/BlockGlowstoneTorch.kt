package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.Property as IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockGlowstoneTorch constructor(assetName: String?) : BlockTorchBase(Material.CIRCUITS),
    IShiftDescription, ISortableBlock {
    init {
        this.setDefaultState(
            this.blockState.getBaseState().setValue(FACING, EnumFacing.UP)
        )
        this.setTickRandomly(true)
        this.setTranslationKey(assetName)
        this.setLightLevel(0.85f)
        this.setSoundType(SoundType.WOOD)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        for (enumfacing: EnumFacing in FACING.getAllowedValues()) {
            if (this.canPlaceAt(worldIn, pos, enumfacing)) {
                return true
            }
        }

        return false
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        this.checkForDrop(worldIn, pos, state)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!this.checkForDrop(worldIn, pos, state)) {
            return
        } else {
            val enumfacing: EnumFacing = state.getValue(FACING)
            val `enumfacing$axis`: EnumFacing.Axis = enumfacing.getAxis()
            val enumfacing1: EnumFacing = enumfacing.getOpposite()
            var flag: Boolean = false

            if (`enumfacing$axis`.isHorizontal() && !worldIn.isSideSolid(pos.offset(enumfacing1), enumfacing, true)) {
                flag = true
            } else if (`enumfacing$axis`.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1))) {
                flag = true
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, pos, state, 0)
                worldIn.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
            }
        }
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        super.updateTick(worldIn, pos, state, rand)

        if (getMetaFromState(state) == 0) {
            this.onBlockAdded(worldIn, pos, state)
        }
    }

    //    @Override
    //    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    //    {
    //        int l = getMetaFromState(worldIn.getBlockState(pos)) & 7;
    //        float f = 0.15F;
    //
    //        if (l == 1)
    //        {
    //            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
    //        }
    //        else if (l == 2)
    //        {
    //            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
    //        }
    //        else if (l == 3)
    //        {
    //            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
    //        }
    //        else if (l == 4)
    //        {
    //            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
    //        }
    //        else
    //        {
    //            f = 0.1F;
    //            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
    //        }
    //
    //        return super.collisionRayTrace(worldIn, pos, start, end);
    //    }
    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(FACING))
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }
}
