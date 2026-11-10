package micdoodle8.mods.galacticraft.planets.mars.blocks

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockBasicMars constructor(assetName: String?) : Block(Material.ROCK), IDetectableResource,
    IPlantableBlock, ITerraformableBlock, ISortableBlock {
    enum class EnumBlockBasic(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ORE_COPPER(0, "ore_copper_mars"),
        ORE_TIN(1, "ore_tin_mars"),
        ORE_DESH(2, "ore_desh_mars"),
        ORE_IRON(3, "ore_iron_mars"),
        COBBLESTONE(4, "cobblestone"),
        SURFACE(5, "mars_surface"),
        MIDDLE(6, "mars_middle"),
        DUNGEON_BRICK(7, "dungeon_brick"),
        DESH_BLOCK(8, "desh_block"),
        MARS_STONE(9, "mars_stone");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockBasic> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBasic? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setTranslationKey(assetName)
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK) {
            return MapColor.GREEN
        } else if (state.getValue(BASIC_TYPE) == EnumBlockBasic.SURFACE) {
            return MapColor.DIRT
        }

        return MapColor.RED
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val type: EnumBlockBasic = world.getBlockState(pos).getValue(BASIC_TYPE) as EnumBlockBasic

        if (type == EnumBlockBasic.DUNGEON_BRICK) {
            return 40.0f
        } else if (type == EnumBlockBasic.DESH_BLOCK) {
            return 60.0f
        }

        return super.getExplosionResistance(world, pos, exploder, explosion)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val state: IBlockState = worldIn.getBlockState(pos)

        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK) {
            return 4.0f
        }

        return this.blockHardness
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.ORE_DESH) {
            return (MarsItems.marsItemBasic)!!
        }

        return Item.getItemFromBlock(this)
    }

    override fun damageDropped(state: IBlockState): Int {
        val meta: Int = state.getBlock().getMetaFromState(state)
        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.MARS_STONE) {
            return 4
        } else if (state.getValue(BASIC_TYPE) == EnumBlockBasic.ORE_DESH) {
            return 0
        } else {
            return meta
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        if (state.getValue(BASIC_TYPE) == EnumBlockBasic.ORE_DESH && fortune >= 1) {
            return if (random.nextFloat() < fortune * 0.29f - 0.25f) 2 else 1
        }

        return 1
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockBasic in EnumBlockBasic.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun isValueable(state: IBlockState?): Boolean {
        when (this.getMetaFromState((state)!!)) {
            0, 1, 2, 3 -> return true
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
        return false
    }

    override fun requiredLiquidBlocksNearby(): Int {
        return 4
    }

    override fun isPlantable(state: IBlockState?): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(state: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        if (rand.nextInt(10) == 0) {
            if (state.getBlock() === this && state.getValue(BASIC_TYPE) == EnumBlockBasic.DUNGEON_BRICK) {
                GalacticraftPlanets.spawnParticle(
                    "sludgeDrip",
                    Vector3(pos.getX() + rand.nextDouble(), pos.getY().toDouble(), pos.getZ() + rand.nextDouble()),
                    Vector3(0.0, 0.0, 0.0)
                )

                if (rand.nextInt(100) == 0) {
                    worldIn.playSound(
                        null,
                        pos.getX().toDouble(),
                        pos.getY().toDouble(),
                        pos.getZ().toDouble(),
                        GCSounds.singleDrip,
                        SoundCategory.AMBIENT,
                        1f,
                        0.8f + rand.nextFloat() / 5.0f
                    )
                }
            }
        }
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        val state: IBlockState = (world)!!.getBlockState(pos)
        val stateAbove: IBlockState = (world)!!.getBlockState((pos)!!.up())
        return state.getValue(BASIC_TYPE) == EnumBlockBasic.SURFACE && !stateAbove.getBlock()
            .isFullCube(stateAbove)
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

    override fun isReplaceableOreGen(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        target: Predicate<IBlockState>
    ): Boolean {
        return (state.getValue(BASIC_TYPE) == EnumBlockBasic.MIDDLE || state.getValue(
            BASIC_TYPE
        ) == EnumBlockBasic.MARS_STONE)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return state.getBlock().getMetaFromState(state) == 10
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockBasic.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockBasic).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        when (meta) {
            0, 1, 2, 3 -> return EnumSortCategoryBlock.ORE
            7 -> return EnumSortCategoryBlock.BRICKS
            8 -> return EnumSortCategoryBlock.INGOT_BLOCK
        }
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        if (state.getBlock() !== this) return 0

        val meta: Int = this.getMetaFromState(state)
        if (meta == 2) {
            val rand: Random = if (world is World) world.rand else Random()
            return MathHelper.getInt(rand, 2, 5)
        }
        return 0
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBasic> =
            PropertyEnum.create<EnumBlockBasic>("basictypemars", EnumBlockBasic::class.java)
    }
}
