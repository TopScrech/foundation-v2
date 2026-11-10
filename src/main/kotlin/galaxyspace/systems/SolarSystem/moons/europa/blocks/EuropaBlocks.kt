package galaxyspace.systems.SolarSystem.moons.europa.blocks

import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks.EnumEuropaBlocks
import galaxyspace.systems.SolarSystem.moons.europa.blocks.EuropaBlocks
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

open class EuropaBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("europablocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
        this.setHardness(0.4f)
    }

    @SideOnly(Side.CLIENT)
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

        override fun getName(): String {
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
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumEuropaBlocks.byMetadata(meta))!!)
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
