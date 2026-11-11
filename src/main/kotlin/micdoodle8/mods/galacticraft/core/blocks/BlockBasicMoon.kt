package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.Footprint
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFlower
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.Random

open class BlockBasicMoon constructor(assetName: String?) : Block(Material.ROCK), IDetectableResource,
    IPlantableBlock, ITerraformableBlock, ISortableBlock, LegacyBlockMetadata {
    enum class EnumBlockBasicMoon(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ORE_COPPER_MOON(0, "ore_copper_moon"),
        ORE_TIN_MOON(1, "ore_tin_moon"),
        ORE_CHEESE_MOON(2, "ore_cheese_moon"),
        MOON_DIRT(3, "moon_dirt_moon"),
        MOON_STONE(4, "moon_stone"),
        MOON_TURF(5, "moon_turf"),
        ORE_SAPPHIRE(6, "ore_sapphire_moon"),
        MOON_DUNGEON_BRICK(14, "moon_dungeon_brick");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockBasicMoon> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBasicMoon {
                if (meta < 7) {
                    return values[meta]!!
                }

                return MOON_DUNGEON_BRICK
            }
        }
    }

    init {
        this.blockHardness = 1.5f
        this.blockResistance = 2.5f
        this.setDefaultState(
            this.blockState.getBaseState().setValue(
                BASIC_TYPE_MOON,
                EnumBlockBasicMoon.ORE_COPPER_MOON
            )
        )
        this.setTranslationKey(assetName)
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val type: EnumBlockBasicMoon =
            (world.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)

        if (type == EnumBlockBasicMoon.MOON_DUNGEON_BRICK) {
            return 40.0f
        } else if (type == EnumBlockBasicMoon.MOON_STONE) {
            return 6.0f
        } else if (type == EnumBlockBasicMoon.ORE_COPPER_MOON || type == EnumBlockBasicMoon.ORE_TIN_MOON || type == EnumBlockBasicMoon.ORE_SAPPHIRE || type == EnumBlockBasicMoon.ORE_CHEESE_MOON) {
            return 3.0f
        }

        return this.blockResistance / 5.0f
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val type: EnumBlockBasicMoon =
            (worldIn.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)

        if (type == EnumBlockBasicMoon.MOON_DIRT || type == EnumBlockBasicMoon.MOON_TURF) {
            return 0.5f
        }

        if (type == EnumBlockBasicMoon.MOON_DUNGEON_BRICK) {
            return 4.0f
        }

        if (type == EnumBlockBasicMoon.ORE_COPPER_MOON || type == EnumBlockBasicMoon.ORE_TIN_MOON || type == EnumBlockBasicMoon.ORE_SAPPHIRE) {
            return 5.0f
        }

        if (type == EnumBlockBasicMoon.ORE_CHEESE_MOON) {
            return 3.0f
        }

        return this.blockHardness
    }

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        val type: EnumBlockBasicMoon =
            (world.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        if (type == EnumBlockBasicMoon.MOON_DIRT || type == EnumBlockBasicMoon.MOON_TURF) {
            return true
        }

        return super.canHarvestBlock(world, pos, player)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumBlockBasicMoon = (state.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        when (type) {
            EnumBlockBasicMoon.ORE_CHEESE_MOON -> return (GCItems.cheeseCurd)!!
            EnumBlockBasicMoon.ORE_SAPPHIRE -> return (GCItems.itemBasicMoon)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBasicMoon = (state.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        if (type == EnumBlockBasicMoon.ORE_CHEESE_MOON) {
            return 0
        } else if (type == EnumBlockBasicMoon.ORE_SAPPHIRE) {
            return 2
        } else {
            return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val type: EnumBlockBasicMoon = (state.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        when (type) {
            EnumBlockBasicMoon.ORE_CHEESE_MOON -> {
                if (fortune >= 1) {
                    return if (random.nextFloat() < fortune * 0.29f - 0.25f) 2 else 1
                }
                return 1
            }

            else -> return 1
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (type: EnumBlockBasicMoon in EnumBlockBasicMoon.entries) {
            list.add(ItemStack(this, 1, type.getMeta()))
        }
    }

    override fun isValueable(state: IBlockState?): Boolean {
        val type: EnumBlockBasicMoon = ((state)!!.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        when (type) {
            EnumBlockBasicMoon.ORE_CHEESE_MOON, EnumBlockBasicMoon.ORE_COPPER_MOON, EnumBlockBasicMoon.ORE_SAPPHIRE, EnumBlockBasicMoon.ORE_TIN_MOON -> return true
            else -> return false
        }
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val type: EnumBlockBasicMoon =
            (world.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)

        if (type != EnumBlockBasicMoon.MOON_TURF) {
            return false
        }

        plantable.getPlant(world, pos.offset(EnumFacing.UP))

        return plantable is BlockFlower
    }

    override fun requiredLiquidBlocksNearby(): Int {
        return 4
    }

    override fun isPlantable(state: IBlockState?): Boolean {
        return (state)!!.getValue(BASIC_TYPE_MOON) == EnumBlockBasicMoon.MOON_TURF
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        val type: EnumBlockBasicMoon =
            ((world)!!.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)

        if (type == EnumBlockBasicMoon.MOON_TURF) {
            val above: BlockPos = (pos)!!.offset(EnumFacing.UP)
            val stateAbove: IBlockState = (world)!!.getBlockState(above)
            return stateAbove.getBlock().isAir(stateAbove, world, above)
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
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        val type: EnumBlockBasicMoon = (state.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)

        if (!worldIn.isClientSide && type == EnumBlockBasicMoon.MOON_TURF) {
            val footprintChunkMap: MutableMap<Long, MutableList<Footprint>?>? =
                (TickHandlerServer.serverFootprintMap.get(GCCoreUtil.getDimensionID(worldIn)) as MutableMap<Long, MutableList<Footprint>?>?)

            if (footprintChunkMap != null) {
                val chunkKey: Long = ChunkPos.asLong(pos.getX() shr 4, pos.getZ() shr 4)
                val footprintList: MutableList<Footprint>? = footprintChunkMap.get(chunkKey)

                if (footprintList != null && !footprintList.isEmpty()) {
                    val toRemove: MutableList<Footprint?> = ArrayList<Footprint?>()

                    for (footprint: Footprint in footprintList) {
                        if ((footprint.position)!!.x > pos.getX() && (footprint.position)!!.x < pos.getX() + 1 && (footprint.position)!!.z > pos.getZ() && (footprint.position)!!.z < pos.getZ() + 1) {
                            toRemove.add(footprint)
                        }
                    }

                    if (!toRemove.isEmpty()) {
                        footprintList.removeAll(toRemove)
                    }
                }
            }

            TickHandlerServer.footprintBlockChanges.add(BlockVec3Dim(pos, GCCoreUtil.getDimensionID(worldIn)))
        }
    }

    override fun isReplaceableOreGen(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        target: Predicate<IBlockState>
    ): Boolean {
        val type: EnumBlockBasicMoon =
            (world.getBlockState(pos).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        return type == EnumBlockBasicMoon.MOON_STONE || type == EnumBlockBasicMoon.MOON_DIRT
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE_MOON,
            EnumBlockBasicMoon.Companion.byMetadata(meta)
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE_MOON)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        val type: EnumBlockBasicMoon =
            (getStateFromMeta(meta).getValue(BASIC_TYPE_MOON) as EnumBlockBasicMoon)
        when (type) {
            EnumBlockBasicMoon.ORE_CHEESE_MOON, EnumBlockBasicMoon.ORE_COPPER_MOON, EnumBlockBasicMoon.ORE_SAPPHIRE, EnumBlockBasicMoon.ORE_TIN_MOON -> return EnumSortCategoryBlock.ORE
            EnumBlockBasicMoon.MOON_DUNGEON_BRICK -> return EnumSortCategoryBlock.BRICKS
        
else -> {}
}
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        if (state.getBlock() !== this) return 0

        val meta: Int = this.getMetaFromState(state)
        if (meta == 2 || meta == 6) {
            val rand: Random = if (world is World) world.random else Random()
            return MathHelper.getInt(rand, 2, 5) + (if (meta == 6) 1 else 0)
        }
        return 0
    }

    companion object {
        @JvmField
        val BASIC_TYPE_MOON: PropertyEnum<EnumBlockBasicMoon> =
            PropertyEnum.create<EnumBlockBasicMoon>("basictypemoon", EnumBlockBasicMoon::class.java)
    }
}
