package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWireSwitch
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.ITileEntityProvider
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty as PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.StringRepresentable as IStringSerializable
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class BlockAluminumWire constructor(assetName: String?) : BlockTransmitter(Material.CLOTH),
    ITileEntityProvider, IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    enum class EnumWireType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ALUMINUM_WIRE(0, "alu_wire"),
        ALUMINUM_WIRE_HEAVY(1, "alu_wire_heavy"),
        ALUMINUM_WIRE_SWITCHED(2, "alu_wire_switch"),
        ALUMINUM_WIRE_SWITCHED_HEAVY(3, "alu_wire_switch_heavy");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getSerializedName(): String = requireNotNull(getName())

        fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumWireType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumWireType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setSoundType(SoundType.CLOTH)
        this.setResistance(0.2f)
        this.setHardness(0.075f)
        this.setTranslationKey(assetName)
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        var state: IBlockState = state
        state = this.getActualState(state, source, pos)
        return BOUNDING_BOXES[getBoundingBoxIdx(state)]
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.Companion.galacticraftBlocksTab
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return false
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state)
    }

    override fun createNewTileEntity(world: World, metadata: Int): TileEntity? {
        val tile: TileEntity
        when (metadata) {
            0 -> tile = TileEntityAluminumWire(1)
            1 -> tile = TileEntityAluminumWire(2)
            2 -> tile = TileEntityAluminumWireSwitch(1)
            3 -> tile = TileEntityAluminumWireSwitch(2)
            else -> return null
        }

        return tile
    }

    @OnlyIn(Dist.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, 0))
        list.add(ItemStack(this, 1, 1))
        list.add(ItemStack(this, 1, 2))
        list.add(ItemStack(this, 1, 3))
    }

    override fun getNetworkType(state: IBlockState?): NetworkType {
        return NetworkType.POWER
    }

    override fun getShiftDescription(itemDamage: Int): String? {
        when (itemDamage) {
            0 -> return GCCoreUtil.translate("tile.aluminum_wire.alu_wire.description")
            1 -> return GCCoreUtil.translate("tile.aluminum_wire.alu_wire_heavy.description")
            2 -> return GCCoreUtil.translate("tile.aluminum_wire.alu_wire_switch.description")
            3 -> return GCCoreUtil.translate("tile.aluminum_wire.alu_wire_switch_heavy.description")
        }
        return ""
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultBlockState()
            .setValue(WIRE_TYPE, (EnumWireType.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(WIRE_TYPE) as EnumWireType).getMeta()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, WIRE_TYPE, UP, DOWN, NORTH, EAST, SOUTH, WEST)
    }

    override fun showDescription(itemDamage: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.TRANSMITTER
    }

    companion object {
        @JvmField
        val WIRE_TYPE: PropertyEnum<EnumWireType> =
            PropertyEnum.create<EnumWireType>("wiretype", EnumWireType::class.java)
        private const val MIN = 0.38f
        private const val MINH = 0.3f
        private const val MAX = 0.62f
        private const val MAXH = 0.7f

        @JvmField
        protected val BOUNDING_BOXES: Array<AxisAlignedBB> = arrayOf<AxisAlignedBB>(
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // No connection                                  0000000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // South                                          0000001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // West                                           0000010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // South West                                    0000011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North                                          0000100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // North South                                   0000101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North West                                    0000110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // North South West                             0000111
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // East                                           0001000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // East South                                    0001001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // West East                                     0001010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // South West East                              0001011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North East                                    0001100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // North South East                             0001101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North East West                              0001110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // North South East West                       0001111

            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down                                          0010000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down South                                    0010001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down West                                     0010010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down South West                              0010011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North                                    0010100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down North South                             0010101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North West                              0010110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down North South West                       0010111
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down East                                     0011000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down East South                              0011001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down West East                               0011010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down South West East                        0011011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North East                              0011100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down North South East                       0011101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North East West                        0011110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down North South East West                 0011111

            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up                                            0100000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up South                                      0100001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up West                                       0100010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up South West                                0100011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up North                                      0100100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up North South                               0100101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up North West                                0100110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up North South West                         0100111
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up East                                       0101000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up East South                                0101001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up West East                                 0101010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up South West East                          0101011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up North East                                0101100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East                         0101101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up North East West                          0101110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East West                   0101111

            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down                                      0110000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down South                                0110001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down West                                 0110010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down South West                          0110011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down North                                0110100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South                         0110101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down North West                          0110110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South West                   0110111
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down East                                 0111000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down East South                          0111001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down West East                           0111010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down South West East                    0111011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down North East                          0111100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up Down North South East                   0111101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down North East West                    0111110
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),  // Up Down North South East West             0111111

            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // No connection                            1000000
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // South                                     1000001
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // West                                      1000010
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // South West                                1000011
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // North                                     1000100
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // North South                               1000101
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // North West                                1000110
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // North South West                          1000111
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // East                                      1001000
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // East South                                1001001
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // West East                                 1001010
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // South West East                           1001011
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // North East                                1001100
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // North South East                          1001101
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // North East West                           1001110
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // North South East West                     1001111

            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down                                     1010000
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // Down South                                1010001
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down West                                 1010010
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // Down South West                           1010011
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down North                                1010100
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // Down North South                          1010101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down North West                           1010110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAXH.toDouble(),
                MAXH.toDouble(),
                1.0
            ),  // Down North South West                     1010111
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down East                                 1011000
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // Down East South                           1011001
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down West East                            1011010
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // Down South West East                      1011011
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down North East                           1011100
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // Down North South East                     1011101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAXH.toDouble(),
                MAXH.toDouble()
            ),  // Down North East West                      1011110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAXH.toDouble(),
                1.0
            ),  // Down North South East West                1011111

            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up                                       1100000
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up South                                  1100001
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up West                                   1100010
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up South West                             1100011
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up North                                  1100100
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up North South                            1100101
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up North West                             1100110
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up North South West                       1100111
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up East                                   1101000
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up East South                             1101001
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up West East                              1101010
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                MINH.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up South West East                        1101011
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up North East                             1101100
            AxisAlignedBB(
                MINH.toDouble(),
                MINH.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East                       1101101
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up North East West                        1101110
            AxisAlignedBB(
                0.0,
                MINH.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East West                  1101111

            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up Down                                  1110000
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up Down South                             1110001
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up Down West                              1110010
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up Down South West                        1110011
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up Down North                             1110100
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South                       1110101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAXH.toDouble(),
                1.0,
                MAXH.toDouble()
            ),  // Up Down North West                        1110110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAXH.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South West                  1110111
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up Down East                              1111000
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                MINH.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down East South                        1111001
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up Down West East                         1111010
            AxisAlignedBB(
                0.0,
                0.0,
                MINH.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down South West East                   1111011
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up Down North East                        1111100
            AxisAlignedBB(
                MINH.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up Down North South East                  1111101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                1.0,
                MAXH.toDouble()
            ),  // Up Down North East West                   1111110
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        ) // Up Down North South East West            1111111

        @JvmStatic
        private fun getBoundingBoxIdx(state: IBlockState): Int {
            var i: Int = 0

            if (state.getValue(NORTH)) {
                i = i or (1 shl EnumFacing.NORTH.getHorizontalIndex())
            }

            if (state.getValue(EAST)) {
                i = i or (1 shl EnumFacing.EAST.getHorizontalIndex())
            }

            if (state.getValue(SOUTH)) {
                i = i or (1 shl EnumFacing.SOUTH.getHorizontalIndex())
            }

            if (state.getValue(WEST)) {
                i = i or (1 shl EnumFacing.WEST.getHorizontalIndex())
            }

            if (state.getValue(DOWN)) {
                i = i or (1 shl 4)
            }

            if (state.getValue(UP)) {
                i = i or (1 shl 5)
            }

            // Is heavy:
            if ((state.getValue(WIRE_TYPE) as EnumWireType).ordinal % 2 == 1) {
                i = i or (1 shl 6)
            }

            return i
        }
    }
}
