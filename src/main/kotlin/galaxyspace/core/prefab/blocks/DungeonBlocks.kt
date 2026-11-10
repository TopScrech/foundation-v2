package galaxyspace.core.prefab.blocks

import galaxyspace.core.prefab.blocks.DungeonBlocks.EnumDungeonBlocks
import galaxyspace.core.prefab.blocks.DungeonBlocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class DungeonBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("dungeon_blocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 3)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumDungeonBlocks in EnumDungeonBlocks.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumDungeonBlocks = (state.getValue(BASIC_TYPE) as EnumDungeonBlocks)
        when (type) {
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumDungeonBlocks = (state.getValue(BASIC_TYPE) as EnumDungeonBlocks)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return true
    }

    override fun isBlockNormalCube(state: IBlockState): Boolean {
        return true
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return true
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullBlock(state: IBlockState): Boolean {
        return false
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.SOLID
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumDungeonBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        CERES_BRICKS(0, "ceres_bricks"),
        IO_BRICKS(1, "io_bricks");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumDungeonBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumDungeonBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumDungeonBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumDungeonBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumDungeonBlocks> =
            PropertyEnum.create<EnumDungeonBlocks>("type", EnumDungeonBlocks::class.java)
    }
}
