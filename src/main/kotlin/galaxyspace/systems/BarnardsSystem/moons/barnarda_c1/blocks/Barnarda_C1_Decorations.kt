package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations.EnumBlockBarnardaC1Decoration
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Decorations
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random


open class Barnarda_C1_Decorations constructor() : Block(Material.ROCK), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c1_decorations")
        this.setDefaultState(
            this.blockState.getBaseState().setValue(
                BASIC_TYPE,
                EnumBlockBarnardaC1Decoration.ICY_DRIPSTONE_BASE
            ).setValue(UP, false)
        )
    }

    override fun getBlockHardness(state: IBlockState, world: World, pos: BlockPos): Float {
        val type: EnumBlockBarnardaC1Decoration = state.getValue(BASIC_TYPE)
        when (type) {
            else -> return this.blockHardness
        }
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state) % 5)
    }

    override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
        return (world.getBlockState(pos.above()) === BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
            .setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ) || world.getBlockState(pos.below()) === BRBlocks.BARNARDA_C1_BLOCKS.defaultBlockState()
            .setValue(
                Barnarda_C1_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC1.ICY_SUBSURFACE
            ) || world.getBlockState(pos.above()).getBlock() === this || world.getBlockState(pos.below())
            .getBlock() === this)
                && (side == EnumFacing.UP || side == EnumFacing.DOWN)
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        if (state.getValue(UP)) {
            if (world.isEmptyBlock(pos.above())) world.destroyBlock(pos, false)
        } else {
            if (world.isEmptyBlock(pos.below())) world.destroyBlock(pos, false)
        }
    }

    override fun getStateForPlacement(
        world: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase,
        hand: EnumHand
    ): IBlockState {
        val state: IBlockState = getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
        return if (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || hitY.toDouble() <= 0.5)) state.setValue(
            UP,
            false
        ) else state.setValue(UP, true)
        // return getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockBarnardaC1Decoration in EnumBlockBarnardaC1Decoration.entries) list.add(
            ItemStack(
                this,
                1,
                blockBasic.getMeta()
            )
        )
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return null
    }

    override fun canSilkHarvest(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Boolean {
        return true
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBarnardaC1Decoration = state.getValue(BASIC_TYPE)

        when (type) {
            else -> return this.getMetaFromState(state)
        }
    }

    override fun getDrops(
        drops: NonNullList<ItemStack>,
        world: IBlockAccess,
        pos: BlockPos,
        state: IBlockState,
        fortune: Int
    ) {
        val rand: Random = if (world is World) world.random else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        for (i: Int in 0..<count) {
            val item: Item = (this.getItemDropped(state, rand, fortune))!!
            if (item !== Items.AIR) {
                val type: EnumBlockBarnardaC1Decoration = state.getValue(BASIC_TYPE)

                when (type) {
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.TRANSLUCENT
    }

    enum class EnumBlockBarnardaC1Decoration(
        private val meta: Int,
        private val serializedName: String,
        private val material: Material,
        private val sound: SoundType
    ) : IStringSerializable {
        ICY_DRIPSTONE_BASE(0, "barnarda_c1_dripstone_base", Material.ROCK, SoundType.STONE),
        ICY_DRIPSTONE_LARGE(1, "barnarda_c1_dripstone_large", Material.ROCK, SoundType.STONE),
        ICY_DRIPSTONE_MEDIUM(2, "barnarda_c1_dripstone_medium", Material.ROCK, SoundType.STONE),
        ICY_DRIPSTONE_SMALL(3, "barnarda_c1_dripstone_small", Material.ROCK, SoundType.STONE),
        ICY_DRIPSTONE_THIN(4, "barnarda_c1_dripstone_thin", Material.ROCK, SoundType.STONE);

        fun getMeta(): Int {
            return this.meta
        }

        fun getMaterial(): Material {
            return this.material
        }

        fun getSoundType(): SoundType {
            return this.sound
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBarnardaC1Decoration? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE,
            EnumBlockBarnardaC1Decoration.entries[meta % 5]
        ).setValue(UP, meta - 5 >= 0)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(BASIC_TYPE)
            .getMeta() + (if (state.getValue(UP)) 5 else 0)
    }

    override fun getMaterial(state: IBlockState): Material {
        return state.getValue(BASIC_TYPE).getMaterial()
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return state.getValue(BASIC_TYPE).getMaterial().getMaterialMapColor()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE, UP)
    }

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?): SoundType {
        return state.getValue(BASIC_TYPE).getSoundType()
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBarnardaC1Decoration> =
            PropertyEnum.create<EnumBlockBarnardaC1Decoration>("type", EnumBlockBarnardaC1Decoration::class.java)

        @JvmField
        val UP: PropertyBool = PropertyBool.create("up")
    }
}
