package galaxyspace.systems.SolarSystem.planets.pluto.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks.EnumPlutoBlocks
import galaxyspace.systems.SolarSystem.planets.pluto.blocks.PlutoBlocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class PlutoBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("plutoblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumPlutoBlocks in EnumPlutoBlocks.entries) {
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

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumPlutoBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        PLUTO_GRUNT_1(0, "pluto_grunt_1"),
        PLUTO_GRUNT_2(1, "pluto_grunt_2"),
        PLUTO_GRUNT_3(2, "pluto_grunt_3"),
        PLUTO_GRUNT_4(3, "pluto_grunt_4"),
        PLUTO_SUBGRUNT(4, "pluto_subgrunt"),
        PLUTO_STONE(5, "pluto_stone");


        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumPlutoBlocks? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumPlutoBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumPlutoBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumPlutoBlocks> =
            PropertyEnum.create<EnumPlutoBlocks>("type", EnumPlutoBlocks::class.java)
    }
}
