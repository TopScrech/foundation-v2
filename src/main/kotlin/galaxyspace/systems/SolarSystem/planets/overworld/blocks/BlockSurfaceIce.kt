package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce.EnumBlockIce
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockSurfaceIce
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockBreakable
import net.minecraft.world.level.block.SoundType
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
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockSurfaceIce constructor() : BlockBreakable(Material.ICE, false), ITerraformableBlock,
    ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("surface_ice")
        this.setHardness(1.0f)
        this.setSoundType(SoundType.GLASS)
    }

    override fun isTerraformable(world: World?, pos: BlockPos?): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockIce in EnumBlockIce.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
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

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        val type: EnumBlockIce = (state.getValue(BASIC_TYPE) as EnumBlockIce)
        when (type) {
            else -> return GSItems.BASIC //Item.getItemFromBlock(this);
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        val type: EnumBlockIce = (state.getValue(BASIC_TYPE) as EnumBlockIce)
        when (type) {
            else -> return 23 + this.getMetaFromState(state)
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
                val type: EnumBlockIce = (state.getValue(BASIC_TYPE) as EnumBlockIce)

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

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getSlipperiness(state: IBlockState, world: IBlockAccess, pos: BlockPos, entity: Entity?): Float {
        return 0.8f
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockIce(private val meta: Int, private val serializedName: String) : IStringSerializable {
        OXYGEN_ICE(0, "oxygen_ice"),
        NITROGEN_ICE(1, "nitrogen_ice"),
        METHANE_ICE(2, "methane_ice"),
        HYDROGEN_ICE(3, "hydrogen_ice"),
        DRY_ICE(4, "dry_ice");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockIce> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockIce? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumBlockIce.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockIce).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockIce> =
            PropertyEnum.create<EnumBlockIce>("type", EnumBlockIce::class.java)
    }
}
