package galaxyspace.systems.SolarSystem.planets.ceres.blocks

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
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

open class CeresBlocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock {
    init {
        this.setTranslationKey("ceresblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumCeresBlocks in EnumCeresBlocks.entries) {
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
            2 -> return GSItems.BASIC
            3 -> return (GCItems.meteoricIronRaw)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            2 -> return 3
            3 -> return 0
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        var bonus: Int = 0

        if (this.getMetaFromState(state) == 3) {
            bonus = 1
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
    enum class EnumCeresBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        CERES_GRUNT(0, "ceres_grunt"),
        CERES_SUBGRUNT(1, "ceres_subgrunt"),
        CERES_DOLOMITE_ORE(2, "ceres_dolomite_ore"),
        CERES_METEORICIRON_ORE(3, "ceres_meteoriciron_ore"),
        CERES_DUNGEON_TOP(4, "ceres_dungeon_top"),
        CERES_DUNGEON_FLOOR(5, "ceres_dungeon_floor");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumCeresBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumCeresBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumCeresBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumCeresBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        if ((world)!!.getBlockState(pos) === this.getDefaultState()
                .withProperty(BASIC_TYPE, EnumCeresBlocks.CERES_GRUNT)
        ) return true

        return false
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumCeresBlocks> =
            PropertyEnum.create<EnumCeresBlocks>("type", EnumCeresBlocks::class.java)
    }
}
