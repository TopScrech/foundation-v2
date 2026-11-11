package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import foundation.block.LegacyBlockMetadata

import asmodeuscore.api.item.IShiftDescription
import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockAdvancedLandingPad
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityAdvLandingPadSingle
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockAdvancedLandingPad constructor() : BlockAdvancedTile(Material.IRON), IPartialSealableBlock,
    IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setResistance(10.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey("advanced_landing_pad")
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return AABB
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i: Int in 0..0) {
            list.add(ItemStack(this, 1, i))
        }
    }

    private fun checkAxis(worldIn: World, pos: BlockPos, block: Block, facing: EnumFacing): Boolean {
        var sameCount: Int = 0
        for (i: Int in 1..5) {
            if (worldIn.getBlockState(pos.offset(facing, i)).getBlock() === block) {
                sameCount++
            }
        }

        return sameCount < 5
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        val id: Block = GSBlocks.ADVANCED_LANDING_PAD_SINGLE

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
                .getBlock() === GSBlocks.ADVANCED_LANDING_PAD_SINGLE && side == EnumFacing.UP
        ) {
            return false
        } else {
            return this.canPlaceBlockAt(worldIn, pos)
        }
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        when (meta) {
            0 -> return TileEntityAdvLandingPadSingle()
            else -> return null
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

    override fun isSealed(worldIn: World?, pos: BlockPos?, direction: EnumFacing?): Boolean {
        return direction == EnumFacing.UP
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getDescription(meta: Int): String? {
        return null
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
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

    /////////////////////////////////////////////////////////////////////////////////////
    enum class EnumLandingPadType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ADVANCED_ROCKET_PAD(0, "rocket_pad");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumLandingPadType? {
                return entries[meta]
            }
        }
    }

    companion object {
        @JvmField
        val PAD_TYPE: PropertyEnum<EnumLandingPadType> =
            PropertyEnum.create<EnumLandingPadType>("type", EnumLandingPadType::class.java)

        @JvmField
        protected val AABB: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0)
    }
}
