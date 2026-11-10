package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockRedstoneRepeater
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockConcealedRepeater constructor(assetName: String?, powered: Boolean) :
    BlockRedstoneRepeater(powered), ISortableBlock {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.blockResistance = 15f
        this.setTranslationKey(assetName)
        this.setCreativeTab(if (powered) null else GalacticraftCore.Companion.galacticraftBlocksTab)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return CUBE_AABB
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun getLightOpacity(state: IBlockState): Int {
        return 0
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return true
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return true
    }

    override fun isSideSolid(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(
        state: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    override fun getPoweredState(unpoweredState: IBlockState): IBlockState {
        val integer: Int = unpoweredState.getValue(DELAY) as Int
        val obool: Boolean = unpoweredState.getValue(LOCKED) as Boolean
        val enumfacing: EnumFacing = unpoweredState.getValue(FACING) as EnumFacing
        return (GCBlocks.concealedRepeater_Powered)!!.getDefaultState()
            .withProperty(FACING, enumfacing).withProperty(DELAY, integer)
            .withProperty(LOCKED, obool)
    }

    override fun getUnpoweredState(poweredState: IBlockState): IBlockState {
        val integer: Int = poweredState.getValue(DELAY) as Int
        val obool: Boolean = poweredState.getValue(LOCKED) as Boolean
        val enumfacing: EnumFacing = poweredState.getValue(FACING) as EnumFacing
        return (GCBlocks.concealedRepeater_Unpowered)!!.getDefaultState()
            .withProperty(FACING, enumfacing).withProperty(DELAY, integer)
            .withProperty(LOCKED, obool)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return ItemBlock.getItemFromBlock(GCBlocks.concealedRepeater_Unpowered)
    }

    @SideOnly(Side.CLIENT)
    override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState): ItemStack {
        return ItemStack(GCBlocks.concealedRepeater_Unpowered)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
    }

    companion object {
        @JvmField
        protected val CUBE_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    }
}
