package galaxyspace.systems.SolarSystem.moons.ganymede.blocks

import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks.EnumGanymedeBlocks
import galaxyspace.systems.SolarSystem.moons.ganymede.blocks.GanymedeBlocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
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
import java.util.Random

open class GanymedeBlocks constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("ganymedeblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumGanymedeBlocks in EnumGanymedeBlocks.entries) {
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

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            3 -> return 4
            else -> return getMetaFromState(state)
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        when (getMetaFromState(state)) {
            3 -> return (AsteroidsItems.basicItem)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumGanymedeBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GANYMEDE_GRUNT(0, "ganymede_grunt"),
        GANYMEDE_STONE(1, "ganymede_stone"),
        GANYMEDE_MAGNESIUM_ORE(2, "ganymede_magnesium_ore"),
        GANYMEDE_TITANIUM_ORE(3, "ganymede_titanium_ore");


        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumGanymedeBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumGanymedeBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumGanymedeBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumGanymedeBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumGanymedeBlocks> =
            PropertyEnum.create<EnumGanymedeBlocks>("type", EnumGanymedeBlocks::class.java)
    }
}
