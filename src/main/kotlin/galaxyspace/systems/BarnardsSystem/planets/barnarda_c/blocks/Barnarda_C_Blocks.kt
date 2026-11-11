package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
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

open class Barnarda_C_Blocks constructor() : Block(Material.ROCK), ISortableBlock, ITerraformableBlock,
    IDetectableResource, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_blocks")
        this.setHarvestLevel(
            "pickaxe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.STONE)
        )
        this.setHarvestLevel(
            "pickaxe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.COBBLESTONE)
        )
        this.setHarvestLevel(
            "pickaxe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.STONE_BRICKS)
        )
        this.setHarvestLevel(
            "shovel",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.DIRT)
        )
        this.setHarvestLevel(
            "shovel",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.DIRT_1)
        )
        this.setHarvestLevel(
            "pickaxe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.SANDSTONE)
        )
        this.setHarvestLevel(
            "axe",
            0,
            this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.OAK_PLANKS)
        )
    }


    override fun getBlockHardness(state: IBlockState, world: World, pos: BlockPos): Float {
        val type: EnumBlockBarnardaC = (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC)
        when (type) {
            EnumBlockBarnardaC.DIRT, EnumBlockBarnardaC.DIRT_1 -> return 0.5f
            EnumBlockBarnardaC.SANDSTONE -> return 1.0f
            EnumBlockBarnardaC.STONE, EnumBlockBarnardaC.COBBLESTONE, EnumBlockBarnardaC.STONE_BRICKS -> return 1.0f
            EnumBlockBarnardaC.OAK_PLANKS -> return 1.5f
            else -> return this.blockHardness
        }
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

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        if (state !== this.defaultBlockState()
                .setValue(BASIC_TYPE, EnumBlockBarnardaC.DIRT)
        ) return false

        return super.canSustainPlant(state, world, pos, direction, plantable)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockBarnardaC in EnumBlockBarnardaC.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(this)
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockBarnardaC = (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC)

        when (type) {
            EnumBlockBarnardaC.STONE -> return 4
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
            val item: Item = this.getItemDropped(state, rand, fortune)
            if (item !== Items.AIR) {
                val type: EnumBlockBarnardaC = (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC)

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockBarnardaC(
        private val meta: Int,
        private val serializedName: String,
        private val material: Material,
        private val sound: SoundType
    ) : IStringSerializable {
        DIRT(0, "barnarda_c_dirt", Material.GROUND, SoundType.GROUND),
        STONE(1, "barnarda_c_stone", Material.ROCK, SoundType.STONE),
        SANDSTONE(2, "barnarda_c_sandstone", Material.ROCK, SoundType.STONE),
        DIRT_1(3, "barnarda_c_dirt_1", Material.GROUND, SoundType.GROUND),
        COBBLESTONE(4, "barnarda_c_cobblestone", Material.ROCK, SoundType.STONE),
        STONE_BRICKS(5, "barnarda_c_stone_bricks", Material.ROCK, SoundType.STONE),
        OAK_PLANKS(6, "barnarda_c_oak_planks", Material.WOOD, SoundType.WOOD);

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
            fun byMetadata(meta: Int): EnumBlockBarnardaC? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockBarnardaC.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC).getMeta()
    }

    override fun getMaterial(state: IBlockState): Material {
        return (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC).getMaterial()
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC).getMaterial()
            .getMaterialMapColor()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    override fun getSoundType(state: IBlockState, world: World, pos: BlockPos, entity: Entity?): SoundType {
        return (state.getValue(BASIC_TYPE) as EnumBlockBarnardaC).getSoundType()
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockBarnardaC> =
            PropertyEnum.create<EnumBlockBarnardaC>("type", EnumBlockBarnardaC::class.java)
    }
}
