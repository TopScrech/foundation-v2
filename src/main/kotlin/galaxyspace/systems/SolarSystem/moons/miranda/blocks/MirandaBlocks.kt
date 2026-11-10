package galaxyspace.systems.SolarSystem.moons.miranda.blocks

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class MirandaBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("mirandablocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumMirandaBlocks in EnumMirandaBlocks.entries) {
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

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        if (state === this.getDefaultState()
                .withProperty(BASIC_TYPE, EnumMirandaBlocks.MIRANDA_ICE)
        ) return 0.98f

        return slipperiness
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }


    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumMirandaBlocks = (state.getValue(BASIC_TYPE) as EnumMirandaBlocks)
        when (type) {
            EnumMirandaBlocks.MIRANDA_ICE -> return Items.AIR
            EnumMirandaBlocks.MIRANDA_DOLOMITE_ORE -> return GSItems.BASIC
            EnumMirandaBlocks.MIRANDA_DIAMOND_ORE -> return Items.DIAMOND
            EnumMirandaBlocks.MIRANDA_QUARTZ_ORE -> return Items.QUARTZ
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun getDrops(
        drops: NonNullList<ItemStack>,
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ) {
        val rand: Random = if (world is World) world.rand else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        for (i: Int in 0..<count) {
            val item: Item = this.getItemDropped(state, rand, fortune)
            if (item !== Items.AIR) {
                val type: EnumMirandaBlocks = (state.getValue(BASIC_TYPE) as EnumMirandaBlocks)

                when (type) {
                    EnumMirandaBlocks.MIRANDA_DOLOMITE_ORE -> drops.add(ItemStack(item, 1, 3))
                    EnumMirandaBlocks.MIRANDA_DIAMOND_ORE -> drops.add(ItemStack(item, 1, 0))
                    EnumMirandaBlocks.MIRANDA_QUARTZ_ORE -> drops.add(ItemStack(item, 1, 0))
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumMirandaBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        MIRANDA_GRUNT(0, "miranda_grunt"),
        MIRANDA_SUBGRUNT(1, "miranda_subgrunt"),
        MIRANDA_STONE(2, "miranda_stone"),

        MIRANDA_IRON_ORE(3, "miranda_iron_ore"),
        MIRANDA_DOLOMITE_ORE(4, "miranda_dolomite_ore"),
        MIRANDA_DIAMOND_ORE(5, "miranda_diamond_ore"),
        MIRANDA_QUARTZ_ORE(6, "miranda_quartz_ore"),
        MIRANDA_COBALT_ORE(7, "miranda_cobalt_ore"),
        MIRANDA_NICKEL_ORE(8, "miranda_nickel_ore"),

        MIRANDA_GRUNT_2(9, "miranda_grunt_2"),
        MIRANDA_GRUNT_3(10, "miranda_grunt_3"),
        MIRANDA_SUBGRUNT_2(11, "miranda_subgrunt_2"),
        MIRANDA_SUBGRUNT_3(12, "miranda_subgrunt_3"),
        MIRANDA_STONE_2(13, "miranda_stone_2"),
        MIRANDA_STONE_3(14, "miranda_stone_3"),
        MIRANDA_ICE(15, "miranda_ice");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumMirandaBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumMirandaBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumMirandaBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumMirandaBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumMirandaBlocks> =
            PropertyEnum.create<EnumMirandaBlocks>("type", EnumMirandaBlocks::class.java)
    }
}
