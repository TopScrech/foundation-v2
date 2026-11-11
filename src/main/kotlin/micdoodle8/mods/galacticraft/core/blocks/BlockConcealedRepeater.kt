package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockRedstoneRepeater
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
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

    @OnlyIn(Dist.CLIENT)
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
        return (GCBlocks.concealedRepeater_Powered)!!.defaultBlockState()
            .setValue(FACING, enumfacing).setValue(DELAY, integer)
            .setValue(LOCKED, obool)
    }

    override fun getUnpoweredState(poweredState: IBlockState): IBlockState {
        val integer: Int = poweredState.getValue(DELAY) as Int
        val obool: Boolean = poweredState.getValue(LOCKED) as Boolean
        val enumfacing: EnumFacing = poweredState.getValue(FACING) as EnumFacing
        return (GCBlocks.concealedRepeater_Unpowered)!!.defaultBlockState()
            .setValue(FACING, enumfacing).setValue(DELAY, integer)
            .setValue(LOCKED, obool)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return ItemBlock.getItemFromBlock(GCBlocks.concealedRepeater_Unpowered)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState): ItemStack {
        return ItemStack(GCBlocks.concealedRepeater_Unpowered)
    }

    @OnlyIn(Dist.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
    }

    companion object {
        @JvmField
        protected val CUBE_AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    }
}
