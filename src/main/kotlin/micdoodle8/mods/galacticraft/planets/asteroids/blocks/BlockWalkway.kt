package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockTransmitter
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe
import micdoodle8.mods.galacticraft.core.tile.TileEntityNull
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BlockWalkway constructor(assetName: String?) : BlockTransmitter(Material.IRON), ITileEntityProvider,
    IShiftDescription, ISortableBlock {
    enum class EnumWalkwayType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        WALKWAY(0, "walkway"),
        WALKWAY_WIRE(1, "walkway_wire"),
        WALKWAY_PIPE(2, "walkway_pipe");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            private val values: Array<EnumWalkwayType> = entries.toTypedArray()

            @JvmStatic
            fun byMetadata(meta: Int): EnumWalkwayType? {
                return values[meta % values.size]
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.METAL)
        this.hasTileEntity = true
        this.setDefaultState(
            this.blockState.getBaseState()
                .withProperty(WALKWAY_TYPE, EnumWalkwayType.WALKWAY)
        )
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
        var state: IBlockState = state
        state = this.getActualState(state, source, pos)

        if (state.getValue(DOWN)) {
            return AABB_CONNECTED_DOWN
        }

        return AABB_UNCONNECTED
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
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

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun isNormalCube(state: IBlockState): Boolean {
        return state.getMaterial().blocksMovement() && state.getBlock().isFullCube(state)
    }

    override fun createNewTileEntity(worldIn: World, metadata: Int): TileEntity? {
        if (metadata == EnumWalkwayType.WALKWAY_PIPE.getMeta()) {
            return TileEntityFluidPipe()
        }

        if (metadata == EnumWalkwayType.WALKWAY_WIRE.getMeta()) {
            return TileEntityAluminumWire(2)
        }

        return TileEntityNull()
    }

    override fun getNetworkType(state: IBlockState?): NetworkType? {
        if ((state)!!.getValue(WALKWAY_TYPE) == EnumWalkwayType.WALKWAY_PIPE) {
            return NetworkType.FLUID
        }

        if ((state)!!.getValue(WALKWAY_TYPE) == EnumWalkwayType.WALKWAY_PIPE) {
            return NetworkType.POWER
        }

        return null
    }

    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        TileEntity tileEntity = worldIn.getTileEntity(pos);
    //        TileEntity[] connectable = new TileEntity[6];
    //
    //        if (tileEntity != null)
    //        {
    //            IBlockState state = worldIn.getBlockState(pos);
    //            if (this.getNetworkType(state) != null)
    //            {
    //                switch (this.getNetworkType(state))
    //                {
    //                case FLUID:
    //                    connectable = OxygenUtil.getAdjacentFluidConnections(tileEntity);
    //                    break;
    //                case POWER:
    //                    connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
    //                    break;
    //                default:
    //                    break;
    //                }
    //            }
    //
    //            float minX = 0.0F;
    //            float minY = 0.32F;
    //            float minZ = 0.0F;
    //            float maxX = 1.0F;
    //            float maxY = 1.0F;
    //            float maxZ = 1.0F;
    //
    //            if (connectable[0] != null)
    //            {
    //                minY = 0.0F;
    //            }
    //
    //            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    //        }
    //    }
    //    private void addCollisionBox(World worldIn, BlockPos pos, AxisAlignedBB aabb, List list)
    //    {
    //        AxisAlignedBB mask1 = this.getCollisionBoundingBox(worldIn, pos, worldIn.getBlockState(pos));
    //
    //        if (mask1 != null && aabb.intersectsWith(mask1))
    //        {
    //            list.add(mask1);
    //        }
    //    }
    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        TileEntity tileEntity = worldIn.getTileEntity(pos);
    //        TileEntity[] connectable = new TileEntity[6];
    //
    //        if (this.getNetworkType(state) != null)
    //        {
    //            switch (this.getNetworkType(state))
    //            {
    //            case FLUID:
    //                connectable = OxygenUtil.getAdjacentFluidConnections(tileEntity);
    //                break;
    //            case POWER:
    //                connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
    //                break;
    //            default:
    //                break;
    //            }
    //        }
    //
    //        this.setBlockBounds((float) this.minVector.x, (float) this.minVector.y, (float) this.minVector.z, (float) this.maxVector.x, (float) this.maxVector.y, (float) this.maxVector.z);
    //        this.addCollisionBox(worldIn, pos, mask, list);
    //
    //        this.setBlockBounds(0.0F, 0.9F, 0.0F, 1.0F, 1.0F, 1.0F);
    //        this.addCollisionBox(worldIn, pos, mask, list);
    //
    //        if (connectable[4] != null)
    //        {
    //            this.setBlockBounds(0, (float) this.minVector.y, (float) this.minVector.z, (float) this.maxVector.x, (float) this.maxVector.y, (float) this.maxVector.z);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        if (connectable[5] != null)
    //        {
    //            this.setBlockBounds((float) this.minVector.x, (float) this.minVector.y, (float) this.minVector.z, 1, (float) this.maxVector.y, (float) this.maxVector.z);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        if (connectable[0] != null)
    //        {
    //            this.setBlockBounds((float) this.minVector.x, 0, (float) this.minVector.z, (float) this.maxVector.x, (float) this.maxVector.y, (float) this.maxVector.z);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        if (connectable[1] != null)
    //        {
    //            this.setBlockBounds((float) this.minVector.x, (float) this.minVector.y, (float) this.minVector.z, (float) this.maxVector.x, 1, (float) this.maxVector.z);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        if (connectable[2] != null)
    //        {
    //            this.setBlockBounds((float) this.minVector.x, (float) this.minVector.y, 0, (float) this.maxVector.x, (float) this.maxVector.y, (float) this.maxVector.z);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        if (connectable[3] != null)
    //        {
    //            this.setBlockBounds((float) this.minVector.x, (float) this.minVector.y, (float) this.minVector.z, (float) this.maxVector.x, (float) this.maxVector.y, 1);
    //            this.addCollisionBox(worldIn, pos, mask, list);
    //        }
    //
    //        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //    }
    override fun getShiftDescription(meta: Int): String? {
        if (meta == EnumWalkwayType.WALKWAY.getMeta()) {
            return GCCoreUtil.translate("tile.walkway.walkway.description")
        } else if (meta == EnumWalkwayType.WALKWAY_WIRE.getMeta()) {
            return GCCoreUtil.translate("tile.walkway.walkway_wire.description")
        } else if (meta == EnumWalkwayType.WALKWAY_PIPE.getMeta()) {
            return GCCoreUtil.translate("tile.walkway.walkway_pipe.description")
        }

        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, WALKWAY_TYPE, NORTH, EAST, SOUTH, WEST, DOWN)
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        var connectable: Array<Any?> = arrayOfNulls<Any>(EnumFacing.VALUES.size)

        var tileEntity: TileEntity? = null

        if (state.getValue(WALKWAY_TYPE) != EnumWalkwayType.WALKWAY) {
            tileEntity = worldIn.getTileEntity(pos)
        }
        for (direction: EnumFacing in EnumFacing.VALUES) {
            if (direction == EnumFacing.UP || (direction == EnumFacing.DOWN && tileEntity == null)) {
                continue
            }

            if (state.getValue(WALKWAY_TYPE) == EnumWalkwayType.WALKWAY) {
                val neighbour: BlockPos = pos.offset(direction)
                val block: Block = worldIn.getBlockState(neighbour).getBlock()

                if (block === this || block.isSideSolid(
                        worldIn.getBlockState(neighbour),
                        worldIn,
                        neighbour,
                        direction.getOpposite()
                    )
                ) {
                    connectable[direction.ordinal] = block
                }
            } else if (tileEntity != null && state.getValue(WALKWAY_TYPE) == EnumWalkwayType.WALKWAY_PIPE) {
                connectable = (OxygenUtil.getAdjacentFluidConnections(tileEntity) as Array<Any?>)
            } else if (tileEntity != null && state.getValue(WALKWAY_TYPE) == EnumWalkwayType.WALKWAY_WIRE) {
                connectable = (EnergyUtil.getAdjacentPowerConnections(tileEntity) as Array<Any?>)
            }
        }

        return state.withProperty(NORTH, connectable[EnumFacing.NORTH.ordinal] != null)
            .withProperty(EAST, connectable[EnumFacing.EAST.ordinal] != null)
            .withProperty(SOUTH, connectable[EnumFacing.SOUTH.ordinal] != null)
            .withProperty(WEST, connectable[EnumFacing.WEST.ordinal] != null)
            .withProperty(DOWN, connectable[EnumFacing.DOWN.ordinal] != null)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(WALKWAY_TYPE, (EnumWalkwayType.Companion.byMetadata(meta))!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(WALKWAY_TYPE) as EnumWalkwayType).getMeta()
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(ItemStack(this, 1, 0))
        list.add(ItemStack(this, 1, 1))
        list.add(ItemStack(this, 1, 2))
    }

    override fun damageDropped(state: IBlockState): Int {
        return this.getMetaFromState(state)
    }

    @SideOnly(Side.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    companion object {
        @JvmField
        val WALKWAY_TYPE: PropertyEnum<EnumWalkwayType> =
            PropertyEnum.create<EnumWalkwayType>("type", EnumWalkwayType::class.java)

        //    private Vector3 minVector = new Vector3(0.0, 0.32, 0.0);
        //    private Vector3 maxVector = new Vector3(1.0, 1.0, 1.0);
        @JvmField
        protected val AABB_UNCONNECTED: AxisAlignedBB = AxisAlignedBB(0.0, 0.32, 0.0, 1.0, 1.0, 1.0)

        @JvmField
        protected val AABB_CONNECTED_DOWN: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    }
}
