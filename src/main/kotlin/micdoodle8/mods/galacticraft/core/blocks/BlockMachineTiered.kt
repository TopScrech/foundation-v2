package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.IMachineSides
import micdoodle8.mods.galacticraft.core.tile.IMachineSidesProperties
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.properties.PropertyInteger
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
import kotlin.math.floor

open class BlockMachineTiered constructor(assetName: String?) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock {
    enum class EnumTieredMachineType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        STORAGE_MODULE(0, "energy_storage"),
        ELECTRIC_FURNACE(1, "electric_furnace"),
        STORAGE_CLUSTER(2, "cluster_storage"),
        ARC_FURNACE(3, "arc_furnace");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumTieredMachineType {
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

        if (metadata >= ELECTRIC_FURNACE_METADATA + 8) {
            worldIn.setBlockState(pos, getStateFromMeta(ELECTRIC_FURNACE_METADATA + 8 + change), 3)
        } else if (metadata >= STORAGE_MODULE_METADATA + 8) {
            worldIn.setBlockState(pos, getStateFromMeta(STORAGE_MODULE_METADATA + 8 + change), 3)
        } else if (metadata >= ELECTRIC_FURNACE_METADATA) {
            worldIn.setBlockState(pos, getStateFromMeta(ELECTRIC_FURNACE_METADATA + change), 3)
        } else if (metadata >= STORAGE_MODULE_METADATA) {
            worldIn.setBlockState(pos, getStateFromMeta(STORAGE_MODULE_METADATA + change), 3)
        }
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

    /**
     * Called when the block is right clicked by the player
     */
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
        }

        return true
    }

    override fun createNewTileEntity(world: World, metadata: Int): TileEntity? {
        val tier: Int = metadata / 8 + 1

        val tile: TileEntity
        if ((metadata and 4) == ELECTRIC_FURNACE_METADATA) {
            tile = TileEntityElectricFurnace(tier)
        } else {
            tile = TileEntityEnergyStorageModule(tier)
        }

        tile.setWorld(world)
        return tile
    }

    open fun getEnergyStorageModule(): ItemStack {
        return ItemStack(this, 1, STORAGE_MODULE_METADATA)
    }

    open fun getEnergyStorageCluster(): ItemStack {
        return ItemStack(this, 1, 8 + STORAGE_MODULE_METADATA)
    }

    open fun getElectricFurnace(): ItemStack {
        return ItemStack(this, 1, ELECTRIC_FURNACE_METADATA)
    }

    open fun getElectricArcFurnace(): ItemStack {
        return ItemStack(this, 1, 8 + ELECTRIC_FURNACE_METADATA)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(this.getEnergyStorageModule())
        list.add(this.getElectricFurnace())
        list.add(this.getEnergyStorageCluster())
        list.add(this.getElectricArcFurnace())
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state) and 12
    }

    override fun getShiftDescription(meta: Int): String? {
        val tier: Int = (if (meta >= 8) 2 else 1)
        when (meta and 4) {
            ELECTRIC_FURNACE_METADATA -> return GCCoreUtil.translate("tile.electric_furnace_tier" + tier + ".description")
            STORAGE_MODULE_METADATA -> return GCCoreUtil.translate("tile.energy_storage_module_tier" + tier + ".description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumTieredMachineType = EnumTieredMachineType.Companion.byMetadata(floor(meta / 4.0).toInt())
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex() + (state.getValue(
            TYPE
        ) as EnumTieredMachineType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE, FILL_VALUE, SIDES)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        var state: IBlockState = state
        val tile: TileEntity? = worldIn.getTileEntity(pos)
        state = IMachineSides.addPropertyForTile(state, tile, MACHINESIDES_RENDERTYPE, (SIDES)!!)

        if (tile !is TileEntityEnergyStorageModule) {
            return state.withProperty(FILL_VALUE, 0)
        }
        var energyLevel: Int = tile.scaledEnergyLevel
        if (state.getValue(TYPE) == EnumTieredMachineType.STORAGE_CLUSTER) energyLevel += 17
        return state.withProperty(FILL_VALUE, energyLevel)
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
        const val STORAGE_MODULE_METADATA: Int = 0

        const val ELECTRIC_FURNACE_METADATA: Int = 4

        @JvmField
        var MACHINESIDES_RENDERTYPE: IMachineSidesProperties = IMachineSidesProperties.TWOFACES_HORIZ

        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumTieredMachineType> =
            PropertyEnum.create<EnumTieredMachineType>("type", EnumTieredMachineType::class.java)

        @JvmField
        val FILL_VALUE: PropertyInteger = PropertyInteger.create("fill_value", 0, 33)

        @JvmField
        val SIDES: PropertyEnum<IMachineSidesProperties.MachineSidesModel>? = MACHINESIDES_RENDERTYPE.asProperty
    }
}
