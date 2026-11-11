package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockDecoMetals.EnumBlockDecoMetals
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockDecoMetals
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

open class BlockDecoMetals constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("decoblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockDecoMetals in EnumBlockDecoMetals.entries) {
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
        val type: EnumBlockDecoMetals = (state.getValue(BASIC_TYPE) as EnumBlockDecoMetals)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockDecoMetals(private val meta: Int, private val serializedName: String) : IStringSerializable {
        DECO_COBALT_1(0, "deco_cobaltum_1"),
        DECO_MAGNESIUM_1(1, "deco_magnesium_1"),
        DECO_NICKEL_1(2, "deco_nickel_1"),
        DECO_COOPER_1(3, "deco_copper_1"),
        DECO_COBALT_2(4, "deco_cobaltum_2"),
        DECO_MAGNESIUM_2(5, "deco_magnesium_2"),
        DECO_NICKEL_2(6, "deco_nickel_2"),
        DECO_COOPER_2(7, "deco_copper_2"),
        DECO_ALUMINUM_1(8, "deco_aluminum_1"),
        DECO_ALUMINUM_2(9, "deco_aluminum_2");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockDecoMetals> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockDecoMetals? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockDecoMetals.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockDecoMetals).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockDecoMetals> =
            PropertyEnum.create<EnumBlockDecoMetals>("type", EnumBlockDecoMetals::class.java)
    }
}
