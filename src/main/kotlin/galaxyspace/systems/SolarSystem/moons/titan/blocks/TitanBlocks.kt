package galaxyspace.systems.SolarSystem.moons.titan.blocks

import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks.EnumTitanBlocks
import galaxyspace.systems.SolarSystem.moons.titan.blocks.TitanBlocks
import micdoodle8.mods.galacticraft.core.GCItems
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
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class TitanBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("titanblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
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

        override fun getName(): String {
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
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumTitanBlocks.byMetadata(meta))!!)
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
