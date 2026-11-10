package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import galaxyspace.core.GSBlocks
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockFutureGlasses.EnumBlockFutureGlass
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockFutureGlasses
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockGlass
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockFutureGlasses constructor() : BlockGlass(Material.GLASS, false), ISortableBlock {
    init {
        this.setTranslationKey("futureglass")
        this.setSoundType(SoundType.GLASS)


        //this.setCreativeTab(GSCreativeTabs.GSBlocksTab);

        // GSBlocks.GS_BLOCKS.add(this);
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return MapColor.AIR
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockFutureGlass in EnumBlockFutureGlass.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(
            BASIC_TYPE,
            (EnumBlockFutureGlass.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockFutureGlass).getMeta()
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
        val type: EnumBlockFutureGlass = (state.getValue(BASIC_TYPE) as EnumBlockFutureGlass)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.TRANSLUCENT
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockFutureGlass(private val meta: Int, private val serializedName: String) : IStringSerializable {
        F_GLASS_BLACK(0, "futureglass_black"),
        F_GLASS_RED(1, "futureglass_red"),
        F_GLASS_GREEN(2, "futureglass_green"),
        F_GLASS_BROWN(3, "futureglass_brown"),
        F_GLASS_BLUE(4, "futureglass_blue"),
        F_GLASS_PURPLE(5, "futureglass_purple"),
        F_GLASS_CYAN(6, "futureglass_cyan"),
        F_GLASS_LIGHTGRAY(7, "futureglass_lightgray"),
        F_GLASS_GRAY(8, "futureglass_gray"),
        F_GLASS_PINK(9, "futureglass_pink"),
        F_GLASS_LIME(10, "futureglass_lime"),
        F_GLASS_YELLOW(11, "futureglass_yellow"),
        F_GLASS_LIGHTBLUE(12, "futureglass_lightblue"),
        F_GLASS_MAGNETA(13, "futureglass_magneta"),
        F_GLASS_ORANGE(14, "futureglass_orange"),
        F_GLASS_WHITE(15, "futureglass_white");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockFutureGlass> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockFutureGlass? {
                return values[meta % values.size]
            }
        }
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockFutureGlass> =
            PropertyEnum.create<EnumBlockFutureGlass>("type", EnumBlockFutureGlass::class.java)
    }
}
