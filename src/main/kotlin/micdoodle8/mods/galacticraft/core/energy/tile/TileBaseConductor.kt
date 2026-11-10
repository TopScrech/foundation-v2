package micdoodle8.mods.galacticraft.core.energy.tile
import micdoodle8.mods.galacticraft.api.transmission.grid.mergeUntyped

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.energy.grid.EnergyNetwork
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * This tile entity pre-fabricated for all conductors.
 *
 * @author Calclavia
 */
abstract class TileBaseConductor : TileEntity(), IConductor {
    @JvmField
    protected var network: IGridNetwork<*, *, *>? = null

    @JvmField
    var adjacentConnections: Array<TileEntity?>? = null

    override fun validate() {
        super.validate()
        if (!this.world.isRemote) {
            TickHandlerServer.energyTransmitterUpdates.add(this)
        }
    }

    override fun invalidate() {
        if (!this.world.isRemote) {
            this.getNetwork()!!.split(this)
        }

        super.invalidate()
    }

    override fun onChunkUnload() {
        super.invalidate()
        super.onChunkUnload()
    }

    override fun getNetwork(): IElectricityNetwork? {
        if (this.network == null) {
            val network: EnergyNetwork = EnergyNetwork()
            network.getTransmitters().add(this)
            this.setNetwork(network)
        }

        return this.network as IElectricityNetwork?
    }

    override fun setNetwork(network: IGridNetwork<*, *, *>?) {
        this.network = network
    }

    override fun refresh() {
        if (!this.world.isRemote) {
            this.adjacentConnections = null

            this.getNetwork()!!.refresh()

            val thisVec: BlockVec3 = BlockVec3(this)
            for (side: EnumFacing in EnumFacing.VALUES) {
                val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, side)

                if (tileEntity is TileBaseConductor && tileEntity.canConnect(side.getOpposite(), NetworkType.POWER)) {
                    val otherNet: IGridNetwork<*, *, *>? = (tileEntity as INetworkProvider).getNetwork()
                    if (this.getNetwork() != otherNet) {
                        if (!(otherNet!!.getTransmitters())!!.isEmpty()) {
                            otherNet.mergeUntyped(this.getNetwork())
                        }
                    }
                }
            }
        }
    }

    override fun getAdjacentConnections(): Array<TileEntity?>? {
        /**
         * Cache the adjacentConnections.
         */
        if (this.adjacentConnections == null) {
            this.adjacentConnections = arrayOfNulls<TileEntity>(6)

            val thisVec: BlockVec3 = BlockVec3(this)
            for (i: Int in 0..5) {
                val side: EnumFacing = EnumFacing.byIndex(i)
                val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, side)

                if (tileEntity is IConnector) {
                    if ((tileEntity as IConnector).canConnect(side.getOpposite(), NetworkType.POWER)) {
                        this.adjacentConnections!![i] = tileEntity
                    }
                }
            }
        }

        return this.adjacentConnections
    }

    override fun hasNetwork(): Boolean {
        return this.network != null
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.POWER
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            this.getPos().getX().toDouble(),
            this.getPos().getY().toDouble(),
            this.getPos().getZ().toDouble(),
            (this.getPos().getX() + 1).toDouble(),
            (this.getPos().getY() + 1).toDouble(),
            (this.getPos().getZ() + 1).toDouble()
        )
    }

    override fun getNetworkType(): NetworkType {
        return NetworkType.POWER
    }

    override fun canTransmit(): Boolean {
        return true
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
