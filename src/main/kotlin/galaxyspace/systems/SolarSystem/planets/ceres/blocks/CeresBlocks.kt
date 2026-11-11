package galaxyspace.systems.SolarSystem.planets.ceres.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks.EnumCeresBlocks
import galaxyspace.systems.SolarSystem.planets.ceres.blocks.CeresBlocks
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
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

open class CeresBlocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("ceresblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @OnlyIn(Dist.CLIENT)
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

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
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
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumCeresBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumCeresBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        if ((world)!!.getBlockState(pos) === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumCeresBlocks.CERES_GRUNT)
        ) return true

        return false
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumCeresBlocks> =
            PropertyEnum.create<EnumCeresBlocks>("type", EnumCeresBlocks::class.java)
    }
}
