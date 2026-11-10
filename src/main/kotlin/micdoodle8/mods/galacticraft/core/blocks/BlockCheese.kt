package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockCheese constructor(assetName: String?) : Block(Material.CAKE), IShiftDescription, ISortableBlock {
    init {
        this.setTickRandomly(true)
        this.disableStats()
        this.setHardness(0.5f)
        this.setDefaultState(this.blockState.getBaseState().withProperty(BITES, 0))
        this.setSoundType(SoundType.CLOTH)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        var bites: Int = 0
        if (state.getBlock() is BlockCheese) {
            bites = (state.getValue(BITES) as Int)
        }
        return CHEESE_AABB[bites]
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
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
        this.eatCheeseSlice(worldIn, pos, worldIn.getBlockState(pos), playerIn)
        return true
    }

    override fun onBlockClicked(worldIn: World, pos: BlockPos, playerIn: EntityPlayer) {
        this.eatCheeseSlice(worldIn, pos, worldIn.getBlockState(pos), playerIn)
    }

    private fun eatCheeseSlice(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer) {
        if (playerIn.canEat(false)) {
            playerIn.getFoodStats().addStats(2, 0.1f)
            val i: Int = (state.getValue(BITES) as Int)

            if (i < 6) {
                worldIn.setBlockState(pos, state.withProperty(BITES, i + 1), 3)
            } else {
                worldIn.setBlockToAir(pos)
            }
        }
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos)
        }
    }

    private fun canBlockStay(worldIn: World, pos: BlockPos): Boolean {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid()
    }

    override fun quantityDropped(par1Random: Random): Int {
        return 0
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(Blocks.AIR)
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(BITES, meta)
    }

    override fun getItem(worldIn: World, pos: BlockPos, state: IBlockState): ItemStack {
        return ItemStack(Items.CAKE, 1, 0)
    }

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BITES) as Int)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(BITES))
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        return (7 - (worldIn.getBlockState(pos).getValue(BITES) as Int)) * 2
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    companion object {
        @JvmField
        val BITES: PropertyInteger = PropertyInteger.create("bites", 0, 6)

        @JvmField
        protected val CHEESE_AABB: Array<AxisAlignedBB> = arrayOf<AxisAlignedBB>(
            AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.1875, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.3125, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.4375, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.5625, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.6875, 0.0, 0.0625, 0.9375, 0.5, 0.9375),
            AxisAlignedBB(0.8125, 0.0, 0.0625, 0.9375, 0.5, 0.9375)
        )
    }
}
