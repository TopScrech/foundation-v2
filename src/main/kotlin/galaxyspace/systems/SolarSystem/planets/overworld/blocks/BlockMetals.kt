package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMetals.EnumBlockMetals
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMetals
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

open class BlockMetals constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("blocksmetals")
        this.setSoundType(SoundType.METAL)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockMetals in EnumBlockMetals.entries) {
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
        val type: EnumBlockMetals = (state.getValue(BASIC_TYPE) as EnumBlockMetals)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockMetals(private val meta: Int, private val serializedName: String) : IStringSerializable {
        COBALT_BLOCK(0, "cobalt_block"),
        NICKEL_BLOCK(1, "nickel_block"),
        MAGNESIUM_BLOCK(2, "magnesium_block");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockMetals> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockMetals? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockMetals.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockMetals).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockMetals> =
            PropertyEnum.create<EnumBlockMetals>("type", EnumBlockMetals::class.java)
    }
}
