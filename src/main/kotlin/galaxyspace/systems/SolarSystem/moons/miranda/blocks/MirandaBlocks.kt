package galaxyspace.systems.SolarSystem.moons.miranda.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks.EnumMirandaBlocks
import galaxyspace.systems.SolarSystem.moons.miranda.blocks.MirandaBlocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class MirandaBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("mirandablocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
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
        if (state === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumMirandaBlocks.MIRANDA_ICE)
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
        val rand: Random = if (world is World) world.random else RANDOM

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

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
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
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumMirandaBlocks.byMetadata(meta))!!)
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
