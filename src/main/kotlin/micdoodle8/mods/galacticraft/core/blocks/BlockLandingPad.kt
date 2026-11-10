package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFuelerSingle
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPadSingle
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockLandingPad constructor(assetName: String?) : BlockAdvancedTile(Material.IRON),
    IPartialSealableBlock, IShiftDescription, ISortableBlock {
    enum class EnumLandingPadType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ROCKET_PAD(0, "rocket"),
        BUGGY_PAD(1, "buggy");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumLandingPadType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumLandingPadType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setResistance(10.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i: Int in 0..1) {
            list.add(ItemStack(this, 1, i))
        }
    }

    private fun checkAxis(worldIn: World, pos: BlockPos, block: Block?, facing: EnumFacing): Boolean {
        var sameCount: Int = 0
        for (i: Int in 1..3) {
            if (worldIn.getBlockState(pos.offset(facing, i)).getBlock() === block) {
                sameCount++
            }
        }

        return sameCount < 3
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        val id: Block? = GCBlocks.landingPad

        if (!checkAxis(worldIn, pos, id, EnumFacing.EAST) || !checkAxis(
                worldIn,
                pos,
                id,
                EnumFacing.WEST
            ) || !checkAxis(worldIn, pos, id, EnumFacing.NORTH) || !checkAxis(worldIn, pos, id, EnumFacing.SOUTH)
        ) {
            return false
        }

        if (worldIn.getBlockState(pos.offset(EnumFacing.DOWN))
                .getBlock() === GCBlocks.landingPad && side == EnumFacing.UP
        ) {
            return false
        } else {
            return this.canPlaceBlockAt(worldIn, pos)
        }
    }

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

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        when (meta) {
            0 -> return TileEntityLandingPadSingle()
            1 -> return TileEntityBuggyFuelerSingle()
            else -> return null
        }
    }

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return direction == EnumFacing.UP
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getShiftDescription(meta: Int): String? {
        if (meta == 0) {
            return GCCoreUtil.translate(this.getTranslationKey() + ".description")
        }
        return GCCoreUtil.translate("tile.buggy_pad.description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(
            PAD_TYPE,
            (EnumLandingPadType.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(PAD_TYPE) as EnumLandingPadType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PAD_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.PAD
    }

    companion object {
        @JvmField
        val PAD_TYPE: PropertyEnum<EnumLandingPadType> =
            PropertyEnum.create<EnumLandingPadType>("type", EnumLandingPadType::class.java)

        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)
    }
}
