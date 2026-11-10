package micdoodle8.mods.galacticraft.planets.venus.blocks

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.Random

open class BlockBasicVenus constructor(assetName: String?) : Block(Material.ROCK), IDetectableResource,
    IPlantableBlock, ITerraformableBlock, ISortableBlock {
    enum class EnumBlockBasicVenus(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ROCK_SOFT(0, "venus_rock_0"),
        ROCK_HARD(1, "venus_rock_1"),
        ROCK_MAGMA(2, "venus_rock_2"),
        ROCK_VOLCANIC_DEPOSIT(3, "venus_rock_3"),
        DUNGEON_BRICK_1(4, "dungeon_brick_venus_1"),
        DUNGEON_BRICK_2(5, "dungeon_brick_venus_2"),
        ORE_ALUMINUM(6, "venus_ore_aluminum"),
        ORE_COPPER(7, "venus_ore_copper"),
        ORE_GALENA(8, "venus_ore_galena"),
        ORE_QUARTZ(9, "venus_ore_quartz"),
        ORE_SILICON(10, "venus_ore_silicon"),
        ORE_TIN(11, "venus_ore_tin"),
        LEAD_BLOCK(12, "lead_block"),
        ORE_SOLAR_DUST(13, "venus_ore_solar");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        fun getItemStack(): ItemStack {
            return ItemStack(VenusBlocks.venusBlock, 1, this.meta)
        }

        companion object {
            private val values: Array<EnumBlockBasicVenus> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockBasicVenus? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.blockHardness = 2.2f
        this.blockResistance = 2.5f
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(
                BASIC_TYPE_VENUS,
                EnumBlockBasicVenus.ROCK_SOFT
            )
        )
        this.setTranslationKey(assetName)
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun harvestBlock(
        worldIn: World,
        player: EntityPlayer,
        pos: BlockPos,
        state: IBlockState,
        te: TileEntity?,
        tool: ItemStack
    ) {
        player.addStat(StatList.getBlockStats(this))
        player.addExhaustion(0.025f)

        if (this.canSilkHarvest(
                worldIn,
                pos,
                worldIn.getBlockState(pos),
                player
            ) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0
        ) {
            val items: ArrayList<ItemStack> = ArrayList<ItemStack>()
            items.add(this.getSilkTouchDrop(state))

            ForgeEventFactory.fireBlockHarvesting(
                items,
                worldIn,
                pos,
                worldIn.getBlockState(pos),
                0,
                1.0f,
                true,
                player
            )

            for (`is`: ItemStack in items) {
                spawnAsEntity(worldIn, pos, `is`)
            }
        } else {
            val i: Int = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            harvesters.set(player)
            this.dropBlockAsItem(worldIn, pos, state, i)
            harvesters.set(null)
            if (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus == EnumBlockBasicVenus.ROCK_MAGMA) {
                worldIn.setBlockState(pos, Blocks.FLOWING_LAVA.getDefaultState())
            }
        }
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion): Float {
        val type: EnumBlockBasicVenus =
            (world.getBlockState(pos).getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)

        if (type == EnumBlockBasicVenus.ROCK_HARD) {
            return 6.0f
        } else if (type == EnumBlockBasicVenus.DUNGEON_BRICK_1 || type == EnumBlockBasicVenus.DUNGEON_BRICK_2) {
            return 40.0f
        } else if (type == EnumBlockBasicVenus.ORE_ALUMINUM || type == EnumBlockBasicVenus.ORE_COPPER || type == EnumBlockBasicVenus.ORE_GALENA || type == EnumBlockBasicVenus.ORE_QUARTZ || type == EnumBlockBasicVenus.ORE_SILICON || type == EnumBlockBasicVenus.ORE_TIN || type == EnumBlockBasicVenus.ORE_SOLAR_DUST) {
            return 3.0f
        }

        return this.blockResistance / 5.0f
    }

    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos): Float {
        val type: EnumBlockBasicVenus =
            (worldIn.getBlockState(pos).getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)

        if (type == EnumBlockBasicVenus.ROCK_HARD) {
            return 1.5f
        }

        if (type == EnumBlockBasicVenus.ROCK_SOFT) {
            return 0.9f
        }

        if (type == EnumBlockBasicVenus.DUNGEON_BRICK_1 || type == EnumBlockBasicVenus.DUNGEON_BRICK_2) {
            return 4.0f
        }

        if (type == EnumBlockBasicVenus.ORE_ALUMINUM || type == EnumBlockBasicVenus.ORE_COPPER || type == EnumBlockBasicVenus.ORE_GALENA || type == EnumBlockBasicVenus.ORE_QUARTZ || type == EnumBlockBasicVenus.ORE_SILICON || type == EnumBlockBasicVenus.ORE_TIN || type == EnumBlockBasicVenus.ORE_SOLAR_DUST) {
            return 5.0f
        }

        return this.blockHardness
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumBlockBasicVenus = (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        when (type) {
            EnumBlockBasicVenus.ORE_SILICON -> return (GCItems.basicItem)!!
            EnumBlockBasicVenus.ORE_QUARTZ -> return Items.QUARTZ
            EnumBlockBasicVenus.ORE_SOLAR_DUST -> return (VenusItems.basicItem)!!
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBasicVenus = (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        when (type) {
            EnumBlockBasicVenus.ORE_SILICON -> return 2
            EnumBlockBasicVenus.ORE_QUARTZ -> return 0
            EnumBlockBasicVenus.ORE_SOLAR_DUST -> return 4
            else -> return getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val type: EnumBlockBasicVenus = (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        when (type) {
            EnumBlockBasicVenus.ORE_SILICON -> {
                if (fortune > 0) {
                    var j: Int = random.nextInt(fortune + 2) - 1

                    if (j < 0) {
                        j = 0
                    }

                    return this.quantityDropped(random) * (j + 1)
                }
                return this.quantityDropped(random)
            }

            else -> return this.quantityDropped(random)
        }
    }

    override fun getExpDrop(state: IBlockState, world: IBlockAccess, pos: BlockPos, fortune: Int): Int {
        val type: EnumBlockBasicVenus = (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        val rand: Random = if (world is World) world.rand else Random()
        if (this.getItemDropped(state, rand, fortune) !== Item.getItemFromBlock(this)) {
            val i: Int

            when (type) {
                EnumBlockBasicVenus.ORE_QUARTZ, EnumBlockBasicVenus.ORE_SILICON -> i = MathHelper.getInt(rand, 2, 5)
                else -> i = 0
            }

            return i
        }
        return 0
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

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (type: EnumBlockBasicVenus in EnumBlockBasicVenus.entries) {
            list.add(ItemStack(this, 1, type.getMeta()))
        }
    }

    override fun isValueable(state: IBlockState?): Boolean {
        val type: EnumBlockBasicVenus = ((state)!!.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        when (type) {
            EnumBlockBasicVenus.ORE_ALUMINUM, EnumBlockBasicVenus.ORE_COPPER, EnumBlockBasicVenus.ORE_GALENA, EnumBlockBasicVenus.ORE_QUARTZ, EnumBlockBasicVenus.ORE_SILICON, EnumBlockBasicVenus.ORE_TIN, EnumBlockBasicVenus.ORE_SOLAR_DUST -> return true
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

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        val type: EnumBlockBasicVenus =
            ((world)!!.getBlockState(pos).getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)

        if (type == EnumBlockBasicVenus.ROCK_HARD || type == EnumBlockBasicVenus.ROCK_SOFT) {
            val above: BlockPos = (pos)!!.offset(EnumFacing.UP)
            val stateAbove: IBlockState = (world)!!.getBlockState(above)
            return stateAbove.getBlock().isAir(stateAbove, world, above)
        }

        return false
    }

    override fun isReplaceableOreGen(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        target: Predicate<IBlockState>
    ): Boolean {
        val type: EnumBlockBasicVenus =
            (world.getBlockState(pos).getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        return type == EnumBlockBasicVenus.ROCK_HARD
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(
            BASIC_TYPE_VENUS,
            (EnumBlockBasicVenus.Companion.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE_VENUS)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        val type: EnumBlockBasicVenus =
            (getStateFromMeta(meta).getValue(BASIC_TYPE_VENUS) as EnumBlockBasicVenus)
        when (type) {
            EnumBlockBasicVenus.ORE_ALUMINUM, EnumBlockBasicVenus.ORE_COPPER, EnumBlockBasicVenus.ORE_GALENA, EnumBlockBasicVenus.ORE_QUARTZ, EnumBlockBasicVenus.ORE_SILICON, EnumBlockBasicVenus.ORE_TIN, EnumBlockBasicVenus.ORE_SOLAR_DUST -> return EnumSortCategoryBlock.ORE
            EnumBlockBasicVenus.DUNGEON_BRICK_1, EnumBlockBasicVenus.DUNGEON_BRICK_2 -> return EnumSortCategoryBlock.BRICKS
            EnumBlockBasicVenus.LEAD_BLOCK -> return EnumSortCategoryBlock.INGOT_BLOCK
            else -> return EnumSortCategoryBlock.GENERAL
        }
    }

    companion object {
        @JvmField
        val BASIC_TYPE_VENUS: PropertyEnum<EnumBlockBasicVenus> =
            PropertyEnum.create<EnumBlockBasicVenus>("basictypevenus", EnumBlockBasicVenus::class.java)
    }
}
