package galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks.EnumBlockBarnardaC1
import galaxyspace.systems.BarnardsSystem.moons.barnarda_c1.blocks.Barnarda_C1_Blocks
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class Barnarda_C1_Blocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock,
    IDetectableResource, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c1_blocks")
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC1.ONYX_ORE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC1.IRON_ORE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC1.COPPER_ORE)
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC1.TIN_ORE)
        )
    }

    override fun getBlockHardness(state: IBlockState, world: World, pos: BlockPos): Float {
        val type: EnumBlockBarnardaC1 = state.getValue(BASIC_TYPE)
        when (type) {
            else -> return 1.0f
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state))
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        return super.canSustainPlant(state, world, pos, direction, plantable)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockBarnardaC1 in EnumBlockBarnardaC1.entries) list.add(
            ItemStack(
                this,
                1,
                blockBasic.getMeta()
            )
        )
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumBlockBarnardaC1 = state.getValue(BASIC_TYPE)

        when (type) {
            EnumBlockBarnardaC1.ONYX_ORE -> return BRItems.BASIC
            else -> return Item.getItemFromBlock(this)
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBarnardaC1 = state.getValue(BASIC_TYPE)

        when (type) {
            EnumBlockBarnardaC1.ONYX_ORE -> return 3
            else -> return this.getMetaFromState(state)
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val bonus: Int = 0

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
            if (item !== Items.AIR) {
                val type: EnumBlockBarnardaC1 = state.getValue(BASIC_TYPE)

                when (type) {
                    else -> drops.add(ItemStack(item, 1, this.damageDropped(state)))
                }
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isValueable(metadata: IBlockState?): Boolean {
        return false
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    enum class EnumBlockBarnardaC1(
        private val meta: Int,
        private val serializedName: String,
        private val material: Material,
        private val sound: SoundType
    ) : IStringSerializable {
        SNOWY_SURFACE(0, "barnarda_c1_snowy_surface", Material.GROUND, SoundType.SNOW),
        SNOWY_SUBSURFACE(1, "barnarda_c1_snowy_subsurface", Material.GROUND, SoundType.SNOW),
        ICY_SUBSURFACE(2, "barnarda_c1_icy_subsurface", Material.ICE, SoundType.GLASS),
        STONE(3, "barnarda_c1_stone", Material.ROCK, SoundType.STONE),
        ONYX_ORE(4, "barnarda_c1_onyx_ore", Material.ROCK, SoundType.STONE),
        IRON_ORE(5, "barnarda_c1_iron_ore", Material.ROCK, SoundType.STONE),
        COPPER_ORE(6, "barnarda_c1_copper_ore", Material.ROCK, SoundType.STONE),
        TIN_ORE(7, "barnarda_c1_tin_ore", Material.ROCK, SoundType.STONE);

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
            fun byMetadata(meta: Int): EnumBlockBarnardaC1? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockBarnardaC1.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(BASIC_TYPE).getMeta()
    }

    override fun getMaterial(state: IBlockState): Material {
        return state.getValue(BASIC_TYPE).getMaterial()
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return state.getValue(BASIC_TYPE).getMaterial().getMaterialMapColor()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?): SoundType {
        return state.getValue(BASIC_TYPE).getSoundType()
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBarnardaC1> =
            PropertyEnum.create<EnumBlockBarnardaC1>("type", EnumBlockBarnardaC1::class.java)
    }
}
