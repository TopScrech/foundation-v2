package galaxyspace.systems.SolarSystem.planets.mercury.blocks

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.planets.mercury.blocks.MercuryBlocks.EnumBlockMercury
import galaxyspace.systems.SolarSystem.planets.mercury.blocks.MercuryBlocks
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
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
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class MercuryBlocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock,
    IDetectableResource {
    init {
        this.setTranslationKey("mercuryblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)


        // GSBlocks.GS_BLOCKS.add(this);
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

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockMercury in EnumBlockMercury.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockMercury = (state.getValue(BASIC_TYPE) as EnumBlockMercury)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isValueable(metadata: IBlockState?): Boolean {
        return false
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        if ((world)!!.getBlockState(pos) === this.getDefaultState()
                .withProperty(BASIC_TYPE, EnumBlockMercury.SURFACE)
        ) return true

        return false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockMercury(private val meta: Int, private val serializedName: String) : IStringSerializable {
        SURFACE(0, "mercury_surface"),
        SUBSURFACE(1, "mercury_subsurface"),
        STONE(2, "mercury_stone"),
        NICKEL_ORE(3, "mercury_nickel_ore"),
        IRON_ORE(4, "mercury_iron_ore"),
        MAGNESIUM_ORE(5, "mercury_magnesium_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockMercury> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockMercury? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockMercury.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockMercury).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockMercury> =
            PropertyEnum.create<EnumBlockMercury>("type", EnumBlockMercury::class.java)
    }
}
