package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

/**
 * Metadata: 3 = Tin Decoration Block 1; 4 = Tin Decoration Block 2; 5 = Copper
 * Ore; 6 = Tin Ore; 7 = Aluminium Ore; 8 = Silicon Ore; 9 = Copper Block;
 * 10 = Tin Block;  11 = Aluminium Block;  12 = Meteoric Iron Block; 13 = Silicon Block;
 */
open class BlockBasic constructor(assetName: String?) : Block(Material.ROCK), IDetectableResource, ISortableBlock, LegacyBlockMetadata {
    enum class EnumBlockBasic(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ALUMINUM_DECORATION_BLOCK_0(3, "deco_block_0"),
        ALUMINUM_DECORATION_BLOCK_1(4, "deco_block_1"),
        ORE_COPPER(5, "ore_copper_gc"),
        ORE_TIN(6, "ore_tin_gc"),
        ORE_ALUMINUM(7, "ore_aluminum_gc"),
        ORE_SILICON(8, "ore_silicon"),
        DECO_BLOCK_COPPER(9, "block_copper_gc"),
        DECO_BLOCK_TIN(10, "block_tin_gc"),
        DECO_BLOCK_ALUMINUM(11, "block_aluminum_gc"),
        DECO_BLOCK_METEOR_IRON(12, "block_meteoric_iron_gc"),
        DECO_BLOCK_SILICON(13, "block_silicon_gc");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBasic? {
                return entries[meta - 3]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.blockResistance = 15f
        this.setDefaultState(
            this.blockState.getBaseState()
                .setValue(BASIC_TYPE, EnumBlockBasic.ALUMINUM_DECORATION_BLOCK_0)
        )
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        when (getMetaFromState(state)) {
            8 -> return (GCItems.basicItem)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        when (getMetaFromState(state)) {
            8 -> return 2
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        var bonus: Int = 0
        if (ConfigManagerCore.quickMode && this.getMetaFromState(state) == 8) {
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

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val metadata: Int = getMetaFromState(world.getBlockState(pos))

        if (metadata < 5) {
            return 2.0f
            //Decoration blocks are soft, like cauldrons or wood 
        } else if (metadata == 12) {
            return 8.0f
            //Meteoric Iron is tougher than diamond
        } else if (metadata > 8) {
            return 6.0f
            //Blocks of metal are tough - like diamond blocks in vanilla
        }

        return super.getExplosionResistance(world, pos, exploder, explosion)
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val metadata: Int = getMetaFromState(blockState)

        if (metadata == 5 || metadata == 6) {
            return 5.0f
        }

        if (metadata == 7) {
            return 6.0f
        }

        if (metadata == 8) {
            return 3.0f
        }

        return this.blockHardness
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (var4: Int in 3..13) {
            list.add(ItemStack(this, 1, var4))
        }
    }

    override fun isValueable(state: IBlockState?): Boolean {
        when (this.getMetaFromState((state)!!)) {
            5, 6, 7, 8 -> return true
        }
        return false
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val metadata: Int = getMetaFromState(world.getBlockState(pos))

        if (metadata == 8) {
            return ItemStack(Item.getItemFromBlock(this), 1, metadata)
        }

        return super.getPickBlock(state, target, world, pos, player)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockBasic.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockBasic).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        when (meta) {
            3, 4 -> return EnumSortCategoryBlock.DECORATION
            5, 6, 7, 8 -> return EnumSortCategoryBlock.ORE
            9, 10, 11, 12, 13 -> return EnumSortCategoryBlock.INGOT_BLOCK
        }
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        if (state.getBlock() !== this) return 0

        val meta: Int = this.getMetaFromState(state)
        if (meta == 8) {
            val rand: Random = if (world is World) world.random else Random()
            return MathHelper.getInt(rand, 2, 5)
        }
        return 0
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBasic> =
            PropertyEnum.create<EnumBlockBasic>("basictype", EnumBlockBasic::class.java)
    }
}
