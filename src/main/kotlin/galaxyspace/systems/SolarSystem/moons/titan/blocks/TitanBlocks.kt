package galaxyspace.systems.SolarSystem.moons.titan.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks.EnumTitanBlocks
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks
import micdoodle8.mods.galacticraft.core.GCItems
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
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class TitanBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("titanblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumTitanBlocks in EnumTitanBlocks.entries) {
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
        val type: EnumTitanBlocks = state.getValue(BASIC_TYPE)
        when (type) {
            EnumTitanBlocks.TITAN_SAPPHIRE_ORE -> return (GCItems.itemBasicMoon)!!
            EnumTitanBlocks.TITAN_EMERALD_ORE -> return Items.EMERALD
            EnumTitanBlocks.TITAN_DIAMOND_ORE -> return Items.DIAMOND
            EnumTitanBlocks.TITAN_COAL_ORE -> return Items.COAL
            EnumTitanBlocks.TITAN_LAPIS_ORE -> return Items.DYE
            EnumTitanBlocks.TITAN_REDSTONE_ORE -> return Items.REDSTONE
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumTitanBlocks = state.getValue(BASIC_TYPE)
        when (type) {
            EnumTitanBlocks.TITAN_SAPPHIRE_ORE -> return 2
            EnumTitanBlocks.TITAN_EMERALD_ORE, EnumTitanBlocks.TITAN_DIAMOND_ORE, EnumTitanBlocks.TITAN_COAL_ORE, EnumTitanBlocks.TITAN_REDSTONE_ORE -> return 0
            EnumTitanBlocks.TITAN_LAPIS_ORE -> return 4
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        var bonus: Int = 0
        var default_count: Int = 0

        val type: EnumTitanBlocks = state.getValue(BASIC_TYPE)

        if (type == EnumTitanBlocks.TITAN_SAPPHIRE_ORE) {
            bonus = 2
        }

        if (type == EnumTitanBlocks.TITAN_LAPIS_ORE) {
            bonus = 2
            default_count = 8
        }

        if (type == EnumTitanBlocks.TITAN_REDSTONE_ORE) {
            bonus = 2
            default_count = 4
        }


        if (fortune > 0 && Item.getItemFromBlock(this) !== this.getItemDropped(state, random, fortune)) {
            var j: Int = random.nextInt(fortune + 2) - 1

            if (j < 0) {
                j = 0
            }

            return (this.quantityDropped(random) + default_count) * (j + 1) + bonus
        } else {
            return this.quantityDropped(random) + default_count + random.nextInt(1 + bonus)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumTitanBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        TITAN_GRUNT(0, "titan_grunt"),
        TITAN_SUBGRUNT(1, "titan_subgrunt"),
        TITAN_STONE(2, "titan_stone"),
        TITAN_SAPPHIRE_ORE(3, "titan_sapphire_ore"),
        TITAN_EMERALD_ORE(4, "titan_emerald_ore"),
        TITAN_DIAMOND_ORE(5, "titan_diamond_ore"),
        TITAN_COAL_ORE(6, "titan_coal_ore"),
        TITAN_LAPIS_ORE(7, "titan_lapis_ore"),
        TITAN_REDSTONE_ORE(8, "titan_redstone_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumTitanBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumTitanBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumTitanBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumTitanBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumTitanBlocks> =
            PropertyEnum.create<EnumTitanBlocks>("type", EnumTitanBlocks::class.java)
    }
}
