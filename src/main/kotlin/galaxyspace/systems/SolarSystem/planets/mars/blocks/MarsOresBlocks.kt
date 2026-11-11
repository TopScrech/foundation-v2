package galaxyspace.systems.SolarSystem.planets.mars.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks.EnumMarsOresBlocks
import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks
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

open class MarsOresBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("marsores")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumMarsOresBlocks in EnumMarsOresBlocks.entries) {
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

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        when (getMetaFromState(state)) {
            0 -> return Items.DIAMOND
            2 -> return Items.COAL
            3 -> return Items.REDSTONE
            4 -> return (GCItems.basicItem)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            0 -> return 0
            2 -> return 0
            3 -> return 0
            4 -> return 2
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        var bonus: Int = 0

        if (this.getMetaFromState(state) == 3) {
            bonus = 4
        }

        if (this.getMetaFromState(state) == 4) {
            bonus = 2
        }

        if (fortune > 0 && Item.getItemFromBlock(this) !== this.getItemDropped(state, random, fortune)) {
            var j: Int = random.nextInt(fortune + 2) - 1

            if (j < 0) {
                j = 0
            }

            return this.quantityDropped(random) * (j + 1) + bonus
        } else {
            return this.quantityDropped(random) + bonus
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumMarsOresBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        MARS_DIAMOND(0, "mars_diamond"),
        MARS_GOLD(1, "mars_gold"),
        MARS_COAL(2, "mars_coal"),
        MARS_REDSTONE(3, "mars_redstone"),
        MARS_SILICON(4, "mars_silicon"),
        MARS_ALUMINUM(5, "mars_aluminum");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumMarsOresBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumMarsOresBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumMarsOresBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumMarsOresBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumMarsOresBlocks> =
            PropertyEnum.create<EnumMarsOresBlocks>("type", EnumMarsOresBlocks::class.java)
    }
}
