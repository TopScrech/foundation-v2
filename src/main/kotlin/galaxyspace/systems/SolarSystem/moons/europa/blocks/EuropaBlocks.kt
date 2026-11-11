package galaxyspace.systems.SolarSystem.moons.europa.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks.EnumEuropaBlocks
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks
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

open class EuropaBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("europablocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
        this.setHardness(0.4f)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumEuropaBlocks in EnumEuropaBlocks.entries) {
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
        when (getMetaFromState(state)) {
            3 -> return Items.EMERALD
            4 -> return (GCItems.basicItem)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            3 -> return 0
            4 -> return 2
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        if (state.getValue(BASIC_TYPE) == EnumEuropaBlocks.EUROPA_BROWN_ICE) return 0.8f

        return super.getSlipperiness(state, world, pos, entity)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumEuropaBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        EUROPA_GRUNT(0, "europa_grunt"),
        EUROPA_STONE(1, "europa_stone"),
        EUROPA_BROWN_ICE(2, "europa_brown_ice"),
        EUROPA_EMERALD_ORE(3, "europa_emerald_ore"),
        EUROPA_SILICON_ORE(4, "europa_silicon_ore"),
        EUROPA_ALUMINUM_ORE(5, "europa_aluminum_ore"),
        EUROPA_SURFACE_ICE2(6, "europa_surface_ice_2");


        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumEuropaBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumEuropaBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumEuropaBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumEuropaBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumEuropaBlocks> =
            PropertyEnum.create<EnumEuropaBlocks>("type", EnumEuropaBlocks::class.java)
    }
}
