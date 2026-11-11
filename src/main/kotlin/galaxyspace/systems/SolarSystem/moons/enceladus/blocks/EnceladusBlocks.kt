package galaxyspace.systems.SolarSystem.moons.enceladus.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks.EnumEnceladusBlocks
import galaxyspace.systems.SolarSystem.moons.enceladus.blocks.EnceladusBlocks
import micdoodle8.mods.galacticraft.api.block.IDetectableResource
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class EnceladusBlocks constructor() : Block(Material.ROCK), IDetectableResource, ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("enceladusblocks")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel(
            "shovel",
            1,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumEnceladusBlocks.ENCELADUS_SNOW
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumEnceladusBlocks.ENCELADUS_GRUNT
            )
        )
        this.setHarvestLevel(
            "pickaxe",
            2,
            this.defaultBlockState().setValue(
                BASIC_TYPE,
                EnumEnceladusBlocks.ENCELADUS_COAL_ORE
            )
        )
    }

    override fun getBlockHardness(state: IBlockState, worldIn: World, pos: BlockPos): Float {
        val type: EnumEnceladusBlocks = (state.getValue(BASIC_TYPE) as EnumEnceladusBlocks)

        when (type) {
            EnumEnceladusBlocks.ENCELADUS_SNOW -> return 0.2f
            else -> return this.blockHardness
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumEnceladusBlocks in EnumEnceladusBlocks.entries) {
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

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
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
                val type: EnumEnceladusBlocks =
                    (state.getValue(BASIC_TYPE) as EnumEnceladusBlocks)

                when (type) {
                    EnumEnceladusBlocks.ENCELADUS_COAL_ORE -> drops.add(ItemStack(Items.COAL, 1, 0))
                    else -> drops.add(ItemStack(item, 1, damageDropped(state)))
                }
            }
        }
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random): Int {
        val type: EnumEnceladusBlocks = (state.getValue(BASIC_TYPE) as EnumEnceladusBlocks)

        when (type) {
            EnumEnceladusBlocks.ENCELADUS_COAL_ORE -> return 5 + random.nextInt(2)
            else -> return 1
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun isValueable(state: IBlockState?): Boolean {
        return true
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumEnceladusBlocks(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ENCELADUS_SNOW(0, "enceladus_snow"),
        ENCELADUS_GRUNT(1, "enceladus_grunt"),
        ENCELADUS_COAL_ORE(2, "enceladus_coal_ore");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumEnceladusBlocks> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumEnceladusBlocks? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(BASIC_TYPE, (EnumEnceladusBlocks.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumEnceladusBlocks).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }


    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumEnceladusBlocks> =
            PropertyEnum.create<EnumEnceladusBlocks>("type", EnumEnceladusBlocks::class.java)
    }
}
