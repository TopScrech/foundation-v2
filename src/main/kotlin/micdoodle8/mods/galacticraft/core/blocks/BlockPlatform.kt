package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockPlatform constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), IPartialSealableBlock,
    IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    enum class EnumCorner(private val meta: Int, private val serializedName: String) : IStringSerializable {
        NONE(0, "none"),
        NW(1, "sw"),
        SW(2, "nw"),
        NE(3, "se"),
        SE(
            4,
            "ne"
        ); // Yes these labels are the wrong way round, n should be s!  But the BlockState model is hard-coded to work with this as it is.

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumCorner> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumCorner? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setResistance(10.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
        this.setDefaultState(
            this.blockState.getBaseState().setValue(CORNER, EnumCorner.NONE)
        )
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    private fun checkAxis(worldIn: World, pos: BlockPos, block: Block?, facing: EnumFacing): Boolean {
        var sameCount: Int = 0
        for (i: Int in 1..2) {
            val bs: IBlockState = worldIn.getBlockState(pos.offset(facing, i))
            if (bs.getBlock() === block && bs.getValue(CORNER) == EnumCorner.NONE) {
                sameCount++
            }
        }

        return sameCount > 1
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        val id: Block? = GCBlocks.platform

        if (checkAxis(worldIn, pos, id, EnumFacing.EAST) ||
            checkAxis(worldIn, pos, id, EnumFacing.WEST) ||
            checkAxis(worldIn, pos, id, EnumFacing.NORTH) ||
            checkAxis(worldIn, pos, id, EnumFacing.SOUTH)
        ) {
            return false
        }

        if (worldIn.getBlockState(pos.offset(EnumFacing.DOWN))
                .getBlock() === GCBlocks.platform && side == EnumFacing.UP
        ) {
            return false
        } else {
            return this.canPlaceBlockAt(worldIn, pos)
        }
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

    @OnlyIn(Dist.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    override fun doesSideBlockRendering(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        face: EnumFacing
    ): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityPlatform(meta)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val var9: TileEntity? = worldIn.getBlockEntity(pos)

        if (var9 is TileEntityPlatform) {
            var9.onDestroy(var9)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return direction == EnumFacing.UP
    }

    override fun damageDropped(state: IBlockState): Int {
        return 0
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(CORNER, (EnumCorner.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(CORNER) as EnumCorner).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, CORNER)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.DECORATION
    }

    override fun addCollisionBoxToList(
        state: IBlockState,
        worldIn: World,
        pos: BlockPos,
        mask: AxisAlignedBB,
        list: MutableList<AxisAlignedBB?>,
        entityIn: Entity?,
        p_185477_7_: Boolean
    ) {
        if (ignoreCollisionTests) return
        val te: TileEntity? = worldIn.getBlockEntity(pos)
        if (te is TileEntityPlatform) {
            if (te.noCollide()) return
        }
        val axisalignedbb: AxisAlignedBB = this.getCollisionBoundingBox(state, worldIn, pos)!!.offset(pos)

        if (axisalignedbb != null && mask.intersects(axisalignedbb)) {
            list.add(axisalignedbb)
        }
    }

    override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        if (world is World && world.provider is IZeroGDimension) return BOUNDING_BOX_ZEROG
        return BOUNDING_BOX
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSelectedBoundingBox(bs: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        val te: TileEntity? = worldIn.getBlockEntity(pos)
        if (te is TileEntityPlatform) {
            if (te.noCollide()) {
                if (bs.getBlock() === this && bs.getValue(CORNER) == EnumCorner.SE) return AxisAlignedBB(
                    pos.getX().toDouble() + 9 / 16.0,
                    pos.getY().toDouble(),
                    pos.getZ().toDouble() + 9 / 16.0,
                    pos.getX().toDouble() + 1.0,
                    pos.getY().toDouble() + HEIGHT,
                    pos.getZ().toDouble() + 1.0
                )
                else return AxisAlignedBB(
                    pos.getX().toDouble(),
                    pos.getY().toDouble(),
                    pos.getZ().toDouble(),
                    pos.getX().toDouble() + 7 / 16.0,
                    pos.getY().toDouble() + HEIGHT,
                    pos.getZ().toDouble() + 7 / 16.0
                )
            }
        }
        return super.getSelectedBoundingBox(bs, worldIn, pos)
    }

    companion object {
        @JvmField
        val CORNER: PropertyEnum<EnumCorner> = PropertyEnum.create<EnumCorner>("type", EnumCorner::class.java)

        const val HEIGHT: Float = 0.875f

        @JvmField
        protected val BOUNDING_BOX: AxisAlignedBB = AxisAlignedBB(0.0, 6 / 16.0, 0.0, 1.0, HEIGHT.toDouble(), 1.0)

        @JvmField
        protected val BOUNDING_BOX_ZEROG: AxisAlignedBB = AxisAlignedBB(0.0, 6 / 16.0, 0.0, 1.0, 1.0, 1.0)

        @JvmField
        var ignoreCollisionTests: Boolean = false
    }
}
