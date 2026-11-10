package galaxyspace.systems.SolarSystem.planets.mars.blocks

import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks.EnumMarsOresBlocks
import galaxyspace.systems.SolarSystem.planets.mars.blocks.MarsOresBlocks
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

open class MarsOresBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("marsores")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
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

        override fun getName(): String {
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
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumMarsOresBlocks.byMetadata(meta))!!)
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
