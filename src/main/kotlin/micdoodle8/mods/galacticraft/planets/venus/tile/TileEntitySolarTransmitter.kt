package micdoodle8.mods.galacticraft.planets.venus.tile
import micdoodle8.mods.galacticraft.api.transmission.grid.mergeUntyped
import micdoodle8.mods.galacticraft.api.transmission.grid.splitUntyped

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced
import micdoodle8.mods.galacticraft.planets.venus.tick.VenusTickHandlerServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

abstract class TileEntitySolarTransmitter : TileEntityAdvanced(), ITransmitter {
    private var network: IGridNetwork<*, *, *>? = null

    @JvmField
    var adjacentConnections: Array<TileEntity?>? = null
    private var validated = true

    override fun validate() {
        this.validated = false
        super.validate()
    }

    override fun invalidate() {
//        if (!BlockFluidPipe.ignoreDrop)
        run {
            this.getNetwork()!!.splitUntyped(this)
        }

        //        else
//        {
//            this.setNetwork(null);
//        }
        super.invalidate()
    }

    override fun onChunkUnload() {
        super.invalidate()
        super.onChunkUnload()
    }

    //    @Override
    //    public boolean canUpdate()
    //    {
    //        return false;
    //    }
    override fun getNetwork(): IGridNetwork<*, *, *>? {
        if (this.network == null) {
            this.resetNetwork()
        }

        return this.network
    }

    override fun hasNetwork(): Boolean {
        return this.network != null
    }

    override fun onNetworkChanged() {
        this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos())
    }

    override fun update() {
        if (!this.world.isRemote) {
            if (!this.validated) {
                VenusTickHandlerServer.Companion.solarTransmitterUpdates.add(this)
                this.validated = true
            }
        }

        super.update()
    }

    protected fun resetNetwork() {
        val network: SolarModuleNetwork = SolarModuleNetwork()
        this.setNetwork(network)
        network.addTransmitter(this)
        network.register()
    }

    override fun setNetwork(network: IGridNetwork<*, *, *>?) {
        if (this.network === network) {
            return
        }

        if (this.world.isRemote && this.network != null) {
            val solarNetwork: SolarModuleNetwork = this.network as SolarModuleNetwork
            solarNetwork.removeTransmitter(this)

            if ((solarNetwork.getTransmitters())!!.isEmpty()) {
                solarNetwork.unregister()
            }
        }

        this.network = network

        if (this.world.isRemote && this.network != null) {
            ((this.network as SolarModuleNetwork).getTransmitters())!!.add(this)
        }
    }

    override fun refresh() {
        if (!this.world.isRemote) {
            this.adjacentConnections = null

            val thisVec: BlockVec3 = BlockVec3(this)
            for (side: EnumFacing in EnumFacing.VALUES) {
                val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, side)

                if (tileEntity != null) {
                    if (tileEntity.javaClass == this.javaClass && tileEntity is INetworkProvider && (tileEntity as INetworkProvider).hasNetwork()) {
                        if (tileEntity !is ITransmitter || ((tileEntity as ITransmitter).canConnect(
                                side.getOpposite(),
                                (tileEntity as ITransmitter).getNetworkType()
                            ))
                        ) {
                            if (!this.hasNetwork()) {
                                this.setNetwork((tileEntity as INetworkProvider).getNetwork())
                                (this.getNetwork() as SolarModuleNetwork).addTransmitter(this)
                            } else if (this.hasNetwork() && this.getNetwork() != (tileEntity as INetworkProvider).getNetwork()) {
                                this.setNetwork(
                                    this.getNetwork()!!
                                        .mergeUntyped((tileEntity as INetworkProvider).getNetwork()) as IGridNetwork<*, *, *>?
                                )
                            }
                        }
                    }
                }
            }

            this.getNetwork()!!.refresh()
        }
    }

    override fun getAdjacentConnections(): Array<TileEntity?>? {
        /**
         * Cache the adjacentConnections.
         */
        if (this.adjacentConnections == null) {
            this.adjacentConnections = arrayOfNulls<TileEntity>(EnumFacing.VALUES.size)


            val thisVec: BlockVec3 = BlockVec3(this)
            for (direction: EnumFacing in EnumFacing.VALUES) {
                val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, direction)

                if (tileEntity is TileEntitySolarArrayModule) {
                    if (this.world.isRemote || tileEntity.canConnect(
                            direction.getOpposite(),
                            NetworkType.SOLAR_MODULE
                        )
                    ) {
                        this.adjacentConnections!![direction.ordinal] = tileEntity
                    }
                }
            }
        }

        return this.adjacentConnections
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.SOLAR_MODULE
    }

    override fun getNetworkType(): NetworkType {
        return NetworkType.SOLAR_MODULE
    }

    override fun getPacketRange(): Double {
        return 0.0
    }

    override fun getPacketCooldown(): Int {
        return 0
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }
}
