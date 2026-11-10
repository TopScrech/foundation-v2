package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.api.tile.IColorable
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemDye
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class BlockFluidPipe constructor(assetName: String?, mode: EnumPipeMode?) : BlockTransmitter(Material.GLASS),
    ITileEntityProvider, IShiftDescription, ISortableBlock {
    private val mode: EnumPipeMode?

    init {
        this.setHardness(0.3f)
        this.setSoundType(SoundType.GLASS)
        this.setDefaultState(
            this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE)
        )
        this.setTranslationKey(assetName)
        this.mode = mode
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        var state: IBlockState = state
        state = this.getActualState(state, source, pos)
        return BOUNDING_BOXES[getBoundingBoxIdx(state)]
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile: TileEntityFluidPipe? = worldIn.getTileEntity(pos) as TileEntityFluidPipe?
        val pipeColor: Int = (state.getValue(COLOR))!!.getDyeDamage()

        if (!ignoreDrop && tile != null && pipeColor != 15) {
            spawnItem(worldIn, pos, pipeColor)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(GCBlocks.oxygenPipe)
        //Never drop the 'pull' variety of pipe
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)
        worldIn.notifyLightSet(pos)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        if (this.mode == EnumPipeMode.NORMAL) {
            return GalacticraftCore.Companion.galacticraftBlocksTab
        }

        return null
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
        if (!(world)!!.isRemote) {
            val tile: TileEntityFluidPipe? = (world)!!.getTileEntity(pos) as TileEntityFluidPipe?
            tile!!.switchType()
        }

        return true
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val tileEntity: TileEntityFluidPipe? = worldIn.getTileEntity(pos) as TileEntityFluidPipe?

        if (super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)) {
            return true
        }

        if (!worldIn.isRemote) {
            val stack: ItemStack = playerIn.inventory.getCurrentItem()

            if (!stack.isEmpty()) {
                if (stack.getItem() is ItemDye) {
                    val dyeColor: Int = playerIn.inventory.getCurrentItem().getItemDamage()

                    val colorBefore: Byte = tileEntity!!.getColor(state)

                    tileEntity.onColorUpdate()

                    worldIn.setBlockState(
                        pos,
                        state.withProperty(COLOR, EnumDyeColor.byDyeDamage(dyeColor))
                    )

                    (GalacticraftCore.Companion.packetPipeline)!!.sendToAllAround(
                        PacketSimple(
                            PacketSimple.EnumSimplePacket.C_RECOLOR_PIPE,
                            GCCoreUtil.getDimensionID(worldIn),
                            arrayOf<Any>(pos)
                        ),
                        NetworkRegistry.TargetPoint(
                            GCCoreUtil.getDimensionID(worldIn),
                            pos.getX().toDouble(),
                            pos.getY().toDouble(),
                            pos.getZ().toDouble(),
                            40.0
                        )
                    )

                    if (colorBefore != dyeColor.toByte() && !playerIn.capabilities.isCreativeMode) {
                        playerIn.inventory.getCurrentItem().shrink(1)
                    }

                    if (colorBefore != dyeColor.toByte() && colorBefore.toInt() != 15) {
                        spawnItem(worldIn, pos, colorBefore.toInt())
                    }

                    //					GCCorePacketManager.sendPacketToClients(GCCorePacketManager.getPacket(GalacticraftCore.CHANNELENTITIES, tileEntity, tileEntity.getColor(), (byte) -1)); TODO Fix pipe color
                    val tileVec: BlockPos = tileEntity.getPos()
                    for (dir: EnumFacing in EnumFacing.VALUES) {
                        val tileAt: TileEntity? = worldIn.getTileEntity(tileVec.offset(dir))

                        if (tileAt != null && tileAt is IColorable) {
                            (tileAt as IColorable).onAdjacentColorChanged(dir)
                        }
                    }

                    return true
                }
            }
        }

        return false
    }

    private fun spawnItem(worldIn: World, pos: BlockPos, colorBefore: Int) {
        val f: Float = 0.7f
        val syncRandom: Random = GCCoreUtil.getRandom(pos)
        val d0: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.5
        val d1: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.2 + 0.6
        val d2: Double = syncRandom.nextFloat() * f + (1.0f - f) * 0.5
        val entityitem: EntityItem =
            EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, ItemStack(Items.DYE, 1, colorBefore))
        entityitem.setDefaultPickupDelay()
        worldIn.spawnEntity(entityitem)
    }

    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
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

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityFluidPipe()
    }

    //    @SideOnly(Side.CLIENT)
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        return this.getCollisionBoundingBox(worldIn.getBlockState(pos), worldIn, pos);
    //    }
    override fun getNetworkType(state: IBlockState?): NetworkType {
        return NetworkType.FLUID
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, COLOR, UP, DOWN, NORTH, EAST, SOUTH, WEST)
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(COLOR))!!.getMetadata()
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.TRANSMITTER
    }

    open fun getMode(): EnumPipeMode? {
        return mode
    }

    enum class EnumPipeMode(private val meta: Int, private val serializedName: String) : IStringSerializable {
        NORMAL(0, "normal"),
        PULL(1, "pull");

        fun getMeta(): Int {
            return meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(ordinal: Int): EnumPipeMode? {
                return entries[ordinal]
            }
        }
    }

    companion object {
        @JvmField
        val COLOR: PropertyEnum<EnumDyeColor?> = PropertyEnum.create<EnumDyeColor?>("color", EnumDyeColor::class.java)

        @JvmField
        var ignoreDrop: Boolean = false

        private const val MIN = 0.35f
        private const val MAX = 0.65f

        @JvmField
        protected val BOUNDING_BOXES: Array<AxisAlignedBB> = arrayOf<AxisAlignedBB>(
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // No connection                                  000000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // South                                          000001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // West                                           000010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // South West                                    000011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North                                          000100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // North South                                   000101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North West                                    000110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // North South West                             000111
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // East                                           001000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // East South                                    001001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // West East                                     001010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // South West East                              001011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North East                                    001100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // North South East                             001101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // North East West                              001110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // North South East West                       001111

            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down                                          010000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down South                                    010001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down West                                     010010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down South West                              010011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North                                    010100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down North South                             010101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North West                              010110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                MAX.toDouble(),
                1.0
            ),  // Down North South West                       010111
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down East                                     011000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down East South                              011001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down West East                               011010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down South West East                        011011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North East                              011100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down North South East                       011101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                MAX.toDouble()
            ),  // Down North East West                        011110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                MAX.toDouble(),
                1.0
            ),  // Down North South East West                 011111

            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up                                            100000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up South                                      100001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up West                                       100010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up South West                                100011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up North                                      100100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up North South                               100101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up North West                                100110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up North South West                         100111
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up East                                       101000
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up East South                                101001
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up West East                                 101010
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up South West East                          101011
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up North East                                101100
            AxisAlignedBB(
                MIN.toDouble(),
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East                         101101
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up North East West                          101110
            AxisAlignedBB(
                0.0,
                MIN.toDouble(),
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up North South East West                   101111

            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down                                      110000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down South                                110001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down West                                 110010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down South West                          110011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down North                                110100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South                         110101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                MAX.toDouble()
            ),  // Up Down North West                          110110
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                MAX.toDouble(),
                1.0,
                1.0
            ),  // Up Down North South West                   110111
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down East                                 111000
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down East South                          111001
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down West East                           111010
            AxisAlignedBB(
                0.0,
                0.0,
                MIN.toDouble(),
                1.0,
                1.0,
                1.0
            ),  // Up Down South West East                    111011
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down North East                          111100
            AxisAlignedBB(
                MIN.toDouble(),
                0.0,
                0.0,
                1.0,
                1.0,
                1.0
            ),  // Up Down North South East                   111101
            AxisAlignedBB(
                0.0,
                0.0,
                0.0,
                1.0,
                1.0,
                MAX.toDouble()
            ),  // Up Down North East West                    111110
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        ) // Up Down North South East West            111111

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

            return i
        }
    }
}
