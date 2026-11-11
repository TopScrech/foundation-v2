package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Glow_Dandelions.EnumBlockGlowDandelions
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Glow_Dandelions
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.IGrowable
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IShearable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Barnarda_C_Glow_Dandelions constructor() : BlockBush(Material.VINE), IGrowable, IShearable, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_glow_dandelions")
        this.setTickRandomly(true)
        this.setSoundType(SoundType.PLANT)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        return TALL_GRASS_AABB
    }

    override fun getCollisionBoundingBox(
        blockState: IBlockState,
        worldIn: IBlockAccess,
        pos: BlockPos
    ): AxisAlignedBB? {
        return NULL_AABB
    }

    override fun onEntityCollision(world: World, pos: BlockPos, state: IBlockState, entity: Entity) {
    }


    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        val type: EnumBlockGlowDandelions =
            (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)

        when (type) {
            else -> return Material.GROUND.getMaterialMapColor()

        }
    }

    override fun getMaterial(state: IBlockState): Material {
        val type: EnumBlockGlowDandelions =
            (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)
        when (type) {
            else -> return this.material
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val type: EnumBlockGlowDandelions =
            (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)

        when (type) {
            else -> return 5
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val type: EnumBlockGlowDandelions =
            (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)

        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockGlowDandelions in EnumBlockGlowDandelions.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun onBlockActivated(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        player: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (!world.isClientSide) {
            val type: EnumBlockGlowDandelions =
                (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)
        }
        return false
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!world.isAreaLoaded(
                pos,
                1
            )
        ) return  // Forge: prevent growing cactus from loading unloaded chunks with block update


        if (!world.isClientSide) {
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        super.neighborChanged(state, world, pos, block, fromPos)

        //canPlaceAt(state, world, pos, EnumBlockDandelions.REEDS, BRBlocks.BARNARDA_C_GRASS.defaultBlockState(), this.defaultBlockState().setValue(BASIC_TYPE, EnumBlockDandelions.REEDS));
    }

    private fun canPlaceAt(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        placer: EntityLivingBase,
        type: EnumBlockGlowDandelions,
        vararg valide: IBlockState?
    ) {
        if (!world.isClientSide && state === this.defaultBlockState()
                .setValue(BASIC_TYPE, type)
        ) {
            var is_forriden: Boolean = true
            for (block: IBlockState? in valide) if (world.getBlockState(pos.below()) === block) is_forriden = false

            if (is_forriden) {
                world.destroyBlock(pos, false)
                if (placer is EntityPlayer && !placer.capabilities.isCreativeMode) world.addFreshEntity(
                    EntityItem(
                        world,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        ItemStack(this.defaultBlockState().getBlock(), 1, this.getMetaFromState(state))
                    )
                )
            }
        }
    }

    override fun onBlockPlacedBy(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockGlowDandelions.GLOW_PLANT_1,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            )
        )
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockGlowDandelions.GLOW_PLANT_2,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            )
        )
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockGlowDandelions.GLOW_PLANT_3,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            )
        )
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockGlowDandelions.GLOW_PLANT_4,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            )
        )
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(world, pos, state)
    }

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        return false
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean {
        return false
    }

    override fun grow(world: World, rand: Random, pos: BlockPos, state: IBlockState) {
    }

    override fun isShearable(item: ItemStack, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return false
    }

    override fun onSheared(
        item: ItemStack,
        world: IBlockAccess?,
        pos: BlockPos?,
        fortune: Int
    ): MutableList<ItemStack?> {
        return mutableListOf()
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
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
            val item: Item = this.getItemDropped(state, rand, fortune)
            if (item !== Items.AIR && state.getMaterial() !== Material.AIR) {
                val type: EnumBlockGlowDandelions =
                    (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)

                when (type) {
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    private fun getType(state: IBlockState): EnumBlockGlowDandelions {
        return (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockGlowDandelions(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GLOW_PLANT_1(0, "barnarda_c_glow_plant_1"),
        GLOW_PLANT_2(1, "barnarda_c_glow_plant_2"),
        GLOW_PLANT_3(2, "barnarda_c_glow_plant_3"),
        GLOW_PLANT_4(3, "barnarda_c_glow_plant_4");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockGlowDandelions? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE,
            (EnumBlockGlowDandelions.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockGlowDandelions).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        return true
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockGlowDandelions> =
            PropertyEnum.create<EnumBlockGlowDandelions>("type", EnumBlockGlowDandelions::class.java)

        @JvmField
        protected val TALL_GRASS_AABB: AxisAlignedBB = AxisAlignedBB(
            0.09999999403953552,
            0.0,
            0.09999999403953552,
            0.8999999761581421,
            0.800000011920929,
            0.8999999761581421
        )
    }
}
