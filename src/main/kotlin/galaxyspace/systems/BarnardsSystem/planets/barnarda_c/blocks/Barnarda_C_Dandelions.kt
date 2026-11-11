package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import foundation.level.offset

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Dandelions
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest
import asmodeuscore.core.astronomy.dimension.world.gen.features.trees.WorldGenTree_Forest2
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
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.common.IShearable
import net.minecraftforge.event.terraingen.TerrainGen
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Barnarda_C_Dandelions constructor() : BlockBush(Material.VINE), IGrowable, IShearable, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_dandelions")
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
        if (getType(state) == EnumBlockDandelions.DESERT_DOWN) {
            if (entity is EntityLivingBase) entity.attackEntityFrom(DamageSource.CACTUS, 0.5f)
        }
    }


    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return Material.GROUND.getMaterialMapColor()

        }
    }

    override fun getMaterial(state: IBlockState): Material {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
        when (type) {
            else -> return this.material
        }
    }

    override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            EnumBlockDandelions.LEAVES_BALLS, EnumBlockDandelions.LIGHT_BALLS -> return 8
            else -> return state.getLightValue()
        }
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
        if (type == EnumBlockDandelions.REEDS || type == EnumBlockDandelions.REEDS_FRUITS) return ItemStack(
            BRItems.BASIC,
            1,
            0
        )

        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockDandelions in EnumBlockDandelions.entries) {
            if ((blockBasic != EnumBlockDandelions.DESERT_UP) && (blockBasic != EnumBlockDandelions.REEDS) && (blockBasic != EnumBlockDandelions.REEDS_FRUITS) && (blockBasic != EnumBlockDandelions.YELLOW_GRASS_UP)) list.add(
                ItemStack(this, 1, blockBasic.getMeta())
            )
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
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
        if (!world.isClientSide) {
            if (type == EnumBlockDandelions.REEDS_FRUITS) {
                world.addFreshEntity(
                    EntityItem(
                        world,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        foundation.item.FoundationItemVariants.stack(BRItems.FOODS, 1, 0)
                    )
                )
                world.setBlock(
                    pos,
                    this.defaultBlockState().setValue(
                        BASIC_TYPE,
                        EnumBlockDandelions.REEDS
                    ),
                    3
                )
                return true
            }
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
            if (state === this.defaultBlockState().setValue(
                    BASIC_TYPE,
                    EnumBlockDandelions.REEDS
                ) || state === this.defaultBlockState().setValue(
                    BASIC_TYPE,
                    EnumBlockDandelions.REEDS_FRUITS
                )
            ) {
                if (world.isEmptyBlock(pos.above())) {
                    var length: Int = 1
                    val reed1: IBlockState = this.defaultBlockState()
                        .setValue(BASIC_TYPE, EnumBlockDandelions.REEDS)
                    val reed2: IBlockState = this.defaultBlockState()
                        .setValue(
                            BASIC_TYPE,
                            EnumBlockDandelions.REEDS_FRUITS
                        )

                    while (world.getBlockState(pos.below(length)) === reed1 || world.getBlockState(pos.below(length)) === reed2) length++

                    if (length < 4) world.setBlock(
                        pos.above(),
                        this.defaultBlockState().setValue(
                            BASIC_TYPE,
                            EnumBlockDandelions.REEDS
                        ),
                        3
                    )
                }
                if (state === this.defaultBlockState()
                        .setValue(BASIC_TYPE, EnumBlockDandelions.REEDS)
                ) if (rand.nextInt(40) == 0) {
                    world.setBlock(
                        pos,
                        this.defaultBlockState().setValue(
                            BASIC_TYPE,
                            EnumBlockDandelions.REEDS_FRUITS
                        ),
                        3
                    )
                }
            }

            if (state === this.defaultBlockState().setValue(
                    BASIC_TYPE,
                    EnumBlockDandelions.VIOLET_TREE_SAPLING
                )
            ) {
                if (world.getLightFromNeighbors(pos.above()) >= 9 && rand.nextInt(7) == 0) {
                    this.grow(world, rand, pos, state)
                }
            }
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
        //super.neighborChanged(state, world, pos, block, fromPos);
        //canPlaceAt(state, world, pos, EnumBlockDandelions.REEDS, BRBlocks.BARNARDA_C_GRASS.defaultBlockState(), this.defaultBlockState().setValue(BASIC_TYPE, EnumBlockDandelions.REEDS));

        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.YELLOW_GRASS_UP
            )
        ) {
            if (world.getBlockState(pos.below()) != this.defaultBlockState()
                    .setValue(
                        BASIC_TYPE,
                        EnumBlockDandelions.YELLOW_GRASS_DOWN
                    )
            ) {
                this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0)
                world.destroyBlock(pos, false)
            }
        }


        if (state === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.REEDS)
        ) {
            if (world.isEmptyBlock(pos.below())) {
                this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0)
                world.destroyBlock(pos, false)
            }
        }

        if (state !== this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.LEAVES_BALLS
            ) && world.isEmptyBlock(pos.below())
        ) {
            this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0)
            world.destroyBlock(pos, false)
        }

        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.LEAVES_BALLS
            ) && world.isEmptyBlock(pos.above())
        ) {
            this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0)
            world.destroyBlock(pos, false)
        }
    }

    private fun canPlaceAt(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        placer: EntityLivingBase,
        type: EnumBlockDandelions,
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
            EnumBlockDandelions.REEDS,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.REEDS)
        )

        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockDandelions.VIOLET_TREE_SAPLING,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        )

        if (state === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.DESERT_DOWN)
        ) {
            if (world.isEmptyBlock(pos.above())) world.setBlockAndUpdate(
                pos.above(),
                this.defaultBlockState()
                    .setValue(BASIC_TYPE, EnumBlockDandelions.DESERT_UP)
            )
        }

        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.YELLOW_GRASS_DOWN
            )
        ) {
            if (world.isEmptyBlock(pos.above())) world.setBlockAndUpdate(
                pos.above(),
                this.defaultBlockState().setValue(
                    BASIC_TYPE,
                    EnumBlockDandelions.YELLOW_GRASS_UP
                )
            )
        }

        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockDandelions.GRASS,
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
            EnumBlockDandelions.HOPPER,
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
            EnumBlockDandelions.LIGHT_BALLS,
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
            EnumBlockDandelions.YELLOW_GRASS_DOWN,
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
            EnumBlockDandelions.RED_FERN,
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
            EnumBlockDandelions.TALLGRASS_DARK,
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
            EnumBlockDandelions.DESERT_DOWN,
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
            EnumBlockDandelions.GREEN_FERN,
            BRBlocks.BARNARDA_C_GRASS.defaultBlockState(),
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            )
        )
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        if (state === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.DESERT_UP)
        ) {
            world.destroyBlock(pos.below(), true)
        }

        if (state === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.DESERT_DOWN)
        ) {
            if (world.getBlockState(pos.above()) === this.defaultBlockState()
                    .setValue(BASIC_TYPE, EnumBlockDandelions.DESERT_UP)
            ) world.destroyBlock(pos.above(), true)
        }

        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.YELLOW_GRASS_UP
            )
        ) {
            if (world.getBlockState(pos.below()) === this.defaultBlockState()
                    .setValue(
                        BASIC_TYPE,
                        EnumBlockDandelions.YELLOW_GRASS_DOWN
                    )
            ) world.destroyBlock(pos.below(), true)
        }

        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.YELLOW_GRASS_DOWN
            )
        ) {
            if (world.getBlockState(pos.above()) === this.defaultBlockState()
                    .setValue(
                        BASIC_TYPE,
                        EnumBlockDandelions.YELLOW_GRASS_UP
                    )
            ) world.destroyBlock(pos.above(), true)
        }
        super.breakBlock(world, pos, state)
    }

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos): Boolean {
        if (world.getBlockState(pos) === this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockDandelions.REEDS)
        ) return false
        return true
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        if (state.getMaterial() !== Material.AIR) {
            val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

            when (type) {
                EnumBlockDandelions.GRASS -> return Items.WHEAT_SEEDS
                EnumBlockDandelions.REEDS, EnumBlockDandelions.REEDS_FRUITS -> return BRItems.BASIC
                else -> return null
            }
        }
        return null
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            EnumBlockDandelions.GRASS, EnumBlockDandelions.REEDS, EnumBlockDandelions.REEDS_FRUITS -> return 0
            else -> return 0
        }
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.VIOLET_TREE_SAPLING
            )
        ) return true

        return false
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean {
        if (state === this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumBlockDandelions.VIOLET_TREE_SAPLING
            )
        ) return true

        return false
    }

    override fun grow(world: World, rand: Random, pos: BlockPos, state: IBlockState) {
        if (rand.nextInt(7) == 0) {
            this.generateTree(world, pos, state, rand)
        }
    }

    open fun generateTree(world: World, pos: BlockPos, state: IBlockState?, rand: Random) {
        if (!TerrainGen.saplingGrowTree(world, rand, pos)) return

        val tree: WorldGenerator = WorldGenTree_Forest(
            BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
            rand.nextInt(3)
        )
        val tree_2: WorldGenerator = WorldGenTree_Forest2(
            BRBlocks.BARNARDA_C_VIOLET_LOG.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_LEAVES.getStateFromMeta(0),
            rand.nextInt(3)
        )

        var check: Boolean = true
        for (poscheck: BlockPos in BlockPos.getAllInBox(pos.offset(-5, 2, -5), pos.offset(5, 10, 5))) {
            if (!world.isEmptyBlock(poscheck)) check = false
        }

        if (check) {
            if (rand.nextInt(2) == 0) tree.generate(world, rand, pos)
            else tree_2.generate(world, rand, pos)
        }
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
            val item: Item = (this.getItemDropped(state, rand, fortune))!!
            if (item !== Items.AIR && state.getMaterial() !== Material.AIR) {
                val type: EnumBlockDandelions =
                    (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

                when (type) {
                    EnumBlockDandelions.GRASS -> {
                        if (rand.nextInt(100) >= 10) break
                        drops.add(ItemStack(item, 1, this.damageDropped(state)))
                    }

                    EnumBlockDandelions.LIGHT_BALLS, EnumBlockDandelions.LEAVES_BALLS -> {
                        if (rand.nextInt(100) >= 80) break
                        drops.add(ItemStack(Items.GLOWSTONE_DUST, 1, 0))
                    }

                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    private fun getType(state: IBlockState): EnumBlockDandelions {
        return (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getAmbientOcclusionLightValue(state: IBlockState): Float {
        return 1.0f
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockDandelions(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GRASS(0, "barnarda_c_tall_grass"),
        HOPPER(1, "barnarda_c_hopper_flower"),
        GREEN_FERN(2, "barnarda_c_green_fern"),
        RED_FERN(3, "barnarda_c_red_fern"),
        LIGHT_BALLS(4, "barnarda_c_light_balls"),
        LEAVES_BALLS(5, "barnarda_c_leaves_balls"),
        DESERT_DOWN(6, "barnarda_c_desert_down"),
        DESERT_UP(7, "barnarda_c_desert_up"),
        YELLOW_GRASS_DOWN(8, "barnarda_c_yellow_grass_down"),
        YELLOW_GRASS_UP(9, "barnarda_c_yellow_grass_up"),
        REEDS(10, "barnarda_c_reeds"),
        REEDS_FRUITS(11, "barnarda_c_reeds_fruits"),
        VIOLET_TREE_SAPLING(12, "barnarda_c_violet_sapling"),
        TALLGRASS_DARK(13, "barnarda_c_tallgrass_dark");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockDandelions? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE,
            (EnumBlockDandelions.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockDandelions).getMeta()
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
        val BASIC_TYPE: PropertyEnum<EnumBlockDandelions> =
            PropertyEnum.create<EnumBlockDandelions>("type", EnumBlockDandelions::class.java)

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
