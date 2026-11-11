package micdoodle8.mods.galacticraft.core.blocks

import foundation.item.legacyItemValue

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDecompressor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import kotlin.math.floor

open class BlockOxygenCompressor constructor(isActive: Boolean, assetName: String?) :
    BlockAdvancedTile(Material.ROCK), IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    enum class EnumCompressorType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        COMPRESSOR(0, "compressor"),
        DECOMPRESSOR(1, "decompressor");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumCompressorType {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val state: IBlockState = (world)!!.getBlockState(pos)
        TileBaseUniversalElectrical.onUseWrenchBlock(state, (world)!!, (pos)!!, state.getValue(FACING))
        return true
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        (entityPlayer)!!.openGui(GalacticraftCore.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val metadata: Int = getMetaFromState(state)
        if (metadata >= OXYGEN_DECOMPRESSOR_METADATA) {
            return TileEntityOxygenDecompressor()
        } else if (metadata >= OXYGEN_COMPRESSOR_METADATA) {
            return TileEntityOxygenCompressor()
        } else {
            return null
        }
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        var change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        if (stack.legacyItemValue >= OXYGEN_DECOMPRESSOR_METADATA) {
            change += OXYGEN_DECOMPRESSOR_METADATA
        } else if (stack.legacyItemValue >= OXYGEN_COMPRESSOR_METADATA) {
            change += OXYGEN_COMPRESSOR_METADATA
        }

        worldIn.setBlock(pos, getStateFromMeta(change), 3)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, OXYGEN_COMPRESSOR_METADATA))
        list.add(ItemStack(this, 1, OXYGEN_DECOMPRESSOR_METADATA))
    }

    override fun damageDropped(state: IBlockState): Int {
        val metadata: Int = getMetaFromState(state)
        if (metadata >= OXYGEN_DECOMPRESSOR_METADATA) {
            return OXYGEN_DECOMPRESSOR_METADATA
        } else if (metadata >= OXYGEN_COMPRESSOR_METADATA) {
            return OXYGEN_COMPRESSOR_METADATA
        } else {
            return 0
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            OXYGEN_COMPRESSOR_METADATA -> return GCCoreUtil.translate("tile.oxygen_compressor.description")
            OXYGEN_DECOMPRESSOR_METADATA -> return GCCoreUtil.translate("tile.oxygen_decompressor.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumCompressorType = EnumCompressorType.Companion.byMetadata(floor(meta / 4.0).toInt())
        return this.defaultBlockState().setValue(FACING, enumfacing)
            .setValue(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumCompressorType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        const val OXYGEN_COMPRESSOR_METADATA: Int = 0

        const val OXYGEN_DECOMPRESSOR_METADATA: Int = 4

        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING

        @JvmField
        val TYPE: PropertyEnum<EnumCompressorType> =
            PropertyEnum.create<EnumCompressorType>("type", EnumCompressorType::class.java)
    }
}
