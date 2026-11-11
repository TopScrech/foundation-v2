package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.BooleanProperty as PropertyBool
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry

abstract class BlockTransmitter(material: Material?) : BlockAdvanced((material)!!) {
    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)

        val tile: TileEntity? = worldIn.getBlockEntity(pos)

        //        this.setBlockBoundsBasedOnState(worldIn, pos);
        (GalacticraftCore.packetPipeline)!!.sendToAllAround(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_UPDATE_WIRE_BOUNDS,
                GCCoreUtil.getDimensionID(worldIn),
                arrayOf<Any>(pos)
            ),
            NetworkRegistry.TargetPoint(
                GCCoreUtil.getDimensionID(worldIn),
                pos.getX().toDouble(),
                pos.getY().toDouble(),
                pos.getZ().toDouble(),
                10.0
            )
        )

        if (tile is INetworkConnection) {
            (tile as INetworkConnection).refresh()
        }
    }

    //    @Override
    //    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    //    }
    //
    //    @Override
    //    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    //    {
    //        this.setBlockBoundsBasedOnState(worldIn, pos);
    //        return super.getSelectedBoundingBox(state, worldIn, pos);
    //    }
    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    //    @Override
    //    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    //    {
    //        TileEntity tileEntity = worldIn.getBlockEntity(pos);
    //
    //        if (tileEntity instanceof ITransmitter)
    //        {
    //            Vector3 minVector = this.getMinVector(worldIn.getBlockState(pos));
    //            Vector3 maxVector = this.getMaxVector(worldIn.getBlockState(pos));
    //
    //            TileEntity[] connectable = new TileEntity[6];
    //            switch (this.getNetworkType(worldIn.getBlockState(pos)))
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
    //
    //            float minX = (float) minVector.x;
    //            float minY = (float) minVector.y;
    //            float minZ = (float) minVector.z;
    //            float maxX = (float) maxVector.x;
    //            float maxY = (float) maxVector.y;
    //            float maxZ = (float) maxVector.z;
    //
    //            if (connectable[0] != null)
    //            {
    //                minY = 0.0F;
    //            }
    //
    //            if (connectable[1] != null)
    //            {
    //                maxY = 1.0F;
    //            }
    //
    //            if (connectable[2] != null)
    //            {
    //                minZ = 0.0F;
    //            }
    //
    //            if (connectable[3] != null)
    //            {
    //                maxZ = 1.0F;
    //            }
    //
    //            if (connectable[4] != null)
    //            {
    //                minX = 0.0F;
    //            }
    //
    //            if (connectable[5] != null)
    //            {
    //                maxX = 1.0F;
    //            }
    //
    //            this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    //        }
    //    }
    abstract fun getNetworkType(state: IBlockState?): NetworkType?

    //    @Override
    //    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    //    {
    //        Vector3 minVector = this.getMinVector(state);
    //        Vector3 maxVector = this.getMaxVector(state);
    //        this.setBlockBounds((float) minVector.x, (float) minVector.y, (float) minVector.z, (float) maxVector.x, (float) maxVector.y, (float) maxVector.z);
    //        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //
    //        TileEntity tileEntity = worldIn.getBlockEntity(pos);
    //        if (tileEntity instanceof ITransmitter)
    //        {
    //            TileEntity[] connectable;
    //            switch (this.getNetworkType(state))
    //            {
    //            case FLUID:
    //                connectable = OxygenUtil.getAdjacentFluidConnections(tileEntity);
    //                break;
    //            case POWER:
    //                connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
    //                break;
    //            default:
    //                connectable = new TileEntity[6];
    //            }
    //
    //            if (connectable[4] != null)
    //            {
    //                this.setBlockBounds(0, (float) minVector.y, (float) minVector.z, (float) maxVector.x, (float) maxVector.y, (float) maxVector.z);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //
    //            if (connectable[5] != null)
    //            {
    //                this.setBlockBounds((float) minVector.x, (float) minVector.y, (float) minVector.z, 1, (float) maxVector.y, (float) maxVector.z);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //
    //            if (connectable[0] != null)
    //            {
    //                this.setBlockBounds((float) minVector.x, 0, (float) minVector.z, (float) maxVector.x, (float) maxVector.y, (float) maxVector.z);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //
    //            if (connectable[1] != null)
    //            {
    //                this.setBlockBounds((float) minVector.x, (float) minVector.y, (float) minVector.z, (float) maxVector.x, 1, (float) maxVector.z);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //
    //            if (connectable[2] != null)
    //            {
    //                this.setBlockBounds((float) minVector.x, (float) minVector.y, 0, (float) maxVector.x, (float) maxVector.y, (float) maxVector.z);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //
    //            if (connectable[3] != null)
    //            {
    //                this.setBlockBounds((float) minVector.x, (float) minVector.y, (float) minVector.z, (float) maxVector.x, (float) maxVector.y, 1);
    //                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    //            }
    //        }
    //
    //        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //    }
    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tileEntity: TileEntity? = worldIn.getBlockEntity(pos)

        if (tileEntity is ITransmitter) {
            var connectable: Array<TileEntity?> = arrayOfNulls<TileEntity>(6)
            when (this.getNetworkType(state)) {
                NetworkType.FLUID -> connectable = OxygenUtil.getAdjacentFluidConnections(tileEntity)
                NetworkType.POWER -> connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity)
                else -> {}
            }

            return state.setValue(DOWN, connectable[EnumFacing.DOWN.ordinal] != null)
                .setValue(UP, connectable[EnumFacing.UP.ordinal] != null)
                .setValue(NORTH, connectable[EnumFacing.NORTH.ordinal] != null)
                .setValue(EAST, connectable[EnumFacing.EAST.ordinal] != null)
                .setValue(SOUTH, connectable[EnumFacing.SOUTH.ordinal] != null)
                .setValue(WEST, connectable[EnumFacing.WEST.ordinal] != null)
        }

        return state
    }

    companion object {
        @JvmField
        val UP: PropertyBool = PropertyBool.create("up")

        @JvmField
        val DOWN: PropertyBool = PropertyBool.create("down")

        @JvmField
        val NORTH: PropertyBool = PropertyBool.create("north")

        @JvmField
        val EAST: PropertyBool = PropertyBool.create("east")

        @JvmField
        val SOUTH: PropertyBool = PropertyBool.create("south")

        @JvmField
        val WEST: PropertyBool = PropertyBool.create("west")
    }
}
