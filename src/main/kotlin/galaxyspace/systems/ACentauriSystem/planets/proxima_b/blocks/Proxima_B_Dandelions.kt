package galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks

import galaxyspace.systems.BarnardsSystem.core.BRBlocks

import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Dandelions
import galaxyspace.systems.ACentauriSystem.planets.proxima_b.blocks.Proxima_B_Grass
import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IShearable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class Proxima_B_Dandelions constructor() : BlockBush(Material.VINE), IGrowable, IShearable {
    init {
        this.setTranslationKey("proxima_b_dandelions")
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
            else -> return 0
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


        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockDandelions in EnumBlockDandelions.entries) {
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
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
        if (!world.isRemote) {
        }

        return false
    }

    override fun updateTick(world: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!world.isAreaLoaded(
                pos,
                1
            )
        ) return  // Forge: prevent growing cactus from loading unloaded chunks with block update


        if (!world.isRemote) {
        }
    }

    override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
    }

    private fun canPlaceAt(
        state: IBlockState,
        world: World,
        pos: BlockPos,
        placer: EntityLivingBase,
        type: EnumBlockDandelions,
        vararg valide: IBlockState?
    ): Boolean {
        if (!world.isRemote && state === this.getDefaultState()
                .withProperty(BASIC_TYPE, type)
        ) {
            var is_forrbiden: Boolean = true
            for (block: IBlockState? in valide) if (world.getBlockState(pos.down()) === block) is_forrbiden = false

            if (is_forrbiden) {
                world.destroyBlock(pos, false)
                if (placer is EntityPlayer && !placer.capabilities.isCreativeMode) world.spawnEntity(
                    EntityItem(
                        world,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        ItemStack(this.getDefaultState().getBlock(), 1, this.getMetaFromState(state))
                    )
                )
            }
            return is_forrbiden
        }
        return super.canPlaceBlockAt(world, pos)
    }

    override fun onBlockPlacedBy(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        //canPlaceAt(state, world, pos, placer, EnumBlockDandelions.REEDS, BRBlocks.BARNARDA_C_GRASS.getDefaultState(), this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS));
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockDandelions.GRASS,
            ACBlocks.PROXIMA_B_GRASS.getDefaultState()
                .withProperty(
                    Proxima_B_Grass.Companion.BASIC_TYPE,
                    Proxima_B_Grass.EnumBlockGrass.GRASS
                )
        )
        canPlaceAt(
            state,
            world,
            pos,
            placer,
            EnumBlockDandelions.GRASS_2,
            ACBlocks.PROXIMA_B_GRASS.getDefaultState()
                .withProperty(
                    Proxima_B_Grass.Companion.BASIC_TYPE,
                    Proxima_B_Grass.EnumBlockGrass.GRASS
                )
        )
    }

    override fun canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean {
        return true
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(world, pos, state)
    }

    override fun isReplaceable(world: IBlockAccess, pos: BlockPos): Boolean {
        //if(world.getBlockState(pos) == this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockDandelions.REEDS))
        //return false;
        return true
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        if (state.getMaterial() !== Material.AIR) {
            val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

            when (type) {
                else -> return null
            }
        }
        return null
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockDandelions = (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

        when (type) {
            else -> return 0
        }
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

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
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
        val rand: Random = if (world is World) world.rand else RANDOM

        val count: Int = quantityDropped(state, fortune, rand)
        for (i: Int in 0..<count) {
            val item: Item = (this.getItemDropped(state, rand, fortune))!!
            if (item !== Items.AIR && state.getMaterial() !== Material.AIR) {
                val type: EnumBlockDandelions =
                    (state.getValue(BASIC_TYPE) as EnumBlockDandelions)

                when (type) {
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    private fun getType(state: IBlockState): EnumBlockDandelions {
        return (state.getValue(BASIC_TYPE) as EnumBlockDandelions)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockDandelions(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GRASS(0, "proxima_b_tallgrass"),
        GRASS_2(1, "proxima_b_tallgrass_2");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
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
        return this.getDefaultState().withProperty(
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
