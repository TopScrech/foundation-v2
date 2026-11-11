package galaxyspace.systems.SolarSystem.planets.haumea.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks.EnumHaumeaBlocks
import galaxyspace.systems.SolarSystem.planets.haumea.blocks.HaumeaBlocks
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

open class HaumeaBlocks constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("haumeablocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumHaumeaBlocks in EnumHaumeaBlocks.entries) {
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
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            1 -> return 2
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val bonus: Int = 0
        /*
        if(this.getMetaFromState(state) == 3)
        {
        	bonus = 4;
        }
        
        if (this.getMetaFromState(state) == 4)
        {
            bonus = 2;
        }
        
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(state, random, fortune))
        {
            int j = random.nextInt(fortune + 2) - 1;

            if (j < 0)
            {
                j = 0;
            }

            return this.quantityDropped(random) * (j + 1) + bonus;
        }
        else*/
        run {
            return this.quantityDropped(random) + bonus
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumHaumeaBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GROUND(0, "haumea_ground"),
        STONE(1, "haumea_stone"),
        COBBLESTONE(2, "haumea_cobblestone"),
        ALUMINUM_ORE(3, "haumea_aluminum_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumHaumeaBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumHaumeaBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumHaumeaBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(BASIC_TYPE).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumHaumeaBlocks> =
            PropertyEnum.create<EnumHaumeaBlocks>("type", EnumHaumeaBlocks::class.java)
    }
}
