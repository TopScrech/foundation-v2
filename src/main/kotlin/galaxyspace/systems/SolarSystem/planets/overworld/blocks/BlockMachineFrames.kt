package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMachineFrames.EnumBlockMachineFrames
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.BlockMachineFrames
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockMachineFrames constructor() : Block(Material.ROCK), ISortableBlock {
    init {
        this.setTranslationKey("machineframes")
        this.setSoundType(SoundType.STONE)
        this.setHarvestLevel("pickaxe", 2)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockMachineFrames in EnumBlockMachineFrames.entries) {
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
        val type: EnumBlockMachineFrames =
            (state.getValue(BASIC_TYPE) as EnumBlockMachineFrames)
        when (type) {
            else -> return getMetaFromState(state)
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockMachineFrames(private val meta: Int, private val serializedName: String) : IStringSerializable {
        BASIC_FRAME(0, "basic_frame"),
        ADVANCE_FRAME(1, "advance_frame"),
        MODERN_FRAME(2, "modern_frame");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumBlockMachineFrames> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockMachineFrames? {
                return values[meta % values.size]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(
            BASIC_TYPE,
            (EnumBlockMachineFrames.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumBlockMachineFrames).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockMachineFrames> =
            PropertyEnum.create<EnumBlockMachineFrames>("type", EnumBlockMachineFrames::class.java)
    }
}
