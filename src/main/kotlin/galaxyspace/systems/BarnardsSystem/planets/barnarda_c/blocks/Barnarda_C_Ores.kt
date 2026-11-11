package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores.EnumBlockBarnardaCOres
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Ores
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

open class Barnarda_C_Ores constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_ores")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel(
            "pickaxe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaCOres.COAL)
        )
        this.setHarvestLevel(
            "pickaxe",
            1,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaCOres.IRON)
        )
        this.setHarvestLevel(
            "pickaxe",
            1,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.COPPER
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            1,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaCOres.TIN)
        )
        this.setHarvestLevel(
            "pickaxe",
            1,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.LAPIS
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaCOres.GOLD)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.REDSTONE
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.DIAMOND
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.NICKEL
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.COBALT
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.QUARTZ
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaCOres.SILICON
            )
        )
    }

    override fun getBlockHardness(state: IBlockState, world: World, pos: BlockPos): Float {
        val type: EnumBlockBarnardaCOres =
            (state.getValue(BASIC_TYPE) as EnumBlockBarnardaCOres)
        when (type) {
            else -> return 1.0f
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockBarnardaCOres in EnumBlockBarnardaCOres.entries) {
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
        val type: EnumBlockBarnardaCOres =
            (state.getValue(BASIC_TYPE) as EnumBlockBarnardaCOres)
        when (type) {
            EnumBlockBarnardaCOres.COAL -> return Items.COAL
            EnumBlockBarnardaCOres.REDSTONE -> return Items.REDSTONE
            EnumBlockBarnardaCOres.LAPIS -> return Items.DYE
            EnumBlockBarnardaCOres.DIAMOND -> return Items.DIAMOND
            EnumBlockBarnardaCOres.SILICON -> return (GCItems.basicItem)!!
            EnumBlockBarnardaCOres.QUARTZ -> return Items.QUARTZ
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBarnardaCOres =
            (state.getValue(BASIC_TYPE) as EnumBlockBarnardaCOres)
        when (type) {
            EnumBlockBarnardaCOres.COAL, EnumBlockBarnardaCOres.REDSTONE, EnumBlockBarnardaCOres.DIAMOND, EnumBlockBarnardaCOres.QUARTZ -> return 0
            EnumBlockBarnardaCOres.LAPIS -> return 4
            EnumBlockBarnardaCOres.SILICON -> return 2
            else -> return this.getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        var bonus: Int = 0
        var default_count: Int = 0

        val type: EnumBlockBarnardaCOres = state.getValue(BASIC_TYPE)

        if (type == EnumBlockBarnardaCOres.REDSTONE) {
            bonus = 2
            default_count = 4
        }

        if (type == EnumBlockBarnardaCOres.SILICON) {
            bonus = 1
        }

        if (type == EnumBlockBarnardaCOres.LAPIS) {
            bonus = 2
            default_count = 8
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

    /*
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World)world).rand : RANDOM;

        int count = quantityDropped(state, fortune, rand);
        for (int i = 0; i < count; i++)
        {
            Item item = this.getItemDropped(state, rand, fortune);
            if (item != Items.AIR)
            {
            	EnumBlockBarnardaCOres type = ((EnumBlockBarnardaCOres) state.getValue(BASIC_TYPE));
            	
            	switch (type)
                {
                	default:
                		drops.add(new ItemStack(item, 1, this.damageDropped(state)));
                		break;
                }
               
            }
        }
    }
	*/
    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    enum class EnumBlockBarnardaCOres(private val meta: Int, private val serializedName: String) : IStringSerializable {
        COAL(0, "coal_ore"),
        IRON(1, "iron_ore"),
        GOLD(2, "gold_ore"),
        REDSTONE(3, "redstone_ore"),
        LAPIS(4, "lapis_ore"),
        DIAMOND(5, "diamond_ore"),
        SILICON(6, "silicon_ore"),
        COPPER(7, "copper_ore"),
        TIN(8, "tin_ore"),
        ALUMINUM(9, "aluminum_ore"),
        QUARTZ(10, "quartz_ore"),
        COBALT(11, "cobaltum_ore"),
        NICKEL(12, "nickel_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockBarnardaCOres> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBarnardaCOres? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE,
            (EnumBlockBarnardaCOres.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockBarnardaCOres).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBarnardaCOres> =
            PropertyEnum.create<EnumBlockBarnardaCOres>("type", EnumBlockBarnardaCOres::class.java)
    }
}
