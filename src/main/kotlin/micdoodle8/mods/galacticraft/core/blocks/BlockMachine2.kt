package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMachineSides
import micdoodle8.mods.galacticraft.core.tile.IMachineSidesProperties
import micdoodle8.mods.galacticraft.core.tile.TileEntityCircuitFabricator
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricIngotCompressor
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.Random

open class BlockMachine2 constructor(assetName: String?) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock {
    enum class EnumMachineExtendedType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        ELECTRIC_COMPRESSOR(0, "electric_compressor"),
        CIRCUIT_FABRICATOR(1, "circuit_fabricator"),
        OXYGEN_STORAGE(2, "oxygen_storage"),
        DECONSTRUCTOR(3, "deconstructor");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumMachineExtendedType {
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

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return true
    }

    override fun isFullCube(state: IBlockState): Boolean {
        return true
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val tile: TileEntity? = worldIn.getTileEntity(pos)
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val metadata: Int = getMetaFromState(state)

        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()
        worldIn.setBlockState(pos, getStateFromMeta((metadata and 0x0c) + change), 3)
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
        if (!(world)!!.isRemote) {
            (entityPlayer)!!.openGui(GalacticraftCore.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
            return true
        }

        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val metadata: Int = getMetaFromState(state)
        if (metadata >= DECONSTRUCTOR_METADATA) {
            return TileEntityDeconstructor()
        } else if (metadata >= OXYGEN_STORAGE_MODULE_METADATA) {
            return TileEntityOxygenStorageModule()
        } else if (metadata >= CIRCUIT_FABRICATOR_METADATA) {
            return TileEntityCircuitFabricator()
        } else if (metadata >= ELECTRIC_COMPRESSOR_METADATA) {
            return TileEntityElectricIngotCompressor()
        } else {
            return null
        }
    }

    open fun getElectricCompressor(): ItemStack {
        return ItemStack(this, 1, ELECTRIC_COMPRESSOR_METADATA)
    }

    open fun getCircuitFabricator(): ItemStack {
        return ItemStack(this, 1, CIRCUIT_FABRICATOR_METADATA)
    }

    open fun getOxygenStorageModule(): ItemStack {
        return ItemStack(this, 1, OXYGEN_STORAGE_MODULE_METADATA)
    }

    open fun getDeconstructor(): ItemStack {
        return ItemStack(this, 1, DECONSTRUCTOR_METADATA)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(this.getElectricCompressor())
        list.add(this.getCircuitFabricator())
        list.add(this.getOxygenStorageModule())
        list.add(this.getDeconstructor())
    }

    override fun damageDropped(state: IBlockState): Int {
        val metadata: Int = getMetaFromState(state)
        return metadata and 0x0c
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            CIRCUIT_FABRICATOR_METADATA -> return GCCoreUtil.translate("tile.circuit_fabricator.description")
            ELECTRIC_COMPRESSOR_METADATA -> return GCCoreUtil.translate("tile.compressor_electric.description")
            OXYGEN_STORAGE_MODULE_METADATA -> return GCCoreUtil.translate("tile.oxygen_storage_module.description")
            DECONSTRUCTOR_METADATA -> return GCCoreUtil.translate("tile.deconstructor.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumMachineExtendedType = EnumMachineExtendedType.Companion.byMetadata(meta / 4)
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumMachineExtendedType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE, SIDES)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileEntity? = worldIn.getTileEntity(pos)
        return IMachineSides.addPropertyForTile(state, tile, MACHINESIDES_RENDERTYPE, (SIDES)!!)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun onSneakUseWrench(
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
        val tile: TileEntity? = (world)!!.getTileEntity(pos)
        if (tile is IMachineSides) {
            (tile as IMachineSides).nextSideConfiguration(tile)
            return true
        }
        return false
    }

    companion object {
        const val ELECTRIC_COMPRESSOR_METADATA: Int = 0

        const val CIRCUIT_FABRICATOR_METADATA: Int = 4

        const val OXYGEN_STORAGE_MODULE_METADATA: Int = 8

        const val DECONSTRUCTOR_METADATA: Int = 12

        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumMachineExtendedType> =
            PropertyEnum.create<EnumMachineExtendedType>("type", EnumMachineExtendedType::class.java)

        @JvmField
        var MACHINESIDES_RENDERTYPE: IMachineSidesProperties = IMachineSidesProperties.TWOFACES_HORIZ

        @JvmField
        val SIDES: PropertyEnum<IMachineSidesProperties.MachineSidesModel>? = MACHINESIDES_RENDERTYPE.asProperty
    }
}
