package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockSlab
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
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

open class BlockSlabGC : BlockSlab, ISortableBlock {
    constructor(name: String?, material: Material) : super(material) {
        this.setTranslationKey(name)
        this.useNeighborBrightness = true
    }

    constructor(material: Material) : super(material)

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (i: Int in 0..<(if (GalacticraftCore.isPlanetsLoaded) 7 else 4)) {
            list.add(ItemStack(this, 1, i))
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state) and 7
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(GCBlocks.slabGCHalf)
    }

    override fun quantityDropped(rand: Random): Int {
        return if (this.isDouble()) 2 else 1
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return if (this.isDouble()) null else GalacticraftCore.galacticraftBlocksTab
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val block: Block = worldIn.getBlockState(pos).getBlock()

        if (block !is BlockSlabGC)  //This will prevent game crashing when harvest block
        {
            return 0f
        }

        when (this.getMetaFromState(worldIn.getBlockState(pos))) {
            2, 3 -> return 1.5f
            else -> return 2.0f
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)) and 7)
    }

    public override fun getTranslationKey(meta: Int): String {
        val type: BlockType = (this.getStateFromMeta(meta).getValue(VARIANT) as BlockType)
        return type.getLangName()
    }

    override fun isDouble(): Boolean {
        return false
    }

    override fun getVariantProperty(): IProperty<*> {
        return VARIANT
    }

    override fun getTypeForItem(stack: ItemStack): Comparable<*> {
        return BlockType.Companion.byMetadata(stack.getMetadata() and 7)!!
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var state: IBlockState = this.getDefaultState()
            .withProperty(VARIANT, (BlockType.Companion.byMetadata(meta and 7))!!)

        if (!this.isDouble()) {
            state = state.withProperty(
                HALF,
                if ((meta and 8) == 0) EnumBlockHalf.BOTTOM else EnumBlockHalf.TOP
            )
        }
        return state
    }

    override fun getMetaFromState(state: IBlockState): Int {
        val b0: Byte = 0
        var i: Int = b0.toInt() or (state.getValue(VARIANT) as BlockType).getMetadata()

        if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
            i = i or 8
        }
        return i
    }

    override fun createBlockState(): BlockStateContainer {
        return if (this.isDouble()) BlockStateContainer(this, VARIANT) else BlockStateContainer(this, HALF, VARIANT)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.SLABS
    }

    enum class BlockType(private val meta: Int, private val langName: String) : IStringSerializable {
        TIN_SLAB_1(0, "tin_slab_1"),
        TIN_SLAB_2(1, "tin_slab_2"),
        MOON_STONE_SLAB(2, "moon_slab"),
        MOON_DUNGEON_BRICK_SLAB(3, "moon_bricks_slab"),
        MARS_COBBLESTONE_SLAB(4, "mars_slab"),
        MARS_DUNGEON_SLAB(5, "mars_bricks_slab"),
        ASTEROIDS_DECO(6, "asteroids_slab");

        override fun toString(): String {
            return this.getName()
        }

        override fun getName(): String {
            return this.langName
        }

        fun getMetadata(): Int {
            return this.meta
        }

        fun getLangName(): String {
            return langName
        }

        companion object {
            private val META_LOOKUP: Array<BlockType> = (arrayOfNulls<BlockType>(entries.toTypedArray().size) as Array<BlockSlabGC.BlockType>)

            @JvmStatic
            fun byMetadata(meta: Int): BlockType? {
                var meta: Int = meta
                if (meta < 0 || meta >= META_LOOKUP.size) {
                    meta = 0
                }
                return META_LOOKUP[meta]
            }

            init {
                val var0: Array<BlockType> = entries.toTypedArray()

                for (var3: BlockType in var0) {
                    META_LOOKUP[var3.getMetadata()] = var3
                }
            }
        }
    }

    companion object {
        @JvmField
        val VARIANT: PropertyEnum<BlockType> = PropertyEnum.create<BlockType>("variant", BlockType::class.java)
    }
}
