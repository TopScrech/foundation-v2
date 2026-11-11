package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockBreakable
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockAirLockWall constructor(assetName: String?) : BlockBreakable(Material.IRON, false),
    IPartialSealableBlock, ISortableBlock, LegacyBlockMetadata {
    enum class EnumAirLockSealConnection(private val serializedName: String) : IStringSerializable {
        X("x"),
        Z("z"),
        FLAT("flat");

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }
    }

    init {
        this.setTickRandomly(true)
        this.setHardness(1000.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        when (getConnection(source, pos)) {
            EnumAirLockSealConnection.X -> return AABB_X
            EnumAirLockSealConnection.Z -> return AABB_Z
            EnumAirLockSealConnection.FLAT -> return AABB_FLAT
            else -> return AABB_FLAT
        }
    }


    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getCollisionBoundingBox(worldIn, pos, state);
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getSelectedBoundingBox(worldIn, pos);
    //    }
    //
    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        switch (getConnection(worldIn, pos))
    //        {
    //        case FLAT:
    //            this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 0.75F, 1.0F);
    //            break;
    //        case X:
    //            this.setBlockBounds(0.25F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
    //            break;
    //        case Z:
    //            this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 0.75F);
    //            break;
    //        }
    //    }
    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
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

    @OnlyIn(Dist.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    override fun quantityDropped(par1Random: Random): Int {
        return 0
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return true
    }

    //    @Override
    //    public Item getItem(World world, BlockPos pos)
    //    {
    //        return null;
    //    }
    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, CONNECTION_TYPE)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        return state.setValue(
            CONNECTION_TYPE,
            getConnection(worldIn, pos)
        )
    }

    private fun getConnection(worldIn: IBlockAccess, pos: BlockPos): EnumAirLockSealConnection {
        val connection: EnumAirLockSealConnection

        val frameID: Block? = GCBlocks.airLockFrame
        val sealID: Block? = GCBlocks.airLockSeal

        val idXMin: Block = worldIn.getBlockState(pos.offset(EnumFacing.WEST)).getBlock()
        val idXMax: Block = worldIn.getBlockState(pos.offset(EnumFacing.WEST)).getBlock()

        if (idXMin !== frameID && idXMax !== frameID && idXMin !== sealID && idXMax !== sealID) {
            connection = EnumAirLockSealConnection.X
        } else {
            var adjacentCount: Int = 0

            for (dir: EnumFacing in EnumFacing.HORIZONTALS) {
                val blockID: Block = worldIn.getBlockState(pos.offset(dir)).getBlock()

                if (blockID === GCBlocks.airLockFrame || blockID === GCBlocks.airLockSeal) {
                    adjacentCount++
                }
            }

            if (adjacentCount == 4) {
                connection = EnumAirLockSealConnection.FLAT
            } else {
                connection = EnumAirLockSealConnection.Z
            }
        }

        return connection
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
    }

    companion object {
        @JvmField
        val CONNECTION_TYPE: PropertyEnum<EnumAirLockSealConnection> =
            PropertyEnum.create<EnumAirLockSealConnection>("connection", EnumAirLockSealConnection::class.java)

        @JvmField
        protected val AABB_X: AxisAlignedBB = AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 1.0)

        @JvmField
        protected val AABB_Z: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 0.75)

        @JvmField
        protected val AABB_FLAT: AxisAlignedBB = AxisAlignedBB(0.0, 0.25, 0.0, 1.0, 0.75, 1.0)
    }
}
