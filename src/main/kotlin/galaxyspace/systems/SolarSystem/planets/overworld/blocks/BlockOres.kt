package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres.EnumBlockOres
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockOres
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
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockOres constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("gsores")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockOres in EnumBlockOres.entries) {
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
        val type: EnumBlockOres = (state.getValue(BASIC_TYPE) as EnumBlockOres)
        when (type) {
            EnumBlockOres.URANIUM -> return GSItems.BASIC
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
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
                val type: EnumBlockOres = (state.getValue(BASIC_TYPE) as EnumBlockOres)

                when (type) {
                    EnumBlockOres.URANIUM -> drops.add(ItemStack(item, 1, 15))
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockOres(private val meta: Int, private val serializedName: String) : IStringSerializable {
        COBALT(0, "cobaltum_ore"),
        NICKEL(1, "nickel_ore"),
        URANIUM(2, "uranium_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockOres> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockOres? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockOres.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockOres).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockOres> =
            PropertyEnum.create<EnumBlockOres>("type", EnumBlockOres::class.java)
    }
}
