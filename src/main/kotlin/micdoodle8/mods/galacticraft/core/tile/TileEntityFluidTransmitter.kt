package micdoodle8.mods.galacticraft.core.tile
import micdoodle8.mods.galacticraft.api.transmission.grid.mergeUntyped
import micdoodle8.mods.galacticraft.api.transmission.grid.splitUntyped

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IBufferTransmitter
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidPipe
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

abstract class TileEntityFluidTransmitter(private val pullAmount: Int) : TileEntityAdvanced(),
    IBufferTransmitter<FluidStack?>, IFluidHandlerWrapper {
    private var network: IGridNetwork<*, *, *>? = null

    @JvmField
    var adjacentConnections: Array<TileEntity?>? = null
    private var validated = true

    override fun validate() {
        this.validated = false
        super.validate()
    }

    override fun invalidate() {
        if (!BlockFluidPipe.ignoreDrop) {
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
                TickHandlerServer.Companion.oxygenTransmitterUpdates.add(this)
                this.validated = true
            }

            val blockType: Block = this.getBlockType()
            if (blockType is BlockFluidPipe && blockType.getMode() == BlockFluidPipe.EnumPipeMode.PULL) {
                val tiles: Array<TileEntity?> = OxygenUtil.getAdjacentFluidConnections(this)

                for (side: EnumFacing in EnumFacing.values()) {
                    val sideTile: TileEntity? = tiles[side.ordinal]

                    if (sideTile != null && sideTile !is IBufferTransmitter<*>) {
                        val handler: IFluidHandler? = sideTile.getCapability<IFluidHandler?>(
                            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                            side.getOpposite()
                        )

                        if (handler != null) {
                            val received: FluidStack? = handler.drain(this.pullAmount, false)

                            if (received != null && received.amount != 0) {
                                handler.drain(this.fill(EnumFacing.DOWN, received, true), true)
                            }
                        }
                    }
                }
            }
        }

        super.update()
    }

    protected fun resetNetwork() {
        val network: FluidNetwork = FluidNetwork()
        network.addTransmitter(this)
        network.register()
        this.setNetwork(network)
    }

    override fun setNetwork(network: IGridNetwork<*, *, *>?) {
        if (this.network === network) {
            return
        }

        if (this.world.isRemote && this.network != null) {
            val fluidNetwork: FluidNetwork = this.network as FluidNetwork
            fluidNetwork.removeTransmitter(this)

            if ((fluidNetwork.getTransmitters())!!.isEmpty()) {
                fluidNetwork.unregister()
            }
        }

        this.network = network

        if (this.world.isRemote && this.network != null) {
            (this.network as FluidNetwork).pipes.add(this)
        }
    }

    override fun refresh() {
        if (!this.world.isRemote) {
            this.adjacentConnections = null

            val thisVec: BlockVec3 = BlockVec3(this)
            for (side: EnumFacing in EnumFacing.values()) {
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
                                (this.getNetwork() as FluidNetwork).addTransmitter(this)
                                (this.getNetwork() as FluidNetwork).onTransmitterAdded(this)
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

    override fun getAdjacentConnections(): Array<TileEntity?> {
        /**
         * Cache the adjacentConnections.
         */
        if (this.adjacentConnections == null) {
            this.adjacentConnections = OxygenUtil.getAdjacentFluidConnections(this, this.world.isRemote)
        }

        return this.adjacentConnections!!
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.FLUID
    }

    @OnlyIn(Dist.CLIENT)
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
        return NetworkType.FLUID
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

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return FluidHandlerWrapper(this, facing) as T
        }
        return super.getCapability<T?>(capability, facing)
    }
}
