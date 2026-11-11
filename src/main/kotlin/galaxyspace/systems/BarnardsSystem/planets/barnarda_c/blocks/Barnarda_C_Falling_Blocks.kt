package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.block.LegacyBlockMetadata
import foundation.block.getMetaFromState

import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks.EnumFallingBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Falling_Blocks
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.block.BlockFalling
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class Barnarda_C_Falling_Blocks constructor() : BlockFalling(), ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("barnarda_c_falling_blocks")
        this.setSoundType(SoundType.SAND)
        this.setHardness(0.5f)
        this.setHarvestLevel("shovel", 1)
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

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumFallingBlockBarnardaC in EnumFallingBlockBarnardaC.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val type: EnumFallingBlockBarnardaC =
            (state.getValue(BASIC_TYPE) as EnumFallingBlockBarnardaC)
        val plantType: EnumPlantType? = plantable.getPlantType(world, pos.offset(direction))

        when (type) {
            EnumFallingBlockBarnardaC.SAND -> {
                if (plantType == EnumPlantType.Desert) return true
                return false
            }

            else -> return false
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumFallingBlockBarnardaC(
        private val meta: Int,
        private val serializedName: String,
        private val material: Material
    ) : IStringSerializable {
        SAND(0, "barnarda_c_sand", Material.SAND);

        fun getMeta(): Int {
            return this.meta
        }

        fun getMaterial(): Material {
            return this.material
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumFallingBlockBarnardaC? {
                return entries[meta]
            }
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState().setValue(
            BASIC_TYPE,
            (EnumFallingBlockBarnardaC.byMetadata(meta))!!
        )
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(BASIC_TYPE) as EnumFallingBlockBarnardaC).getMeta()
    }

    override fun getMaterial(state: IBlockState): Material {
        return (state.getValue(BASIC_TYPE) as EnumFallingBlockBarnardaC).getMaterial()
    }

    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor {
        return (state.getValue(BASIC_TYPE) as EnumFallingBlockBarnardaC).getMaterial()
            .getMaterialMapColor()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, BASIC_TYPE)
    }

    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumFallingBlockBarnardaC> =
            PropertyEnum.create<EnumFallingBlockBarnardaC>("type", EnumFallingBlockBarnardaC::class.java)
    }
}
