package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockDecoMetals.EnumBlockDecoMetals
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockDecoMetals
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockDecoMetals constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("decoblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
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

        override fun getName(): String {
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
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockDecoMetals.byMetadata(meta))!!)
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
