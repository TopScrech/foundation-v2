package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFenceGate
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.Property as IProperty
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockWallGC /* Do not extend BlockWall */ constructor(name: String?) : Block(Material.ROCK),
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(1.5f)
        this.setResistance(2.5f)
        this.setDefaultState(
            this.defaultBlockState().setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(VARIANT, BlockType.TIN_1_WALL)
        )
        this.setTranslationKey(name)
    }

    override fun isFullCube(state: IBlockState): kotlin.Boolean {
        return false
    }

    override fun isPassable(world: IBlockAccess, pos: BlockPos): kotlin.Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState): kotlin.Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return if (face != EnumFacing.UP && face != EnumFacing.DOWN) BlockFaceShape.MIDDLE_POLE_THICK else BlockFaceShape.CENTER_BIG
    }

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos): kotlin.Boolean {
        return true
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        var state: IBlockState = state
        state = this.getActualState(state, source, pos)
        return AABB_BY_INDEX[getAABBIndex(state)]
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        var blockState: IBlockState = blockState
        blockState = this.getActualState(blockState, worldIn, pos)
        return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)]
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos)
    //    {
    //        boolean flag = this.canConnectTo(world, pos.north());
    //        boolean flag1 = this.canConnectTo(world, pos.south());
    //        boolean flag2 = this.canConnectTo(world, pos.west());
    //        boolean flag3 = this.canConnectTo(world, pos.east());
    //        float f = 0.25F;
    //        float f1 = 0.75F;
    //        float f2 = 0.25F;
    //        float f3 = 0.75F;
    //        float f4 = 1.0F;
    //
    //        if (flag)
    //        {
    //            f2 = 0.0F;
    //        }
    //        if (flag1)
    //        {
    //            f3 = 1.0F;
    //        }
    //        if (flag2)
    //        {
    //            f = 0.0F;
    //        }
    //        if (flag3)
    //        {
    //            f1 = 1.0F;
    //        }
    //
    //        if (flag && flag1 && !flag2 && !flag3)
    //        {
    //            f4 = 0.8125F;
    //            f = 0.3125F;
    //            f1 = 0.6875F;
    //        }
    //        else if (!flag && !flag1 && flag2 && flag3)
    //        {
    //            f4 = 0.8125F;
    //            f2 = 0.3125F;
    //            f3 = 0.6875F;
    //        }
    //        this.setBlockBounds(f, 0.0F, f2, f1, f4, f3);
    //    }
    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    //    {
    //        this.setBlockBoundsBasedOnState(world, pos);
    //        this.maxY = 1.5D;
    //        return super.getCollisionBoundingBox(world, pos, state);
    //    }
    private fun canConnectTo(world: IBlockAccess, pos: BlockPos): kotlin.Boolean {
        val state: IBlockState = world.getBlockState(pos)
        val block: Block = state.getBlock()
        return if (block === Blocks.BARRIER) false else if (block !== this && block !is BlockFenceGate) if (block.getMaterial(
                state
            ).isOpaque() && block.isFullCube(state)
        ) block.getMaterial(state) !== Material.GOURD else false else true
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i: Int in 0..<(if (GalacticraftCore.isPlanetsLoaded) 6 else 4)) {
            list.add(ItemStack(this, 1, i))
        }
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
    }

    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): kotlin.Boolean {
        return if (side == EnumFacing.DOWN) super.shouldSideBeRendered(blockState, blockAccess, pos, side) else true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(VARIANT, BlockType.entries[meta % 6])
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(VARIANT) as BlockType).ordinal
    }

    override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val flag: kotlin.Boolean = this.canConnectTo(world, pos.north())
        val flag1: kotlin.Boolean = this.canConnectTo(world, pos.east())
        val flag2: kotlin.Boolean = this.canConnectTo(world, pos.south())
        val flag3: kotlin.Boolean = this.canConnectTo(world, pos.west())
        val flag4: kotlin.Boolean = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3
        return state.setValue(UP, !flag4 || !world.isEmptyBlock(pos.above()))
            .setValue(NORTH, flag)
            .setValue(EAST, flag1)
            .setValue(SOUTH, flag2)
            .setValue(WEST, flag3)
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(UP, NORTH, EAST, WEST, SOUTH, VARIANT))
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.WALLS
    }

    enum class BlockType(private val serializedName: String) : IStringSerializable {
        TIN_1_WALL("tin_1_wall"),
        TIN_2_WALL("tin_2_wall"),
        MOON_STONE_WALL("moon_stone_wall"),
        MOON_DUNGEON_BRICK_WALL("moon_dungeon_brick_wall"),
        MARS_COBBLESTONE_WALL("mars_cobblestone_wall"),
        MARS_DUNGEON_BRICK_WALL("mars_dungeon_brick_wall");

        override fun toString(): String {
            return this.getName()
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }
    }

    companion object {
        @JvmField
        protected val AABB_BY_INDEX: Array<AxisAlignedBB> = arrayOf<AxisAlignedBB>(
            AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75),
            AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 0.75),
            AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 1.0),
            AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 0.75),
            AxisAlignedBB(0.3125, 0.0, 0.0, 0.6875, 0.875, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 0.75),
            AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 1.0),
            AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 0.75),
            AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.3125, 1.0, 0.875, 0.6875),
            AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 0.75),
            AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        )

        @JvmField
        protected val CLIP_AABB_BY_INDEX: Array<AxisAlignedBB?> = ((arrayOf<AxisAlignedBB>(
            AABB_BY_INDEX[0].setMaxY(1.5),
            AABB_BY_INDEX[1].setMaxY(1.5),
            AABB_BY_INDEX[2].setMaxY(1.5),
            AABB_BY_INDEX[3].setMaxY(1.5),
            AABB_BY_INDEX[4].setMaxY(1.5),
            AABB_BY_INDEX[5].setMaxY(1.5),
            AABB_BY_INDEX[6].setMaxY(1.5),
            AABB_BY_INDEX[7].setMaxY(1.5),
            AABB_BY_INDEX[8].setMaxY(1.5),
            AABB_BY_INDEX[9].setMaxY(1.5),
            AABB_BY_INDEX[10].setMaxY(1.5),
            AABB_BY_INDEX[11].setMaxY(1.5),
            AABB_BY_INDEX[12].setMaxY(1.5),
            AABB_BY_INDEX[13].setMaxY(1.5),
            AABB_BY_INDEX[14].setMaxY(1.5),
            AABB_BY_INDEX[15].setMaxY(1.5)
        ) as Array<AxisAlignedBB?>?))!!

        @JvmField
        val UP: PropertyBool = PropertyBool.create("up")

        @JvmField
        val NORTH: PropertyBool = PropertyBool.create("north")

        @JvmField
        val EAST: PropertyBool = PropertyBool.create("east")

        @JvmField
        val SOUTH: PropertyBool = PropertyBool.create("south")

        @JvmField
        val WEST: PropertyBool = PropertyBool.create("west")

        @JvmField
        val VARIANT: PropertyEnum<BlockType> = PropertyEnum.create<BlockType>("variant", BlockType::class.java)

        @JvmStatic
        private fun getAABBIndex(state: IBlockState): Int {
            var i: Int = 0

            if (state.getValue(NORTH)) {
                i = i or (1 shl EnumFacing.NORTH.getHorizontalIndex())
            }

            if (state.getValue(EAST)) {
                i = i or (1 shl EnumFacing.EAST.getHorizontalIndex())
            }

            if (state.getValue(SOUTH)) {
                i = i or (1 shl EnumFacing.SOUTH.getHorizontalIndex())
            }

            if (state.getValue(WEST)) {
                i = i or (1 shl EnumFacing.WEST.getHorizontalIndex())
            }

            return i
        }
    }
}
